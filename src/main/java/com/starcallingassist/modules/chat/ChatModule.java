package com.starcallingassist.modules.chat;

import com.google.inject.Inject;
import com.starcallingassist.StarModuleContract;
import com.starcallingassist.contracts.ChatMessageContract;
import com.starcallingassist.events.InfoLogMessage;
import com.starcallingassist.events.DebugLogMessage;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;

@Slf4j
public class ChatModule extends StarModuleContract
{
	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private ClientThread clientThread;

	@Subscribe
	public void onInfoLogMessage(InfoLogMessage event)
	{
		queueChatMessage(event);
	}

	@Subscribe
	public void onDebugLogMessage(DebugLogMessage event)
	{
		if (config.chatMessages())
		{
			queueChatMessage(event);
		}
	}

	protected void queueChatMessage(ChatMessageContract event)
	{
		String formattedMessage = event.getUseHighlighting()
			? buildHighlightedChatMessage(event.getMessage())
			: buildRegularChatMessage(event.getMessage());

		chatMessageManager.queue(
			QueuedMessage.builder()
				.type(ChatMessageType.CONSOLE)
				.runeLiteFormattedMessage(formattedMessage)
				.build()
		);
	}

	protected String buildHighlightedChatMessage(String input)
	{
		ChatMessageBuilder builder = new ChatMessageBuilder();
		StringBuilder currentSegment = new StringBuilder();

		boolean insideAsterisks = false;

		for (char c : input.toCharArray())
		{
			if (c != '*')
			{
				currentSegment.append(c);
				continue;
			}

			builder = builder
				.append(insideAsterisks ? ChatColorType.HIGHLIGHT : ChatColorType.NORMAL)
				.append(currentSegment.toString());

			currentSegment.setLength(0);
			insideAsterisks = !insideAsterisks;
		}

		if (currentSegment.length() > 0)
		{
			builder = builder.append(currentSegment.toString());
		}

		return builder.build();
	}

	protected String buildRegularChatMessage(String value)
	{
		return new ChatMessageBuilder()
			.append(ChatColorType.NORMAL)
			.append(value)
			.build();
	}
}
