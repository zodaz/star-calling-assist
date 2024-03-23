package com.starcallingassist.sidepanel;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.starcallingassist.StarCallingAssistPlugin;
import com.starcallingassist.sidepanel.constants.OrderBy;
import com.starcallingassist.sidepanel.constants.Region;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.WorldService;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.PluginPanel;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.ResponseBody;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public class SidePanel extends PluginPanel
{
    private static final Color ODD_ROW_COLOR = new Color(44, 44, 44);

    public static final int WORLD_COLUMN_WIDTH = 30;
    public static final int TIER_COLUMN_WIDTH = 20;
    public static final int DEAD_TIME_COLUMN_WIDTH = 40;
    public static final int FOUND_BY_COLUMN_WIDTH = 50;

    private static final int FETCH_TIMEOUT = 20 * 1000;

    private long nextFetch = 0;

    private TableHeader worldHeader;
    private TableHeader tierHeader;
    private TableHeader locationHeader;
    private TableHeader deadTimeHeader;
    private TableHeader foundByHeader;

    private JPanel tableRowContainer;
    private InfoPanel infoPanel;

    @Inject OkHttpClient okHttpClient;
    @Inject Gson gson;
    @Inject WorldService worldService;
    @Inject StarCallingAssistPlugin plugin;
    @Inject ConfigManager configManager;

    @Getter
    private OrderBy orderBy = OrderBy.TIER;

    @Getter
    @Setter
    private boolean ascendingOrder = true;

    private boolean sidePanelOpened = true;

    private final List<TableRow> tableRows = new ArrayList<>();
    private List<StarData> starData = new ArrayList<>();
    private List<World> worldList = new ArrayList<>();

    private class HeaderMouseListener extends MouseAdapter {
	private final OrderBy orderBy;
	private final SidePanel sidePanel;

	public HeaderMouseListener(OrderBy orderBy, SidePanel sidePanel) {
	    this.orderBy = orderBy;
	    this.sidePanel = sidePanel;
	}

	@Override
	public void mousePressed(MouseEvent mouseEvent) {
	    if (SwingUtilities.isRightMouseButton(mouseEvent))
		return;
	    sidePanel.setAscendingOrder(sidePanel.getOrderBy() != orderBy || !sidePanel.isAscendingOrder());
	    sidePanel.setOrderBy(orderBy);
	}
    }

    public void init()
    {
	setBorder(null);
	setLayout(new DynamicGridLayout(0, 1));

	infoPanel = new InfoPanel(plugin);

	JPanel headerContainer = buildTableHeader();
	tableRowContainer = new JPanel();
	tableRowContainer.setLayout(new GridLayout(0, 1));

	add(infoPanel);
	add(headerContainer);
	add(tableRowContainer);

	fetchWorldData();
	fetchStarData();
    }

    @Override
    public void onActivate()
    {
	sidePanelOpened = true;
	fetchStarData();
    }

    @Override
    public void onDeactivate()
    {
	sidePanelOpened = false;
    }

    public boolean isOpen()
    {
	return sidePanelOpened;
    }

    public void updateInfoPanel()
    {
	infoPanel.rebuild();
    }

    public void updateTableRows()
    {
	tableRowContainer.removeAll();

	tableRows.sort((r1, r2) ->
        {
	   switch (orderBy)
	   {
	       case WORLD:
		   return ascendingOrder ? Integer.compare(r2.getData().getWorldId(), r1.getData().getWorldId()) : Integer.compare(r1.getData().getWorldId(), r2.getData().getWorldId());
	       case TIER:
		   return ascendingOrder ? Integer.compare(r2.getData().getTier(plugin.getConfig().estimateTier()), r1.getData().getTier(plugin.getConfig().estimateTier()))
					 : Integer.compare(r1.getData().getTier(plugin.getConfig().estimateTier()), r2.getData().getTier(plugin.getConfig().estimateTier()));
	       case LOCATION:
		   return ascendingOrder ? r2.getData().getLocation().compareTo(r1.getData().getLocation()) : r1.getData().getLocation().compareTo(r2.getData().getLocation());
	       case DEAD_TIME:
		   return ascendingOrder ? Integer.compare(r2.getData().getDeadTime(), r1.getData().getDeadTime()) : Integer.compare(r1.getData().getDeadTime(), r2.getData().getDeadTime());
	       default:
		   return 0;
	   }
        });

	for (TableRow row : tableRows)
	{
	    row.setRowVisible(shouldBeVisible(row.getData()));
	    if(row.isRowVisible())
		tableRowContainer.add(row);
	}

	colorRows();

	tableRowContainer.revalidate();
	tableRowContainer.repaint();
    }

    public void rebuildTableRows()
    {
	tableRows.clear();
	for (StarData data : starData)
	    tableRows.add(new TableRow(data, plugin));
	updateTableRows();
    }

    private boolean shouldBeVisible(StarData data)
    {
	if(data.isP2p() && !plugin.getConfig().showMembers())
	    return false;

	if(!data.isP2p() && !plugin.getConfig().showF2P())
	    return false;

	if(data.isPvp() && !plugin.getConfig().showPvp())
	    return false;

	if(data.isHighRisk() && !plugin.getConfig().showHighRisk())
	    return false;

	if(data.getTotalLevelType().ordinal() > plugin.getConfig().totalLevelType().ordinal())
	    return false;

	if(data.getTier(plugin.getConfig().estimateTier()) < plugin.getConfig().minTier() || data.getTier(plugin.getConfig().estimateTier()) > plugin.getConfig().maxTier())
	    return false;

	if(data.getDeadTime() < plugin.getConfig().minDeadTime())
	    return false;

	Region[] regions = Region.values();

	if(data.getRegion() >= 0 && data.getRegion() < regions.length)
	    return Boolean.parseBoolean(configManager.getConfiguration("starcallingassistplugin", regions[data.getRegion()].getKeyName()));

	return true;
    }

    private void colorRows()
    {
	int i = 0;
	for(TableRow row : tableRows)
	{
	    if(!row.isRowVisible())
		continue;

	    if(i++ % 2 == 0)
		row.setBackground(ColorScheme.DARKER_GRAY_COLOR);
	    else
		row.setBackground(ODD_ROW_COLOR);
	}
    }

    private JPanel buildTableHeader()
    {
	JPanel header = new JPanel(new BorderLayout());
	JPanel leftSide = new JPanel(new BorderLayout());
	JPanel center = new JPanel(new BorderLayout());
	JPanel rightSide = new JPanel(new BorderLayout());

	worldHeader = new TableHeader("W", orderBy == OrderBy.WORLD, ascendingOrder);
	worldHeader.setPreferredSize(new Dimension(WORLD_COLUMN_WIDTH, 0));
	worldHeader.addMouseListener(new HeaderMouseListener(OrderBy.WORLD, this));

	tierHeader = new TableHeader("T", orderBy == OrderBy.TIER, ascendingOrder);
	tierHeader.setPreferredSize(new Dimension(TIER_COLUMN_WIDTH, 0));
	tierHeader.addMouseListener(new HeaderMouseListener(OrderBy.TIER, this));

	locationHeader = new TableHeader("Location", orderBy == OrderBy.LOCATION, ascendingOrder);
	locationHeader.addMouseListener(new HeaderMouseListener(OrderBy.LOCATION, this));

	deadTimeHeader = new TableHeader("Dead", orderBy == OrderBy.DEAD_TIME, ascendingOrder);
	deadTimeHeader.setPreferredSize(new Dimension(DEAD_TIME_COLUMN_WIDTH, 0));
	deadTimeHeader.addMouseListener(new HeaderMouseListener(OrderBy.DEAD_TIME, this));

	foundByHeader = new TableHeader("Found by");
	foundByHeader.setPreferredSize(new Dimension(FOUND_BY_COLUMN_WIDTH, 0));

	leftSide.add(worldHeader, BorderLayout.WEST);
	leftSide.add(tierHeader, BorderLayout.CENTER);
	center.add(locationHeader, BorderLayout.CENTER);
	rightSide.add(deadTimeHeader, BorderLayout.CENTER);
	rightSide.add(foundByHeader, BorderLayout.EAST);

	header.add(leftSide, BorderLayout.WEST);
	header.add(center, BorderLayout.CENTER);
	header.add(rightSide, BorderLayout.EAST);

	return header;
    }

    public void setOrderBy(OrderBy order)
    {
	worldHeader.highlight(false, ascendingOrder);
	tierHeader.highlight(false, ascendingOrder);
	locationHeader.highlight(false, ascendingOrder);
	deadTimeHeader.highlight(false, ascendingOrder);

	switch (order)
	{
	    case WORLD:
		worldHeader.highlight(true, ascendingOrder);
		break;
	    case TIER:
		tierHeader.highlight(true, ascendingOrder);
		break;
	    case LOCATION:
		locationHeader.highlight(true, ascendingOrder);
		break;
	    case DEAD_TIME:
		deadTimeHeader.highlight(true, ascendingOrder);
		break;
	}
	orderBy = order;
	updateTableRows();
    }

    public void fetchWorldData()
    {
	WorldResult worldResult = worldService.getWorlds();
	if(worldResult == null)
	    return;
	List<World> worlds = worldResult.getWorlds();
	if (worlds == null || worlds.isEmpty())
	    return;
	worldList = worlds;
    }

    private World getWorldObject(int worldId)
    {
	Optional<World> result = worldList.stream().filter(world -> world.getId() == worldId).findFirst();
	if(result.isPresent())
	    return result.get();
	return null;
    }

    public void fetchStarData()
    {
	// Never fetch data if sidepanel is closed
	if(!sidePanelOpened)
	    return;

	// Don't fetch if less than 10s since last
	if(nextFetch > System.currentTimeMillis())
	    return;

	if(plugin.getConfig().getAuthorization().isEmpty())
	{
	    SwingUtilities.invokeLater(() -> infoPanel.setErrorMessage(""));
	    return;
	}

	if(plugin.getClient().getGameState() != GameState.LOGGED_IN)
	{
	    SwingUtilities.invokeLater(() -> infoPanel.setErrorMessage("You need to be logged in to update the list!"));
	    return;
	}

	nextFetch = System.currentTimeMillis() + FETCH_TIMEOUT;

	try
	{
	    Request request = new Builder()
		    .url(plugin.getConfig().getEndpoint().replaceAll("\\s+", ""))
		    .addHeader("authorization", plugin.getConfig().getAuthorization().replaceAll("\\s+", ""))
		    .addHeader("plugin", plugin.getName())
		    .addHeader("crowdsourcing", String.valueOf(plugin.getConfig().autoCall() && plugin.getConfig().updateStar()))
		    .get()
		    .build();

	    okHttpClient.newCall(request).enqueue(new Callback()
	    {
		@Override
		public void onFailure(Call call, IOException e)
		{
		    SwingUtilities.invokeLater(() -> infoPanel.setErrorMessage(e.getMessage()));
		    call.cancel();
		}
		@Override
		public void onResponse(Call call, Response res) throws IOException
		{
		    if (res.isSuccessful()) {
			SwingUtilities.invokeLater(() -> infoPanel.setErrorMessage(""));
			parseData(res.body());
		    } else {
			SwingUtilities.invokeLater(() -> infoPanel.setErrorMessage(res.message()));
		    }
		    res.close();
		}
	    });
	}
	catch (IllegalArgumentException iae)
	{
	    SwingUtilities.invokeLater(() -> infoPanel.setErrorMessage("Invalid endpoint!"));
	}
    }

    private void parseData(@Nullable ResponseBody body)
    {
	List<StarData> starData = new ArrayList<>();
	if(body == null)
	    return;

	try{

	    JsonArray jsonArray = gson.fromJson(body.string(), JsonArray.class);
	    if(jsonArray.size() < 1)
		return;
	    for(final JsonElement element : jsonArray)
	    {
		JsonObject obj = element.getAsJsonObject();
		if(obj.has("world") && obj.has("tier") &&
		   obj.has("calledLocation") && obj.has("calledBy") &&
		   obj.has("calledAt") && obj.has("location"))
		{
		    starData.add(
			    new StarData(
				    obj.get("world").getAsInt(),
				    getWorldObject(obj.get("world").getAsInt()),
				    obj.get("tier").getAsInt(),
				    obj.get("calledLocation").getAsString(),
				    obj.get("calledBy").getAsString(),
				    obj.get("calledAt").getAsLong(),
				    obj.get("location").getAsInt()
			    )
		    );
		}
	    }
	}
	catch (Exception e)
	{
	    log.error("Error parsing response! " + e.getMessage());
	}
	if(!starData.isEmpty())
	{
	    this.starData = starData;
	    SwingUtilities.invokeLater(this::rebuildTableRows);
	}
    }

    private List<Integer> getHiddenRegions()
    {
	List<Integer> hiddenRegions = new ArrayList<>();

	for(Region region : Region.values())
	    if(!Boolean.parseBoolean(configManager.getConfiguration("starcallingassistplugin", region.keyName)))
		hiddenRegions.add(region.ordinal());

	return hiddenRegions;
    }

}
