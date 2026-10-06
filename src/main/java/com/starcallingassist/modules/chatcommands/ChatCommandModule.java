package com.starcallingassist.modules.chatcommands;

import com.google.inject.Inject;
import com.starcallingassist.PluginModuleContract;
import com.starcallingassist.events.AnnouncementsRefreshed;
import com.starcallingassist.modules.crowdsourcing.objects.AnnouncedStar;
import com.starcallingassist.objects.Star;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import net.runelite.api.Client;
import net.runelite.api.MessageNode;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
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
			String formattedResponse = new ChatMessageBuilder()
				.append(ChatColorType.HIGHLIGHT)
				.append(response)
				.build();
			MessageNode messageNode = chatMessage.getMessageNode();
			messageNode.setRuneLiteFormatMessage(formattedResponse);
			client.refreshChat();
		});
	}

	static String formatStars(List<Star> stars, StarChatCommandFilter filter)
	{
		if (stars.isEmpty())
		{
			return filter.isAll()
				? "No active stars were found."
				: "No active stars matched " + filter.description() + ".";
		}

		StringBuilder response = new StringBuilder(filter.isAll() ? "Stars: " : "Stars (" + filter.description() + "): ");
		int starsShown = 0;
		for (Star star : stars)
		{
			if (starsShown == MAX_STARS_IN_RESPONSE)
			{
				break;
			}

			String entry = "T" + star.getTier() + " W" + star.getWorld();
			String location = StarChatCommandFilter.cleanLocation(star);
			if (!location.isEmpty())
			{
				entry += " (" + abbreviateLocation(location) + ")";
			}

			int separatorLength = starsShown == 0 ? 0 : 3;
			if (response.length() + separatorLength + entry.length() > MAX_RESPONSE_LENGTH)
			{
				break;
			}

			if (starsShown > 0)
			{
				response.append(" | ");
			}
			response.append(entry);
			starsShown++;
		}

		if (starsShown == 0)
		{
			return "Active stars were found, but their details were too long to display.";
		}
		if (starsShown < stars.size())
		{
			response.append(" | +").append(stars.size() - starsShown).append(" more");
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
