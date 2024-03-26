package com.starcallingassist.modules.overlay;

import com.google.inject.Inject;
import com.starcallingassist.PluginModuleContract;
import com.starcallingassist.StarCallingAssistConfig;
import com.starcallingassist.events.PluginConfigChanged;
import com.starcallingassist.events.WorldStarUpdated;
import com.starcallingassist.objects.Star;
import java.awt.image.BufferedImage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.ImageUtil;

public class OverlayModule extends PluginModuleContract
{
	@Inject
	private StarCallingAssistConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private WorldMapPointManager worldMapPointManager;

	private Star currentStar = null;

	@Override
	public void shutDown()
	{
		worldMapPointManager.removeIf(ActiveStarWorldMapPoint.class::isInstance);
		overlayManager.removeIf(StarDepletionEstimationOverlay.class::isInstance);
		currentStar = null;
	}

	@Subscribe
	public void onWorldStarUpdated(WorldStarUpdated event)
	{
		currentStar = event.getStar();

		updateWorldMapPoint();
		updateTierDepletionEstimationOverlay();
	}

	@Subscribe
	public void onPluginConfigChanged(PluginConfigChanged event)
	{
		if (event.getKey().equals("starOnWorldMap"))
		{
			updateWorldMapPoint();
		}

		if (event.getKey().equals("tierDepletionEstimation"))
		{
			updateTierDepletionEstimationOverlay();
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

	private void updateTierDepletionEstimationOverlay()
	{
		overlayManager.removeIf(StarDepletionEstimationOverlay.class::isInstance);
		if (config.tierDepletionEstimation() && currentStar != null && currentStar.getTier() != null)
		{
			overlayManager.add(new StarDepletionEstimationOverlay(currentStar));
		}
	}

	public BufferedImage getStarActiveLocationImage()
	{
		return ImageUtil.loadImageResource(getClass(), "/star_active.png");
	}
}
