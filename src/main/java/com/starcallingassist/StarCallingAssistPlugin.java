package com.starcallingassist;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.starcallingassist.events.PluginConfigChanged;
import com.starcallingassist.modules.callButton.CallButtonModule;
import com.starcallingassist.modules.crowdsourcing.AnnouncementModule;
import com.starcallingassist.modules.crowdsourcing.BroadcastModule;
import com.starcallingassist.modules.logging.ChatLoggerModule;
import com.starcallingassist.modules.overlay.OverlayModule;
import com.starcallingassist.modules.scout.ScoutModule;
import com.starcallingassist.modules.shortestpath.ShortestPathModule;
import com.starcallingassist.modules.sidepanel.SidePanelModule;
import com.starcallingassist.modules.starobserver.StarObserverModule;
import com.starcallingassist.modules.worldhop.WorldHopModule;
import com.starcallingassist.modules.worldmap.WorldMapModule;
import java.lang.reflect.InvocationTargetException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import lombok.Getter;
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

public class StarCallingAssistPlugin extends Plugin
{
	@Getter
	@Inject
	private StarCallingAssistConfig config;

	@Provides
	protected StarCallingAssistConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(StarCallingAssistConfig.class);
	}

	private final ArrayList<Class<? extends PluginModuleContract>> modules = new ArrayList<>(Arrays.asList(
		CallButtonModule.class,
		BroadcastModule.class,
		AnnouncementModule.class,
		OverlayModule.class,
		ChatLoggerModule.class,
		ScoutModule.class,
		SidePanelModule.class,
		StarObserverModule.class,
		WorldHopModule.class,
		WorldMapModule.class,
		ShortestPathModule.class
	));

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

		T module;

		try
		{
			module = className.getDeclaredConstructor().newInstance();
		}
		catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
		{
			e.printStackTrace();
			return;
		}

		injector.injectMembers(module);
		module.setInjector(injector);

		this.registeredModules.put(className, module);
	}

	@Override
	protected void startUp()
	{
		for (Class<? extends PluginModuleContract> module : modules)
		{
			this.registerModule(module);
		}

		for (PluginModuleContract module : this.registeredModules.values())
		{
			eventBus.register(module);
			module.startUp();
		}
	}

	@Override
	protected void shutDown()
	{
		for (PluginModuleContract module : this.registeredModules.values())
		{
			eventBus.unregister(module);
			module.shutDown();
		}

		secondsElapsed = 1;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		Class<?> inter = config.getClass().getInterfaces()[0];
		ConfigGroup group = inter.getAnnotation(ConfigGroup.class);

		if (group != null && event.getGroup().equals(group.value()))
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
		for (PluginModuleContract module : this.registeredModules.values())
		{
			module.onSecondElapsed(secondsElapsed);
		}

		secondsElapsed++;
	}
}