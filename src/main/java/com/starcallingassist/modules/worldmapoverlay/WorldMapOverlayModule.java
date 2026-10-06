package com.starcallingassist.modules.worldmapoverlay;

import com.google.inject.Inject;
import com.starcallingassist.PluginModuleContract;
import com.starcallingassist.StarCallingAssistConfig;
import com.starcallingassist.enums.SignalEventType;
import com.starcallingassist.events.AnnouncementAttributesUpdated;
import com.starcallingassist.events.PluginConfigChanged;
import com.starcallingassist.events.ShowStarDetailsOnWorldMapRequested;
import com.starcallingassist.events.SignalEvent;
import com.starcallingassist.modules.sidepanel.objects.StarListEntryAttributes;
import com.starcallingassist.modules.worldmapoverlay.enums.WorldMapDisplayLevel;
import com.starcallingassist.modules.worldmapoverlay.worldmapstardetails.WorldMapStarDetailsOverlay;
import com.starcallingassist.modules.worldmapoverlay.worldmapstardetails.WorldMapStarDetailsOverlayMouseAdapter;
import com.starcallingassist.enums.StarLocationDetails;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;

/**
 * Module handling displaying stars on the world map.
 */
@Slf4j
public class WorldMapOverlayModule extends PluginModuleContract
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private WorldMapPointManager worldMapPointManager;

	@Inject
	private MouseManager mouseManager;

	@Inject
	private Client client;

	@Inject
	private EventBus eventBus;

	@Inject
	private StarCallingAssistConfig config;

	@Inject
	SpriteManager spriteManager;

	@Inject
	private WorldMapStarDetailsOverlay worldMapStarDetailsOverlay;

	@Inject
	private WorldMapStarDetailsOverlayMouseAdapter worldMapStarDetailsOverlayMouseAdapter;

	/**
	 * Location name -> {@link StarLocationWorldMapPoint} mapping for every star on the world map.
	 */
	private final Map<String, StarLocationWorldMapPoint> worldMapPoints = new HashMap<>();

	/**
	 * The world map point of the star that should currently be flashing on the world map.
	 */
	private StarLocationWorldMapPoint highlightedMapPoint = null;

	/**
	 * Whether the {@link #highlightedMapPoint} should be lit up.
	 * Alternates every game tick to create the flashing effect.
	 */
	private boolean highlighted;

	@Override
	public void startUp()
	{
		eventBus.register(worldMapStarDetailsOverlay);
		worldMapStarDetailsOverlay.loadSavedSettings();

		for (StarLocationDetails starLocationDetails : StarLocationDetails.values())
		{
			final StarLocationWorldMapPoint worldMapPoint = new StarLocationWorldMapPoint(starLocationDetails);
			worldMapPoints.put(starLocationDetails.getName(), worldMapPoint);
		}

		updateWorldMapPoints();
	}

	@Override
	public void shutDown()
	{
		unregisterWorldMapStarDetailsOverlay();
		eventBus.unregister(worldMapStarDetailsOverlay);

		worldMapPoints.clear();
		worldMapPointManager.removeIf(StarLocationWorldMapPoint.class::isInstance);
	}

	@Subscribe
	private void onAnnouncementAttributesUpdated(AnnouncementAttributesUpdated announcementAttributesUpdated)
	{
		final Map<String, Integer> highestTiers = new HashMap<>();
		final Map<String, Integer> starCounts = new HashMap<>();

		// Find the amount of stars at each location and the highest tier star at each location.
		for (StarListEntryAttributes attributes : announcementAttributesUpdated.getAnnouncementAttributes().values())
		{
			final String locationName = attributes.getStar().getLocation().getName();

			if (!worldMapPoints.containsKey(locationName) || !attributes.shouldBeVisible())
			{
				continue;
			}

			starCounts.put(locationName, starCounts.getOrDefault(locationName, 0) + 1);
			highestTiers.put(locationName, Math.max(highestTiers.getOrDefault(locationName, 0), attributes.getTier()));
		}

		// Update the images on the world map according to highestTier and starCounts map.
		for (StarLocationWorldMapPoint worldMapPoint : worldMapPoints.values())
		{
			final int starCount = starCounts.getOrDefault(worldMapPoint.getName(), 0);
			int highestTier = highestTiers.getOrDefault(worldMapPoint.getName(), 0);

			if (starCount == 0)
			{
				highestTier = 9;
			}

			worldMapPoint.updateImage(highestTier, starCount, false);
		}

		updateWorldMapPoints();
	}

	@Subscribe
	private void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (client.getWidget(InterfaceID.Worldmap.MAP_CONTAINER) == null)
		{
			worldMapStarDetailsOverlay.closeOverlay();
		}
	}

	@Subscribe
	private void onMenuOptionClicked(MenuOptionClicked menuOptionClicked)
	{
		// Check if a star was clicked on the world map.
		if (menuOptionClicked.getMenuAction() == MenuAction.RUNELITE &&
			client.getWidget(InterfaceID.Worldmap.MAP_CONTAINER) != null)
		{
			Pattern pattern = Pattern.compile("<col=[^>]+>(.*?)</col>");
			Matcher matcher = pattern.matcher(menuOptionClicked.getMenuTarget());

			if (matcher.find())
			{
				StarLocationDetails starLocation = StarLocationDetails.getByName(matcher.group(1));

				if (starLocation != null)
				{
					dispatch(new ShowStarDetailsOnWorldMapRequested(starLocation.getName()));
				}
			}
		}
	}

	@Subscribe
	private void onWidgetClosed(WidgetClosed widgetClosed)
	{
		if (widgetClosed.getGroupId() == InterfaceID.WORLDMAP)
		{
			if (highlightedMapPoint != null)
			{
				highlightedMapPoint.updateImage(highlightedMapPoint.getTier(), highlightedMapPoint.getStarCount(), false);
				highlightedMapPoint = null;
			}

			worldMapStarDetailsOverlay.closeOverlay();
		}
	}

	@Subscribe
	private void onGameTick(GameTick gameTick)
	{
		highlighted = !highlighted;

		if (highlightedMapPoint != null)
		{
			highlightedMapPoint.updateImage(highlightedMapPoint.getTier(), highlightedMapPoint.getStarCount(), highlighted);
		}
	}

	@Subscribe
	private void onSignalEvent(SignalEvent signalEvent)
	{
		if (signalEvent.getSignal() == SignalEventType.WORLDMAP_STAR_DETAILS_CLOSED)
		{
			unregisterWorldMapStarDetailsOverlay();

			if (highlightedMapPoint != null)
			{
				highlightedMapPoint.updateImage(highlightedMapPoint.getTier(), highlightedMapPoint.getStarCount(), false);
				highlightedMapPoint = null;
			}

			updateWorldMapPoints();
		}
	}

	@Subscribe
	private void onShowStarDetailsOnWorldMapRequested(ShowStarDetailsOnWorldMapRequested showStarDetailsOnWorldMapRequested)
	{
		if (config.worldMapStarDetails())
		{
			registerWorldMapStarDetailsOverlay();
			worldMapStarDetailsOverlay.showStarLocation(showStarDetailsOnWorldMapRequested.getStarLocation());
		}

		if (highlightedMapPoint != null)
		{
			highlightedMapPoint.updateImage(highlightedMapPoint.getTier(), highlightedMapPoint.getStarCount(), false);
			highlightedMapPoint = null;
		}

		final StarLocationWorldMapPoint mapPoint = worldMapPoints.get(showStarDetailsOnWorldMapRequested.getStarLocation());

		if (mapPoint != null)
		{
			highlightedMapPoint = mapPoint;
		}

		updateWorldMapPoints();
	}

	@Subscribe
	private void onPluginConfigChanged(PluginConfigChanged pluginConfigChanged)
	{
		if (pluginConfigChanged.getKey().equals("worldMapStarDetails"))
		{
			if (!config.worldMapStarDetails())
			{
				worldMapStarDetailsOverlay.closeOverlay();
			}
		}
		else if (pluginConfigChanged.getKey().equals("worldMapDisplayLevel"))
		{
			updateWorldMapPoints();
		}
	}

	/**
	 * Update the visibility of stars on the world map based on the current config settings.
	 */
	private void updateWorldMapPoints()
	{
		WorldMapDisplayLevel displayLevel = config.worldMapDisplayLevel();

		for (StarLocationWorldMapPoint mapPoint : worldMapPoints.values())
		{
			switch (displayLevel)
			{
				case ALL:
				{
					if (!mapPoint.isVisible())
					{
						mapPoint.setVisible(true);
						worldMapPointManager.add(mapPoint);
					}
					break;
				}
				case ACTIVE:
				{
					if (mapPoint.isVisible() && mapPoint.getStarCount() < 1)
					{
						mapPoint.setVisible(false);
						worldMapPointManager.remove(mapPoint);
					}
					else if (!mapPoint.isVisible() && mapPoint.getStarCount() > 0)
					{
						mapPoint.setVisible(true);
						worldMapPointManager.add(mapPoint);
					}
					break;
				}
				case NONE:
				{
					if (mapPoint.isVisible() && mapPoint != highlightedMapPoint)
					{
						mapPoint.setVisible(false);
						worldMapPointManager.remove(mapPoint);
					}
					break;
				}
			}
		}

		// Special case, the highlightedMapPoint should always be visible.
		if (highlightedMapPoint != null && !highlightedMapPoint.isVisible())
		{
			highlightedMapPoint.setVisible(true);
			worldMapPointManager.add(highlightedMapPoint);
		}
	}

	private void registerWorldMapStarDetailsOverlay()
	{
		unregisterWorldMapStarDetailsOverlay();

		eventBus.register(worldMapStarDetailsOverlayMouseAdapter);

		overlayManager.add(worldMapStarDetailsOverlay);
		mouseManager.registerMouseListener(worldMapStarDetailsOverlayMouseAdapter);
	}

	private void unregisterWorldMapStarDetailsOverlay()
	{
		eventBus.unregister(worldMapStarDetailsOverlayMouseAdapter);

		overlayManager.removeIf(WorldMapStarDetailsOverlay.class::isInstance);
		mouseManager.unregisterMouseListener(worldMapStarDetailsOverlayMouseAdapter);
	}
}
