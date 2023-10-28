package com.starcallingassist.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class WorldHopRequest
{
	@Getter
	protected int world;
}
