package com.starcallingassist.modules.worldmapoverlay.enums;

import lombok.AllArgsConstructor;

/**
 * Enum representing the different settings for how to display stars on the world map.
 */
@AllArgsConstructor
public enum WorldMapDisplayLevel
{
	NONE("None"),
	ACTIVE("Active stars"),
	ALL("All locations");

	private final String name;

	@Override
	public String toString()
	{
		return name;
	}
}
