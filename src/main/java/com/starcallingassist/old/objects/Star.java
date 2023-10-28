package com.starcallingassist.old.objects;

import com.starcallingassist.support.StarLocation;
import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;

public class Star
{
	private static Star currentStar = null;
	private static final int[] TIER_IDS = new int[]{41229, 41228, 41227, 41226, 41225, 41224, 41223, 41021, 41020};

	public final GameObject starObject;
	public final int tier;
	public final WorldPoint location;
	public final int world;

	Star(GameObject starObject, int tier, WorldPoint location, int world)
	{
		this.starObject = starObject;
		this.tier = tier;
		this.location = location;
		this.world = world;
	}

	public static Star getStar()
	{
		return currentStar;
	}

	public static void removeStar()
	{
		currentStar = null;
	}

	public static void setStar(GameObject star, int tier, int world)
	{
		currentStar = new Star(star, tier, star.getWorldLocation(), world);
	}

	public static int getTier(int id)
	{
		for (int i = 0; i < TIER_IDS.length; i++)
		{
			if (id == TIER_IDS[i])
			{
				return i + 1;
			}
		}

		return -1;
	}

	public static String getLocationName(WorldPoint location)
	{
		String starLocation = new StarLocation(location).getLocationName();
		if (starLocation == null)
		{
			return "unknown";
		}

		return starLocation;
	}
}
