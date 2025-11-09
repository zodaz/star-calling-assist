package com.starcallingassist.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
public class RouteViaShortestPathRequested
{
	@Getter
	private final WorldPoint target;
}
