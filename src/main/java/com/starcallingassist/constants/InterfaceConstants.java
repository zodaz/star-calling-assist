package com.starcallingassist.constants;

import net.runelite.api.widgets.InterfaceID;

public class InterfaceConstants
{
	public static final int FIXED_CLASSIC_TOP_LEVEL_INTERFACE_ID = 548;
	public static final int RESIZABLE_CLASSIC_TOP_LEVEL_INTERFACE_ID = 161;
	public static final int RESIZABLE_MODERN_TOP_LEVEL_INTERFACE_ID = 164;
	public static final int FIXED_CLASSIC_WORLD_MAP_PARENT_COMPONENT_ID = (FIXED_CLASSIC_TOP_LEVEL_INTERFACE_ID << 16) | 42;
	public static final int RESIZABLE_CLASSIC_WORLD_MAP_PARENT_COMPONENT_ID = (RESIZABLE_CLASSIC_TOP_LEVEL_INTERFACE_ID << 16) | 18;
	public static final int RESIZABLE_MODERN_WORLD_MAP_PARENT_COMPONENT_ID = (RESIZABLE_MODERN_TOP_LEVEL_INTERFACE_ID << 16) | 18;
	public static final int CLOSE_WORLD_MAP_MINIMAP_ORB = (InterfaceID.MINIMAP << 16) | 53;
	public static final int CLOSE_WORLD_MAP_CROSS = (InterfaceID.WORLD_MAP << 16) | 38;
	public static final int WORLD_MAP_UPDATE_PLAYER_POSITION_SCRIPT_ID = 1749;
	public static final int WORLD_MAP_PAN_TO_POSITION_SCRIPT_ID = 1756;
}
