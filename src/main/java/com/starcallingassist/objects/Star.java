package com.starcallingassist.objects;

import java.util.Objects;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.coords.WorldPoint;

public class Star
{
	@Getter
	private final Integer world;

	@Getter
	private final StarLocation location;

	@Getter
	private final Integer tier;

	@Getter
	@Setter
	private Integer currentMiners;

	public Star(Integer world, StarLocation location, Integer tier)
	{
		this.world = world;
		this.location = location;
		this.tier = tier;
	}

	public Star(Integer world, @Nonnull WorldPoint location, Integer tier)
	{
		this(world, new StarLocation(location), tier);
	}

	public Star(Integer world, @Nonnull WorldPoint location)
	{
		this(world, location, null);
	}

	public boolean isSameAs(@Nonnull Star star)
	{
		return Objects.equals(world, star.world)
			&& Objects.equals(location.getWorldPoint(), star.location.getWorldPoint());
	}
}
