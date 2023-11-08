package com.starcallingassist.modules.startracker;

import com.starcallingassist.PluginModuleContract;
import com.starcallingassist.StarCallingAssistConfig;
import com.starcallingassist.events.AnnouncementReceived;
import com.starcallingassist.events.PluginConfigChanged;
import com.starcallingassist.events.StarDepleted;
import com.starcallingassist.events.StarTierChanged;
import com.starcallingassist.objects.Star;
import java.awt.image.BufferedImage;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.WorldChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.worldmap.WorldMapPointManager;
import net.runelite.client.util.ImageUtil;

public class StarTrackerModule extends PluginModuleContract
{
	@Inject
	private StarCallingAssistConfig config;

	@Inject
	private Client client;

	@Inject
	private WorldMapPointManager worldMapPointManager;

	public final ConcurrentHashMap<Integer, Star> stars = new ConcurrentHashMap<>();

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
	public void onPluginConfigChanged(PluginConfigChanged event)
	{
		if (event.getKey().equals("starOnWorldMap"))
		{
			updateWorldMapPoint();
		}
	}

	@Subscribe
	public void onAnnouncementReceived(AnnouncementReceived event)
	{
		Star star = event.getAnnouncement().getStar();
		
		if (star.getTier() != null)
		{
			stars.put(star.getWorld(), star);
		}
		else
		{
			stars.remove(star.getWorld());
		}

		updateWorldMapPoint();
	}

	@Subscribe
	public void onStarTierChanged(StarTierChanged event)
	{
		stars.put(event.getStar().getWorld(), event.getStar());
		updateWorldMapPoint();
	}

	@Subscribe
	public void onStarDepleted(StarDepleted event)
	{
		stars.remove(event.getStar().getWorld());
		updateWorldMapPoint();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged state)
	{
		if (state.getGameState() == GameState.LOGGED_IN)
		{
			updateWorldMapPoint();
		}
	}

	@Subscribe
	public void onWorldChanged(WorldChanged event)
	{
		updateWorldMapPoint();
	}

	private void updateWorldMapPoint()
	{
		worldMapPointManager.removeIf(ActiveStarWorldMapPoint.class::isInstance);

		Star worldStar = stars.get(client.getWorld());
		if (config.starOnWorldMap() && worldStar != null && worldStar.getTier() != null)
		{
			worldMapPointManager.add(new ActiveStarWorldMapPoint(this, worldStar));
		}
	}

	public BufferedImage getStarActiveLocationImage()
	{
		return ImageUtil.loadImageResource(getClass(), "/star_active.png");
	}
}
