package com.starcallingassist.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class WorldHopRequest
{
	@Getter
	private final int world;
}
