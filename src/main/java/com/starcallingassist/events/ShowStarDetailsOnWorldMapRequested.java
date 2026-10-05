package com.starcallingassist.events;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Event for requesting the <code>WorldMapStarDetailsOverlay</code> to open on the world map.
 */
@Getter
@AllArgsConstructor
public class ShowStarDetailsOnWorldMapRequested
{
	/**
	 * The star location to display.
	 */
	private final String starLocation;
}
