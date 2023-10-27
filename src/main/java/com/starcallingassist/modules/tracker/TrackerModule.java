package com.starcallingassist.modules.tracker;

import com.starcallingassist.StarModuleContract;
import com.starcallingassist.old.objects.Star;
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
import net.runelite.client.events.ConfigChanged;

@Slf4j
public class TrackerModule extends StarModuleContract
{
	private static final int PLAYER_RENDER_DISTANCE = 13;
	private WorldPoint confirmDeadLocation = null;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Override
	public void shutDown()
	{
		Star.removeStar();
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("starcallingassistplugin"))
		{
			return;
		}

		if (event.getKey().equals("autoCall"))
		{
			if (config.autoCall())
			{
				clientThread.invokeLater(() -> plugin.prepareCall(false));
			}

			return;
		}

		if (event.getKey().equals("updateStar"))
		{
			if (config.autoCall() && config.updateStar())
			{
				clientThread.invokeLater(() -> plugin.prepareCall(false));
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
		if (confirmDeadLocation != null)
		{
			if (Star.getStar() == null)
			{
				if (client.getLocalPlayer().getWorldLocation().distanceTo(confirmDeadLocation) <= 32)
				{
					plugin.attemptCall(client.getLocalPlayer().getName(), client.getWorld(), 0, Star.getLocationName(confirmDeadLocation));
				}
			}

			confirmDeadLocation = null;
		}

		if (Star.getStar() != null)
		{
			if (client.getLocalPlayer().getWorldLocation().distanceTo(Star.getStar().location) > 32)
			{
				Star.removeStar();
			}

			if (Star.getStar() != null)
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
				plugin.prepareCall(false);
			}
			else
			{
				plugin.setMiners(-1);
				plugin.prepareCall(false);
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

	private boolean withinPlayerDistance()
	{
		return client.getLocalPlayer().getWorldLocation().distanceTo(new WorldArea(Star.getStar().location, 2, 2)) <= PLAYER_RENDER_DISTANCE;
	}

	//Credit to https://github.com/pwatts6060/star-info/. Simplified to fit our needs.
	private void countMiners()
	{
		int miners = 0;
		Star star = Star.getStar();

		if (!withinPlayerDistance())
		{
			plugin.setMiners(-1);
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

		plugin.setMiners(miners);
	}
}
