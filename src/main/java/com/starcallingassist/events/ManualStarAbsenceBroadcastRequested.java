package com.starcallingassist.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ManualStarAbsenceBroadcastRequested
{
	@Getter
	private final Boolean isPublicCall;

	public ManualStarAbsenceBroadcastRequested()
	{
		this(true);
	}
}
