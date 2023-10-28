package com.starcallingassist.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class StarDepletionManuallyRequested
{
	@Getter
	protected Boolean isPublicCall;

	public StarDepletionManuallyRequested()
	{
		this(true);
	}
}
