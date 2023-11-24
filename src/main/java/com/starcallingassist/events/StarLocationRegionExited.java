package com.starcallingassist.events;

import com.starcallingassist.objects.StarLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class StarLocationRegionExited
{
	@Getter
	private final StarLocation location;
}
