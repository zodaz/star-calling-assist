package com.starcallingassist.modules.callButton;

import com.google.inject.Inject;
import com.starcallingassist.PluginModuleContract;
import com.starcallingassist.StarCallingAssistConfig;
import com.starcallingassist.events.ManualStarAbsenceBroadcastRequested;
import com.starcallingassist.events.ManualStarPresenceBroadcastRequested;
import com.starcallingassist.events.PluginConfigChanged;
import com.starcallingassist.modules.callButton.enums.CallType;
import net.runelite.api.Client;
import net.runelite.api.ScriptEvent;
import net.runelite.api.ScriptID;
import net.runelite.api.gameval.SpriteID;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.events.WidgetLoaded;
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

	private Widget callIcon;
	private Widget callBackground;
	private Widget callContainer;

	@Override
	public void startUp()
	{
		clientThread.invokeLater(this::createCallButton);
	}

	@Override
	public void shutDown()
	{
		clientThread.invokeLater(this::removeCallButton);
	}

	@Subscribe
	public void onWidgetLoaded(WidgetLoaded event)
	{
		// consider adding callbutton support for InterfaceID.ORBS_NOMAP interface
		if (event.getGroupId() == InterfaceID.ORBS)
		{
			createCallButton();
		}
	}

	@Subscribe
	public void onScriptPostFired(ScriptPostFired event)
	{
		// use wiki orb as trigger for drawing call button, this seems more reliable than prayer scripts
		if (event.getScriptId() == ScriptID.WIKI_ICON_UPDATE)
		{
			createCallButton();
		}
	}

	@Subscribe
	public void onPluginConfigChanged(PluginConfigChanged event)
	{
		if (event.getKey().equals("callHorn"))
			{
			clientThread.invokeLater(() -> {
				removeCallButton();
				if (config.callHorn())
				{
					createCallButton();
				}
			});
		}
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
		if (!config.callHorn())
		{
			return;
		}

		// consider adding callbutton support OrbsNomap interface
		Widget orbsContainer = client.getWidget(InterfaceID.Orbs.UNIVERSE);
		if (orbsContainer == null)
		{
			return;
		}

		removeCallButton();

		callContainer = orbsContainer.createChild(-1, WidgetType.GRAPHIC);
		callContainer.setSpriteId(SpriteID.Ring34._0);
		callContainer.setOriginalWidth(34);
		callContainer.setOriginalHeight(34);
		setWidgetLocation(callContainer, 0, 0);
		callContainer.setHasListener(true);
		callContainer.setOnMouseOverListener((JavaScriptCallback) ev -> callContainer.setSpriteId(SpriteID.Ring34._1));
		callContainer.setOnMouseLeaveListener((JavaScriptCallback) ev -> callContainer.setSpriteId(SpriteID.Ring34._0));
		callContainer.revalidate();

		callBackground = orbsContainer.createChild(-1, WidgetType.GRAPHIC);
		callBackground.setSpriteId(SpriteID.OrbFiller.HITPOINTS_POISON);
		callBackground.setOriginalWidth(26);
		callBackground.setOriginalHeight(26);
		setWidgetLocation(callBackground, 4, 4);
		callBackground.setAction(CallType.STAR.getOp() - 1, "Call star");
		callBackground.setAction(CallType.DEAD.getOp() - 1, "Call dead");
		callBackground.setAction(CallType.DEAD_PRIVATE.getOp() - 1, "Call private");
		callBackground.setHasListener(true);
		callBackground.setNoClickThrough(true);
		callBackground.setOnOpListener((JavaScriptCallback) this::callButtonClicked);
		callBackground.revalidate();

		callIcon = orbsContainer.createChild(-1, WidgetType.GRAPHIC);
		callIcon.setSpriteId(SpriteID.BarbassaultIcons.HORN_FOR_ATTACKER);
		callIcon.setOriginalWidth(16);
		callIcon.setOriginalHeight(16);
		setWidgetLocation(callIcon, 9, 9);
		callIcon.revalidate();
	}

	// note: this is a misnomer as the widget still exists, it is just hidden.
	private void removeCallButton()
	{
		if (callContainer != null)
		{
			callContainer.setHidden(true);
			callContainer = null;
		}
		if (callBackground != null)
		{
			callBackground.setHidden(true);
			callBackground = null;
		}
		if (callIcon != null)
		{
			callIcon.setHidden(true);
			callIcon = null;
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
