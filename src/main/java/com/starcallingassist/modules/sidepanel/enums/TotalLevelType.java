package com.starcallingassist.modules.sidepanel.enums;

import lombok.Getter;

public enum TotalLevelType
{
	NONE(null),
	TOTAL_500("500"),
	TOTAL_750("750"),
	TOTAL_1250("1250"),
	TOTAL_1500("1500"),
	TOTAL_1750("1750"),
	TOTAL_2000("2000"),
	TOTAL_2200("2200");

	@Getter
	private final String name;

	TotalLevelType(String name)
	{
		this.name = name;
	}

	public static TotalLevelType fromString(String value)
	{
		if (value == null)
		{
			return TotalLevelType.NONE;
		}

		for (TotalLevelType type : TotalLevelType.values())
		{
			if (type.name != null && type.name.equalsIgnoreCase(value))
			{
				return type;
			}
		}

		return TotalLevelType.NONE;
	}
}
