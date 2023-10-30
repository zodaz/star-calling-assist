package com.starcallingassist.modules.tracker;

import com.starcallingassist.StarModuleContract;
import com.starcallingassist.events.StarAbandoned;
import com.starcallingassist.events.StarDepleted;
import com.starcallingassist.events.StarDiscovered;
import com.starcallingassist.events.StarTierChanged;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.eventbus.Subscribe;

@Slf4j
public class TrackerModule extends StarModuleContract
{
	@Inject
	protected Client client;


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
		log.debug("Star depleted: World {}, Tier {}, {} Miners, at {}", event.getStar().getWorld(), event.getStar().getTier(), event.getStar().getCurrentMiners(), event.getStar().getLocation().getLocationName());
	}

	@Subscribe
	public void onStarAbandoned(StarAbandoned event)
	{
		log.debug("Star abandoned: World {}, Tier {}, {} Miners, at {}", event.getStar().getWorld(), event.getStar().getTier(), event.getStar().getCurrentMiners(), event.getStar().getLocation().getLocationName());
	}
}
