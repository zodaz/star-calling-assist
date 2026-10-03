package com.starcallingassist.modules.chatcommands;

import com.google.inject.Inject;
import com.starcallingassist.PluginModuleContract;
import com.starcallingassist.events.AnnouncementsRefreshed;
import com.starcallingassist.modules.crowdsourcing.objects.AnnouncedStar;
import com.starcallingassist.objects.Star;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.MessageNode;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatCommandManager;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.eventbus.Subscribe;

public class ChatCommandModule extends PluginModuleContract
{
	static final String STARS_COMMAND = "!stars";
	private static final int MAX_STARS_IN_RESPONSE = 5;
	private static final int MAX_RESPONSE_LENGTH = 240;
	private static final int MAX_LOCATION_LENGTH = 40;

	@Inject
	private ChatCommandManager chatCommandManager;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	private volatile boolean running;
	private volatile List<Star> activeStars = Collections.emptyList();

	@Override
	public void startUp()
	{
		running = true;
		chatCommandManager.registerCommandAsync(STARS_COMMAND, this::showStars);
	}

	@Override
	public void shutDown()
	{
		running = false;
		activeStars = Collections.emptyList();
		chatCommandManager.unregisterCommand(STARS_COMMAND);
	}

	@Subscribe
	public void onAnnouncementsRefreshed(AnnouncementsRefreshed event)
	{
		List<Star> refreshedStars = new ArrayList<>();
		for (AnnouncedStar announcement : event.getAnnouncements())
		{
			Star star = announcement.getStar();
			if (star != null && star.getWorld() != null && star.getTier() != null && star.getTier() >= 1 && star.getTier() <= 9)
			{
				refreshedStars.add(star);
			}
		}
		refreshedStars.sort(Comparator.comparing(Star::getTier).reversed().thenComparing(Star::getWorld));
		activeStars = Collections.unmodifiableList(refreshedStars);
	}

	private void showStars(ChatMessage chatMessage, String commandText)
	{
		if (!running)
		{
			return;
		}

		StarChatCommandFilter filter = StarChatCommandFilter.fromCommand(commandText);
		setResponse(chatMessage, formatStars(filter.apply(activeStars), filter));
	}

	private void setResponse(ChatMessage chatMessage, String response)
	{
		clientThread.invoke(() ->
		{
			if (!running)
			{
				return;
			}

			String[] lines = response.split("\\n");
			ChatMessageBuilder responseBuilder = new ChatMessageBuilder();
			appendHeader(responseBuilder, lines[0]);

			for (int index = 1; index < lines.length; index++)
			{
				responseBuilder.append("\n");
				appendStarLine(responseBuilder, lines[index]);
			}

			String formattedResponse = responseBuilder.build().replace("\n", "<br>");
			MessageNode messageNode = chatMessage.getMessageNode();
			messageNode.setRuneLiteFormatMessage(formattedResponse);
			client.refreshChat();
		});
	}

	private static void appendHeader(ChatMessageBuilder builder, String header)
	{
		int openingBracket = header.indexOf('[');
		int closingBracket = header.indexOf(']');

		if (openingBracket == -1 || closingBracket <= openingBracket)
		{
			builder.append(Color.RED, header);
			return;
		}

		builder.append(Color.RED, header.substring(0, openingBracket + 1));
		builder.append(Color.BLACK, header.substring(openingBracket + 1, closingBracket));
		builder.append(Color.RED, header.substring(closingBracket));
	}

	private static void appendStarLine(ChatMessageBuilder builder, String line)
	{
		int separator = line.indexOf(" - ");

		if (separator == -1)
		{
			builder.append(Color.RED, line);
			return;
		}

		builder.append(Color.BLACK, line.substring(0, separator + 3));
		builder.append(Color.RED, line.substring(separator + 3));
	}

	static String formatStars(List<Star> stars, StarChatCommandFilter filter)
	{
		if (stars.isEmpty())
		{
			return filter.isAll()
				? "No active stars were found."
				: "No active stars matched " + filter.description() + ".";
		}

		StringBuilder response = new StringBuilder(filter.isAll() ? "Stars:" : "Stars [" + filter.description() + "]:");
		int starsShown = 0;
		for (Star star : stars)
		{
			if (starsShown == MAX_STARS_IN_RESPONSE)
			{
				break;
			}

			String entry = "W" + star.getWorld() + " T" + star.getTier();
			String location = StarChatCommandFilter.cleanLocation(star);
			if (!location.isEmpty())
			{
				entry += " - " + abbreviateLocation(location);
			}

			if (response.length() + 1 + entry.length() > MAX_RESPONSE_LENGTH)
			{
				break;
			}

			response.append('\n').append(entry);
			starsShown++;
		}

		if (starsShown == 0)
		{
			return "Active stars were found, but their details were too long to display.";
		}
		if (starsShown < stars.size())
		{
			String remaining = "+" + (stars.size() - starsShown) + " more";
			if (response.length() + 1 + remaining.length() <= MAX_RESPONSE_LENGTH)
			{
				response.append('\n').append(remaining);
			}
		}
		return response.toString();
	}

	private static String abbreviateLocation(String location)
	{
		if (location.length() <= MAX_LOCATION_LENGTH)
		{
			return location;
		}
		return location.substring(0, MAX_LOCATION_LENGTH - 1) + "…";
	}
}
