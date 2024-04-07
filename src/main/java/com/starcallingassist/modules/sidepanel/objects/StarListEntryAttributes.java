package com.starcallingassist.modules.sidepanel.objects;

import com.starcallingassist.constants.PluginColors;
import com.starcallingassist.enums.Region;
import com.starcallingassist.modules.sidepanel.decorators.StarListGroupEntryDecorator;
import com.starcallingassist.modules.sidepanel.enums.TotalLevelType;
import com.starcallingassist.objects.Star;
import java.awt.Color;
import java.util.EnumSet;
import java.util.Objects;
import javax.annotation.Nonnull;
import lombok.Getter;
import net.runelite.http.api.worlds.World;
import net.runelite.http.api.worlds.WorldType;

public class StarListEntryAttributes
{
	@Getter
	private final Star star;

	@Getter
	private final World world;

	@Getter
	private final long updatedAt;

	@Getter
	private final String foundBy;

	private final StarListGroupEntryDecorator decorator;


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

	public StarListEntryAttributes(@Nonnull Star star, @Nonnull World world, long updatedAt, StarListGroupEntryDecorator decorator)
	{
		this.star = star;
		this.world = world;
		this.updatedAt = updatedAt;
		this.foundBy = star.getFoundBy();
		this.decorator = decorator;
	}

	public int getTier()
	{
		return getTier(decorator.shouldEstimateTier());
	}

	public int getTier(boolean useEstimated)
	{
		int tier = star.getTier() == null ? 0 : star.getTier();
		if (!useEstimated)
		{
			return tier;
		}

		long timeSinceUpdate = System.currentTimeMillis() - (updatedAt * 1000L);

		for (int i = 0; i < timeUntilDead.length; i++)
		{
			if (timeUntilDead[i] > (timeUntilDead[tier] - timeSinceUpdate))
			{
				return i;
			}
		}

		return 9;
	}

	public int getDeadTime()
	{
		long deadAt = (updatedAt * 1000L) + timeUntilDead[getTier(false)];

		return (int) ((deadAt - System.currentTimeMillis()) / (60 * 1000));
	}

	public TotalLevelType getTotalLevelType()
	{
		if (!world.getTypes().contains(WorldType.SKILL_TOTAL))
		{
			return TotalLevelType.NONE;
		}

		return TotalLevelType.fromString(
			world.getActivity().substring(0, 4).trim()
		);
	}

	public boolean isCurrentWorld()
	{
		return decorator.getCurrentWorldId() == world.getId();
	}

	public boolean isCurrentLocation()
	{
		return decorator.getCurrentPlayerLocations()
			.stream()
			.anyMatch(location -> Objects.equals(location.getWorldPoint(), star.getLocation().getWorldPoint()));
	}

	public boolean isWilderness()
	{
		return star.getLocation().getRegion() == Region.WILDERNESS;
	}

	public boolean isUnverified()
	{
		return star.getLocation().getRegion() == Region.UNKNOWN;
	}

	public boolean shouldBeVisible()
	{
		EnumSet<WorldType> types = world.getTypes();

		if (types.contains(WorldType.MEMBERS) && !decorator.showMembersWorlds())
		{
			return false;
		}

		if (!types.contains(WorldType.MEMBERS) && !decorator.showFreeToPlayWorlds())
		{
			return false;
		}

		if (types.contains(WorldType.PVP) && !decorator.showPvPWorlds())
		{
			return false;
		}

		if (types.contains(WorldType.HIGH_RISK) && !decorator.showHighRiskWorlds())
		{
			return false;
		}

		if (getTotalLevelType().ordinal() > decorator.maxTotalLevel().ordinal())
		{
			return false;
		}

		if (getTier() < decorator.minTier() || getTier() > decorator.maxTier())
		{
			return false;
		}

		if (getDeadTime() < decorator.minDeadTime())
		{
			return false;
		}

		return decorator.visibleRegions().contains(star.getLocation().getRegion());
	}


	public Color getWorldColor()
	{
		if (isCurrentWorld())
		{
			return PluginColors.CURRENT_WORLD;
		}

		EnumSet<WorldType> types = world.getTypes();

		if (types.contains(WorldType.PVP) || types.contains(WorldType.HIGH_RISK) || types.contains(WorldType.DEADMAN))
		{
			return PluginColors.DANGEROUS_AREA;
		}
		else if (types.contains(WorldType.SEASONAL))
		{
			return PluginColors.SEASONAL_WORLD;
		}
		else if (types.contains(WorldType.NOSAVE_MODE) || types.contains(WorldType.BETA_WORLD))
		{
			return PluginColors.NOSAVE_WORLD;
		}
		else if (types.contains(WorldType.QUEST_SPEEDRUNNING))
		{
			return PluginColors.SPEEDRUNNING_WORLD;
		}
		else if (types.contains(WorldType.FRESH_START_WORLD))
		{
			return PluginColors.FRESH_START_WORLD;
		}
		else if (types.contains(WorldType.MEMBERS))
		{
			return PluginColors.MEMBERS_WORLD;
		}

		return PluginColors.STAR_LIST_GROUP_LABEL;
	}

	public boolean isDangerousArea()
	{
		return isWilderness() || getWorldColor() == PluginColors.DANGEROUS_AREA;
	}

	public String getWorldType()
	{
		if (world.getTypes().contains(WorldType.SKILL_TOTAL))
		{
			return world.getActivity().substring(0, 4) + " Total";
		}

		if (world.getTypes().contains(WorldType.PVP))
		{
			return "PvP World";
		}

		if (world.getTypes().contains(WorldType.HIGH_RISK))
		{
			return "High-risk PvP";
		}

		if (world.getTypes().contains(WorldType.BETA_WORLD))
		{
			return "Beta World";
		}

		if (world.getTypes().contains(WorldType.DEADMAN))
		{
			return "Deadman Mode";
		}

		if (world.getTypes().contains(WorldType.SEASONAL))
		{
			return "Seasonal";
		}

		return null;
	}

	public Color getWorldLimitationColor()
	{
		EnumSet<WorldType> types = world.getTypes();

		if (world.getTypes().contains(WorldType.SKILL_TOTAL))
		{
			return PluginColors.TOTAL_LEVEL_RESTRICTED_WORLD;
		}
		else if (types.contains(WorldType.SEASONAL))
		{
			return PluginColors.SEASONAL_WORLD;
		}
		else if (types.contains(WorldType.NOSAVE_MODE) || types.contains(WorldType.BETA_WORLD))
		{
			return PluginColors.NOSAVE_WORLD;
		}
		else if (types.contains(WorldType.QUEST_SPEEDRUNNING))
		{
			return PluginColors.SPEEDRUNNING_WORLD;
		}
		else if (types.contains(WorldType.FRESH_START_WORLD))
		{
			return PluginColors.FRESH_START_WORLD;
		}
		else if (types.contains(WorldType.PVP) || types.contains(WorldType.HIGH_RISK))
		{
			return PluginColors.PVP_WORLD;
		}

		return null;
	}
}
