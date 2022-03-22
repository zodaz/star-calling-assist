package com.starcallingassist;

import com.google.inject.Provides;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.SpriteID;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ResizeableChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.awt.*;
import java.io.IOException;

@PluginDescriptor(
	name = "Star Calling Assist",
	description = "Assists with calling crashed stars found around the game",
	enabledByDefault = false
)
public class StarCallingAssistPlugin extends Plugin
{

    private static final int[] TIER_IDS = new int[]{41229,41228,41227,41226,41225,41224,41223,41021,41020};
    private static final Point BUTTON_RESIZEABLE_LOCATION = new Point(130, 150);
    private static final Point BUTTON_FIXED_LOCATION = new Point(208, 55);

    private Widget parent, callButton;
    private Star lastCalledStar;
    private boolean autoCall, chatLogging, updateStar;

    @Inject
    private ChatMessageManager chatMessageManager;
    @Inject
    private StarCallingAssistConfig starConfig;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private CallSender sender;
    @Inject
    private OverlayManager overlayManager;

    @Provides StarCallingAssistConfig
    provideConfig(ConfigManager configManager) {return configManager.getConfig(StarCallingAssistConfig.class);}

    @Override
    protected void startUp() throws Exception
    {
	sender.updateConfig();
	autoCall = starConfig.autoCall();
	chatLogging = starConfig.chatMessages();
	updateStar = starConfig.updateStar();
	lastCalledStar = null;
	if (parent != null) {
	    clientThread.invokeLater(this::createCallButton);
	}
	else
	{
	    parent = client.getWidget(WidgetInfo.MINIMAP_ORBS);
	    clientThread.invokeLater(this::createCallButton);
	}
    }

    @Override
    protected void shutDown() throws Exception
    {
	Star.REMOVE_STAR();
	lastCalledStar = null;
	removeCallButton();
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
	int tier = getTier(event.getGameObject().getId());
	if (tier != -1)
	{
	    Star.SET_STAR(event.getGameObject(), tier, client.getWorld());
	    if(autoCall)
		attemptCall(false);
	}
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event)
    {
	if (getTier(event.getGameObject().getId()) != -1)
	    Star.REMOVE_STAR();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged state)
    {
	if (state.getGameState() == GameState.HOPPING || state.getGameState() == GameState.LOGGING_IN)
	{
	    Star.REMOVE_STAR();
	    removeCallButton();
	}
    }

    @Subscribe
    public void onGameTick(GameTick tick)
    {
	if (Star.GET_STAR() != null)
	    if (client.getLocalPlayer().getWorldLocation().distanceTo(Star.GET_STAR().location) > 32)
		Star.REMOVE_STAR();
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
	if (!event.getGroup().equals("starcallingassistplugin"))
	    return;
	if(event.getKey().equals("endpoint")) {
	    sender.updateConfig();
	}
	else if (event.getKey().equals("autoCall")) {
	    autoCall = starConfig.autoCall();
	    if (autoCall)
		clientThread.invokeLater(() -> {attemptCall(false);});
	}
	else if (event.getKey().equals("chatMessages")) {
	    chatLogging = starConfig.chatMessages();
	}
	else if (event.getKey().equals("updateStar")) {
	    updateStar = starConfig.updateStar();
	    if (autoCall && updateStar)
		clientThread.invokeLater(() -> {attemptCall(false);});
	}
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event)
    {
	if(event.getGroupId() == WidgetID.MINIMAP_GROUP_ID && (callButton == null || parent == null))
	{
	    removeCallButton();
	    parent = client.getWidget(WidgetInfo.MINIMAP_ORBS);
	    createCallButton();
	}
    }

    @Subscribe
    public void onResizeableChanged(ResizeableChanged event)
    {
	removeCallButton();
	parent = client.getWidget(WidgetInfo.MINIMAP_ORBS);
	createCallButton();
    }

    private void createCallButton()
    {
	if (callButton != null || parent == null)
	    return;
	callButton = parent.createChild(WidgetType.GRAPHIC);
	callButton.setSpriteId(SpriteID.BARBARIAN_ASSAULT_HORN_FOR_ATTACKER_ICON);
	callButton.setOriginalWidth(20);
	callButton.setOriginalHeight(23);
	setCallButtonLocation();
	callButton.setAction(4, "Call star");
	callButton.setHasListener(true);
	callButton.setNoClickThrough(true);
	callButton.setOnOpListener((JavaScriptCallback) ev -> attemptCall(true));
	callButton.revalidate();
    }

    private void removeCallButton()
    {
	if (parent == null || callButton == null)
	    return;
	Widget[] children = parent.getChildren();
	if (children.length <= callButton.getIndex() || children[callButton.getIndex()] != callButton)
	    return;
	children[callButton.getIndex()] = null;
	callButton = null;
	parent = null;
    }

    private void attemptCall(boolean manual)
    {
	if (Star.GET_STAR() == null)
	{
	    if(manual)
	    	logToChat("Unable to find star.");
	    return;
	}
	else if (lastCalledStar != null
		 && lastCalledStar.world == Star.GET_STAR().world
		 && lastCalledStar.tier == Star.GET_STAR().tier
		 && lastCalledStar.location.equals(Star.GET_STAR().location))
	{
	    if (manual)
	    	logToChat("Star has already been called.");
	    return;
	}
	//Won't automatically call star again if tier decreased and the updateStar option disabled
	else if(lastCalledStar != null  && lastCalledStar.world == Star.GET_STAR().world
					&& lastCalledStar.location.equals(Star.GET_STAR().location)
					&& lastCalledStar.tier > Star.GET_STAR().tier
					&& !updateStar && !manual)
	{
	    return;
	}

	String username = client.getLocalPlayer().getName();
	String world = "W" + Integer.toString(client.getWorld());
	String tier = Star.GET_STAR().getTierString();
	String location = getLocationName(Star.GET_STAR().location.getX(), Star.GET_STAR().location.getY());
	if (location.equals("unknown"))
	{
	    logToChat("Star location name is unknown, manual call required.");
	    return;
	}
	new Thread(() -> {
	    try
	    {
		if(sender.sendCall(username, world, tier, location))
		{
		    lastCalledStar = Star.GET_STAR();
		    clientThread.invokeLater(() -> {
			logHighlightedToChat("Successfully called: ", world + " " + tier + " " + location);
		    });
		}
		else
		    clientThread.invokeLater(() -> {logToChat("Unable to post call to " + starConfig.getEndpoint() + ".");});
	    }
	    catch (IOException ioe)
	    {
		clientThread.invokeLater(() -> {logToChat("Unable to post call to " + starConfig.getEndpoint() + ".");});
	    }
	}).start();
    }

    private void logHighlightedToChat(String normal, String highlight)
    {
	if(chatLogging)
	{
	    String chatMessage = new ChatMessageBuilder()
		    .append(ChatColorType.NORMAL)
		    .append(normal)
		    .append(ChatColorType.HIGHLIGHT)
		    .append(highlight)
		    .build();
	    chatMessageManager.queue(QueuedMessage.builder()
			    .type(ChatMessageType.CONSOLE)
			    .runeLiteFormattedMessage(chatMessage)
			    .build());
	}
    }

    private void logToChat(String message)
    {
	if (chatLogging)
	    client.addChatMessage(ChatMessageType.CONSOLE, "", message, "");
    }

    private String getLocationName(int x, int y)
    {
	String locationName = Star.LOCATION_NAMES.get(new Point(x, y));
	if (locationName != null)
	    return locationName;
	return "unknown";
    }

    private int getTier(int id)
    {
	for (int i = 0; i < TIER_IDS.length; i++)
	    if(id == TIER_IDS[i])
		return i + 1;
	return -1;
    }

    private void setCallButtonLocation()
    {
	if (client.isResized())
	{
	    callButton.setOriginalX(BUTTON_RESIZEABLE_LOCATION.x);
	    callButton.setOriginalY(BUTTON_RESIZEABLE_LOCATION.y);
	}
	else
	{
	    callButton.setOriginalX(BUTTON_FIXED_LOCATION.x);
	    callButton.setOriginalY(BUTTON_FIXED_LOCATION.y);
	}
    }
}