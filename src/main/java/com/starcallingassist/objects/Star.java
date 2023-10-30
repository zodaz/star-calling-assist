package com.starcallingassist.objects;

import com.starcallingassist.support.StarLocation;
import java.util.Objects;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.coords.WorldPoint;

public class Star
{
	@Getter
	protected Integer world;

	@Getter
	protected StarLocation location;

	@Getter
	@Setter
	protected Integer tier;

	@Getter
	@Setter
	protected Integer currentMiners;

	public Star(Integer world, WorldPoint location, Integer tier)
	{
		this.world = world;
		this.location = new StarLocation(location);
		this.tier = tier;
	}

	public Star(Integer world, WorldPoint location)
	{
		this(world, location, null);
	}

	public boolean isSameAs(@Nonnull Star star)
	{
		return Objects.equals(world, star.world)
			&& Objects.equals(location.getWorldPoint(), star.location.getWorldPoint());
	}
}
