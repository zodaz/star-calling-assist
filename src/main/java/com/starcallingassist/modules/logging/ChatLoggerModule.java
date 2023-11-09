package com.starcallingassist.modules.logging;

import com.google.inject.Inject;
import com.starcallingassist.PluginModuleContract;
import com.starcallingassist.StarCallingAssistConfig;
import com.starcallingassist.enums.ChatLogLevel;
import com.starcallingassist.events.LogMessage;
import com.starcallingassist.events.StarAbandoned;
import com.starcallingassist.events.StarApproached;
import com.starcallingassist.events.StarDepleted;
import com.starcallingassist.events.StarMissing;
import com.starcallingassist.events.StarRegionScouted;
import com.starcallingassist.events.StarScouted;
import com.starcallingassist.events.StarTierChanged;
import com.starcallingassist.events.WorldStarUpdated;
import net.runelite.api.ChatMessageType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.eventbus.Subscribe;

public class ChatLoggerModule extends PluginModuleContract
{
	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private ClientThread clientThread;

	@Inject
	private StarCallingAssistConfig config;

	@Subscribe
	public void onStarAbandoned(StarAbandoned event)
	{
		dispatch(new LogMessage("You've moved away from the star..", ChatLogLevel.DEBUG));
	}

	@Subscribe
	public void onStarApproached(StarApproached event)
	{
		dispatch(new LogMessage("You approach the star..", ChatLogLevel.DEBUG));
	}

	@Subscribe
	public void onStarDepleted(StarDepleted event)
	{
		dispatch(new LogMessage("The star has fully depleted.", ChatLogLevel.VERBOSE));
	}

	@Subscribe
	public void onStarMissing(StarMissing event)
	{
		dispatch(new LogMessage("The star you are looking for seems to be missing.", ChatLogLevel.VERBOSE));
	}

	@Subscribe
	public void onStarRegionScouted(StarRegionScouted event)
	{
		dispatch(new LogMessage(String.format("You've scouted the star landing area for *%s*", event.getLocation().getName()), ChatLogLevel.DEBUG));
	}

	@Subscribe
	public void onStarScouted(StarScouted event)
	{
		dispatch(new LogMessage(String.format(
			"You've discovered a previously-unknown *T%d* star near *%s*!",
			event.getStar().getTier(),
			event.getStar().getLocation().getName()
		), ChatLogLevel.NORMAL));
	}

	@Subscribe
	public void onStarTierChanged(StarTierChanged event)
	{
		dispatch(new LogMessage(String.format("The star has *degraded*. It is now a *T%d*.", event.getStar().getTier()), ChatLogLevel.NORMAL));
	}

	@Subscribe
	public void onWorldStarUpdated(WorldStarUpdated event)
	{
		if (event.getStar() == null)
		{
			return;
		}

		dispatch(new LogMessage(String.format(
			"Star Update (world *%d*): Star near *%s* is now a *T%d*.",
			event.getStar().getWorld(),
			event.getStar().getLocation().getName(),
			event.getStar().getTier()
		), ChatLogLevel.DEBUG));
	}

	@Subscribe
	public void onLogMessage(LogMessage event)
	{
		if (config.logLevel().getValue() < event.getLogLevel().getValue())
		{
			return;
		}

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
			builder = builder
				.append(insideAsterisks ? ChatColorType.HIGHLIGHT : ChatColorType.NORMAL)
				.append(currentSegment.toString());
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
