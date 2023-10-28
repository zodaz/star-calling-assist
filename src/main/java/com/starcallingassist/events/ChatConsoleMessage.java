package com.starcallingassist.events;

import com.starcallingassist.contracts.ChatMessageContract;
import lombok.Getter;

public class ChatConsoleMessage implements ChatMessageContract
{
	@Getter
	protected String message;

	@Getter
	protected Boolean useHighlighting;

	public ChatConsoleMessage(String message)
	{
		this(message, true);
	}

	public ChatConsoleMessage(String message, Boolean useHighlighting)
	{
		this.message = message;
		this.useHighlighting = useHighlighting;
	}
}
