package com.starcallingassist.modules.overlaypanel;

import com.google.inject.Inject;
import com.starcallingassist.PluginModuleContract;
import com.starcallingassist.StarCallingAssistConfig;
import com.starcallingassist.events.PluginConfigChanged;
import com.starcallingassist.events.CurrentWorldStarUpdated;
import com.starcallingassist.objects.Star;
import net.runelite.api.Client;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.overlay.OverlayManager;

public class OverlayPanelModule extends PluginModuleContract
{
	@Inject
	private StarCallingAssistConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private Client client;

	private Star currentStar = null;

	@Override
	public void shutDown()
	{
		overlayManager.removeIf(StarDetailsOverlayPanel.class::isInstance);
		currentStar = null;
	}

	@Subscribe
	public void onCurrentWorldStarUpdated(CurrentWorldStarUpdated event)
	{
		currentStar = event.getStar();

		updateStarDetailsOverlay();
	}

	@Subscribe
	public void onPluginConfigChanged(PluginConfigChanged event)
	{
		if (event.getKey().equals("starDetailsOverlay"))
		{
			updateStarDetailsOverlay();
		}
	}

	private void updateStarDetailsOverlay()
	{
		overlayManager.removeIf(StarDetailsOverlayPanel.class::isInstance);
		if (config.starDetailsOverlay() && currentStar != null && currentStar.getTier() != null)
		{
			overlayManager.add(new StarDetailsOverlayPanel(client, currentStar));
		}
	}
}
