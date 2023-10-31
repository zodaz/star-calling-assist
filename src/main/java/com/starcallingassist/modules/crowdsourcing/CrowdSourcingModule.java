package com.starcallingassist.modules.crowdsourcing;

import com.starcallingassist.PluginModuleContract;
import com.starcallingassist.StarCallingAssistConfig;
import com.starcallingassist.events.DebugLogMessage;
import com.starcallingassist.events.PluginConfigChanged;
import com.starcallingassist.events.StarAbandoned;
import com.starcallingassist.events.StarApproached;
import com.starcallingassist.events.StarCallManuallyRequested;
import com.starcallingassist.events.StarCalled;
import com.starcallingassist.events.StarDepleted;
import com.starcallingassist.events.StarDepletionManuallyRequested;
import com.starcallingassist.events.StarDiscovered;
import com.starcallingassist.events.StarTierChanged;
import com.starcallingassist.objects.Star;
import com.starcallingassist.services.CallStarPayload;
import com.starcallingassist.services.HttpService;
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
public class CrowdSourcingModule extends PluginModuleContract
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private StarCallingAssistConfig config;

	@Inject
	private HttpService httpService;

	private Star currentStar = null;

	private Star lastCalledStar = null;

	private boolean currentStarApproached = false;

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
		if (currentStar == null || !config.autoCall())
		{
			return;
		}

		if (event.getKey().equals("autoCall"))
		{
			clientThread.invokeLater(() -> attemptAutomaticUpdate(currentStar));
			return;
		}

		if (event.getKey().equals("updateStar") && config.updateStar())
		{
			clientThread.invokeLater(() -> attemptAutomaticUpdate(currentStar));
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
	}

	@Subscribe
	public void onStarApproached(StarApproached event)
	{
		currentStar = event.getStar();
		currentStarApproached = true;
	}

	@Subscribe
	public void onStarAbandoned(StarAbandoned event)
	{
		if (event.getStar().getWorld() != client.getWorld())
		{
			currentStar = null;
		}

		currentStarApproached = false;
	}

	@Subscribe
	public void onStarTierChanged(StarTierChanged event)
	{
		Star nextStar = event.getStar();
		nextStar.setCurrentMiners(currentStar == null ? null : currentStar.getCurrentMiners());
		currentStar = nextStar;

		if (config.autoCall() && config.updateStar())
		{
			attemptAutomaticUpdate(currentStar);
		}
	}

	@Subscribe
	public void onStarDepleted(StarDepleted event)
	{
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
		String playerName = config.includeIgn() ? client.getLocalPlayer().getName() : null;

		CallStarPayload payload = new CallStarPayload(playerName, star, locationName);

		try
		{
			httpService.post(payload, new Callback()
			{
				@Override
				public void onFailure(Call call, IOException e)
				{
					clientThread.invokeLater(() -> dispatch(new DebugLogMessage("Unable to post call to " + config.getEndpoint() + ".")));
					call.cancel();
				}

				@Override
				public void onResponse(Call call, Response res) throws IOException
				{
					if (!res.isSuccessful())
					{
						clientThread.invokeLater(() -> dispatch(new DebugLogMessage("Issue posting call to " + config.getEndpoint() + ": *" + res.message() + "*")));
						res.close();
						return;
					}

					lastCalledStar = star;
					clientThread.invokeLater(() -> dispatch(new DebugLogMessage("Successfully posted call: *" + payload.toCallout() + "*")));
					dispatch(new StarCalled(star, payload));
					res.close();
				}
			});
		}
		catch (IllegalArgumentException e)
		{
			clientThread.invokeLater(() -> dispatch(new DebugLogMessage("Issue posting call to " + config.getEndpoint() + ": *Invalid endpoint*")));
		}
	}
}
