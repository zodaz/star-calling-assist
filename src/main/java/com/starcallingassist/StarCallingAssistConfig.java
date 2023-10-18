package com.starcallingassist;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("starcallingassistplugin")
public interface StarCallingAssistConfig extends Config
{
    @ConfigItem(
	    keyName = "endpoint",
	    position = 0,
	    name = "Call endpoint",
	    description = "Endpoint to post calls to.")
    default String getEndpoint()
    {
	return "https://public.starminers.site/crowdsource";
    }

    @ConfigItem(
	    keyName = "authorization",
	    position = 1,
	    name = "Authorization",
	    description = "Used to set the http authorization header. Leave blank if unused.")
    default String getAuthorization()
    {
	return "";
    }

    @ConfigItem(
	    keyName = "includeIgn",
	    name = "Send in-game name",
	    description = "Includes your in-game name with your calls. This is required if you want the stars you find " +
			  "to count towards your called stars total.",
	    position = 2
    )
    default boolean includeIgn() {return false;}

    @ConfigItem(
	    keyName = "autoCall",
	    name = "Auto call stars",
	    description = "Automatically call stars as they appear or fully depletes",
	    position = 3
    )
    default boolean autoCall() {return true;}

    @ConfigItem(
	    keyName = "updateStar",
	    name = "Auto update stars",
	    description = "Posts a new call when the tier of a star changes (Auto call must be enabled)",
	    position = 4
    )
    default boolean updateStar() {return true;}

    @ConfigItem(
	    keyName = "chatMessages",
	    name = "Display chat messages",
	    description = "Display chat messages on successful calls, unsuccessful calls and other errors",
	    position = 5
    )
    default boolean chatMessages() {return true;}

    @ConfigItem(
	    keyName = "callHorn",
	    name = "Call Button",
	    description = "Enables a button which can be used to call a star.",
	    position = 6
    )
    default boolean callHorn() {return false;}
}
