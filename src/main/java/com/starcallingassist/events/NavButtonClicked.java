package com.starcallingassist.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.ui.NavigationButton;

@AllArgsConstructor
public class NavButtonClicked
{
	@Getter
	private NavigationButton button;
}
