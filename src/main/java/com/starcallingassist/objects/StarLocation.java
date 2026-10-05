package com.starcallingassist.objects;

import com.starcallingassist.enums.Region;
import com.starcallingassist.enums.StarLocationDetails;
import java.awt.Point;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

/**
 * Wrapper class for {@link StarLocationDetails} that allows for star locations that are not
 * defined by it to be handled. This is needed to work with stars manually
 * called-out by a player in the star miners discord or in-game cc etc.
 */
public class StarLocation
{
	@Getter
	@Nullable
	private final StarLocationDetails starLocationDetails;

	/**
	 * Stars with a called location that doesn't exist in {@link StarLocationDetails} will use this name.
	 * This often happens if a star was manually called-out by a player in the star miners discord or in-game cc.
	 */
	private String originalInputName;

	/**
	 * Unknown stars found by the plugin with coordinates that doesn't exist in {@link StarLocationDetails} will use
	 * this point. This only happens when Jagex relocates an existing star or adds a new star to the game.
	 */
	private Point originalPoint;

	public StarLocation(String location)
	{
		originalInputName = location;
		starLocationDetails = StarLocationDetails.getByName(location);
	}

	public StarLocation(Point point)
	{
		originalPoint = point;
		starLocationDetails = StarLocationDetails.getByCoordinates(point);
	}

	public StarLocation(@Nonnull WorldPoint location)
	{
		this(new Point(location.getX(), location.getY()));
	}

	public WorldPoint getWorldPoint()
	{
		if (starLocationDetails == null)
		{
			return null;
		}

		return new WorldPoint(starLocationDetails.getCoordinates().x, starLocationDetails.getCoordinates().y, 0);
	}

	public String getName()
	{
		if (starLocationDetails != null)
		{
			return starLocationDetails.getName();
		}

		if (originalInputName != null)
		{
			return originalInputName;
		}

		// This will only be reached the case if the plugin found a star that Jagex relocated or added.
		if (originalPoint != null)
		{
			return originalPoint.x + "," + originalPoint.y;
		}

		return null;
	}

	public Region getRegion()
	{
		if (starLocationDetails != null)
		{
			return starLocationDetails.getRegion();
		}

		return Region.UNKNOWN;
	}
}
