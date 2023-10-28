package com.starcallingassist.modules.callButton;

import com.starcallingassist.StarModuleContract;
import com.starcallingassist.events.ManualStarDepletedCallRequested;
import com.starcallingassist.events.ManualStarDroppedCallRequested;
import java.awt.Point;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.ScriptEvent;
import net.runelite.api.SpriteID;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ResizeableChanged;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.JavaScriptCallback;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.api.widgets.WidgetType;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;

@Slf4j
public class CallButtonModule extends StarModuleContract
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	private static final Point BUTTON_RESIZEABLE_LOCATION = new Point(130, 150);
	private static final Point BUTTON_FIXED_LOCATION = new Point(208, 55);
	private static final int CALL_STAR = 5;
	private static final int CALL_DEAD = 6;
	private static final int CALL_PRIVATE = 7;

	private Widget parent = null;
	private Widget callButton = null;

	@Override
	public void startUp()
	{
		parent = client.getWidget(WidgetInfo.MINIMAP_ORBS);
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
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("starcallingassistplugin"))
		{
			return;
		}

		if (event.getKey().equals("callHorn"))
		{
			removeCallButton();
		}

		parent = client.getWidget(WidgetInfo.MINIMAP_ORBS);
		clientThread.invokeLater(this::createCallButton);
	}


	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		if (event.getGroupId() == WidgetID.MINIMAP_GROUP_ID && (callButton == null || parent == null))
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

	private void createCallButton()
	{
		if (callButton != null || parent == null || !config.callHorn())
		{
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

	private void removeCallButton()
	{
		if (parent == null || callButton == null)
		{
			return;
		}

		Widget[] children = parent.getChildren();
		if (children == null || children.length <= callButton.getIndex() || !children[callButton.getIndex()].equals(callButton))
		{
			return;
		}

		children[callButton.getIndex()] = null;
		callButton = null;
		parent = null;
	}


	private void callButtonClicked(ScriptEvent event)
	{
		if (event.getOp() == CALL_STAR)
		{
			dispatch(new ManualStarDroppedCallRequested());
		}

		if (event.getOp() == CALL_DEAD)
		{
			dispatch(new ManualStarDepletedCallRequested());
		}

		if (event.getOp() == CALL_PRIVATE)
		{
			dispatch(new ManualStarDepletedCallRequested(false));
		}
	}
}
