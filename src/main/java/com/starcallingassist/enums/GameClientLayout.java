package com.starcallingassist.enums;

import com.starcallingassist.constants.InterfaceConstants;

import net.runelite.api.Client;

/**
 * Enum representing the different game client layouts.
 */
public enum GameClientLayout
{
	FIXED,
	RESIZABLE_CLASSIC,
	RESIZABLE_MODERN,
	UNKNOWN;

	/**
	 * Get the current game client layout.
	 *
	 * @param client The runelite client instance.
	 * @return       The current client layout.
	 */
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
