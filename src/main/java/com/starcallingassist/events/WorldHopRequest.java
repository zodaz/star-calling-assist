package com.starcallingassist.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.http.api.worlds.World;

@AllArgsConstructor
public class WorldHopRequest
{
	@Getter
	private final World world;
}
