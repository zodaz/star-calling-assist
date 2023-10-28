package com.starcallingassist.events;

import com.starcallingassist.contracts.ChatMessageContract;
import lombok.Getter;

public class ChatDebugMessage implements ChatMessageContract
{
	@Getter
	protected String message;

	@Getter
	protected Boolean useHighlighting;

	public ChatDebugMessage(String message)
	{
		this(message, true);
	}

	public ChatDebugMessage(String message, Boolean useHighlighting)
	{
		this.message = message;
		this.useHighlighting = useHighlighting;
	}
}
