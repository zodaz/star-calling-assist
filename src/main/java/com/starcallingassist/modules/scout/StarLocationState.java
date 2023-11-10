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

	@Setter
	private Long lastScoutedAt;

	public StarLocationState()
	{
		this.regionLoaded = false;
		this.playerWithinBounds = false;
		this.lastScoutedAt = 0L;
	}

	public Color getColor()
	{
		if (lastScoutedAt > System.currentTimeMillis() - 60000)
		{
			return PluginColors.SCOUT_BOUNDS_COLOR_SCOUTED;
		}

		if (!regionLoaded)
		{
			return PluginColors.SCOUT_BOUNDS_COLOR_UNSCOUTABLE;
		}

		return PluginColors.SCOUT_BOUNDS_COLOR_SCOUTABLE;
	}
}
