package com.starcallingassist.modules.crowdsourcing;

import com.google.inject.Inject;
import com.starcallingassist.PluginModuleContract;
import com.starcallingassist.StarCallingAssistConfig;
import com.starcallingassist.enums.ChatLogLevel;
import com.starcallingassist.events.BroadcastSuccessful;
import com.starcallingassist.events.LogMessage;
import com.starcallingassist.events.ManualStarAbsenceBroadcastRequested;
import com.starcallingassist.events.ManualStarPresenceBroadcastRequested;
import com.starcallingassist.events.PluginConfigChanged;
import com.starcallingassist.events.StarAbandoned;
import com.starcallingassist.events.StarApproached;
import com.starcallingassist.events.StarDepleted;
import com.starcallingassist.events.StarMissing;
import com.starcallingassist.events.StarScouted;
import com.starcallingassist.events.StarTierChanged;
import com.starcallingassist.modules.crowdsourcing.objects.CallStarPayload;
import com.starcallingassist.objects.Star;
import com.starcallingassist.objects.StarLocation;
import com.starcallingassist.services.HttpService;
import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nonnull;
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

public class BroadcastModule extends PluginModuleContract
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
	public void onStarScouted(StarScouted event)
	{
		currentStar = event.getStar();
		currentStarApproached = false;

		if (config.autoCall())
		{
			attemptAutomaticUpdate(currentStar);
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
	public void onStarMissing(StarMissing event)
	{
		if (config.autoCall())
		{
			attemptBroadcast(Star.fromExistingWithTierChange(event.getStar(), null));
		}
	}

	@Subscribe
	public void onManualStarPresenceBroadcastRequested(ManualStarPresenceBroadcastRequested event)
	{
		if (currentStar == null)
		{
			dispatch(new LogMessage("Unable to find star.", ChatLogLevel.NORMAL));
			return;
		}

		if (lastCalledStar != null && lastCalledStar.isSameAs(currentStar) && Objects.equals(lastCalledStar.getTier(), currentStar.getTier()))
		{
			dispatch(new LogMessage("This star has already been called.", ChatLogLevel.NORMAL));
			return;
		}

		attemptBroadcast(
			currentStar,
			currentStar.getTier() == null ? "dead" : currentStar.getLocation().getName()
		);
	}

	@Subscribe
	public void onManualStarAbsenceBroadcastRequested(ManualStarAbsenceBroadcastRequested event)
	{
		attemptBroadcast(new CallStarPayload(client.getLocalPlayer().getName(),
			client.getWorld(),
			0,
			(event.getIsPublicCall() ? "dead" : "pdead"),
			-1));
	}

	@Subscribe
	protected void onGameTick(GameTick event)
	{
		if (currentStar != null && currentStarApproached)
		{
			currentStar.setCurrentMiners(countMiners(currentStar));
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

	private void attemptAutomaticUpdate(@Nonnull Star star)
	{
		if (lastCalledStar != null && lastCalledStar.isSameAs(star) && Objects.equals(lastCalledStar.getTier(), star.getTier()))
		{
			return;
		}

		attemptBroadcast(star);
	}

	private void attemptBroadcast(@Nonnull Star star)
	{
		attemptBroadcast(star, star.getLocation().getName());
	}

	private void attemptBroadcast(@Nonnull Star star, String locationName)
	{
		attemptBroadcast(new CallStarPayload(client.getLocalPlayer().getName(), star, locationName));
	}

	private void attemptBroadcast(@Nonnull CallStarPayload payload)
	{
		try
		{
			httpService.post(payload, new Callback()
			{
				@Override
				public void onFailure(Call call, IOException e)
				{
					clientThread.invokeLater(() -> dispatch(new LogMessage("Unable to post call to " + config.getEndpoint() + ".", ChatLogLevel.CALLS)));
					call.cancel();
				}

				@Override
				public void onResponse(Call call, Response res) throws IOException
				{
					if (!res.isSuccessful())
					{
						clientThread.invokeLater(() -> dispatch(new LogMessage("Issue posting call to " + config.getEndpoint() + ": *" + res.message() + "*", ChatLogLevel.CALLS)));
						res.close();
						return;
					}

					lastCalledStar = payload.toStar();
					dispatch(new BroadcastSuccessful(lastCalledStar, payload));
					clientThread.invokeLater(() -> dispatch(new LogMessage("Star successfully called: *" + payload.toCallout() + "*", ChatLogLevel.CALLS)));
					res.close();
				}
			});
		}
		catch (IllegalArgumentException e)
		{
			clientThread.invokeLater(() -> dispatch(new LogMessage("Issue posting call to " + config.getEndpoint() + ": *Invalid endpoint*", ChatLogLevel.CALLS)));
		}
	}
}
