package com.starcallingassist.modules.worldmapoverlay;

import com.starcallingassist.enums.StarLocationDetails;
import com.starcallingassist.enums.TierImage;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.worldmap.WorldMapPoint;


@Getter
@Slf4j
public class StarLocationWorldMapPoint extends WorldMapPoint
{
	private static final int STAR_COUNT_CIRCLE_OFFSET = 2;
	private static final int STAR_COUNT_CIRCLE_DIAMETER = 14;
	private static final int STAR_COUNT_CIRCLE_BORDER_DIAMETER = 18;

	// Not possible to center single digits in a "visually pleasing" manner
	// with an algorithm utilizing FontMetrics character width's for this font.
	/**
	 * X offset of each digit in the star count display. Index = digit.
	 */
	private static final int[] STAR_COUNT_DIGIT_X_OFFSET =  {
		0, // 0
		3, // 1
		2, // 2
		3, // 3
		4, // 4
		3, // 5
		3, // 6
		3, // 7
		2, // 8
		2  // 9
	};

	@Setter
	private boolean visible;

	private int tier;
	private int starCount;

	private final StarLocationDetails starLocationDetails;

	public StarLocationWorldMapPoint(StarLocationDetails starLocationDetails)
	{
		super(starLocationDetails.getWorldPoint(), createImage(0, 0, false));
		this.starLocationDetails = starLocationDetails;

		this.setJumpOnClick(true);
		this.setName(starLocationDetails.getName());
	}

	/**
	 * Update the image of this WorldMapPoint on the world map.
	 *
	 * @param tier        The tier of star to use the image from.
	 * @param starCount   Amount of active stars at this location.
	 * @param highlighted Whether the star should be highlighted.
	 */
	public void updateImage(int tier, int starCount, boolean highlighted)
	{
		this.tier = tier;
		this.starCount = Math.max(0, Math.min(9, starCount));

		setImage(createImage(tier, starCount, highlighted));
	}

	/**
	 * Creates The image to be used by the StarLocationWorldMapPoint
	 *
	 * @param tier        The tier of star to use the image from.
	 * @param starCount   Amount of active stars at this location.
	 * @param highlighted Whether the star should be highlighted.
	 */
	private static BufferedImage createImage(int tier, int starCount, boolean highlighted)
	{
		final BufferedImage tierImage = TierImage.getByTier(tier).getImage();

		if (tierImage == null)
		{
			return null;
		}

		// Create a new image on which to make our modifications
		final BufferedImage displayImage = new BufferedImage(tierImage.getWidth(), tierImage.getHeight(), tierImage.getType());
		Graphics2D graphics2D = displayImage.createGraphics();

		if (starCount == 0)
		{
			graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
			graphics2D.drawImage(tierImage, 0, 0, null);

			// Add a yellow "tint" to the star image if highlighted.
			if (highlighted)
			{
				graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.3f));
				graphics2D.setColor(Color.YELLOW);
				graphics2D.fillRect(0, 0, tierImage.getWidth(), tierImage.getHeight());
			}
		}
		else
		{
			final String starCountString = Integer.toString(starCount);

			graphics2D.drawImage(tierImage, 0, 0, null);

			// Add a yellow "tint" to the star image if highlighted.
			if (highlighted)
			{
				graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.3f));
				graphics2D.setColor(Color.YELLOW);
				graphics2D.fillRect(0, 0, tierImage.getWidth(), tierImage.getHeight());
				graphics2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			}

			graphics2D.setColor(Color.BLACK);
			graphics2D.fillOval(0, 0, STAR_COUNT_CIRCLE_BORDER_DIAMETER, STAR_COUNT_CIRCLE_BORDER_DIAMETER);

			graphics2D.setColor(Color.YELLOW);
			graphics2D.fillOval(STAR_COUNT_CIRCLE_OFFSET, STAR_COUNT_CIRCLE_OFFSET, STAR_COUNT_CIRCLE_DIAMETER, STAR_COUNT_CIRCLE_DIAMETER);

			graphics2D.setColor(Color.BLACK);
			graphics2D.setFont(FontManager.getRunescapeBoldFont());

			graphics2D.drawString(starCountString,STAR_COUNT_CIRCLE_OFFSET + STAR_COUNT_DIGIT_X_OFFSET[starCount], STAR_COUNT_CIRCLE_OFFSET + graphics2D.getFontMetrics().getAscent());
		}

		graphics2D.dispose();

		return displayImage;
	}
}
