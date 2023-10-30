package com.starcallingassist.events;

import com.starcallingassist.contracts.ChatMessageContract;
import lombok.Getter;

public class InfoLogMessage implements ChatMessageContract
{
	@Getter
	protected String message;

	@Getter
	protected Boolean useHighlighting;

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
