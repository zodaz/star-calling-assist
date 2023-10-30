package com.starcallingassist.events;

import com.starcallingassist.objects.Star;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class StarMinersChanged
{
	@Getter
	protected Star star;

	@Getter
	protected Integer previousMiners;
}
