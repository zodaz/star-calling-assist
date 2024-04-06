package com.starcallingassist.modules.overlay;

import com.starcallingassist.objects.Star;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

@Slf4j
public class StarDetailsOverlay extends OverlayPanel
{
	private final Client client;
	private final Star star;
	private int lastKnownStarPercentage = 0;
	private long lastKnownStarHealthTime = System.currentTimeMillis();
	private static final long ONE_PERCENT_TIME_ESTIMATE = 4200;

	public StarDetailsOverlay(Client client, Star star)
	{
		super();
		this.client = client;
		this.star = star;

		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(PRIORITY_MED);
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

		int miningLevel = (int) Math.floor((double) client.getBoostedSkillLevel(Skill.MINING) / 10);

		panelComponent.getChildren().add(LineComponent.builder()
			.left("Crashed Star")
			.right("Tier " + star.getTier())
			.rightColor(miningLevel >= star.getTier() ? Color.WHITE : Color.RED)
			.build()
		);

		panelComponent.getChildren().add(LineComponent.builder()
			.left("Est. remaining")
			.right(String.format("%01d:%02d", minutes, seconds))
			.build()
		);

		return super.render(graphics);
	}
}
