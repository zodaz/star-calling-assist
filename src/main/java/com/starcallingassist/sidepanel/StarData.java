package com.starcallingassist.sidepanel;

import com.starcallingassist.sidepanel.constants.TotalLevelType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldType;

import java.util.EnumSet;

@Slf4j
public class StarData
{
    private int tier;
    @Getter
    private int worldId;
    @Getter
    private World world;
    @Getter
    private int region;
    @Getter
    private long updatedAt;
    @Getter
    private String location;
    @Getter
    private String foundBy;

    // Estimated time until dead for each tier in ms. Tier 0 at index 0, tier 1 at index 1 etc
    private final int[] timeUntilDead = {
	0,
	1 * 7 * 60 * 1000,   // 7 minutes
	2 * 7 * 60 * 1000,   // 14 minutes
	3 * 7 * 60 * 1000,   // 21 minutes
	4 * 7 * 60 * 1000,   // 28 minutes
	5 * 7 * 60 * 1000,   // 35 minutes
	6 * 7 * 60 * 1000,   // 42 minutes
	7 * 7 * 60 * 1000,   // 49 minutes
	8 * 7 * 60 * 1000,   // 56 minutes
	9 * 7 * 60 * 1000    // 63 minutes
    };

    public StarData(int worldId, World world, int tier, String location, String foundBy, long updatedAt, int region)
    {
	this.worldId = worldId;
	this.world = world;
	this.tier = tier;
	this.location = location;
	this.foundBy = foundBy;
	this.updatedAt = updatedAt;
	this.region = region;
    }

    public int getTier(boolean estimate)
    {
	if(!estimate)
	    return tier;

	long timeSinceUpdate = System.currentTimeMillis() - (updatedAt * 1000L);

	for(int i = 0; i < timeUntilDead.length; i++)
	    if(timeUntilDead[i] > (timeUntilDead[tier] - timeSinceUpdate))
		return i;

	return 9;
    }

    public int getDeadTime()
    {
	long deadAt = (updatedAt * 1000L) + timeUntilDead[tier];
	return (int)((deadAt - System.currentTimeMillis()) / (60 * 1000));
    }

    public EnumSet<WorldType> getWorldTypes()
    {
	if(world == null)
	    return null;
	return world.getTypes();
    }

    public TotalLevelType getTotalLevelType()
    {
	if(!world.getTypes().contains(WorldType.SKILL_TOTAL))
	    return TotalLevelType.NONE;

	switch (world.getActivity().substring(0,4).trim())
	{
	    case "500":
		return TotalLevelType.TOTAL_500;
	    case "750":
		return TotalLevelType.TOTAL_750;
	    case "1250":
		return TotalLevelType.TOTAL_1250;
	    case "1500":
		return TotalLevelType.TOTAL_1500;
	    case "1750":
		return TotalLevelType.TOTAL_1750;
	    case "2000":
		return TotalLevelType.TOTAL_2000;
	    case "2200":
		return TotalLevelType.TOTAL_2200;
	    default:
		return TotalLevelType.NONE;
	}
    }

    public String getWorldTypeSpecifier()
    {
	if(world == null)
	    return "";
	if(world.getTypes().contains(WorldType.SKILL_TOTAL))
	    return world.getActivity().substring(0,4).trim();
	if(world.getTypes().contains(WorldType.PVP))
	    return "PVP";
	if(world.getTypes().contains(WorldType.HIGH_RISK))
	    return "HR";
	if(world.getTypes().contains(WorldType.BETA_WORLD))
	    return "Beta";
	if(world.getTypes().contains(WorldType.DEADMAN))
	    return "DMM";
	if(world.getTypes().contains(WorldType.SEASONAL))
	    return "S";
	return "";
    }

    public boolean isWilderness()
    {
	return region == 13;
    }

    public boolean isPvp()
    {
	if(world == null)
	    return true;
	return world.getTypes().contains(WorldType.PVP);
    }

    public boolean isHighRisk()
    {
	if(world == null)
	    return true;
	return world.getTypes().contains(WorldType.HIGH_RISK);
    }

    public boolean isP2p()
    {
	if(world == null)
	    return true;
	return world.getTypes().contains(WorldType.MEMBERS);
    }

}
