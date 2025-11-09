package com.starcallingassist.modules.sidepanel;

import com.google.inject.Inject;
import com.starcallingassist.PluginModuleContract;
import com.starcallingassist.events.AnnouncementReceived;
import com.starcallingassist.events.AnnouncementRefreshFailed;
import com.starcallingassist.events.AnnouncementsRefreshed;
import com.starcallingassist.events.NavButtonClicked;
import com.starcallingassist.events.PluginConfigChanged;
import com.starcallingassist.events.ShowWorldPointOnWorldMapRequested;
import com.starcallingassist.events.StarDepleted;
import com.starcallingassist.events.StarLocationRegionEntered;
import com.starcallingassist.events.StarLocationRegionExited;
import com.starcallingassist.events.StarMissing;
import com.starcallingassist.events.StarScouted;
import com.starcallingassist.events.StarTierChanged;
import com.starcallingassist.events.WorldHopRequest;
import com.starcallingassist.events.RouteViaShortestPathRequested;
import com.starcallingassist.modules.crowdsourcing.objects.AnnouncedStar;
import com.starcallingassist.modules.sidepanel.decorators.MasterPanelDecorator;
import com.starcallingassist.objects.Star;
import com.starcallingassist.objects.StarLocation;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.WorldChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.WorldService;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;

public class SidePanelModule extends PluginModuleContract
{
	@Inject
	private Client client;

	@Inject
	private ClientToolbar clientToolbar;

	@Inject
	private WorldService worldService;

	private SidePanel sidePanel;

	private NavigationButton navButton;

	private List<World> worldList = new ArrayList<>();

	private final List<StarLocation> currentPlayerRegions = new ArrayList<>();

	@Override
	public void startUp()
	{
		if (sidePanel == null)
		{
			sidePanel = new SidePanel(new MasterPanelDecorator()
			{
				@Override
				public void onWorldHopRequest(WorldHopRequest worldHopRequest)
				{
					dispatch(worldHopRequest);
				}

				@Override
				public void onShowWorldPointOnWorldMapRequested(ShowWorldPointOnWorldMapRequested showWorldPointOnWorldMapRequested)
				{
					dispatch(showWorldPointOnWorldMapRequested);
				}

				@Override
				public void onRouteViaShortestPathRequested(RouteViaShortestPathRequested routeViaShortestPathRequested)
				{
					dispatch(routeViaShortestPathRequested);
				}

				@Override
				public List<StarLocation> getCurrentPlayerRegions()
				{
					return currentPlayerRegions;
				}

				@Override
				public void onSidePanelVisibilityChanged(boolean isVisible)
				{
					dispatch(new NavButtonClicked(isVisible));
				}
			});
			sidePanel.setInjector(injector);
			injector.injectMembers(sidePanel);
		}

		sidePanel.startUp();

		navButton = NavigationButton.builder()
			.tooltip("Star Miners")
			.icon(ImageUtil.loadImageResource(getClass(), "/sminers.png"))
			.panel(sidePanel)
			.build();

		clientToolbar.addNavigation(navButton);

		fetchWorldData();
		sidePanel.setCurrentWorld(client.getWorld());
	}

	@Override
	public void shutDown()
	{
		sidePanel.shutDown();
		currentPlayerRegions.clear();
		clientToolbar.removeNavigation(navButton);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged state)
	{
		if (state.getGameState() == GameState.LOGGED_IN)
		{
			SwingUtilities.invokeLater(sidePanel::rebuild);
		}
	}

	@Subscribe
	public void onWorldChanged(WorldChanged event)
	{
		currentPlayerRegions.clear();
		sidePanel.setCurrentWorld(client.getWorld());
		SwingUtilities.invokeLater(sidePanel::rebuild);
	}

	@Subscribe
	public void onPluginConfigChanged(PluginConfigChanged event)
	{
		if (event.getKey().equals("endpoint") || event.getKey().equals("authorization"))
		{
			sidePanel.setErrorMessage("");
		}

		sidePanel.rebuild();
	}

	@Subscribe
	public void onAnnouncementRefreshFailed(AnnouncementRefreshFailed event)
	{
		SwingUtilities.invokeLater(() -> sidePanel.setErrorMessage(event.getMessage()));
	}

	@Subscribe
	public void onAnnouncementsRefreshed(AnnouncementsRefreshed event)
	{
		SwingUtilities.invokeLater(() -> sidePanel.setErrorMessage(""));
	}

	@Subscribe
	public void onAnnouncementReceived(AnnouncementReceived event)
	{
		AnnouncedStar announcement = event.getAnnouncement();
		Integer world = announcement.getStar().getWorld();

		World worldObject = getWorldObject(world);
		if (worldObject == null)
		{
			return;
		}

		sidePanel.onStarUpdate(
			announcement.getStar(),
			worldObject,
			announcement.getUpdatedAt()
		);
	}

	@Subscribe
	public void onStarScouted(StarScouted event)
	{
		updateStarFromLocalStateChange(event.getStar());
	}

	@Subscribe
	public void onStarTierChanged(StarTierChanged event)
	{
		updateStarFromLocalStateChange(event.getStar());
	}

	@Subscribe
	public void onStarDepleted(StarDepleted event)
	{
		updateStarFromLocalStateChange(event.getStar());
	}

	@Subscribe
	public void onStarMissing(StarMissing event)
	{
		updateStarFromLocalStateChange(event.getStar());
	}

	@Subscribe
	public void onStarLocationRegionEntered(StarLocationRegionEntered event)
	{
		if (!currentPlayerRegions.contains(event.getLocation()))
		{
			currentPlayerRegions.add(event.getLocation());
			SwingUtilities.invokeLater(sidePanel::rebuild);
		}
	}

	@Subscribe
	public void onStarLocationRegionExited(StarLocationRegionExited event)
	{
		currentPlayerRegions.remove(event.getLocation());
		SwingUtilities.invokeLater(sidePanel::rebuild);
	}

	@Override
	public void onSecondElapsed(int secondsSinceStartup)
	{
		// Every 10 minutes
		if (secondsSinceStartup % (60 * 10) == 0)
		{
			fetchWorldData();
		}
	}

	private void fetchWorldData()
	{
		WorldResult worldResult = worldService.getWorlds();
		if (worldResult == null)
		{
			return;
		}

		List<World> worlds = worldResult.getWorlds();
		if (worlds == null || worlds.isEmpty())
		{
			return;
		}

		worldList = worlds;
	}

	private World getWorldObject(int worldId)
	{
		return worldList.stream()
			.filter(world -> world.getId() == worldId)
			.findFirst()
			.orElse(null);
	}

	private void updateStarFromLocalStateChange(Star star)
	{
		World worldObject = getWorldObject(star.getWorld());
		if (worldObject == null)
		{
			return;
		}

		sidePanel.onStarUpdate(
			star,
			worldObject,
			(System.currentTimeMillis() / 1000L) - 5 // Always make sure it's slightly outdated
		);
	}
}
