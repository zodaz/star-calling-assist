package com.starcallingassist.modules.tracker;

import com.starcallingassist.StarModuleContract;
import com.starcallingassist.events.StarAbandoned;
import com.starcallingassist.events.StarApproached;
import com.starcallingassist.events.StarDepleted;
import com.starcallingassist.events.StarDiscovered;
import com.starcallingassist.events.StarMinersChanged;
import com.starcallingassist.events.StarTierChanged;
import com.starcallingassist.objects.Star;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;

@Slf4j
public class TrackerModule extends StarModuleContract
{
	@Inject
	protected Client client;

	protected Star activeStar = null;

	@Subscribe
	public void onStarDiscovered(StarDiscovered event)
	{
		log.debug("Star discovered: World {}, Tier {}, {} Miners, at {}", event.getStar().getWorld(), event.getStar().getTier(), event.getStar().getCurrentMiners(), event.getStar().getLocation().getLocationName());
	}

	@Subscribe
	public void onStarTierChanged(StarTierChanged event)
	{
		log.debug("Star changed: World {}, Tier {}, {} Miners, at {}", event.getStar().getWorld(), event.getStar().getTier(), event.getStar().getCurrentMiners(), event.getStar().getLocation().getLocationName());
	}

	@Subscribe
	public void onStarDepleted(StarDepleted event)
	{
		activeStar = null;
		log.debug("Star depleted: World {}, Tier {}, {} Miners, at {}", event.getStar().getWorld(), event.getStar().getTier(), event.getStar().getCurrentMiners(), event.getStar().getLocation().getLocationName());
	}

	@Subscribe
	public void onStarApproached(StarApproached event)
	{
		activeStar = event.getStar();
		log.debug("Star approached: World {}, Tier {}, {} Miners, at {}", event.getStar().getWorld(), event.getStar().getTier(), event.getStar().getCurrentMiners(), event.getStar().getLocation().getLocationName());
	}

	@Subscribe
	public void onStarAbandoned(StarAbandoned event)
	{
		event.getStar().setCurrentMiners(null);
		activeStar = null;

		log.debug("Star abandoned: World {}, Tier {}, {} Miners, at {}", event.getStar().getWorld(), event.getStar().getTier(), event.getStar().getCurrentMiners(), event.getStar().getLocation().getLocationName());
	}

	@Subscribe
	public void onStarMinersChanged(StarMinersChanged event)
	{
		log.debug("Number of miners changed: Previously {}, now {}", event.getPreviousMiners(), event.getStar().getCurrentMiners());
	}

	@Subscribe
	protected void onGameTick(GameTick event)
	{
		if (activeStar == null)
		{
			return;
		}

		Integer previousMiners = activeStar.getCurrentMiners();
		Integer currentMiners = countMiners(activeStar);

		if (! currentMiners.equals(previousMiners))
		{
			activeStar.setCurrentMiners(currentMiners);
			dispatch(new StarMinersChanged(activeStar, previousMiners));
		}
	}

	protected Integer countMiners(@Nonnull Star star)
	{
		WorldPoint starLocation = star.getLocation().getWorldPoint();

		WorldArea areaX = new WorldArea(starLocation.dx(-1), 4, 2);
		WorldArea areaY = new WorldArea(starLocation.dy(-1), 2, 4);

		int miners = 0;

		for (Player player : client.getPlayers())
		{
			if (player.getWorldLocation().isInArea2D(areaX, areaY))
			{
				miners++;
			}
		}

		return miners;
	}
}
