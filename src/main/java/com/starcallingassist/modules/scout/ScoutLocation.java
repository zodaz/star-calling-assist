package com.starcallingassist.modules.scout;

import com.starcallingassist.objects.StarLocation;
import java.awt.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import static net.runelite.api.Constants.CHUNK_SIZE;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
public class ScoutLocation
{
	private StarLocation starLocation;

	@Getter
	@Setter
	private boolean regionLoaded;

	@Getter
	@Setter
	private Long lastSeen;


	public ScoutLocation(Point point)
	{
		this.starLocation = new StarLocation(point);
		this.regionLoaded = false;
		this.lastSeen = 0L;
	}

	public WorldPoint getWorldPoint()
	{
		return starLocation.getWorldPoint();
	}

	public String getName()
	{
		return starLocation.getName();
	}

	public WorldArea scoutableBounds()
	{
		if (starLocation == null)
		{
			return null;
		}

		WorldPoint point = getWorldPoint();

		return new WorldArea(
			(point.getX() & ~(CHUNK_SIZE - 1)) - (3 * CHUNK_SIZE),
			(point.getY() & ~(CHUNK_SIZE - 1)) - (3 * CHUNK_SIZE),
			CHUNK_SIZE * 7,
			CHUNK_SIZE * 7,
			0
		);
	}
}
