package com.starcallingassist.events;

import com.starcallingassist.contracts.ChatMessageContract;
import lombok.Getter;

public class InfoLogMessage implements ChatMessageContract
{
	@Getter
	private final String message;

	@Getter
	private final Boolean useHighlighting;

	public InfoLogMessage(String message)
	{
		this(message, true);
	}

	public InfoLogMessage(String message, Boolean useHighlighting)
	{
		this.message = message;
		this.useHighlighting = useHighlighting;
	}
}
