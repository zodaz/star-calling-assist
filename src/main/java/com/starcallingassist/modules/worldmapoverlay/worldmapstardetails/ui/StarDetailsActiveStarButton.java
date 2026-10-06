package com.starcallingassist.modules.worldmapoverlay.worldmapstardetails.ui;

import com.starcallingassist.enums.TierImage;
import com.starcallingassist.modules.sidepanel.objects.StarListEntryAttributes;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.starcallingassist.modules.worldmapoverlay.worldmapstardetails.WorldMapStarDetailsOverlay;
import net.runelite.client.ui.FontManager;

/**
 * The custom button element displaying tier and world - used in the {@link WorldMapStarDetailsOverlay}.
 */
public class StarDetailsActiveStarButton extends StarDetailsButton
{
	private static final int TEXT_X_OFFSET = 28;
	private static final int TEXT_Y_OFFSET = 6;
	private static final int PADDING = 2;

	final StarListEntryAttributes starAttributes;

	final BufferedImage tierImage;

	final StarDetailsTextField textField;

	public StarDetailsActiveStarButton(int x, int y, int width, int height, BufferedImage normalSprite,
									   BufferedImage hoverSprite, StarListEntryAttributes starAttributes,
									   Runnable onClick)
	{
		super(x, y, width, height, normalSprite, hoverSprite,
			"Hop to world " + starAttributes.getWorld().getId(), onClick);

		this.starAttributes = starAttributes;

		this.tierImage = TierImage.getByTier(starAttributes.getTier()).getImage();

		textField = new StarDetailsTextField(
			"Tier " + starAttributes.getTier() + " W" + starAttributes.getWorld().getId(),
			x + TEXT_X_OFFSET,
			y + TEXT_Y_OFFSET,
			width - TEXT_X_OFFSET,
			height,
			new Padding(PADDING),
			FontManager.getRunescapeSmallFont(),
			starAttributes.getWorldColor()
		);
	}

	@Override
	public void render(Graphics2D graphics2D)
	{
		super.render(graphics2D);

		Composite composite = graphics2D.getComposite();

		if (hovering)
		{
			graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
		}

		graphics2D.drawImage(tierImage, x, y + 3, tierImage.getWidth(), tierImage.getHeight(), null);

		Color previousColor = graphics2D.getColor();
		graphics2D.setColor(starAttributes.getWorldColor());

		textField.render(graphics2D);

		graphics2D.setColor(previousColor);
		graphics2D.setComposite(composite);
	}
}
