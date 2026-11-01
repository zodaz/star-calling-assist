package com.starcallingassist.modules.worldutil;

import com.google.inject.Inject;
import com.starcallingassist.PluginModuleContract;
import java.util.HashMap;
import java.util.List;
import net.runelite.client.game.WorldService;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldResult;

/**
 * Utility module providing a list of {@link World } kept up-to date.
 */
public class WorldUtilModule extends PluginModuleContract
{
	@Inject
	private WorldService worldService;

	/**
	 * Mapping of world id numbers and {@link World } objects fetched from the RuneLite http API.
	 */
	private static volatile HashMap<Integer, World> worlds = new HashMap<>();

	@Override
	public void startUp()
	{
		fetchWorldData();
	}

	@Override
	public void onSecondElapsed(int secondsSinceStartup)
	{
		// Every 10 minutes
		if (secondsSinceStartup % (60 * 10) == 0)
		{
			fetchWorldData();
		}
	}

	/**
	 * Get the RuneLite {@link World } from a world id number.
	 * @param worldId - World id number.
	 * @return A {@link World } object if a world was found with the requested world id number. Else null.
	 */
	public static World getWorldFromId(Integer worldId)
	{
		return worlds.get(worldId);
	}

	private void fetchWorldData()
	{
		WorldResult worldResult = worldService.getWorlds();

		if (worldResult == null)
		{
			return;
		}

		List<World> worldList = worldResult.getWorlds();
		if (worldList == null || worldList.isEmpty())
		{
			return;
		}

		HashMap<Integer, World> worldMap = new HashMap<>();

		for (World world : worldList)
		{
			if (world == null)
			{
				continue;
			}

			worldMap.put(world.getId(), world);
		}

		worlds = worldMap;
	}
}
