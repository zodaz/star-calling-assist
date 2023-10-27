package com.starcallingassist;

import com.google.inject.Inject;
import com.google.inject.Provides;
import com.starcallingassist.modules.callButton.CallButtonModule;
import com.starcallingassist.modules.sidepanel.SidePanelModule;
import com.starcallingassist.modules.tracker.TrackerModule;
import com.starcallingassist.old.objects.CallSender;
import com.starcallingassist.old.objects.Star;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import lombok.Getter;
import lombok.Setter;
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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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

	@Getter
	@Setter
	private int miners = 0;

	@Getter
	@Setter
	private Star lastCalledStar;

	@Inject
	private CallSender sender;

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
		lastCalledStar = null;

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
		lastCalledStar = null;

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

	public void prepareCall(boolean manual)
	{
		if (Star.getStar() == null)
		{
			if (manual)
			{
				logToChat("Unable to find star.");
			}

			return;
		}

		if (lastCalledStar != null
			&& lastCalledStar.world == Star.getStar().world
			&& lastCalledStar.tier == Star.getStar().tier
			&& lastCalledStar.location.equals(Star.getStar().location)
		)
		{
			if (manual)
			{
				logToChat("This star has already been called.");
			}

			return;
		}

		//Won't automatically call star again if tier decreased and the updateStar option disabled
		if (lastCalledStar != null
			&& lastCalledStar.world == Star.getStar().world
			&& lastCalledStar.location.equals(Star.getStar().location)
			&& lastCalledStar.tier > Star.getStar().tier
			&& !config.updateStar() && !manual
		)
		{
			return;
		}

		String location = Star.getLocationName(Star.getStar().location);
		if (location.equals("unknown"))
		{
			logToChat("Star location is unknown, manual call required.");
			return;
		}

		attemptCall(client.getLocalPlayer().getName(), client.getWorld(), Star.getStar().tier, location);
	}

	public void attemptCall(String username, int world, int tier, String location)
	{
		try
		{
			sender.sendCall(username, world, tier, location, miners, new Callback()
			{
				@Override
				public void onFailure(Call call, IOException e)
				{
					clientThread.invokeLater(() -> {
						logToChat("Unable to post call to " + config.getEndpoint() + ".");
					});

					call.cancel();
				}

				@Override
				public void onResponse(Call call, Response res) throws IOException
				{
					if (res.isSuccessful())
					{
						if (tier > 0)
						{
							lastCalledStar = Star.getStar();
						}

						clientThread.invokeLater(() -> logHighlightedToChat(
							"Successfully posted call: ",
							"W" + world + " T" + tier + " " + location + ((miners == -1 || tier == 0) ? "" : (" " + miners + " Miners"))
						));
					}
					else
					{
						clientThread.invokeLater(() -> logHighlightedToChat(
							"Issue posting call to " + config.getEndpoint() + ": ",
							res.message()
						));
					}

					res.close();
				}
			});
		}
		catch (IllegalArgumentException e)
		{
			clientThread.invokeLater(() -> {
				logHighlightedToChat("Issue posting call to " + config.getEndpoint() + ": ", "Invalid endpoint");
			});
		}
	}
}