package com.starcallingassist.modules.overlay;

import com.starcallingassist.objects.Star;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayUtil;

@Slf4j
public class StarDepletionEstimationOverlay extends Overlay
{
	private final Star star;
	private int lastKnownStarPercentage = 0;
	private long lastKnownStarHealthTime = System.currentTimeMillis();
	private static final long ONE_PERCENT_TIME_ESTIMATE = 4200;

	public StarDepletionEstimationOverlay(Star star)
	{
		super();
		this.star = star;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (star == null)
		{
			return null;
		}

		NPC starNpc = star.getNpc();
		if (starNpc == null)
		{
			return null;
		}

		int currentStarPercentage = starNpc.getHealthRatio() * 100 / starNpc.getHealthScale();
		if (currentStarPercentage != lastKnownStarPercentage)
		{
			lastKnownStarPercentage = currentStarPercentage;
			lastKnownStarHealthTime = System.currentTimeMillis();
		}

		long remainingTimeInMillis = ONE_PERCENT_TIME_ESTIMATE * currentStarPercentage;
		remainingTimeInMillis -= System.currentTimeMillis() - lastKnownStarHealthTime;

		long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTimeInMillis);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(remainingTimeInMillis - TimeUnit.MINUTES.toMillis(minutes));

		String displayText = "Ests. remaining: " + String.format("%01d:%02d", minutes, seconds);
		Point textLocation = starNpc.getCanvasTextLocation(graphics, displayText, starNpc.getLogicalHeight() - 40);
		if (textLocation != null)
		{
			OverlayUtil.renderTextLocation(graphics, textLocation, displayText, Color.GREEN);
		}

		return null;
	}
}
