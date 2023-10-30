package com.starcallingassist.modules.worldhop;

import com.starcallingassist.StarModuleContract;
import com.starcallingassist.events.InfoLogMessage;
import com.starcallingassist.events.WorldHopRequest;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.World;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;

public class WorldHopModule extends StarModuleContract
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	private Integer hopTarget;

	private Integer hopAttempts = 0;

	@Override
	public void startUp()
	{
		resetHopState();
	}

	@Subscribe
	public void onWorldHopRequest(WorldHopRequest event)
	{
		if (client.getGameState() == GameState.LOGGED_IN)
		{
			hopTarget = event.getWorld();
			clientThread.invokeLater(() -> dispatch(new InfoLogMessage("Attempting to quick-hop to world *" + hopTarget + "*")));
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (hopTarget != null)
		{
			attemptQuickHop();
		}
	}

	private void attemptQuickHop()
	{
		if (++hopAttempts >= 5)
		{
			resetHopState();
			dispatch(new InfoLogMessage("Unable to quick-hop to world *" + hopTarget + "*"));
			return;
		}


		if (client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST) == null)
		{
			client.openWorldHopper();
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
				break;
			}
		}

		resetHopState();
	}

	private void resetHopState()
	{
		hopTarget = null;
		hopAttempts = 0;
	}
}
