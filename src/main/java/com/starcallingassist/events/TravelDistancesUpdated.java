package com.starcallingassist.events;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
public class TravelDistancesUpdated
{
	@Getter
	private final Map<WorldPoint, Integer> distances;
}
