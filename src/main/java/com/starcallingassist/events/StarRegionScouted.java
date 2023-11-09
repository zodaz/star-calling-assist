package com.starcallingassist.events;


import com.starcallingassist.objects.StarLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class StarRegionScouted
{
	@Getter
	private final StarLocation location;
}
