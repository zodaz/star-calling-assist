package com.starcallingassist.modules.chatcommands;

import com.starcallingassist.objects.Star;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.runelite.client.util.Text;

class StarChatCommandFilter
{
	private static final Pattern TIER_FILTER = Pattern.compile("(?i)^t?([1-9])(\\+)?(?:\\s+(.+))?$");

	private final Integer exactTier;
	private final Integer minimumTier;
	private final String location;

	private StarChatCommandFilter(Integer exactTier, Integer minimumTier, String location)
	{
		this.exactTier = exactTier;
		this.minimumTier = minimumTier;
		this.location = location;
	}

	static StarChatCommandFilter fromCommand(String commandText)
	{
		String arguments = commandText.length() <= ChatCommandModule.STARS_COMMAND.length()
			? ""
			: commandText.substring(ChatCommandModule.STARS_COMMAND.length()).trim();
		if (arguments.isEmpty())
		{
			return new StarChatCommandFilter(null, null, "");
		}

		Matcher matcher = TIER_FILTER.matcher(arguments);
		if (matcher.matches())
		{
			int tier = Integer.parseInt(matcher.group(1));
			String location = matcher.group(3) == null ? "" : matcher.group(3).trim();
			return matcher.group(2) == null
				? new StarChatCommandFilter(tier, null, location)
				: new StarChatCommandFilter(null, tier, location);
		}

		return new StarChatCommandFilter(null, null, arguments);
	}

	List<Star> apply(List<Star> stars)
	{
		List<Star> matchingStars = new ArrayList<>();
		for (Star star : stars)
		{
			if (star.getTier() == null || star.getLocation() == null)
			{
				continue;
			}
			if (exactTier != null && !exactTier.equals(star.getTier()))
			{
				continue;
			}
			if (minimumTier != null && star.getTier() < minimumTier)
			{
				continue;
			}
			if (!location.isEmpty() && !cleanLocation(star).toLowerCase(Locale.ROOT).contains(location.toLowerCase(Locale.ROOT)))
			{
				continue;
			}
			matchingStars.add(star);
		}
		return matchingStars;
	}

	boolean isAll()
	{
		return exactTier == null && minimumTier == null && location.isEmpty();
	}

	String description()
	{
		List<String> parts = new ArrayList<>();
		if (exactTier != null)
		{
			parts.add("T" + exactTier);
		}
		if (minimumTier != null)
		{
			parts.add("T" + minimumTier + "+");
		}
		if (!location.isEmpty())
		{
			parts.add(displayLocation());
		}
		return String.join(", ", parts);
	}

	private String displayLocation()
	{
		return Character.toUpperCase(location.charAt(0)) + location.substring(1);
	}

	static String cleanLocation(Star star)
	{
		String name = star.getLocation().getName();
		return name == null ? "" : Text.removeTags(name).replaceAll("[\\r\\n\\t]+", " ").trim();
	}
}
