package com.starcallingassist;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("starcallingassistplugin")
public interface StarCallingAssistConfig extends Config
{

    @ConfigItem(
	    keyName = "endpoint", position = 0, name = "Call endpoint", description = "Endpoint to post calls to.")
    default String getEndpoint()
    {
	return "http://blablacxcer.com";//"http://server.groupstars.site/chat";
    }

    @ConfigItem(
	    keyName = "autoCall",
	    name = "Auto calling stars",
	    description = "Automatically call stars as they appear",
	    position = 1
    )
    default boolean autoCall() {return false;}

    @ConfigItem(
	    keyName = "updateStar",
	    name = "Auto update stars",
	    description = "Posts a new call when the tier of a star changes (auto calling must be enabled)",
	    position = 2
    )
    default boolean updateStar() {return false;}

    @ConfigItem(
	    keyName = "chatMessages",
	    name = "Display chat messages",
	    description = "Display chat messages on successful calls, unsuccessful calls and other errors",
	    position = 3
    )
    default boolean chatMessages() {return true;}

}
