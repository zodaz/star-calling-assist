package com.starcallingassist.events;

import com.starcallingassist.enums.ChatLogLevel;
import lombok.Getter;

public class LogMessage
{
	@Getter
	private final String message;

	@Getter
	private final Boolean useHighlighting;

	@Getter
	private final ChatLogLevel logLevel;

	public LogMessage(String message, ChatLogLevel level, Boolean useHighlighting)
	{
		this.message = message;
		this.logLevel = level;
		this.useHighlighting = useHighlighting;
	}

	public LogMessage(String message, ChatLogLevel level)
	{
		this(message, level, true);
	}

	public LogMessage(String message)
	{
		this(message, ChatLogLevel.NORMAL);
	}
}
