package com.starcallingassist.modules.callButton;

import com.google.inject.Inject;
import com.starcallingassist.PluginModuleContract;
import com.starcallingassist.StarCallingAssistConfig;
import com.starcallingassist.events.ManualStarAbsenceBroadcastRequested;
import com.starcallingassist.events.ManualStarPresenceBroadcastRequested;
import com.starcallingassist.events.PluginConfigChanged;
import com.starcallingassist.modules.callButton.enums.CallType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ScriptEvent;
import net.runelite.api.SpriteID;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ResizeableChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;

public class CallButtonModule extends PluginModuleContract
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private StarCallingAssistConfig config;

	private Widget minimapContainerWidget = null;

	@Override
	public void startUp()
	{
		minimapContainerWidget = client.getWidget(ComponentID.MINIMAP_CONTAINER);
		clientThread.invokeLater(this::createCallButton);
	}

	@Override
	public void shutDown()
	{
		removeCallButton();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged state)
	{
		if (state.getGameState() == GameState.HOPPING || state.getGameState() == GameState.LOGGING_IN)
		{
			removeCallButton();
		}
	}

	@Subscribe
	public void onPluginConfigChanged(PluginConfigChanged event)
	{
		if (event.getKey().equals("callHorn"))
		{
			redrawCallButton();
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		redrawCallButton();
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() == InterfaceID.MINIMAP && minimapContainerWidget == null)
		{
			redrawCallButton();
		}
	}

	@Subscribe
	public void onResizeableChanged(ResizeableChanged event)
	{
		redrawCallButton();
	}

	private void redrawCallButton()
	{
		removeCallButton();
		minimapContainerWidget = client.getWidget(ComponentID.MINIMAP_CONTAINER);
		clientThread.invokeLater(this::createCallButton);
	}

	private void setWidgetLocation(Widget widget, int offsetX, int offsetY)
	{
		if (client.isResized())
		{
			widget.setOriginalX(119 + offsetX);
			widget.setOriginalY(147 + offsetY);
			return;
		}

		// If the activity advisor is enabled on fixed mode
		if (client.getVarbitValue(5368) == 0)
		{
			widget.setOriginalX(195 + offsetX);
			widget.setOriginalY(18 + offsetY);
			return;
		}

		widget.setOriginalX(202 + offsetX);
		widget.setOriginalY(49 + offsetY);
	}

	private void createCallButton()
	{
		if (minimapContainerWidget == null || !config.callHorn())
		{
			return;
		}

		Widget callButtonContainer = minimapContainerWidget.createChild(-1, WidgetType.GRAPHIC);
		callButtonContainer.setSpriteId(2138);
		callButtonContainer.setOriginalWidth(34);
		callButtonContainer.setOriginalHeight(34);
		callButtonContainer.setHasListener(true);
		callButtonContainer.setOnMouseOverListener((JavaScriptCallback) ev -> callButtonContainer.setSpriteId(3517));
		callButtonContainer.setOnMouseLeaveListener((JavaScriptCallback) ev -> callButtonContainer.setSpriteId(2138));
		setWidgetLocation(callButtonContainer, 0, 0);
		callButtonContainer.revalidate();

		Widget callButtonBackground = minimapContainerWidget.createChild(-1, WidgetType.GRAPHIC);
		callButtonBackground.setSpriteId(1061);
		callButtonBackground.setOriginalWidth(26);
		callButtonBackground.setOriginalHeight(26);
		setWidgetLocation(callButtonBackground, 4, 4);
		callButtonBackground.setAction(CallType.STAR.getOp(), "Call star");
		callButtonBackground.setAction(CallType.DEAD.getOp(), "Call dead");
		callButtonBackground.setAction(CallType.DEAD_PRIVATE.getOp(), "Call private");
		callButtonBackground.setHasListener(true);
		callButtonBackground.setNoClickThrough(true);
		callButtonBackground.setOnOpListener((JavaScriptCallback) this::callButtonClicked);
		callButtonBackground.revalidate();

		Widget callButtonIcon = minimapContainerWidget.createChild(WidgetType.GRAPHIC);
		callButtonIcon.setSpriteId(SpriteID.BARBARIAN_ASSAULT_HORN_FOR_ATTACKER_ICON);
		callButtonIcon.setOriginalWidth(16);
		callButtonIcon.setOriginalHeight(16);
		setWidgetLocation(callButtonIcon, 9, 9);
		callButtonIcon.revalidate();
	}

	private void removeCallButton()
	{
		if (minimapContainerWidget != null)
		{
			minimapContainerWidget.deleteAllChildren();
			minimapContainerWidget = null;
		}

	}

	private void callButtonClicked(ScriptEvent event)
	{
		if (event.getOp() == CallType.STAR.getOp())
		{
			dispatch(new ManualStarPresenceBroadcastRequested());
		}

		if (event.getOp() == CallType.DEAD.getOp())
		{
			dispatch(new ManualStarAbsenceBroadcastRequested());
		}

		if (event.getOp() == CallType.DEAD_PRIVATE.getOp())
		{
			dispatch(new ManualStarAbsenceBroadcastRequested(false));
		}
	}
}
