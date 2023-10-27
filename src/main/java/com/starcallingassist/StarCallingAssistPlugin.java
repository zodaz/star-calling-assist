package com.starcallingassist;

import com.google.inject.Provides;
import com.starcallingassist.sidepanel.SidePanel;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.api.widgets.*;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.WorldService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.task.Schedule;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import javax.inject.Inject;
import javax.swing.*;
import java.awt.Point;
import java.io.IOException;
import java.time.temporal.ChronoUnit;

@PluginDescriptor(
    name = "Star Miners",
    description = "Displays a list of active stars and crowdsources data about stars you find and mine",
    tags = {"star", "shooting", "shootingstar", "meteor", "crowdsource", "crowdsourcing"}
)
@Slf4j
public class StarCallingAssistPlugin extends Plugin
{
    private static final Point BUTTON_RESIZEABLE_LOCATION = new Point(130, 150);
    private static final Point BUTTON_FIXED_LOCATION = new Point(208, 55);
    private static final int CALL_STAR = 5;
    private static final int CALL_DEAD = 6;
    private static final int CALL_PRIVATE = 7;
    private static final int PLAYER_RENDER_DISTANCE = 13;

    private Widget parent, callButton;
    private Star lastCalledStar;

    private int miners = 0, hopTarget = -1, hopAttempts = 0;

    private WorldPoint confirmDeadLocation = null;

    @Inject
    private StarCallingAssistConfig starConfig;
    @Inject
    private ChatMessageManager chatMessageManager;
    @Inject
    private WorldService worldService;
    @Inject
    private Client client;
    @Inject
    private ClientThread clientThread;
    @Inject
    private ClientToolbar clientToolbar;
    @Inject
    private CallSender sender;

    private SidePanel sidePanel;

    private NavigationButton navButton;

    @Provides
    StarCallingAssistConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(StarCallingAssistConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        lastCalledStar = null;
        parent = client.getWidget(WidgetInfo.MINIMAP_ORBS);
        clientThread.invokeLater(this::createCallButton);

        sidePanel = injector.getInstance(SidePanel.class);
        sidePanel.init();

        navButton = NavigationButton.builder()
                                    .tooltip("Star Miners")
                                    .icon(ImageUtil.loadImageResource(getClass(), "/sminers.png"))
                                    .panel(sidePanel)
                                    .build();
        clientToolbar.addNavigation(navButton);
        navButton.setOnClick(this::fetchStarData);
    }

    @Override
    protected void shutDown() throws Exception
    {
        Star.removeStar();
        lastCalledStar = null;
        removeCallButton();
        clientToolbar.removeNavigation(navButton);
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event)
    {
        int tier = Star.getTier(event.getGameObject().getId());
        if (tier == -1) {
            return;
        }

        Star.setStar(event.getGameObject(), tier, client.getWorld());

        if (starConfig.autoCall()) {
            if (whitinPlayerDistance()) {
                countMiners();
                prepareCall(false);
            } else {
                miners = -1;
                prepareCall(false);
            }
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event)
    {
        if (Star.getTier(event.getGameObject().getId()) == -1) {
            return;
        }

        //Causes a check for whether the star fully depleted in the next GameTick event
        if (starConfig.autoCall()) {
            confirmDeadLocation = event.getGameObject().getWorldLocation();
        }

        Star.removeStar();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged state)
    {
        if (state.getGameState() == GameState.HOPPING || state.getGameState() == GameState.LOGGING_IN) {
            Star.removeStar();
            removeCallButton();
        }

        if (state.getGameState() == GameState.LOGGED_IN) {
            SwingUtilities.invokeLater(() -> sidePanel.rebuildTableRows());
            fetchStarData();
        }
    }

    @Subscribe
    public void onGameTick(GameTick tick)
    {
        if (hopTarget != -1) {
            performHop();
        }

        if (confirmDeadLocation != null) {
            if (Star.getStar() == null) {
                if (client.getLocalPlayer().getWorldLocation().distanceTo(confirmDeadLocation) <= 32) {
                    attemptCall(client.getLocalPlayer().getName(), client.getWorld(), 0, Star.getLocationName(confirmDeadLocation));
                }
            }

            confirmDeadLocation = null;
        }

        if (Star.getStar() != null) {
            if (client.getLocalPlayer().getWorldLocation().distanceTo(Star.getStar().location) > 32) {
                Star.removeStar();
            }

            if (Star.getStar() != null) {
                countMiners();
            }
        }
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event)
    {
        if (!event.getGroup().equals("starcallingassistplugin")) {
            return;
        }

        if (event.getKey().equals("autoCall")) {
            if (starConfig.autoCall()) {
                clientThread.invokeLater(() -> prepareCall(false));
            }

            return;
        }

        if (event.getKey().equals("updateStar")) {
            if (starConfig.autoCall() && starConfig.updateStar()) {
                clientThread.invokeLater(() -> prepareCall(false));
            }

            return;
        }

        if (event.getKey().equals("callHorn")) {
            removeCallButton();
            parent = client.getWidget(WidgetInfo.MINIMAP_ORBS);
            clientThread.invokeLater(this::createCallButton);
            return;
        }

        if (event.getKey().equals("endpoint")) {
            fetchStarData();
            return;
        }

        if (event.getKey().equals("authorization")) {
            sidePanel.updateInfoPanel();
            return;
        }

        if (event.getKey().equals("estimateTier")) {
            sidePanel.rebuildTableRows();
            return;
        }

        sidePanel.updateTableRows();
    }

    @Subscribe
    public void onWidgetLoaded(WidgetLoaded event)
    {
        if (event.getGroupId() == WidgetID.MINIMAP_GROUP_ID && (callButton == null || parent == null)) {
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

    @Schedule(
        period = 30,
        unit = ChronoUnit.SECONDS
    )
    public void fetchStarData()
    {
        if (navButton.isSelected()) {
            sidePanel.fetchStarData();
        }
    }

    @Schedule(
        period = 10,
        unit = ChronoUnit.MINUTES
    )
    public void fetchWorldData()
    {
        sidePanel.fetchWorldData();
    }

    public Client getClient()
    {
        return client;
    }

    public StarCallingAssistConfig getConfig()
    {
        return starConfig;
    }

    public void queueWorldHop(int worldId)
    {
        if (client.getGameState() == GameState.LOGGED_IN) {
            hopTarget = worldId;
            clientThread.invokeLater(() -> logHighlightedToChat("Attempting to quick-hop to world ", String.valueOf(hopTarget)));
        }
    }

    private void performHop()
    {
        if (++hopAttempts >= 5) {
            logHighlightedToChat("Unable to quick-hop to world ", String.valueOf(hopTarget));
            hopTarget = -1;
            hopAttempts = 0;
            return;
        }

        if (client.getWidget(WidgetInfo.WORLD_SWITCHER_LIST) == null) {
            client.openWorldHopper();
            return;
        }

        World[] worldList = client.getWorldList();

        if (worldList == null) {
            return;
        }

        for (World world : worldList) {
            if (world.getId() == hopTarget) {
                client.hopToWorld(world);
                break;
            }
        }

        hopTarget = -1;
        hopAttempts = 0;
    }

    //Credit to https://github.com/pwatts6060/star-info/. Simplified to fit our needs.
    private void countMiners()
    {
        miners = 0;
        Star star = Star.getStar();

        if (!whitinPlayerDistance()) {
            miners = -1;
            return;
        }

        WorldArea areaH = new WorldArea(star.location.dx(-1), 4, 2);
        WorldArea areaV = new WorldArea(star.location.dy(-1), 2, 4);

        for (Player p : client.getPlayers()) {
            if (!p.getWorldLocation().isInArea2D(areaH, areaV)) {
                continue;
            }

            miners++;
        }
    }

    private boolean whitinPlayerDistance()
    {
        return client.getLocalPlayer().getWorldLocation().distanceTo(new WorldArea(Star.getStar().location, 2, 2)) <= PLAYER_RENDER_DISTANCE;
    }

    private void createCallButton()
    {
        if (callButton != null || parent == null || !starConfig.callHorn()) {
            return;
        }

        callButton = parent.createChild(WidgetType.GRAPHIC);
        callButton.setSpriteId(SpriteID.BARBARIAN_ASSAULT_HORN_FOR_ATTACKER_ICON);
        callButton.setOriginalWidth(20);
        callButton.setOriginalHeight(23);
        setCallButtonLocation();
        callButton.setAction(4, "Call star");
        callButton.setAction(5, "Call dead");
        callButton.setAction(6, "Call private");
        callButton.setHasListener(true);
        callButton.setNoClickThrough(true);
        callButton.setOnOpListener((JavaScriptCallback) this::callButtonClicked);
        callButton.revalidate();
    }

    private void callButtonClicked(ScriptEvent event)
    {
        switch (event.getOp()) {
            case CALL_STAR: {
                prepareCall(true);
                break;
            }
            case CALL_DEAD: {
                attemptCall(client.getLocalPlayer().getName(), client.getWorld(), 0, "dead");
                break;
            }
            case CALL_PRIVATE: {
                attemptCall(client.getLocalPlayer().getName(), client.getWorld(), 0, "pdead");
                break;
            }
        }
    }

    private void removeCallButton()
    {
        if (parent == null || callButton == null) {
            return;
        }

        Widget[] children = parent.getChildren();
        if (children == null || children.length <= callButton.getIndex() || !children[callButton.getIndex()].equals(callButton)) {
            return;
        }

        children[callButton.getIndex()] = null;
        callButton = null;
        parent = null;
    }

    private void prepareCall(boolean manual)
    {
        if (Star.getStar() == null) {
            if (manual) {
                logToChat("Unable to find star.");
            }

            return;
        }

        if (lastCalledStar != null
            && lastCalledStar.world == Star.getStar().world
            && lastCalledStar.tier == Star.getStar().tier
            && lastCalledStar.location.equals(Star.getStar().location)
        ) {
            if (manual) {
                logToChat("This star has already been called.");
            }

            return;
        }

        //Won't automatically call star again if tier decreased and the updateStar option disabled
        if (lastCalledStar != null
            && lastCalledStar.world == Star.getStar().world
            && lastCalledStar.location.equals(Star.getStar().location)
            && lastCalledStar.tier > Star.getStar().tier
            && !starConfig.updateStar() && !manual
        ) {
            return;
        }

        String location = Star.getLocationName(Star.getStar().location);
        if (location.equals("unknown")) {
            logToChat("Star location is unknown, manual call required.");
            return;
        }

        attemptCall(client.getLocalPlayer().getName(), client.getWorld(), Star.getStar().tier, location);
    }

    private void attemptCall(String username, int world, int tier, String location)
    {
        try {
            sender.sendCall(username, world, tier, location, miners, new Callback()
            {
                @Override
                public void onFailure(Call call, IOException e)
                {
                    clientThread.invokeLater(() -> {
                        logToChat("Unable to post call to " + starConfig.getEndpoint() + ".");
                    });

                    call.cancel();
                }

                @Override
                public void onResponse(Call call, Response res) throws IOException
                {
                    if (res.isSuccessful()) {
                        if (tier > 0) {
                            lastCalledStar = Star.getStar();
                        }

                        clientThread.invokeLater(() -> logHighlightedToChat(
                            "Successfully posted call: ",
                            "W" + world + " T" + tier + " " + location + ((miners == -1 || tier == 0) ? "" : (" " + miners + " Miners"))
                        ));
                    } else {
                        clientThread.invokeLater(() -> logHighlightedToChat(
                            "Issue posting call to " + starConfig.getEndpoint() + ": ",
                            res.message()
                        ));
                    }

                    res.close();
                }
            });
        } catch (IllegalArgumentException iae) {
            clientThread.invokeLater(() -> {
                logHighlightedToChat("Issue posting call to " + starConfig.getEndpoint() + ": ", "Invalid endpoint");
            });
        }
    }

    private void logHighlightedToChat(String normal, String highlight)
    {
        if (starConfig.chatMessages()) {
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

    private void logToChat(String message)
    {
        if (starConfig.chatMessages()) {
            client.addChatMessage(ChatMessageType.CONSOLE, "", message, "");
        }
    }

    private void setCallButtonLocation()
    {
        if (client.isResized()) {
            callButton.setOriginalX(BUTTON_RESIZEABLE_LOCATION.x);
            callButton.setOriginalY(BUTTON_RESIZEABLE_LOCATION.y);
        } else {
            callButton.setOriginalX(BUTTON_FIXED_LOCATION.x);
            callButton.setOriginalY(BUTTON_FIXED_LOCATION.y);
        }
    }
}