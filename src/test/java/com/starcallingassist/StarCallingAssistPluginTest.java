package com.starcallingassist;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class StarCallingAssistPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(StarCallingAssistPlugin.class);
		RuneLite.main(args);
	}
}