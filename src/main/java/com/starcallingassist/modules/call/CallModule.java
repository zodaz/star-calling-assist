package com.starcallingassist.modules.call;

import com.starcallingassist.StarModuleContract;
import com.starcallingassist.events.DebugLogMessage;
import com.starcallingassist.events.InfoLogMessage;
import com.starcallingassist.events.PluginConfigChanged;
import com.starcallingassist.events.StarAbandoned;
import com.starcallingassist.events.StarApproached;
import com.starcallingassist.events.StarCallManuallyRequested;
import com.starcallingassist.events.StarDepleted;
import com.starcallingassist.events.StarDepletionManuallyRequested;
import com.starcallingassist.events.StarDiscovered;
import com.starcallingassist.events.StarTierChanged;
import com.starcallingassist.objects.Star;
import com.starcallingassist.old.objects.CallSender;
import java.io.IOException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import javax.annotation.Nonnull;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@Slf4j
public class CallModule extends StarModuleContract
{
	@Inject
	protected Client client;

	@Inject
	protected ClientThread clientThread;

	@Inject
	private CallSender sender;

	protected Star currentStar = null;

	protected Star lastCalledStar = null;

	protected boolean currentStarApproached = false;

	@Override
	public void shutDown()
	{
		currentStar = null;
		lastCalledStar = null;
		currentStarApproached = false;
	}

	@Subscribe
	public void onPluginConfigChanged(PluginConfigChanged event)
	{
		if (event.getKey().equals("autoCall"))
		{
			if (config.autoCall() && currentStar != null)
			{
				clientThread.invokeLater(() -> attemptAutomaticUpdate(currentStar));
			}

			return;
		}

		if (event.getKey().equals("updateStar"))
		{
			if (config.autoCall() && config.updateStar() && currentStar != null)
			{
				clientThread.invokeLater(() -> attemptAutomaticUpdate(currentStar));
			}
		}
	}

	@Subscribe
	public void onStarDiscovered(StarDiscovered event)
	{
		currentStar = event.getStar();
		currentStarApproached = false;

		if (config.autoCall())
		{
			// We'll attempt to call the star with a slight delay, so that the player has an opportunity
			// to get close enough for the "miner count" logic to run. This allows us to communicate
			// the number of players that are mining the star, without a secondary update call.
			attemptAutomaticUpdateWithDelay(currentStar);
		}

		log.debug("Star discovered: World {}, Tier {}, {} Miners, at {}", event.getStar().getWorld(), event.getStar().getTier(), event.getStar().getCurrentMiners(), event.getStar().getLocation().getLocationName());
	}

	@Subscribe
	public void onStarApproached(StarApproached event)
	{
		currentStar = event.getStar();
		currentStarApproached = true;

		log.debug("Star approached: World {}, Tier {}, {} Miners, at {}", event.getStar().getWorld(), event.getStar().getTier(), event.getStar().getCurrentMiners(), event.getStar().getLocation().getLocationName());
	}

	@Subscribe
	public void onStarAbandoned(StarAbandoned event)
	{
		if (event.getStar().getWorld() != client.getWorld())
		{
			log.debug("Resetting current star - hopped world while star not depleted.");
			currentStar = null;
		}

		currentStarApproached = false;

		log.debug("Star abandoned: World {}, Tier {}, {} Miners, at {}", event.getStar().getWorld(), event.getStar().getTier(), event.getStar().getCurrentMiners(), event.getStar().getLocation().getLocationName());
	}

	@Subscribe
	public void onStarTierChanged(StarTierChanged event)
	{
		Star nextStar = event.getStar();
		nextStar.setCurrentMiners(currentStar == null ? null : currentStar.getCurrentMiners());
		currentStar = nextStar;

		log.debug("Star changed: World {}, Tier {}, {} Miners, at {}", event.getStar().getWorld(), event.getStar().getTier(), event.getStar().getCurrentMiners(), event.getStar().getLocation().getLocationName());

		if (config.autoCall() && config.updateStar())
		{
			attemptAutomaticUpdate(currentStar);
		}
	}

	@Subscribe
	public void onStarDepleted(StarDepleted event)
	{
		log.debug("Star depleted: World {}, Tier {}, {} Miners, at {}", event.getStar().getWorld(), event.getStar().getTier(), event.getStar().getCurrentMiners(), event.getStar().getLocation().getLocationName());

		if (config.autoCall())
		{
			attemptAutomaticUpdate(event.getStar());
		}

		currentStar = null;
		currentStarApproached = false;
	}

	@Subscribe
	public void onStarCallManuallyRequested(StarCallManuallyRequested event)
	{
		if (currentStar == null)
		{
			dispatch(new DebugLogMessage("Unable to find star."));
			return;
		}

		if (lastCalledStar != null && lastCalledStar.isSameAs(currentStar) && Objects.equals(lastCalledStar.getTier(), currentStar.getTier()))
		{
			dispatch(new DebugLogMessage("This star has already been called."));
			return;
		}

		attemptCall(
			currentStar,
			currentStar.getTier() == null ? "dead" : currentStar.getLocation().getLocationName()
		);
	}

	@Subscribe
	public void onStarDepletionManuallyRequested(StarDepletionManuallyRequested event)
	{
		if (currentStar == null)
		{
			return;
		}

		attemptCall(
			new Star(currentStar.getWorld(), currentStar.getLocation().getWorldPoint()),
			event.getIsPublicCall() ? "dead" : "pdead"
		);
	}

	@Subscribe
	protected void onGameTick(GameTick event)
	{
		if (currentStar == null || !currentStarApproached)
		{
			return;
		}

		Integer previousMiners = currentStar.getCurrentMiners();
		Integer currentMiners = countMiners(currentStar);

		if (!currentMiners.equals(previousMiners))
		{
			currentStar.setCurrentMiners(currentMiners);
		}
	}

	// Credit to https://github.com/pwatts6060/star-info/. Simplified to fit our needs.
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

	private void attemptAutomaticUpdateWithDelay(@Nonnull Star star)
	{
		log.debug("DELAYED PREPARE CALL");

		new Timer().schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				attemptAutomaticUpdate(star);
			}
		}, 10000);
	}

	private void attemptAutomaticUpdate(@Nonnull Star star)
	{
		if (lastCalledStar != null && lastCalledStar.isSameAs(star) && Objects.equals(lastCalledStar.getTier(), star.getTier()))
		{
			return;
		}

		attemptCall(star, star.getLocation().getLocationName());
	}

	private void attemptCall(@Nonnull Star star, String locationName)
	{
		Player player = client.getLocalPlayer();
		String playerName = config.includeIgn() && player != null ? player.getName() : "";

		int tier = star.getTier() == null ? 0 : star.getTier();
		int miners = star.getCurrentMiners() == null ? -1 : star.getCurrentMiners();

		log.debug("ATTEMPT CALL; Player {} World {} Tier {}, {} Miners, Location {}", playerName, star.getWorld(), tier, miners, locationName);

//		try
//		{
//			sender.sendCall(playerName, star.getWorld(), tier, locationName, miners, new Callback()
//			{
//				@Override
//				public void onFailure(Call call, IOException e)
//				{
//					clientThread.invokeLater(() -> {
//						dispatch(new DebugLogMessage("Unable to post call to " + config.getEndpoint() + "."));
//					});
//
//					call.cancel();
//				}
//
//				@Override
//				public void onResponse(Call call, Response res) throws IOException
//				{
//					if (res.isSuccessful())
//					{
//						lastCalledStar = star;
//
//						clientThread.invokeLater(() -> {
//							String callout = "W" + star.getWorld();
//							callout += " T" + tier;
//							callout += " " + locationName;
//
//							if (miners != -1 && tier != 0)
//							{
//								callout += " " + miners + " Miners";
//							}
//
//							dispatch(new InfoLogMessage("Successfully posted call: *" + callout + "*"));
//						});
//					}
//					else
//					{
//						clientThread.invokeLater(() -> dispatch(new InfoLogMessage("Issue posting call to " + config.getEndpoint() + ": *" + res.message() + "*")));
//					}
//
//					res.close();
//				}
//			});
//		}
//		catch (IllegalArgumentException e)
//		{
//			clientThread.invokeLater(() -> dispatch(new InfoLogMessage("Issue posting call to " + config.getEndpoint() + ": *Invalid endpoint*")));
//		}
	}
}
