package com.starcallingassist.modules.worldmapoverlay.worldmapstardetails;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.MouseAdapter;

/**
 * Custom {@link MouseAdapter} providing mouse event callbacks to the {@link WorldMapStarDetailsOverlay}.
 */
@Slf4j
@Singleton
public class WorldMapStarDetailsOverlayMouseAdapter extends MouseAdapter
{
	@Inject
	private Client client;

	@Inject
	private WorldMapStarDetailsOverlay overlay;

	private Point lastMousePosition = new Point();

	private boolean dragStartedInside = false;
	private boolean dragging = false;


	public WorldMapStarDetailsOverlayMouseAdapter()
	{
	}

	@Override
	public MouseEvent mousePressed(MouseEvent mouseEvent)
	{
		if (!overlay.isVisible())
		{
			return mouseEvent;
		}

		setLastMousePosition(mouseEvent.getPoint());

		if (overlay.getBounds().contains(mouseEvent.getPoint()))
		{
			overlay.mousePressed(mouseEvent);
			mouseEvent.consume();
		}

		return mouseEvent;
	}

	@Override
	public MouseEvent mouseReleased(MouseEvent mouseEvent)
	{
		if (!overlay.isVisible())
		{
			return mouseEvent;
		}

		setLastMousePosition(mouseEvent.getPoint());

		dragging = false;
		dragStartedInside = false;
		overlay.mouseReleased();

		return mouseEvent;
	}

	@Override
	public MouseEvent mouseDragged(MouseEvent mouseEvent)
	{
		if (!overlay.isVisible())
		{
			return mouseEvent;
		}

		setLastMousePosition(mouseEvent.getPoint());

		if (overlay.getBounds().contains(mouseEvent.getPoint()))
		{
			if (!dragging)
			{
				dragStartedInside = true;
			}
		}
		else if (!dragging)
		{
			dragStartedInside = false;
		}

		dragging = true;

		if (dragStartedInside)
		{
			overlay.mouseDragged(mouseEvent);
		}

		return mouseEvent;
	}

	@Override
	public MouseEvent mouseMoved(MouseEvent mouseEvent)
	{
		if (!overlay.isVisible())
		{
			return mouseEvent;
		}

		setLastMousePosition(mouseEvent.getPoint());

		return mouseEvent;
	}

	@Subscribe
	private void onMenuEntryAdded(MenuEntryAdded ignoredMenuEntryAdded)
	{
		if (!overlay.isVisible())
		{
			return;
		}

		if (overlay.getBounds().contains(lastMousePosition))
		{
			List<MenuEntry> newMenuEntries = new ArrayList<>();
			overlay.fillMenuEntries(client.getMenu(), newMenuEntries);
			newMenuEntries.add(client.getMenu().createMenuEntry(-1).setOption("Cancel").setType(MenuAction.RUNELITE));

			client.getMenu().setMenuEntries(newMenuEntries.toArray(new MenuEntry[0]));
		}
	}
	
	private void setLastMousePosition(Point point)
	{
		if (!overlay.isVisible())
		{
			return;
		}

		lastMousePosition = point;
		overlay.updateLastMousePosition(lastMousePosition);
	}
}
