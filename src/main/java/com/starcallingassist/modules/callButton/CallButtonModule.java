package com.starcallingassist.modules.callButton;

import com.starcallingassist.PluginModuleContract;
import com.starcallingassist.StarCallingAssistConfig;
import com.starcallingassist.events.PluginConfigChanged;
import com.starcallingassist.events.StarCallManuallyRequested;
import com.starcallingassist.events.StarDepletionManuallyRequested;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ScriptEvent;
import net.runelite.api.SpriteID;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ResizeableChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
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

	private static final int CALL_STAR = 5;
	private static final int CALL_DEAD = 6;
	private static final int CALL_PRIVATE = 7;

	private Widget minimapOrbsWidget = null;

	@Override
	public void startUp()
	{
		minimapOrbsWidget = client.getWidget(WidgetInfo.MINIMAP_ORBS);
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
		if (event.getGroupId() == WidgetID.MINIMAP_GROUP_ID && minimapOrbsWidget == null)
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
		minimapOrbsWidget = client.getWidget(WidgetInfo.MINIMAP_ORBS);
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
		if (minimapOrbsWidget == null || !config.callHorn())
		{
			return;
		}

		Widget callButtonContainer = minimapOrbsWidget.createChild(-1, WidgetType.GRAPHIC);
		callButtonContainer.setSpriteId(2138);
		callButtonContainer.setOriginalWidth(34);
		callButtonContainer.setOriginalHeight(34);
		callButtonContainer.setHasListener(true);
		callButtonContainer.setOnMouseOverListener((JavaScriptCallback) ev -> callButtonContainer.setSpriteId(3517));
		callButtonContainer.setOnMouseLeaveListener((JavaScriptCallback) ev -> callButtonContainer.setSpriteId(2138));
		setWidgetLocation(callButtonContainer, 0, 0);
		callButtonContainer.revalidate();

		Widget callButtonBackground = minimapOrbsWidget.createChild(-1, WidgetType.GRAPHIC);
		callButtonBackground.setSpriteId(1061);
		callButtonBackground.setOriginalWidth(26);
		callButtonBackground.setOriginalHeight(26);
		setWidgetLocation(callButtonBackground, 4, 4);
		callButtonBackground.setAction(4, "Call star");
		callButtonBackground.setAction(5, "Call dead");
		callButtonBackground.setAction(6, "Call private");
		callButtonBackground.setHasListener(true);
		callButtonBackground.setNoClickThrough(true);
		callButtonBackground.setOnOpListener((JavaScriptCallback) this::callButtonClicked);
		callButtonBackground.revalidate();

		Widget callButtonIcon = minimapOrbsWidget.createChild(WidgetType.GRAPHIC);
		callButtonIcon.setSpriteId(SpriteID.BARBARIAN_ASSAULT_HORN_FOR_ATTACKER_ICON);
		callButtonIcon.setOriginalWidth(16);
		callButtonIcon.setOriginalHeight(16);
		setWidgetLocation(callButtonIcon, 9, 9);
		callButtonIcon.revalidate();
	}

	private void removeCallButton()
	{
		if (minimapOrbsWidget != null)
		{
			minimapOrbsWidget.deleteAllChildren();
			minimapOrbsWidget = null;
		}

	}

	private void callButtonClicked(ScriptEvent event)
	{
		if (event.getOp() == CALL_STAR)
		{
			dispatch(new StarCallManuallyRequested());
		}

		if (event.getOp() == CALL_DEAD)
		{
			dispatch(new StarDepletionManuallyRequested());
		}

		if (event.getOp() == CALL_PRIVATE)
		{
			dispatch(new StarDepletionManuallyRequested(false));
		}
	}
}
