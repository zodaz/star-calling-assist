package com.starcallingassist.modules.sidepanel;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.starcallingassist.StarCallingAssistConfig;
import com.starcallingassist.constants.PluginColors;
import com.starcallingassist.enums.Region;
import com.starcallingassist.events.WorldHopRequest;
import com.starcallingassist.modules.sidepanel.decorators.HeaderPanelDecorator;
import com.starcallingassist.modules.sidepanel.decorators.MasterPanelDecorator;
import com.starcallingassist.modules.sidepanel.decorators.StarListPanelDecorator;
import com.starcallingassist.modules.sidepanel.enums.OrderBy;
import com.starcallingassist.modules.sidepanel.enums.TotalLevelType;
import com.starcallingassist.modules.sidepanel.panels.HeaderPanel;
import com.starcallingassist.modules.sidepanel.panels.StarListPanel;
import com.starcallingassist.objects.Star;
import java.awt.BorderLayout;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import lombok.Setter;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.http.api.worlds.World;

public class SidePanel extends PluginPanel
{
	@Setter
	protected Injector injector;

	@Inject
	private StarCallingAssistConfig config;

	@Inject
	private ConfigManager configManager;

	@Setter
	private int currentWorld = 0;

	private final HeaderPanel headerPanel;

	private StarListPanel starTable;

	public SidePanel(MasterPanelDecorator decorator)
	{
		super(false);
		setLayout(new BorderLayout());
		setBackground(PluginColors.SCROLLBOX_BACKGROUND);

		starTable = new StarListPanel(new StarListPanelDecorator()
		{

			@Override
			public boolean hasAuthorization()
			{
				return SidePanel.this.hasAuthorization();
			}

			@Override
			public boolean shouldEstimateTier()
			{
				return config.estimateTier();
			}

			@Override
			public boolean showFreeToPlayWorlds()
			{
				return config.showF2P();
			}

			@Override
			public boolean showMembersWorlds()
			{
				return config.showMembers();
			}

			@Override
			public boolean showPvPWorlds()
			{
				return config.showPvp();
			}

			@Override
			public boolean showHighRiskWorlds()
			{
				return config.showHighRisk();
			}

			@Override
			public TotalLevelType maxTotalLevel()
			{
				return config.totalLevelType();
			}

			@Override
			public int minTier()
			{
				return config.minTier();
			}

			@Override
			public int maxTier()
			{
				return config.maxTier();
			}

			@Override
			public int minDeadTime()
			{
				return config.minDeadTime();
			}

			@Override
			public List<Region> visibleRegions()
			{
				return Arrays.stream(Region.values())
					.filter(region -> Boolean.parseBoolean(configManager.getConfiguration("starcallingassistplugin", region.getKeyName())))
					.collect(Collectors.toList());
			}

			@Override
			public Boolean showWorldTypeColumn()
			{
				return config.showWorldType();
			}

			@Override
			public Boolean showTierColumn()
			{
				return config.showTier();
			}

			@Override
			public Boolean showDeadTimeColumn()
			{
				return config.showDeadTime();
			}

			@Override
			public Boolean showFoundByColumn()
			{
				return config.showFoundBy();
			}

			@Override
			public int getCurrentWorldId()
			{
				return currentWorld;
			}

			@Override
			public void onWorldHopRequest(WorldHopRequest request)
			{
				decorator.onWorldHopRequest(request);
			}
		});

		headerPanel = new HeaderPanel(new HeaderPanelDecorator()
		{
			@Override
			public boolean hasAuthorization()
			{
				return SidePanel.this.hasAuthorization();
			}

			@Override
			public void onSortingChanged(OrderBy orderBy)
			{
				starTable.setOrderByColumn(orderBy);
				starTable.rebuild();
			}
		});

		add(headerPanel, BorderLayout.NORTH);
		add(starTable, BorderLayout.CENTER);
	}

	private boolean hasAuthorization()
	{
		return !config.getAuthorization().isEmpty();
	}

	public void startUp()
	{
		headerPanel.startUp();
		starTable.startUp();
	}

	public void shutDown()
	{
		//
	}

	public void onStarUpdate(@Nonnull Star star, @Nonnull World world, long updatedAt, @Nonnull String playerName)
	{
		starTable.onStarUpdate(star, world, updatedAt, playerName);
	}

	public void setErrorMessage(String errorMessage)
	{
		headerPanel.setErrorMessage(errorMessage);
		rebuild();
	}

	public void rebuild()
	{
		headerPanel.rebuild();
		starTable.rebuild();

		revalidate();
		repaint();
	}
}