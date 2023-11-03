package com.starcallingassist.modules.worldhop;

import com.google.inject.Inject;
import com.starcallingassist.PluginModuleContract;
import com.starcallingassist.enums.ChatLogLevel;
import com.starcallingassist.events.LogMessage;
import com.starcallingassist.events.WorldHopRequest;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.World;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.util.WorldUtil;

public class WorldHopModule extends PluginModuleContract
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	private static final int DISPLAY_SWITCHER_MAX_ATTEMPTS = 3;
	private int displaySwitcherAttempts = 0;

	private Integer hopTarget;

	private Integer hopAttempts = 0;

	@Override
	public void startUp()
	{
		resetQuickHopper();
	}

	@Subscribe
	public void onWorldHopRequest(WorldHopRequest event)
	{
		if (client.getGameState() == GameState.LOGIN_SCREEN)
		{
			clientThread.invokeLater(() -> changeWorldLoginScreen(event.getWorld()));
			return;
		}

		if (client.getGameState() == GameState.LOGGED_IN)
		{
			clientThread.invokeLater(() -> changeWorldLoggedIn(event.getWorld()));
		}
	}

	private void changeWorldLoginScreen(net.runelite.http.api.worlds.World world)
	{
		final World rsWorld = client.createWorld();
		rsWorld.setActivity(world.getActivity());
		rsWorld.setAddress(world.getAddress());
		rsWorld.setId(world.getId());
		rsWorld.setPlayerCount(world.getPlayers());
		rsWorld.setLocation(world.getLocation());
		rsWorld.setTypes(WorldUtil.toWorldTypes(world.getTypes()));

		client.changeWorld(rsWorld);
	}

	private void changeWorldLoggedIn(net.runelite.http.api.worlds.World world)
	{
		displaySwitcherAttempts = 0;
		hopTarget = world.getId();
		dispatch(new LogMessage("Attempting to quick-hop to world *" + hopTarget + "*", ChatLogLevel.NORMAL));
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (hopTarget == null)
		{
			return;
		}

		if (client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST) == null)
		{
			client.openWorldHopper();

			if (++displaySwitcherAttempts >= DISPLAY_SWITCHER_MAX_ATTEMPTS)
			{
				resetQuickHopper();
				dispatch(new LogMessage("Failed to open world switcher after *" + displaySwitcherAttempts + "* attempts", ChatLogLevel.NORMAL));
			}

			return;
		}

		if (++hopAttempts >= 5)
		{
			resetQuickHopper();
			dispatch(new LogMessage("Unable to quick-hop to world *" + hopTarget + "*", ChatLogLevel.NORMAL));
			return;
		}

		World[] worldList = client.getWorldList();
		if (worldList == null)
		{
			return;
		}

		for (World world : worldList)
		{
			if (world.getId() == hopTarget)
			{
				client.hopToWorld(world);
				resetQuickHopper();
				break;
			}
		}
	}

	private void resetQuickHopper()
	{
		hopTarget = null;
		hopAttempts = 0;
	}
}
