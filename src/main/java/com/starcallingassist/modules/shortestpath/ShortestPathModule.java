package com.starcallingassist.modules.shortestpath;

import com.google.inject.Inject;
import com.starcallingassist.PluginModuleContract;
import com.starcallingassist.StarCallingAssistConfig;
import com.starcallingassist.enums.ChatLogLevel;
import com.starcallingassist.events.AnnouncementReceived;
import com.starcallingassist.events.LogMessage;
import com.starcallingassist.events.NavButtonClicked;
import com.starcallingassist.events.PluginConfigChanged;
import com.starcallingassist.events.RouteViaShortestPathRequested;
import com.starcallingassist.events.StarDepleted;
import com.starcallingassist.events.StarMissing;
import com.starcallingassist.events.StarScouted;
import com.starcallingassist.events.StarTierChanged;
import com.starcallingassist.events.TravelDistancesUpdated;
import com.starcallingassist.events.WorldHopRequest;
import com.starcallingassist.objects.Star;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.PluginMessage;

public class ShortestPathModule extends PluginModuleContract
{
	private static final String NAMESPACE = "shortestpath";
	private static final String QUERY_CANCELLED = "CANCELLED";

	private static final int PENDING_ROUTE_TIMEOUT_SECONDS = 60;
	private static final int QUERY_TIMEOUT_SECONDS = 30;

	// How close the player has to be to the star for the route to be considered completed.
	private static final int ARRIVAL_DISTANCE = 2;

	// How far the player has to move before the travel distances are calculated again.
	private static final int DISTANCE_REFRESH_THRESHOLD = 64;

	// How far the end of a path may be from the star, for it to still count as a path to the star.
	private static final int UNREACHED_TOLERANCE = 10;

	@Inject
	private Client client;

	@Inject
	private StarCallingAssistConfig config;

	private final Map<Integer, Star> stars = new ConcurrentHashMap<>();

	private volatile Star routedStar;

	private volatile Integer pendingRouteWorld;

	private int pendingRouteSecondsLeft = 0;

	private volatile boolean sidePanelShowing = false;

	private final Map<WorldPoint, Integer> distances = new HashMap<>();

	private final Set<WorldPoint> queriedLocations = new HashSet<>();

	private final Deque<WorldPoint> queryQueue = new ArrayDeque<>();

	private WorldPoint distancesOrigin;

	private String queryId;

	private WorldPoint queryTarget;

	private int querySecondsLeft = 0;

	private int queryCounter = 0;

	private boolean queriesUnanswered = false;

	@Override
	public synchronized void shutDown()
	{
		stars.clear();
		routedStar = null;
		pendingRouteWorld = null;
		sidePanelShowing = false;
		resetDistances();
	}

	@Subscribe
	public void onRouteViaShortestPathRequested(RouteViaShortestPathRequested event)
	{
		route(event.getStar());
	}

	@Subscribe
	public synchronized void onWorldHopRequest(WorldHopRequest event)
	{
		if (!config.routeOnHop())
		{
			return;
		}

		// The route can only be set once we're logged in to the star's world, so it is picked up on a later game tick.
		pendingRouteWorld = event.getWorld().getId();
		pendingRouteSecondsLeft = PENDING_ROUTE_TIMEOUT_SECONDS;
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return;
		}

		WorldPoint playerLocation = player.getWorldLocation();

		Integer pendingWorld = pendingRouteWorld;
		if (pendingWorld != null && pendingWorld == client.getWorld())
		{
			pendingRouteWorld = null;

			Star star = stars.get(pendingWorld);
			if (star != null)
			{
				route(star);
			}
		}

		Star routed = routedStar;
		if (routed != null
			&& routed.getWorld() == client.getWorld()
			&& routed.getLocation().getWorldArea().distanceTo(playerLocation) <= ARRIVAL_DISTANCE)
		{
			clearRoute();
		}

		refreshDistances(playerLocation);
	}

	@Override
	public synchronized void onSecondElapsed(int secondsSinceStartup)
	{
		if (pendingRouteWorld != null && --pendingRouteSecondsLeft <= 0)
		{
			pendingRouteWorld = null;
		}

		// Versions of the shortest path plugin that don't support queries will never answer them,
		// in which case we won't ask again until the travel distances are due to be refreshed.
		if (queryId != null && --querySecondsLeft <= 0)
		{
			WorldPoint origin = distancesOrigin;
			resetDistances();
			distancesOrigin = origin;
			queriesUnanswered = true;
		}
	}

	@Subscribe
	public synchronized void onNavButtonClicked(NavButtonClicked event)
	{
		sidePanelShowing = event.isVisible();

		if (sidePanelShowing)
		{
			distancesOrigin = null;
		}
	}

	@Subscribe
	public synchronized void onPluginConfigChanged(PluginConfigChanged event)
	{
		if (event.getKey().equals("showTravelDistance"))
		{
			distancesOrigin = null;
		}
	}

	@Subscribe
	public void onAnnouncementReceived(AnnouncementReceived event)
	{
		Star star = event.getAnnouncement().getStar();
		Star routed = routedStar;

		if (trackStar(star) && routed != null && config.clearRouteWhenDone())
		{
			dispatch(new LogMessage(String.format(
				"The star near *%s* on world *%d* is gone, so the route to it has been cleared.",
				routed.getLocation().getName(),
				routed.getWorld()
			), ChatLogLevel.NORMAL));
		}
	}

	@Subscribe
	public void onStarScouted(StarScouted event)
	{
		trackStar(event.getStar());
	}

	@Subscribe
	public void onStarTierChanged(StarTierChanged event)
	{
		trackStar(event.getStar());
	}

	@Subscribe
	public void onStarDepleted(StarDepleted event)
	{
		trackStar(event.getStar());
	}

	@Subscribe
	public void onStarMissing(StarMissing event)
	{
		trackStar(event.getStar());
	}

	@Subscribe
	public synchronized void onPluginMessage(PluginMessage event)
	{
		if (!NAMESPACE.equals(event.getNamespace()) || !"result".equals(event.getName()))
		{
			return;
		}

		Map<String, Object> data = event.getData();
		if (data == null || queryId == null || !queryId.equals(data.get("id")))
		{
			return;
		}

		WorldPoint target = queryTarget;
		queryId = null;
		queryTarget = null;

		if (QUERY_CANCELLED.equals(data.get("reason")))
		{
			// Showing a path takes priority over answering queries, so we'll simply ask again later.
			queryQueue.add(target);
		}
		else
		{
			Integer distance = parseDistance(data, target);
			Integer previous = distance == null ? distances.remove(target) : distances.put(target, distance);

			if (config.showTravelDistance() && (distance == null ? previous != null : !distance.equals(previous)))
			{
				dispatch(new TravelDistancesUpdated(new HashMap<>(distances)));
			}
		}

		sendNextQuery();
	}

	/**
	 * Keeps track of the given star, and clears the route to it if it turns out to be gone.
	 *
	 * @return whether the route was cleared.
	 */
	private boolean trackStar(Star star)
	{
		boolean isGone = star.getTier() == null || star.getTier() < 1;
		if (isGone)
		{
			stars.remove(star.getWorld());
		}
		else
		{
			stars.put(star.getWorld(), star);
		}

		Star routed = routedStar;
		if (routed == null || !routed.getWorld().equals(star.getWorld()))
		{
			return false;
		}

		if (!isGone && routed.isSameAs(star))
		{
			return false;
		}

		clearRoute();
		return true;
	}

	private void route(Star star)
	{
		WorldPoint target = star.getLocation().getWorldPoint();
		if (target == null)
		{
			return;
		}

		Map<String, Object> data = new HashMap<>();
		data.put("target", target);
		dispatch(new PluginMessage(NAMESPACE, "path", data));

		routedStar = star;
	}

	private void clearRoute()
	{
		routedStar = null;

		if (config.clearRouteWhenDone())
		{
			dispatch(new PluginMessage(NAMESPACE, "clear"));
		}
	}

	private synchronized void refreshDistances(WorldPoint playerLocation)
	{
		if (!sidePanelShowing || !config.showTravelDistance())
		{
			return;
		}

		if (distancesOrigin == null || distancesOrigin.distanceTo2D(playerLocation) >= DISTANCE_REFRESH_THRESHOLD)
		{
			// We'll hold off until the previous round is done, so that we're not flooding the pathfinder while travelling.
			if (queryId != null || !queryQueue.isEmpty())
			{
				return;
			}

			distancesOrigin = playerLocation;
			queriedLocations.clear();
			queriesUnanswered = false;
		}

		if (queriesUnanswered)
		{
			return;
		}

		for (Star star : stars.values())
		{
			WorldPoint location = star.getLocation().getWorldPoint();
			if (location != null && queriedLocations.add(location))
			{
				queryQueue.add(location);
			}
		}

		sendNextQuery();
	}

	private void sendNextQuery()
	{
		if (queryId != null || queryQueue.isEmpty())
		{
			return;
		}

		queryTarget = queryQueue.poll();
		queryId = "starminers-" + (++queryCounter);
		querySecondsLeft = QUERY_TIMEOUT_SECONDS;

		Map<String, Object> data = new HashMap<>();
		data.put("id", queryId);
		data.put("start", distancesOrigin);
		data.put("target", queryTarget);
		dispatch(new PluginMessage(NAMESPACE, "query", data));
	}

	private Integer parseDistance(Map<String, Object> data, WorldPoint target)
	{
		if (!Boolean.TRUE.equals(data.get("reached")))
		{
			Object closest = data.get("closest");
			if (!(closest instanceof WorldPoint) || ((WorldPoint) closest).distanceTo2D(target) > UNREACHED_TOLERANCE)
			{
				return null;
			}
		}

		Object cost = data.get("cost");
		if (cost instanceof Integer && (Integer) cost >= 0)
		{
			return (Integer) cost;
		}

		Object path = data.get("path");
		if (path instanceof List && !((List<?>) path).isEmpty())
		{
			return ((List<?>) path).size();
		}

		return null;
	}

	private void resetDistances()
	{
		queryId = null;
		queryTarget = null;
		queryQueue.clear();
		queriedLocations.clear();
		distancesOrigin = null;

		if (!distances.isEmpty())
		{
			distances.clear();
			dispatch(new TravelDistancesUpdated(new HashMap<>()));
		}
	}
}
