package com.starcallingassist.modules.shortestpath;

import com.google.inject.Inject;
import com.starcallingassist.PluginModuleContract;
import com.starcallingassist.events.RouteViaShortestPathRequested;
import com.starcallingassist.events.ShowWorldPointOnWorldMapRequested;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.PluginMessage;

import java.util.HashMap;
import java.util.Map;

public class ShortestPathModule extends PluginModuleContract
{
    @Inject
    private EventBus eventBus;

    @Subscribe
    public void onRouteViaShortestPathRequested(RouteViaShortestPathRequested routeViaShortestPathRequested)
    {
        Map<String, Object> data = new HashMap<>();
        data.put("target", routeViaShortestPathRequested.getTarget());
        eventBus.post(new PluginMessage("shortestpath", "path", data));
    }
}
