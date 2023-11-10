package com.starcallingassist.modules.scout;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import static net.runelite.api.Perspective.LOCAL_TILE_SIZE;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

public class ScoutWorldOverlay extends Overlay
{
	@Inject
	private Client client;

	private final ScoutModule module;

	@Inject
	public ScoutWorldOverlay(ScoutModule module)
	{
		super();
		this.module = module;

		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();

		module.getLocations().forEach((location, state) -> {
			if (location.getWorldPoint().distanceTo(playerLocation) >= Perspective.SCENE_SIZE)
			{
				return;
			}

			graphics.setStroke(new BasicStroke(2));
			graphics.setColor(state.getColor());

			renderScoutableBounds(graphics, location.getScoutableBounds());
		});

		return null;
	}

	private void renderScoutableBounds(Graphics2D graphics, WorldArea bounds)
	{
		int sceneMinX = client.getBaseX();
		int sceneMinY = client.getBaseY();
		int sceneMaxX = sceneMinX + Perspective.SCENE_SIZE - 1;
		int sceneMaxY = sceneMinY + Perspective.SCENE_SIZE - 1;

		GeneralPath path = new GeneralPath();
		for (int x = 0; x <= bounds.getWidth(); x += bounds.getWidth())
		{
			int sceneX = Math.max(sceneMinX, Math.min(sceneMaxX, bounds.getX() + x));
			if (sceneX <= sceneMinX || sceneX >= sceneMaxX)
			{
				// We'll want to ignore rendering lines at the very bounds of the screen
				// as these don't represent the actual chunk borders. Instead, we'll
				// just leave them open, indicating to the player that the line
				// probably continues in the other area.
				continue;
			}

			int sceneStartY = Math.min(sceneMaxY, Math.max(sceneMinY, bounds.getY()));
			int sceneEndY = Math.min(sceneMaxY, Math.max(sceneMinY, bounds.getY() + bounds.getHeight()));

			LocalPoint lp1 = LocalPoint.fromWorld(client, sceneX, sceneStartY);
			LocalPoint lp2 = LocalPoint.fromWorld(client, sceneX, sceneEndY);

			boolean first = true;
			for (int y = lp1.getY(); y <= lp2.getY(); y += LOCAL_TILE_SIZE)
			{
				if (y == sceneMinY || y == sceneMaxY)
				{
					continue;
				}

				Point p = Perspective.localToCanvas(client,
					new LocalPoint(lp1.getX() - LOCAL_TILE_SIZE / 2, y - LOCAL_TILE_SIZE / 2),
					client.getPlane()
				);

				if (p != null)
				{
					if (first)
					{
						path.moveTo(p.getX(), p.getY());
						first = false;
					}
					else
					{
						path.lineTo(p.getX(), p.getY());
					}
				}
			}
		}

		for (int y = 0; y <= bounds.getHeight(); y += bounds.getHeight())
		{
			int sceneY = Math.max(sceneMinY, Math.min(sceneMaxY, bounds.getY() + y));
			if (sceneY <= sceneMinY || sceneY >= sceneMaxY)
			{
				// We'll want to ignore rendering lines at the very bounds of the screen
				// as these don't represent the actual chunk borders. Instead, we'll
				// just leave them open, indicating to the player that the line
				// probably continues in the other area.
				continue;
			}

			int sceneStartX = Math.min(sceneMaxX, Math.max(sceneMinX, bounds.getX()));
			int sceneEndX = Math.min(sceneMaxX, Math.max(sceneMinX, bounds.getX() + bounds.getWidth()));

			LocalPoint lp1 = LocalPoint.fromWorld(client, sceneStartX, sceneY);
			LocalPoint lp2 = LocalPoint.fromWorld(client, sceneEndX, sceneY);

			boolean first = true;
			for (int x = lp1.getX(); x <= lp2.getX(); x += LOCAL_TILE_SIZE)
			{
				Point p = Perspective.localToCanvas(client,
					new LocalPoint(x - LOCAL_TILE_SIZE / 2, lp1.getY() - LOCAL_TILE_SIZE / 2),
					client.getPlane()
				);

				if (p != null)
				{
					if (first)
					{
						path.moveTo(p.getX(), p.getY());
						first = false;
					}
					else
					{
						path.lineTo(p.getX(), p.getY());
					}
				}
			}
		}

		graphics.draw(path);
	}
}
