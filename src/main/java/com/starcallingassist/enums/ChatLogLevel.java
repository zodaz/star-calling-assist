package com.starcallingassist.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enum representing the different verbosity levels for the game chat logs.
 */
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
}
