package com.starcallingassist;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.starcallingassist.events.PluginConfigChanged;
import com.starcallingassist.modules.callButton.CallButtonModule;
import com.starcallingassist.modules.crowdsourcing.AnnouncementModule;
import com.starcallingassist.modules.crowdsourcing.BroadcastModule;
import com.starcallingassist.modules.logging.ChatLoggerModule;
import com.starcallingassist.modules.overlaypanel.OverlayPanelModule;
import com.starcallingassist.modules.scout.ScoutModule;
import com.starcallingassist.modules.shortestpath.ShortestPathModule;
import com.starcallingassist.modules.sidepanel.SidePanelModule;
import com.starcallingassist.modules.spriteutil.SpriteUtilModule;
import com.starcallingassist.modules.starobserver.StarObserverModule;
import com.starcallingassist.modules.worldhop.WorldHopModule;
import com.starcallingassist.modules.worldmap.WorldMapModule;
import com.starcallingassist.modules.worldmapoverlay.WorldMapOverlayModule;
import java.lang.reflect.InvocationTargetException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.task.Schedule;

@PluginDescriptor(
	name = "Star Miners",
	description = "Displays a list of active stars and crowdsources data about stars you find and mine",
	tags = {"star", "shooting", "shootingstar", "meteor", "crowdsource", "crowdsourcing"}
)
@Slf4j
public class StarCallingAssistPlugin extends Plugin
{
	private static final ArrayList<Class<? extends PluginModuleContract>> MODULES = new ArrayList<>(Arrays.asList(
		AnnouncementModule.class,
		BroadcastModule.class,
		CallButtonModule.class,
		ChatLoggerModule.class,
		OverlayPanelModule.class,
		ScoutModule.class,
		SidePanelModule.class,
		SpriteUtilModule.class,
		StarObserverModule.class,
		WorldHopModule.class,
		WorldMapModule.class,
		WorldMapOverlayModule.class,
		ShortestPathModule.class
	));

	@Getter
	@Inject
	private StarCallingAssistConfig config;

	@Provides
	protected StarCallingAssistConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(StarCallingAssistConfig.class);
	}

	@Inject
	private EventBus eventBus;

	private final HashMap<Class<? extends PluginModuleContract>, PluginModuleContract> registeredModules = new HashMap<>();

	private int secondsElapsed = 1;

	protected <T extends PluginModuleContract> void registerModule(Class<T> className)
	{
		if (this.registeredModules.containsKey(className))
		{
			return;
		}

		final T module;

		try
		{
			module = className.getDeclaredConstructor().newInstance();
		}
		catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
		{
			log.error("Error registering module: ", e);
			return;
		}

		injector.injectMembers(module);
		module.setInjector(injector);

		this.registeredModules.put(className, module);
	}

	@Override
	protected void startUp()
	{
		for (final Class<? extends PluginModuleContract> module : MODULES)
		{
			this.registerModule(module);
		}

		for (final PluginModuleContract module : this.registeredModules.values())
		{
			eventBus.register(module);
			module.startUp();
		}
	}

	@Override
	protected void shutDown()
	{
		for (final PluginModuleContract module : this.registeredModules.values())
		{
			eventBus.unregister(module);
			module.shutDown();
		}

		secondsElapsed = 1;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals(StarCallingAssistConfig.CONFIG_GROUP))
		{
			eventBus.post(PluginConfigChanged.fromRuneLiteEvent(event));
		}
	}

	@Schedule(
		period = 1,
		unit = ChronoUnit.SECONDS
	)
	public void everySecondTick()
	{
		for (final PluginModuleContract module : this.registeredModules.values())
		{
			module.onSecondElapsed(secondsElapsed);
		}

		secondsElapsed++;
	}
}
