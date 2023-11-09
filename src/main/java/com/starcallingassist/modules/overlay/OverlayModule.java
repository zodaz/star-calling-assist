package com.starcallingassist.modules.overlay;

import com.google.inject.Inject;
import com.starcallingassist.PluginModuleContract;
import com.starcallingassist.StarCallingAssistConfig;
import com.starcallingassist.events.PluginConfigChanged;
import com.starcallingassist.events.WorldStarUpdated;
import com.starcallingassist.objects.Star;
import java.awt.image.BufferedImage;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.ImageUtil;

@Slf4j
public class OverlayModule extends PluginModuleContract
{
	@Inject
	private StarCallingAssistConfig config;

	@Inject
	private WorldMapPointManager worldMapPointManager;

	private Star currentStar = null;

	@Override
	public void startUp()
	{
		updateWorldMapPoint();
	}

	@Override
	public void shutDown()
	{
		worldMapPointManager.removeIf(ActiveStarWorldMapPoint.class::isInstance);
	}

	@Subscribe
	public void onWorldStarUpdated(WorldStarUpdated event)
	{
		currentStar = event.getStar();
		updateWorldMapPoint();
	}

	@Subscribe
	public void onPluginConfigChanged(PluginConfigChanged event)
	{
		if (event.getKey().equals("starOnWorldMap"))
		{
			updateWorldMapPoint();
		}
	}

	private void updateWorldMapPoint()
	{
		worldMapPointManager.removeIf(ActiveStarWorldMapPoint.class::isInstance);
		if (config.starOnWorldMap() && currentStar != null && currentStar.getTier() != null)
		{
			worldMapPointManager.add(new ActiveStarWorldMapPoint(this, currentStar));
		}
	}

	public BufferedImage getStarActiveLocationImage()
	{
		return ImageUtil.loadImageResource(getClass(), "/star_active.png");
	}
}
