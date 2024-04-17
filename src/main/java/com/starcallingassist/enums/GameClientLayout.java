package com.starcallingassist.enums;

import com.starcallingassist.constants.InterfaceConstants;

import net.runelite.api.Client;

public enum GameClientLayout
{
	FIXED,
	RESIZABLE_CLASSIC,
	RESIZABLE_MODERN,
	UNKNOWN;

	public static GameClientLayout currentGameClientLayout(Client client)
	{
		if (!client.isResized())
		{
			return GameClientLayout.FIXED;
		}
		else if (client.getTopLevelInterfaceId() == InterfaceConstants.RESIZABLE_CLASSIC_TOP_LEVEL_INTERFACE_ID)
		{
			return GameClientLayout.RESIZABLE_CLASSIC;
		}
		else if (client.getTopLevelInterfaceId() == InterfaceConstants.RESIZABLE_MODERN_TOP_LEVEL_INTERFACE_ID)
		{
			return GameClientLayout.RESIZABLE_MODERN;
		}

		return GameClientLayout.UNKNOWN;
	}
}
