package com.starcallingassist.modules.worldmapoverlay.worldmapstardetails.ui;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;

import com.starcallingassist.modules.worldmapoverlay.worldmapstardetails.WorldMapStarDetailsOverlay;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Menu;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;

/**
 * A simple button element - used in the {@link WorldMapStarDetailsOverlay}.
 */
@Slf4j
public class StarDetailsButton extends StarDetailsUIElement
{

	private final BufferedImage normalSprite;
	private final BufferedImage hoverSprite;

	private final String menuOptionText;

	private final Runnable onClick;

	public StarDetailsButton(int x, int y, int width, int height, BufferedImage normalSprite, BufferedImage hoverSprite,
							 String menuOptionText, Runnable onClick)
	{
		super(x, y, width, height, true);

		this.normalSprite = normalSprite;
		this.hoverSprite = hoverSprite;
		this.menuOptionText = menuOptionText;
		this.onClick = onClick;
	}

	public StarDetailsButton(int width, int height, BufferedImage normalSprite, BufferedImage hoverSprite,
							 String menuOptionText, Runnable onClick)
	{
		this(0, 0, width, height, normalSprite, hoverSprite, menuOptionText, onClick);
	}

	@Override
	public void render(Graphics2D graphics2D)
	{
		if (hovering)
		{
			graphics2D.drawImage(hoverSprite, x, y, width, height, null);
		}
		else
		{
			graphics2D.drawImage(normalSprite, x, y, width, height, null);
		}
	}

	@Override
	public void onMousePressed(MouseEvent mouseEvent)
	{
		if (mouseEvent.getButton() == MouseEvent.BUTTON1 && onClick != null)
		{
			onClick.run();
			mouseEvent.consume();
		}
	}

	@Override
	public boolean fillMenuEntries(Menu menu, List<MenuEntry> menuEntries)
	{
		if (menuOptionText != null && hovering)
		{
			menuEntries.add(menu.createMenuEntry(-1).setOption(menuOptionText).setType(MenuAction.RUNELITE_WIDGET));
			return true;
		}

		return false;
	}
}
