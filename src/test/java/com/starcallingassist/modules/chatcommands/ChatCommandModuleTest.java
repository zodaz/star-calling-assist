package com.starcallingassist.modules.chatcommands;

import static org.junit.Assert.assertEquals;

import com.starcallingassist.objects.Star;
import com.starcallingassist.objects.StarLocation;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class ChatCommandModuleTest
{
	@Test
	public void formatsACompactStarList()
	{
		String formatted = ChatCommandModule.formatStars(Arrays.asList(
			new Star(533, new StarLocation("West Lumbridge Swamp mine"), 9, ""),
			new Star(330, new StarLocation("Al Kharid mine"), 8, "")),
			StarChatCommandFilter.fromCommand("!stars"));

		assertEquals("Stars: T9 W533 (West Lumbridge Swamp mine) | T8 W330 (Al Kharid mine)", formatted);
	}

	@Test
	public void explainsAnEmptyFilteredList()
	{
		assertEquals("No active stars matched T7, Varrock.",
			ChatCommandModule.formatStars(Collections.emptyList(), StarChatCommandFilter.fromCommand("!stars t7 varrock")));
	}
}
