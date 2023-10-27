package com.starcallingassist.modules.sidepanel.elements;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Link extends JLabel
{
	protected final String href;
	protected final String label;

	public Link(String href, String label)
	{
		this.href = href;
		this.label = label;

		setCursor(new Cursor(Cursor.HAND_CURSOR));
		setOpaque(false);
		setText(getDefaultState());

		addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				try
				{
					Desktop.getDesktop().browse(new URI(href));
				}
				catch (Exception ex)
				{
					log.error(ex.getMessage());
				}
			}

			@Override
			public void mouseEntered(MouseEvent e)
			{
				setText(getHoverState());
			}

			@Override
			public void mouseExited(MouseEvent e)
			{
				setText(getDefaultState());
			}
		});
	}

	public Link center()
	{
		setHorizontalAlignment(SwingConstants.CENTER);

		return this;
	}

	protected String buildHref(String color)
	{
		return "<html><a href='" + href + "' style='color: " + color + "'>" + label + "</a></html>";
	}

	protected String getDefaultState()
	{
		return buildHref("#4287f5");
	}

	protected String getHoverState()
	{
		return buildHref("orange");
	}
}

