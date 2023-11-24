package com.starcallingassist.modules.overlay;

import com.starcallingassist.objects.Star;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;

public class ActiveStarWorldMapPoint extends WorldMapPoint
{
	public ActiveStarWorldMapPoint(OverlayModule module, Star star)
	{
		super(star.getLocation().getWorldPoint(), module.getStarActiveLocationImage());

		setName(String.format("%s (T%s)", star.getLocation().getName(), star.getTier()));
		this.setSnapToEdge(true);
		this.setJumpOnClick(true);
	}
}
