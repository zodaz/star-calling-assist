package com.starcallingassist;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.starcallingassist.modules.callButton.CallButtonModule;
import com.starcallingassist.modules.sidepanel.SidePanelModule;
import com.starcallingassist.modules.tracker.TrackerModule;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@Slf4j
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

	@Inject
	protected EventBus eventBus;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	protected final HashMap<Class<? extends StarModuleContract>, StarModuleContract> modules = new HashMap<>();

	@Provides
	protected StarCallingAssistConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(StarCallingAssistConfig.class);
	}

	protected <T extends StarModuleContract> void registerModule(Class<T> className)
	{
		if (this.modules.containsKey(className))
		{
			return;
		}

		T module;

		try
		{
			module = className.getDeclaredConstructor().newInstance();
		}
		catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
			   InvocationTargetException e)
		{
			e.printStackTrace();
			return;
		}

		injector.injectMembers(module);
		module.setInjector(injector);
		module.setConfig(config);
		module.setPlugin(this);

		this.modules.put(className, module);
	}

	@Override
	protected void startUp()
	{
		this.registerModule(CallButtonModule.class);
		this.registerModule(SidePanelModule.class);
		this.registerModule(TrackerModule.class);

		for (StarModuleContract module : this.modules.values())
		{
			eventBus.register(module);
			module.startUp();
		}
	}

	@Override
	protected void shutDown()
	{
		for (StarModuleContract module : this.modules.values())
		{
			eventBus.unregister(module);
			module.shutDown();
		}
	}

	public void logHighlightedToChat(String normal, String highlight)
	{
		if (config.chatMessages())
		{
			String chatMessage = new ChatMessageBuilder()
				.append(ChatColorType.NORMAL)
				.append(normal)
				.append(ChatColorType.HIGHLIGHT)
				.append(highlight)
				.build();

			chatMessageManager.queue(
				QueuedMessage.builder()
					.type(ChatMessageType.CONSOLE)
					.runeLiteFormattedMessage(chatMessage)
					.build()
			);
		}
	}

	public void logToChat(String message)
	{
		if (config.chatMessages())
		{
			client.addChatMessage(ChatMessageType.CONSOLE, "", message, "");
		}
	}
}