package com.starcallingassist.modules.worldmapoverlay.worldmapstardetails.ui;

import lombok.AllArgsConstructor;

/**
 * Padding values for UI. Follows CSS ordering.
 */
@AllArgsConstructor
public class Padding
{
	public Padding(int padding)
	{
		this(padding, padding, padding, padding);
	}

	public final int top;
	public final int right;
	public final int bottom;
	public final int left;
}
