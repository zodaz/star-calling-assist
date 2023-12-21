package com.starcallingassist.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SidePanelOpenChanged
{
	@Getter
	private final boolean sidePanelOpen;
}
