package com.starcallingassist.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatLogLevel
{
	NONE("None"),
	NORMAL("Normal"),
	CALLS("Calls"),
	VERBOSE("Verbose"),
	DEBUG("Debug");

	private final String name;

	@Override
	public String toString()
	{
		return getName();
	}


	public int getValue()
	{
		if (this == NORMAL)
		{
			return 1;
		}

		if (this == CALLS)
		{
			return 2;
		}

		if (this == VERBOSE)
		{
			return 3;
		}

		if (this == DEBUG)
		{
			return 4;
		}


		return 0;
	}
}
