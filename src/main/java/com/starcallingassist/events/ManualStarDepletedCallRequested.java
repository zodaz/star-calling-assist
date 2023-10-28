package com.starcallingassist.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ManualStarDepletedCallRequested
{
	@Getter
	protected Boolean isPublicCall;

	public ManualStarDepletedCallRequested()
	{
		this(true);
	}
}
