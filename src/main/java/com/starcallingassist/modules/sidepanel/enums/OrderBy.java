package com.starcallingassist.modules.sidepanel.enums;

import lombok.Getter;

public enum OrderBy
{
	WORLD("World"),
	TIER("Tier"),
	LOCATION("Location"),
	DEAD_TIME("Dead Time");

	@Getter
	private final String name;

	OrderBy(String name)
	{
		this.name = name;
	}

	public static OrderBy fromString(String value)
	{
		for (OrderBy orderBy : OrderBy.values())
		{
			if (orderBy.name.equalsIgnoreCase(value))
			{
				return orderBy;
			}
		}

		return null;
	}
}
