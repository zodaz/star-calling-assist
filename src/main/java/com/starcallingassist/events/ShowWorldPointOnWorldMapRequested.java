package com.starcallingassist.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
public class ShowWorldPointOnWorldMapRequested
{
	@Getter
	private final WorldPoint worldPoint;
}
