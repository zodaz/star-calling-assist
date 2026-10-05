package com.starcallingassist.modules.shortestpath;

import com.google.inject.Inject;
import com.starcallingassist.PluginModuleContract;
import com.starcallingassist.StarCallingAssistConfig;
import com.starcallingassist.enums.ChatLogLevel;
import com.starcallingassist.enums.StarLocationDetails;
import com.starcallingassist.events.AnnouncementsReceived;
import com.starcallingassist.events.LogMessage;
import com.starcallingassist.events.RouteViaShortestPathRequested;
import com.starcallingassist.events.StarDepleted;
import com.starcallingassist.events.StarMissing;
import com.starcallingassist.events.StarScouted;
import com.starcallingassist.events.StarTierChanged;
import com.starcallingassist.events.WorldHopRequest;
import com.starcallingassist.modules.crowdsourcing.objects.AnnouncedStar;
import com.starcallingassist.objects.Star;
import java.util.HashMap;
import java.util.Map;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.PluginMessage;

public class ShortestPathModule extends PluginModuleContract
{
	private static final String NAMESPACE = "shortestpath";

	private static final int PENDING_ROUTE_TIMEOUT_SECONDS = 60;

	// How close the player has to be to the star for the route to be considered completed.
	private static final int ARRIVAL_DISTANCE = 2;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private StarCallingAssistConfig config;

	private volatile Star routedStar;

	private volatile Star pendingRouteStar;

	private int pendingRouteSecondsLeft = 0;

	@Override
	public synchronized void shutDown()
	{
		routedStar = null;
		pendingRouteStar = null;
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
		// Hops that aren't for a specific star (e.g. from the world map) carry no star, which drops any pending route.
		pendingRouteStar = event.getStar();
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

		Star pending = pendingRouteStar;
		if (pending != null && pending.getWorld() == client.getWorld())
		{
			pendingRouteStar = null;
			route(pending);
		}

		Star routed = routedStar;
		if (routed == null || routed.getWorld() != client.getWorld())
		{
			return;
		}

		StarLocationDetails details = routed.getLocation().getStarLocationDetails();
		if (details != null && details.getWorldArea().distanceTo(player.getWorldLocation()) <= ARRIVAL_DISTANCE)
		{
			clearRoute();
		}
	}

	@Override
	public synchronized void onSecondElapsed(int secondsSinceStartup)
	{
		if (pendingRouteStar != null && --pendingRouteSecondsLeft <= 0)
		{
			pendingRouteStar = null;
		}
	}

	@Subscribe
	public void onAnnouncementsReceived(AnnouncementsReceived event)
	{
		for (AnnouncedStar announcement : event.getAnnouncements())
		{
			Star routed = routedStar;

			if (clearRouteIfGone(announcement.getStar()) && routed != null && config.clearRouteWhenDone())
			{
				dispatch(new LogMessage(String.format(
					"The star near *%s* on world *%d* is gone, so the route to it has been cleared.",
					routed.getLocation().getName(),
					routed.getWorld()
				), ChatLogLevel.NORMAL));
			}
		}
	}

	@Subscribe
	public void onStarScouted(StarScouted event)
	{
		clearRouteIfGone(event.getStar());
	}

	@Subscribe
	public void onStarTierChanged(StarTierChanged event)
	{
		clearRouteIfGone(event.getStar());
	}

	@Subscribe
	public void onStarDepleted(StarDepleted event)
	{
		clearRouteIfGone(event.getStar());
	}

	@Subscribe
	public void onStarMissing(StarMissing event)
	{
		clearRouteIfGone(event.getStar());
	}

	/**
	 * Clears the route if the given update means that the star we're routing to is no longer there.
	 *
	 * @return whether the route was cleared.
	 */
	private boolean clearRouteIfGone(Star star)
	{
		Star routed = routedStar;
		if (routed == null || !routed.getWorld().equals(star.getWorld()))
		{
			return false;
		}

		boolean isGone = star.getTier() == null || star.getTier() < 1;
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
		clientThread.invokeLater(() -> dispatch(new PluginMessage(NAMESPACE, "path", data)));

		routedStar = star;
	}

	private void clearRoute()
	{
		routedStar = null;

		if (config.clearRouteWhenDone())
		{
			clientThread.invokeLater(() -> dispatch(new PluginMessage(NAMESPACE, "clear")));
		}
	}
}
