package com.starcallingassist.modules.scout;

import com.starcallingassist.constants.PluginColors;
import java.awt.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class StarLocationState
{
	@Getter
	@Setter
	private boolean regionLoaded;

	@Getter
	@Setter
	private boolean playerWithinBounds;

	@Getter
	@Setter
	private boolean worldPointLoaded;

	@Setter
	private Long lastScoutedAt;

	public StarLocationState()
	{
		reset();
	}

	public Color getColor()
	{
		if (lastScoutedAt > System.currentTimeMillis() - 60000)
		{
			return PluginColors.SCOUT_BOUNDS_COLOR_SCOUTED;
		}

		if (!regionLoaded || !worldPointLoaded)
		{
			return PluginColors.SCOUT_BOUNDS_COLOR_UNSCOUTABLE;
		}

		return PluginColors.SCOUT_BOUNDS_COLOR_SCOUTABLE;
	}

	public void reset()
	{
		this.regionLoaded = false;
		this.playerWithinBounds = false;
		this.worldPointLoaded = false;
		this.lastScoutedAt = 0L;
	}
}
