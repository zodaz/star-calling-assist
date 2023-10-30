package com.starcallingassist.events;


import com.starcallingassist.objects.Star;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class StarAbandoned
{
	@Getter
	protected Star star;
}
