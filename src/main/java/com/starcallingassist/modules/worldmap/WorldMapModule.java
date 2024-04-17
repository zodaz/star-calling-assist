package com.starcallingassist.modules.worldmap;

import com.google.inject.Inject;
import com.starcallingassist.PluginModuleContract;
import com.starcallingassist.constants.InterfaceConstants;
import com.starcallingassist.enums.GameClientLayout;
import com.starcallingassist.events.ShowWorldPointOnWorldMapRequested;
import javax.annotation.Nullable;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.WidgetNode;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetModalMode;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.eventbus.Subscribe;

public class WorldMapModule extends PluginModuleContract
{
	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Nullable
	private WidgetNode worldMap;

	@Override
	public void shutDown()
	{
		closeWorldMap();
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked)
	{
		if (worldMap == null)
		{
			return;
		}

		Widget widget = menuOptionClicked.getWidget();

		if (widget == null)
		{
			return;
		}

		if(widget.getId() == InterfaceConstants.CLOSE_WORLD_MAP_MINIMAP_ORB || widget.getId() == InterfaceConstants.CLOSE_WORLD_MAP_CROSS)
		{
			closeWorldMap();
			menuOptionClicked.consume();
		}
	}

	@Subscribe
	public void onGameTick(GameTick gametick)
	{
		if(client.getGameState() == GameState.LOGGED_IN && worldMap != null)
		{
			setWorldMapPlayerPosition(client.getLocalPlayer().getWorldLocation());
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if(gameStateChanged.getGameState() != GameState.LOGGED_IN && worldMap != null)
		{
			closeWorldMap();
		}
	}

	@Subscribe
	public void onShowWorldPointOnWorldMapRequested(ShowWorldPointOnWorldMapRequested showWorldPointOnWorldMapRequested)
	{
		showWorldPointOnWorldMap(showWorldPointOnWorldMapRequested.getWorldPoint());
	}

	public void openWorldMap()
	{
		if (!client.isClientThread())
		{
			clientThread.invokeLater(this::openWorldMap);
			return;
		}

		if (client.getGameState() != GameState.LOGGED_IN || isWorldMapOpen())
		{
			return;
		}

		worldMap = client.openInterface(getWorldMapParentComponentId(), InterfaceID.WORLD_MAP, WidgetModalMode.NON_MODAL);
	}

	/**
	 * Pans the world map to the specified {@link WorldPoint}. Opens the world
	 * map if it is not already open.
	 * @param worldPoint {@link WorldPoint} to pan to in the world map
	 * @return World map coordinate as integer
	 */
	public void showWorldPointOnWorldMap(WorldPoint worldPoint)
	{
		if (!client.isClientThread())
		{
			clientThread.invokeLater(() -> showWorldPointOnWorldMap(worldPoint));
			return;
		}

		if (!isWorldMapOpen())
		{
			openWorldMap();
		}

		panWorldMapToPosition(toWorldMapPosition(worldPoint));
	}

	public void setWorldMapPlayerPosition(WorldPoint point)
	{
		client.runScript(InterfaceConstants.WORLD_MAP_UPDATE_PLAYER_POSITION_SCRIPT_ID, toWorldMapPosition(point), -1, -1);
	}

	public void closeWorldMap()
	{
		if (!client.isClientThread())
		{
			clientThread.invokeLater(this::closeWorldMap);
			return;
		}

		if (worldMap == null)
		{
			return;
		}

		try
		{
			client.closeInterface(worldMap, true);
		}
		catch (IllegalArgumentException ignored)
		{
		}

		worldMap = null;
	}

	/**
	 * Translates a {@link WorldPoint} to a format expected by the world map client scripts
	 * @param worldPoint {@link WorldPoint} to translate
	 * @return World map coordinate as integer
	 */
	private int toWorldMapPosition(WorldPoint worldPoint)
	{
		return (worldPoint.getPlane() << 28) | (worldPoint.getX() << 14) | worldPoint.getY();
	}

	private void panWorldMapToPosition(int position)
	{
		client.runScript(InterfaceConstants.WORLD_MAP_PAN_TO_POSITION_SCRIPT_ID, 1, position, 1);
	}

	private boolean isWorldMapOpen()
	{
		return client.getWidget(InterfaceID.WORLD_MAP, 0) != null;
	}

	private int getWorldMapParentComponentId()
	{
		GameClientLayout gameClientLayout = GameClientLayout.currentGameClientLayout(client);
		switch (gameClientLayout)
		{
			case FIXED:
				return InterfaceConstants.FIXED_CLASSIC_WORLD_MAP_PARENT_COMPONENT_ID;
			case RESIZABLE_CLASSIC:
				return InterfaceConstants.RESIZABLE_CLASSIC_WORLD_MAP_PARENT_COMPONENT_ID;
			case RESIZABLE_MODERN:
				return InterfaceConstants.RESIZABLE_MODERN_WORLD_MAP_PARENT_COMPONENT_ID;
		}

		return -1;
	}
}
