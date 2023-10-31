package com.starcallingassist.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class StarDepletionManuallyRequested
{
	@Getter
	private final Boolean isPublicCall;

	public StarDepletionManuallyRequested()
	{
		this(true);
	}
}
