package com.starcallingassist.objects;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;

@Getter
public class Star
{
	private final Integer world;
	private final StarLocation location;
	private final Integer tier;

	@Nullable
	private final String foundBy;

	@Setter
	private Integer currentMiners;

	@Setter
	@Nullable
	private NPC npc;

	public Star(Integer world, StarLocation location, Integer tier, String foundBy)
	{
		this.world = world;
		this.location = location;
		this.tier = tier;
		this.foundBy = foundBy;
	}

	public Star(Integer world, @Nonnull WorldPoint location, Integer tier)
	{
		this(world, new StarLocation(location), tier, null);
	}

	public static Star fromExistingWithTierChange(Star star, Integer tier)
	{
		return new Star(
			star.getWorld(),
			star.getLocation(),
			tier,
			star.getFoundBy()
		);
	}

	public boolean isSameAs(@Nonnull Star star)
	{
		return Objects.equals(world, star.world)
			&& Objects.equals(location.getWorldPoint(), star.location.getWorldPoint());
	}
}
