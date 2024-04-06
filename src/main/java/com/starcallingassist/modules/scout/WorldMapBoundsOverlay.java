package com.starcallingassist.modules.scout;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.worldmap.WorldMap;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

class WorldMapBoundsOverlay extends Overlay
{
	@Inject
	private Client client;

	private final ScoutModule module;

	@Inject
	WorldMapBoundsOverlay(ScoutModule module)
	{
		this.module = module;

		setPosition(OverlayPosition.DYNAMIC);
		setPriority(Overlay.PRIORITY_HIGH);
		setLayer(OverlayLayer.MANUAL);
		drawAfterInterface(InterfaceID.WORLD_MAP);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		drawRegionOverlay(graphics);

		return null;
	}

	private void drawRegionOverlay(Graphics2D graphics)
	{
		Widget map = client.getWidget(ComponentID.WORLD_MAP_MAPVIEW);
		if (map == null)
		{
			return;
		}

		Rectangle worldMapRect = map.getBounds();
		graphics.setClip(worldMapRect);

		module.getLocations().forEach((location, state) -> {
			WorldArea bounds = location.getScoutableBounds();
			if (bounds == null)
			{
				return;
			}

			Point topLeft = mapWorldPointToGraphicsPoint(new WorldPoint(bounds.getX(), bounds.getY(), 0));
			Point topRight = mapWorldPointToGraphicsPoint(new WorldPoint(bounds.getX() + bounds.getWidth(), bounds.getY(), 0));
			Point bottomLeft = mapWorldPointToGraphicsPoint(new WorldPoint(bounds.getX(), bounds.getY() + bounds.getHeight(), 0));
			Point bottomRight = mapWorldPointToGraphicsPoint(new WorldPoint(bounds.getX() + bounds.getWidth(), bounds.getY() + bounds.getHeight(), 0));

			if (topLeft == null && topRight == null && bottomLeft == null && bottomRight == null)
			{
				return;
			}

			int mapRectX = (int) worldMapRect.getX();
			int mapRectY = (int) worldMapRect.getY();
			int mapRectWidth = (int) worldMapRect.getWidth();
			int mapRectHeight = (int) worldMapRect.getHeight();

			topLeft = topLeft == null ? new Point(mapRectX, mapRectY) : topLeft;
			topRight = topRight == null ? new Point(mapRectX + mapRectWidth, mapRectY) : topRight;
			bottomLeft = bottomLeft == null ? new Point(mapRectX, mapRectY + mapRectHeight) : bottomLeft;
			bottomRight = bottomRight == null ? new Point(mapRectX + mapRectWidth, mapRectY + mapRectHeight) : bottomRight;

			Color stateColor = state.getColor();
			graphics.setColor(new Color(stateColor.getRed(), stateColor.getGreen(), stateColor.getBlue(), (50 * 255 / 100)));
			graphics.fillRect(
				bottomLeft.getX(),
				bottomLeft.getY(),
				topRight.getX() - topLeft.getX(),
				topRight.getY() - bottomLeft.getY()
			);

			graphics.setColor(stateColor);
			graphics.setStroke(new BasicStroke(2));
			graphics.drawLine(topLeft.getX(), topLeft.getY(), topRight.getX(), topRight.getY());
			graphics.drawLine(topLeft.getX(), topLeft.getY(), bottomLeft.getX(), bottomLeft.getY());
			graphics.drawLine(bottomRight.getX(), bottomRight.getY(), topRight.getX(), topRight.getY());
			graphics.drawLine(bottomRight.getX(), bottomRight.getY(), bottomLeft.getX(), bottomLeft.getY());
		});
	}

	public Point mapWorldPointToGraphicsPoint(WorldPoint worldPoint)
	{
		WorldMap worldMap = client.getWorldMap();

		if (!worldMap.getWorldMapData().surfaceContainsPosition(worldPoint.getX(), worldPoint.getY()))
		{
			return null;
		}

		float pixelsPerTile = worldMap.getWorldMapZoom();

		Widget map = client.getWidget(ComponentID.WORLD_MAP_MAPVIEW);
		if (map != null)
		{
			Rectangle worldMapRect = map.getBounds();

			int widthInTiles = (int) Math.ceil(worldMapRect.getWidth() / pixelsPerTile);
			int heightInTiles = (int) Math.ceil(worldMapRect.getHeight() / pixelsPerTile);

			Point worldMapPosition = worldMap.getWorldMapPosition();

			//Offset in tiles from anchor sides
			int yTileMax = worldMapPosition.getY() - heightInTiles / 2;
			int yTileOffset = (yTileMax - worldPoint.getY() - 1) * -1;
			int xTileOffset = worldPoint.getX() + widthInTiles / 2 - worldMapPosition.getX();

			int xGraphDiff = ((int) (xTileOffset * pixelsPerTile));
			int yGraphDiff = (int) (yTileOffset * pixelsPerTile);

			//Center on tile.
			yGraphDiff -= pixelsPerTile - Math.ceil(pixelsPerTile / 2);
			xGraphDiff += pixelsPerTile - Math.ceil(pixelsPerTile / 2);

			yGraphDiff = worldMapRect.height - yGraphDiff;
			yGraphDiff += (int) worldMapRect.getY();
			xGraphDiff += (int) worldMapRect.getX();

			return new Point(xGraphDiff, yGraphDiff);
		}

		return null;
	}
}
