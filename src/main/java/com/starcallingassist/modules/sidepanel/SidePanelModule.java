package com.starcallingassist.modules.sidepanel;

import com.starcallingassist.StarModuleContract;
import com.starcallingassist.old.SidePanel;
import java.time.temporal.ChronoUnit;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.World;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

@Slf4j
public class SidePanelModule extends StarModuleContract
{
	@Inject
	@Getter
	private Client client;
	@Inject
	private ClientThread clientThread;

	@Inject
	private SidePanel sidePanel;

	private NavigationButton navButton;

	@Inject
	private ClientToolbar clientToolbar;

	private int hopTarget = -1;
	private int hopAttempts = 0;

	@Override
	public void startUp()
	{
		sidePanel.init();

		navButton = NavigationButton.builder()
			.tooltip("Star Miners")
			.icon(ImageUtil.loadImageResource(getClass(), "/sminers.png"))
			.panel(sidePanel)
			.build();

		clientToolbar.addNavigation(navButton);
		navButton.setOnClick(this::fetchStarData);
	}

	@Override
	public void shutDown()
	{
		clientToolbar.removeNavigation(navButton);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged state)
	{
		if (state.getGameState() == GameState.LOGGED_IN)
		{
			sidePanel.setModule(this);
			SwingUtilities.invokeLater(() -> sidePanel.rebuildTableRows());
			fetchStarData();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("starcallingassistplugin"))
		{
			return;
		}

		if (event.getKey().equals("endpoint"))
		{
			fetchStarData();
			return;
		}

		if (event.getKey().equals("authorization"))
		{
			sidePanel.updateInfoPanel();
			return;
		}

		if (event.getKey().equals("estimateTier"))
		{
			sidePanel.setModule(this);
			sidePanel.rebuildTableRows();
			return;
		}

		sidePanel.updateTableRows();
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (hopTarget != -1)
		{
			performHop();
		}

	}

	public void queueWorldHop(int worldId)
	{
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			hopTarget = worldId;
			clientThread.invokeLater(() -> plugin.logHighlightedToChat("Attempting to quick-hop to world ", String.valueOf(hopTarget)));
		}
	}

	private void performHop()
	{
		if (++hopAttempts >= 5)
		{
			plugin.logHighlightedToChat("Unable to quick-hop to world ", String.valueOf(hopTarget));
			hopTarget = -1;
			hopAttempts = 0;
			return;
		}

		if (client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST) == null)
		{
			client.openWorldHopper();
			return;
		}

		World[] worldList = client.getWorldList();

		if (worldList == null)
		{
			return;
		}

		for (World world : worldList)
		{
			if (world.getId() == hopTarget)
			{
				client.hopToWorld(world);
				break;
			}
		}

		hopTarget = -1;
		hopAttempts = 0;
	}

	@Schedule(
		period = 30,
		unit = ChronoUnit.SECONDS
	)
	public void fetchStarData()
	{
		if (navButton.isSelected())
		{
			sidePanel.setModule(this);
			sidePanel.fetchStarData();
		}
	}

	@Schedule(
		period = 10,
		unit = ChronoUnit.MINUTES
	)
	public void fetchWorldData()
	{
		sidePanel.fetchWorldData();
	}
}
