package com.starcallingassist.modules.scout;

import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;

public class ScoutWorldMapPoint extends WorldMapPoint
{
	public ScoutWorldMapPoint(ScoutModule module, ScoutLocation location)
	{
		super(location.getWorldPoint(), module.getStarScoutLocationImage());

		setName(location.getName());
		this.setSnapToEdge(false);
		this.setJumpOnClick(false);
	}
}
