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
	    name = "Automatic star calling",
	    description = "Automatically call stars that appear",
	    position = 1
    )
    default boolean autoCall() {return false;}

    @ConfigItem(
	    keyName = "updateStar",
	    name = "Auto update stars",
	    description = "Allows for a new automatic call to be made when the tier of a star changes (if automatic star calling enabled)",
	    position = 2
    )
    default boolean updateStar() {return false;}

    @ConfigItem(
	    keyName = "chatMessages",
	    name = "Display chat messages",
	    description = "Displays chat messages on successful calls, unsuccessful calls and other errors",
	    position = 3
    )
    default boolean chatMessages() {return true;}

}
