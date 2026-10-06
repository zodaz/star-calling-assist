package com.starcallingassist.modules.worldmapoverlay.worldmapstardetails.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.starcallingassist.modules.worldmapoverlay.worldmapstardetails.WorldMapStarDetailsOverlay;
import lombok.Getter;
import net.runelite.client.ui.FontManager;

/**
 * An icon with a word wrapping text field - used in the {@link WorldMapStarDetailsOverlay}.
 */
public class StarDetailsIconTextField extends StarDetailsUIElement
{

	private static final int DEFAULT_ICON_SIZE = 16;
	private static final int ICON_MARGIN_BOTTOM = 2;

	@Getter
	private final StarDetailsTextField textField;

	private final BufferedImage icon;
	private final Padding padding;

	private int iconWidth = DEFAULT_ICON_SIZE;
	private int iconHeight = DEFAULT_ICON_SIZE;

	public StarDetailsIconTextField(BufferedImage icon, String text, int x, int y, int width,
									int maxHeight, Padding padding, Padding textPadding, Color textColor)
	{
		super(x, y, width, maxHeight, false);

		this.icon = icon;
		this.padding = padding;

		if (icon != null)
		{
			iconWidth = icon.getWidth();
			iconHeight = icon.getHeight();
		}

		textField = new StarDetailsTextField(
			text,
			x + DEFAULT_ICON_SIZE + padding.left,
			y + padding.top,
			width - DEFAULT_ICON_SIZE - padding.left - padding.right,
			maxHeight,
			textPadding,
			FontManager.getRunescapeSmallFont(),
			textColor
		);

		height = Math.max(padding.top + textField.getHeight(), padding.top + iconHeight + padding.bottom);
	}

	/**
	 * Renders the UI element at the current parent-relative X and Y coordinates.
	 *
	 * @param graphics2D The {@link Graphics2D} instance to draw to.
	 */
	@Override
	public void render(Graphics2D graphics2D)
	{
		graphics2D.drawImage(icon, x + padding.left, y + padding.top, iconWidth, iconHeight, null);
		textField.render(graphics2D);
	}
}
