package com.starcallingassist.modules.chatcommands;

import static org.junit.Assert.assertEquals;

import com.starcallingassist.objects.Star;
import com.starcallingassist.objects.StarLocation;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class StarChatCommandFilterTest
{
	private static final List<Star> STARS = Arrays.asList(
		new Star(330, new StarLocation("Varrock east bank"), 7, ""),
		new Star(434, new StarLocation("Rimmington mine"), 8, ""),
		new Star(533, new StarLocation("Southeast Varrock mine"), 9, ""));

	@Test
	public void filtersAnExactTier()
	{
		List<Star> stars = StarChatCommandFilter.fromCommand("!stars t7").apply(STARS);

		assertEquals(1, stars.size());
		assertEquals(Integer.valueOf(330), stars.get(0).getWorld());
	}

	@Test
	public void filtersMinimumTierAndLocation()
	{
		List<Star> stars = StarChatCommandFilter.fromCommand("!stars t7+ varrock").apply(STARS);

		assertEquals(2, stars.size());
		assertEquals(Integer.valueOf(330), stars.get(0).getWorld());
		assertEquals(Integer.valueOf(533), stars.get(1).getWorld());
	}
}
