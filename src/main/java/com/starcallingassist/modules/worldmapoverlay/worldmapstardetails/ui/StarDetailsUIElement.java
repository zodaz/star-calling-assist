package com.starcallingassist.modules.worldmapoverlay.worldmapstardetails.ui;

import com.starcallingassist.modules.worldmapoverlay.worldmapstardetails.WorldMapStarDetailsOverlay;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.List;
import lombok.Getter;
import net.runelite.api.Menu;
import net.runelite.api.MenuEntry;

/**
 * Base class for various UI elements used in the {@link WorldMapStarDetailsOverlay}.
 */
@Getter
public abstract class StarDetailsUIElement
{
	/**
	 * The X coordinate of this UI element relative to parent.
	 */
	protected int x;

	/**
	 * The Y coordinate of this UI element relative to parent.
	 */
	protected int y;

	protected int width;
	protected int height;

	protected boolean hovering;

	/**
	 * Whether dragging of the overlay should be blocked if dragging is starting on this element.
	 */
	protected final boolean blockDragging;

	/**
	 * @param x             The X coordinate relative to parent.
	 * @param y             The X coordinate relative to parent.
	 * @param width         Width of this UI element.
	 * @param height        Height of this UI element.
	 * @param blockDragging Whether dragging of the overlay should be blocked if dragging is starting on this element.
	 */
	StarDetailsUIElement(int x, int y, int width, int height, boolean blockDragging)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.blockDragging = blockDragging;
	}

	/**
	 * Renders the UI element at the current parent-relative X and Y coordinates.
	 *
	 * @param graphics2D The {@link Graphics2D} instance to draw to.
	 */
	public abstract void render(Graphics2D graphics2D);

	/**
	 * Renders the UI element at the provided parent-relative X and Y coordinates.
	 * Updates the X and Y coordinates for this UI element.
	 *
	 * @param x          The X coordinate relative to parent.
	 * @param y	         The Y coordinate relative to parent.
	 * @param graphics2D The {@link Graphics2D} instance to draw to.
	 */
	public void render(int x, int y, Graphics2D graphics2D)
	{
		this.x = x;
		this.y = y;

		render(graphics2D);
	}

	public boolean fillMenuEntries(Menu menu, List<MenuEntry> menuEntries)
	{
		return false;
	}

	/**
	 * Updates the position of this UI element. <p><b>Note:</b> The position is overriden on render
	 * if the UI element is rendered using {@link #render(int, int, Graphics2D)}.</p>
	 *
	 * @param x The X coordinate relative to parent.
	 * @param y The Y coordinate relative to parent.
	 */
	public void setPosition(int x, int y)
	{
		this.x = x;
		this.y = y;
	}

	/**
	 * Local bounds of this UI element.
	 *
	 * @return The local bounds of this UI element.
	 */
	public Rectangle getLocalBounds()
	{
		return new Rectangle(x, y, width, height);
	}

	/**
	 * Bounds in parent space.
	 *
	 * @param parentPosition Position of the parent of this UI element.
	 * @return               The bounds translated to parent space.
	 */
	public Rectangle getBoundsInParentCoordinates(Point parentPosition)
	{
		return new Rectangle(x + parentPosition.x, y + parentPosition.y, width, height);
	}

	/**
	 * Callback to update the most recently known mouse position.
	 * Used to determine if this element is being hovered.
	 *
	 * @param localMousePosition The current mouse position in local space.
	 */
	public void updateMousePosition(Point localMousePosition)
	{
		hovering = this.getLocalBounds().contains(localMousePosition);
	}

	/**
	 * Callback for mouse pressed events. Used to determine if the event occurred within the bounds of this element.
	 *
	 * @param mouseEvent The mouse event.
	 */
	public void mousePressed(MouseEvent mouseEvent)
	{
		if (hovering)
		{
			onMousePressed(mouseEvent);
		}
	}

	/**
	 * Callback for mouse pressed events that occur within the bounds of this element.
	 *
	 * @param mouseEvent The mouse event.
	 */
	protected void onMousePressed(MouseEvent mouseEvent) { }
}
