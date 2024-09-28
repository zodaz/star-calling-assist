package com.starcallingassist.modules.scout;

import com.starcallingassist.enums.StarLocationDetails;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;

public class ScoutWorldMapPoint extends WorldMapPoint
{
	public ScoutWorldMapPoint(ScoutModule module, StarLocationDetails location)
	{
		super(location.getWorldPoint(), module.getStarScoutLocationImage());

		setName(location.getName());
		this.setSnapToEdge(false);
		this.setJumpOnClick(false);
	}
}
