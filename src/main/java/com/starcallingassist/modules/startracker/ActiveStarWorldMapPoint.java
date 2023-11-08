package com.starcallingassist.modules.startracker;

import com.starcallingassist.objects.Star;
import com.starcallingassist.objects.StarLocation;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;

public class ActiveStarWorldMapPoint extends WorldMapPoint
{
	public ActiveStarWorldMapPoint(StarTrackerModule module, Star star)
	{
		super(star.getLocation().getWorldPoint(), module.getStarActiveLocationImage());

		setName(String.format("%s (T%s)", star.getLocation().getName(), star.getTier()));
		this.setSnapToEdge(true);
		this.setJumpOnClick(true);
	}
}
