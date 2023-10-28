package com.starcallingassist.modules.tracker;

import com.starcallingassist.StarModuleContract;
import com.starcallingassist.events.ChatConsoleMessage;
import com.starcallingassist.events.ChatDebugMessage;
import com.starcallingassist.events.ManualStarDepletedCallRequested;
import com.starcallingassist.events.ManualStarDroppedCallRequested;
import com.starcallingassist.events.StarCallingAssistConfigChanged;
import com.starcallingassist.old.objects.CallSender;
import com.starcallingassist.old.objects.Star;
import java.io.IOException;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

@Slf4j
public class TrackerModule extends StarModuleContract
{
	private static final int PLAYER_RENDER_DISTANCE = 13;
	private WorldPoint confirmDeadLocation = null;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private CallSender sender;

	private int miners = 0;

	private Star lastCalledStar;

	@Override
	public void startUp()
	{
		lastCalledStar = null;
	}

	@Override
	public void shutDown()
	{
		Star.removeStar();
		lastCalledStar = null;
	}

	@Subscribe
	public void onStarCallingAssistConfigChanged(StarCallingAssistConfigChanged event)
	{
		if (event.getKey().equals("autoCall"))
		{
			if (config.autoCall())
			{
				clientThread.invokeLater(() -> prepareCall(false));
			}

			return;
		}

		if (event.getKey().equals("updateStar"))
		{
			if (config.autoCall() && config.updateStar())
			{
				clientThread.invokeLater(() -> prepareCall(false));
			}
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged state)
	{
		if (state.getGameState() == GameState.HOPPING || state.getGameState() == GameState.LOGGING_IN)
		{
			Star.removeStar();
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		Star star = Star.getStar();
		if (confirmDeadLocation != null)
		{
			if (star == null)
			{
				if (client.getLocalPlayer().getWorldLocation().distanceTo(confirmDeadLocation) <= 32)
				{
					attemptCall(client.getLocalPlayer().getName(), client.getWorld(), 0, Star.getLocationName(confirmDeadLocation));
				}
			}

			confirmDeadLocation = null;
		}

		if (star != null)
		{
			if (client.getLocalPlayer().getWorldLocation().distanceTo(star.location) > 32)
			{
				Star.removeStar();
			}

			if (star != null)
			{
				countMiners();
			}
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		int tier = Star.getTier(event.getGameObject().getId());
		if (tier == -1)
		{
			return;
		}

		Star.setStar(event.getGameObject(), tier, client.getWorld());

		if (config.autoCall())
		{
			if (withinPlayerDistance())
			{
				countMiners();
				prepareCall(false);
			}
			else
			{
				miners = -1;
				prepareCall(false);
			}
		}
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		if (Star.getTier(event.getGameObject().getId()) == -1)
		{
			return;
		}

		//Causes a check for whether the star fully depleted in the next GameTick event
		if (config.autoCall())
		{
			confirmDeadLocation = event.getGameObject().getWorldLocation();
		}

		Star.removeStar();
	}

	@Subscribe
	public void onManualStarDroppedCallRequested(ManualStarDroppedCallRequested event)
	{
		log.debug("Manual star dropped call requested");
	}

	@Subscribe
	public void onManualStarDepletedCallRequested(ManualStarDepletedCallRequested event)
	{
		if (event.getIsPublicCall())
		{
			log.debug("Manual (public) star depletion call requested");
			// attemptCall(client.getLocalPlayer().getName(), client.getWorld(), 0, "dead");
		}
		else
		{
			log.debug("Manual (private) star depletion call requested");
			// attemptCall(client.getLocalPlayer().getName(), client.getWorld(), 0, "pdead");
		}
	}

	private boolean withinPlayerDistance()
	{
		return client.getLocalPlayer().getWorldLocation().distanceTo(new WorldArea(Star.getStar().location, 2, 2)) <= PLAYER_RENDER_DISTANCE;
	}

	//Credit to https://github.com/pwatts6060/star-info/. Simplified to fit our needs.
	private void countMiners()
	{
		miners = 0;
		Star star = Star.getStar();

		if (!withinPlayerDistance())
		{
			miners = -1;
			return;
		}

		WorldArea areaH = new WorldArea(star.location.dx(-1), 4, 2);
		WorldArea areaV = new WorldArea(star.location.dy(-1), 2, 4);

		for (Player p : client.getPlayers())
		{
			if (!p.getWorldLocation().isInArea2D(areaH, areaV))
			{
				continue;
			}

			miners++;
		}
	}

	public void prepareCall(boolean manual)
	{
		if (Star.getStar() == null)
		{
			if (manual)
			{
				dispatch(new ChatDebugMessage("Unable to find star."));
			}

			return;
		}

		if (lastCalledStar != null
			&& lastCalledStar.world == Star.getStar().world
			&& lastCalledStar.tier == Star.getStar().tier
			&& lastCalledStar.location.equals(Star.getStar().location)
		)
		{
			if (manual)
			{
				dispatch(new ChatDebugMessage("This star has already been called."));
			}

			return;
		}

		// Won't automatically call star again if tier decreased and the updateStar option disabled
		if (lastCalledStar != null
			&& lastCalledStar.world == Star.getStar().world
			&& lastCalledStar.location.equals(Star.getStar().location)
			&& lastCalledStar.tier > Star.getStar().tier
			&& !config.updateStar() && !manual
		)
		{
			return;
		}

		String location = Star.getLocationName(Star.getStar().location);
		if (location.equals("unknown"))
		{
			dispatch(new ChatDebugMessage("Star location is unknown, manual call required."));
			return;
		}

		attemptCall(client.getLocalPlayer().getName(), client.getWorld(), Star.getStar().tier, location);
	}

	public void attemptCall(String username, int world, int tier, String location)
	{
		try
		{
			sender.sendCall(username, world, tier, location, miners, new Callback()
			{
				@Override
				public void onFailure(Call call, IOException e)
				{
					clientThread.invokeLater(() -> {
						dispatch(new ChatDebugMessage("Unable to post call to " + config.getEndpoint() + "."));
					});

					call.cancel();
				}

				@Override
				public void onResponse(Call call, Response res) throws IOException
				{
					if (res.isSuccessful())
					{
						if (tier > 0)
						{
							lastCalledStar = Star.getStar();
						}

						clientThread.invokeLater(() -> {
							String callout = "W" + world;
							callout += " T" + tier;
							callout += " " + location;

							if (miners != -1 && tier != 0)
							{
								callout += " " + miners + " Miners";
							}

							dispatch(new ChatConsoleMessage("Successfully posted call: *" + callout + "*"));
						});
					}
					else
					{
						clientThread.invokeLater(() -> dispatch(new ChatConsoleMessage("Issue posting call to " + config.getEndpoint() + ": *" + res.message() + "*")));
					}

					res.close();
				}
			});
		}
		catch (IllegalArgumentException e)
		{
			clientThread.invokeLater(() -> dispatch(new ChatConsoleMessage("Issue posting call to " + config.getEndpoint() + ": *Invalid endpoint*")));
		}
	}
}
