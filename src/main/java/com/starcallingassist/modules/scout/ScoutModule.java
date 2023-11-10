package com.starcallingassist.modules.scout;

import com.starcallingassist.PluginModuleContract;
import com.starcallingassist.StarCallingAssistConfig;
import com.starcallingassist.events.PluginConfigChanged;
import com.starcallingassist.events.StarLocationRegionEntered;
import com.starcallingassist.events.StarLocationRegionExited;
import com.starcallingassist.events.StarLocationScouted;
import com.starcallingassist.objects.StarLocation;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.ImageUtil;

@Slf4j
public class ScoutModule extends PluginModuleContract
{
	@Inject
	private Client client;

	@Inject
	private StarCallingAssistConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private WorldMapPointManager worldMapPointManager;

	private ScoutWorldOverlay scoutWorldOverlay;
	private WorldMapBoundsOverlay scoutWorldMapBoundsOverlay;

	@Getter
	private final HashMap<StarLocation, StarLocationState> locations = new HashMap<>();

	private boolean overlayVisible = false;

	public ScoutModule()
	{
		StarLocation.LOCATIONS.keySet().forEach(point -> locations.put(new StarLocation(point), new StarLocationState()));
	}

	@Override
	public void startUp()
	{
		scoutWorldOverlay = new ScoutWorldOverlay(this);
		injector.injectMembers(scoutWorldOverlay);

		scoutWorldMapBoundsOverlay = new WorldMapBoundsOverlay(this);
		injector.injectMembers(scoutWorldMapBoundsOverlay);

		if (config.scoutOverlay())
		{
			addOverlay();
		}
	}

	@Override
	public void shutDown()
	{
		removeOverlay();
		resetLocationState();
	}

	@Subscribe
	public void onPluginConfigChanged(PluginConfigChanged event)
	{
		if (!event.getKey().equals("scoutOverlay"))
		{
			return;
		}

		if (config.scoutOverlay() && !overlayVisible)
		{
			addOverlay();
		}
		else if (!config.scoutOverlay() && overlayVisible)
		{
			removeOverlay();
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGIN_SCREEN)
		{
			resetLocationState();
		}
	}

	private void resetLocationState()
	{
		locations.values().forEach(entry -> {
			entry.setRegionLoaded(false);
			entry.setPlayerWithinBounds(false);
			entry.setLastScoutedAt(0L);
		});
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
		if (playerLocation == null)
		{
			return;
		}

		locations.forEach((location, state) -> {
			boolean isPlayerWithinBounds = location.getScoutableBounds().contains(playerLocation);
			boolean isRegionLoaded = Arrays.stream(client.getMapRegions()).anyMatch(region -> region == location.getWorldPoint().getRegionID());

			boolean wasPlayerWithinBounds = state.isPlayerWithinBounds();
			boolean wasRegionLoaded = state.isRegionLoaded();

			if (wasPlayerWithinBounds && !isPlayerWithinBounds)
			{
				state.setPlayerWithinBounds(false);
				dispatch(new StarLocationRegionExited(location));
			}

			if (!wasPlayerWithinBounds && isPlayerWithinBounds)
			{
				state.setPlayerWithinBounds(true);
				dispatch(new StarLocationRegionEntered(location));
			}

			if ((isPlayerWithinBounds && isRegionLoaded && !wasRegionLoaded) || (isPlayerWithinBounds && !wasPlayerWithinBounds && isRegionLoaded))
			{
				state.setRegionLoaded(true);
				state.setPlayerWithinBounds(true);
				dispatch(new StarLocationScouted(location));
			}

			if (wasRegionLoaded && !isRegionLoaded)
			{
				state.setRegionLoaded(false);
			}

			if (isPlayerWithinBounds && isRegionLoaded)
			{
				state.setLastScoutedAt(System.currentTimeMillis());
			}
		});
	}

	private void addOverlay()
	{
		if (!overlayVisible)
		{
			overlayManager.add(scoutWorldOverlay);
			overlayManager.add(scoutWorldMapBoundsOverlay);
			locations.keySet().forEach(location -> worldMapPointManager.add(new ScoutWorldMapPoint(this, location)));
			overlayVisible = true;
		}
	}

	private void removeOverlay()
	{
		if (overlayVisible)
		{
			overlayManager.remove(scoutWorldOverlay);
			overlayManager.remove(scoutWorldMapBoundsOverlay);
			worldMapPointManager.removeIf(ScoutWorldMapPoint.class::isInstance);
			overlayVisible = false;
		}
	}

	public BufferedImage getStarScoutLocationImage()
	{
		return ImageUtil.loadImageResource(getClass(), "/star_scoutloc.png");
	}
}
