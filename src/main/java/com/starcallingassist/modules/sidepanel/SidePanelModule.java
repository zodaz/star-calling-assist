package com.starcallingassist.modules.sidepanel;

import com.google.inject.Inject;
import com.starcallingassist.PluginModuleContract;
import com.starcallingassist.events.PluginConfigChanged;
import com.starcallingassist.old.SidePanel;
import java.time.temporal.ChronoUnit;
import javax.swing.SwingUtilities;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;

public class SidePanelModule extends PluginModuleContract
{
	@Inject
	@Getter
	private Client client;

	@Inject
	private SidePanel sidePanel;

	private NavigationButton navButton;

	@Inject
	private ClientToolbar clientToolbar;

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
	public void onPluginConfigChanged(PluginConfigChanged event)
	{
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
