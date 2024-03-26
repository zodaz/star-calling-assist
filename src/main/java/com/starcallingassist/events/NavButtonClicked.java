package com.starcallingassist.events;

import lombok.Getter;
import net.runelite.client.ui.NavigationButton;

@Getter
public class NavButtonClicked
{
	private final boolean sidePanelShowing;
	private final NavigationButton button;

	public NavButtonClicked(NavigationButton button)
	{
		this.button = button;
		this.sidePanelShowing = button.getPanel().isShowing();
	}
}
