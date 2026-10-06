package com.starcallingassist.modules.worldmapoverlay.worldmapstardetails.ui;

import com.starcallingassist.modules.worldmapoverlay.worldmapstardetails.WorldMapStarDetailsOverlay;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import lombok.Getter;

/**
 * A word wrapping text field - used in the {@link WorldMapStarDetailsOverlay}.
 */
public class StarDetailsTextField extends StarDetailsUIElement
{
	private final int maxHeight;
	private final Padding padding;

	private Color color;
	private Font font;
	private FontMetrics fontMetrics;

	@Getter
	private String text;

	/**
	 * The image containing the text field. The image itself is only re-rendered
	 * when the text or dimensions of the text field change.
	 */
	private BufferedImage renderedText;

	/**
	 * @param text The text to display in this text field.
	 * @param x The X coordinate relative to parent.
	 * @param y The X coordinate relative to parent.
	 * @param width The width of the text field.
	 * @param maxHeight The maximum height of the text field.
	 * @param padding Padding.
	 * @param font The font to be used in this text field.
	 */
	public StarDetailsTextField(String text, int x, int y, int width, int maxHeight, Padding padding, Font font, Color color)
	{
		super(x, y, width, maxHeight, false);

		this.maxHeight = maxHeight;
		this.padding = padding;
		this.color = color;

		setFont(font);
		setText(text);
	}

	@Override
	public void render(final Graphics2D graphics2D)
	{
		if (this.renderedText == null)
		{
			return;
		}

		graphics2D.drawImage(renderedText, x, y, width, height, null);
	}

	/**
	 * Update the text in this text field.
	 *
	 * @param text The new text to display.
	 */
	public void setText(final String text)
	{
		this.text = text;

		renderText();
	}

	/**
	 * Update the color of this text field.
	 *
	 * @param color The new text color.
	 */
	public void setColor(final Color color)
	{
		this.color = color;

		renderText();
	}

	/**
	 * Sets the font to use for this text field. Also sets the {@link FontMetrics} to be used.
	 *
	 * @param font The font to use.
	 */
	private void setFont(final Font font)
	{
		this.font = font;

		if (renderedText == null)
		{
			 renderedText = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		}

		// Retrieve the FontMetrics for this font
		final Graphics2D graphics2D = renderedText.createGraphics();
		fontMetrics = graphics2D.getFontMetrics(font);

		graphics2D.dispose();
	}

	/**
	 * Renders the {@link #text} onto the {@link #renderedText} image according to {@link #width},
	 * {@link #maxHeight}, {@link #font}, {@link #color} and {@link #padding}. This only needs to
	 * be done when the size of the text field or the text itself changes.
	 */
	private void renderText()
	{
		final String[] lines = getWrappedLinesToRender();

		// Vertical space taken up by all lines of text
		height = (padding.top + padding.bottom) + (fontMetrics.getHeight() * lines.length);

		final BufferedImage renderTarget = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		Graphics2D graphics2D = renderTarget.createGraphics();
		graphics2D.setFont(font);
		graphics2D.setColor(color);

		int yPos = padding.top;

		for (String line : lines)
		{
			// Offset the text Y-position to point at font ascent instead of font baseline
			graphics2D.drawString(line, padding.left, yPos + fontMetrics.getAscent());
			yPos += fontMetrics.getHeight();
		}

		renderedText = renderTarget;

		graphics2D.dispose();
	}

	/**
	 * Word wraps {@link #text} into an array of strings based on
	 * {@link #width}, {@link #maxHeight} and {@link #padding}.
	 *
	 * @return An array of strings, each string representing a line that fits within {@link #width}.
	 */
	private String[] getWrappedLinesToRender()
	{
		final String[] words = text.split(" ");
		final StringBuilder wrappedLines = new StringBuilder();

		// Max lines we can have while staying within maxHeight
		final int maxLines = (maxHeight - (padding.top + padding.bottom)) / fontMetrics.getHeight();

		// Width in pixels that remain before reaching width
		int widthRemaining = width - (padding.left + padding.right);
		int currentLine = 1;

		for (final String word : words)
		{
			final int wordWidth = fontMetrics.stringWidth(word);

			if (wordWidth <= widthRemaining)
			{
				// Add next word
				wrappedLines.append(word).append(' ');
				widthRemaining -= wordWidth + fontMetrics.stringWidth(" ");
			}
			else
			{
				// maxLines reached
				if (currentLine >= maxLines)
				{
					break;
				}

				// Wrap to new line
				currentLine++;
				widthRemaining = width - wordWidth;
				wrappedLines.append('\n').append(word).append(' ');
			}
		}

		return wrappedLines.toString().split("\n");
	}
}
