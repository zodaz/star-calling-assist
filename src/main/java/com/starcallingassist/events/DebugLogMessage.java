package com.starcallingassist.events;

import com.starcallingassist.contracts.ChatMessageContract;
import lombok.Getter;

public class DebugLogMessage implements ChatMessageContract
{
	@Getter
	protected String message;

	@Getter
	protected Boolean useHighlighting;

	public DebugLogMessage(String message)
	{
		this(message, true);
	}

	public DebugLogMessage(String message, Boolean useHighlighting)
	{
		this.message = message;
		this.useHighlighting = useHighlighting;
	}
}
