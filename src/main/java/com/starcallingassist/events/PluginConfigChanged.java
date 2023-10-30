package com.starcallingassist.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.events.ConfigChanged;

@AllArgsConstructor
public class PluginConfigChanged
{
	@Getter
	private ConfigChanged base;

	public String getKey()
	{
		return base.getKey();
	}
}