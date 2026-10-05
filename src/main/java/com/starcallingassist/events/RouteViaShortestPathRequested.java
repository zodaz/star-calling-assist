package com.starcallingassist.events;

import com.starcallingassist.objects.Star;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class RouteViaShortestPathRequested
{
	@Getter
	private final Star star;
}
