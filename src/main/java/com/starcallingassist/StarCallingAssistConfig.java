package com.starcallingassist;

import com.starcallingassist.sidepanel.constants.RegionKeyName;
import com.starcallingassist.sidepanel.constants.TotalLevelType;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup("starcallingassistplugin")
public interface StarCallingAssistConfig extends Config
{
    @ConfigItem(
	    keyName = "endpoint",
	    position = 0,
	    name = "Endpoint",
	    description = "Endpoint to post calls to and fetch calls from")
    default String getEndpoint()
    {
	return "https://public.starminers.site/crowdsource";
    }

    @ConfigItem(
	    keyName = "authorization",
	    position = 1,
	    name = "Authorization",
	    description = "Used to set the http authorization header.")
    default String getAuthorization()
    {
	return "";
    }

    @ConfigSection(
	    name = "Caller Settings",
	    description = "Settings to configure the caller part of the plugin.",
	    position = 2
    )
    String callerSection = "Caller Settings";

    @ConfigItem(
	    keyName = "includeIgn",
	    name = "Send in-game name",
	    description = "Includes your in-game name with your calls. This is required if you want the stars you find " +
			  "to count towards your called stars total.",
	    position = 3,
	    section = callerSection
    )
    default boolean includeIgn() {return false;}

    @ConfigItem(
	    keyName = "autoCall",
	    name = "Auto call stars",
	    description = "Automatically call stars as they appear or fully depletes",
	    position = 4,
	    section = callerSection
    )
    default boolean autoCall() {return true;}

    @ConfigItem(
	    keyName = "updateStar",
	    name = "Auto update stars",
	    description = "Posts a new call when the tier of a star changes (Auto call must be enabled)",
	    position = 5,
	    section = callerSection
    )
    default boolean updateStar() {return true;}

    @ConfigItem(
	    keyName = "chatMessages",
	    name = "Display chat messages",
	    description = "Display chat messages on successful calls, unsuccessful calls and other errors",
	    position = 6,
	    section = callerSection
    )
    default boolean chatMessages() {return true;}

    @ConfigItem(
	    keyName = "callHorn",
	    name = "Call Button",
	    description = "Enables a button which can be used to call a star.",
	    position = 7,
	    section = callerSection
    )
    default boolean callHorn() {return false;}

    @ConfigSection(
	    name = "Star Panel Settings",
	    description = "Settings to configure side panel displaying active stars.",
	    position = 8
    )
    String panelSection = "Star Panel Settings";

    @ConfigItem(
	    keyName = "estimateTier",
	    name = "Estimate Tier",
	    description = "Estimates the current tier of stars in the list.",
	    position = 9,
	    section = panelSection
    )
    default boolean estimateTier() {return true;}

    @ConfigItem(
	    keyName = "minTier",
	    name = "Minimum Tier",
	    description = "Lowest tier of stars to be displayed in the side-panel.",
	    position = 10,
	    section = panelSection
    )
    @Range(
	    min = 1,
	    max = 9
    )
    default int minTier() {return 1;}

    @ConfigItem(
	    keyName = "maxTier",
	    name = "Maximum Tier",
	    description = "Highest tier of stars to be displayed in the side-panel.",
	    position = 11,
	    section = panelSection
    )
    @Range(
	    min = 1,
	    max = 9
    )
    default int maxTier() {return 9;}

    @ConfigItem(
	    keyName = "minDeadTime",
	    name = "Min. Dead Time",
	    description = "Hides stars that are estimated to be depleted in less than the specified amount of minutes",
	    position = 12,
	    section = panelSection
    )
    @Range(
	    min = -90,
	    max = 90
    )
    @Units(
	    value = Units.MINUTES
    )
    default int minDeadTime() {return -5;}

    @ConfigItem(
	    keyName = "showF2P",
	    name = "Show F2P",
	    description = "Show or hide f2p worlds.",
	    position = 13,
	    section = panelSection
    )
    default boolean showF2P() {return true;}

    @ConfigItem(
	    keyName = "showMembers",
	    name = "Show Members",
	    description = "Show or hide members worlds.",
	    position = 14,
	    section = panelSection
    )
    default boolean showMembers() {return true;}

    @ConfigItem(
	    keyName = "showPvp",
	    name = "Show PVP",
	    description = "Show or hide PVP worlds.",
	    position = 15,
	    section = panelSection
    )
    default boolean showPvp() {return false;}

    @ConfigItem(
	    keyName = "showHighRisk",
	    name = "Show High-Risk",
	    description = "Show or hide high-risk worlds.",
	    position = 16,
	    section = panelSection
    )
    default boolean showHighRisk() {return true;}

    @ConfigItem(
	    keyName = "totalLevelType",
	    name = "Max total world",
	    description = "Hides worlds with a total level requirement higher than this.",
	    position = 17,
	    section = panelSection
    )
    default TotalLevelType totalLevelType() { return TotalLevelType.TOTAL_2200; }

    @ConfigSection(
	    name = "Region Toggles",
	    description = "Toggle regions to be displayed in the side-panel",
	    position = 18
    )
    String regionSection = "Region Toggles";

    @ConfigItem(
	    keyName = RegionKeyName.KEY_ASGARNIA,
	    name = "Asgarnia",
	    description = "Show or hide this region.",
	    position = 19,
	    section = regionSection
    )
    default boolean asgarnia() {return true;}

    @ConfigItem(
	    keyName = RegionKeyName.KEY_KARAMJA,
	    name = "Crandor/Karamja",
	    description = "Show or hide this region.",
	    position = 20,
	    section = regionSection
    )
    default boolean karamja() {return true;}

    @ConfigItem(
	    keyName = RegionKeyName.KEY_FELDIP,
	    name = "Feldip Hills/Isle Of Souls",
	    description = "Show or hide this region.",
	    position = 21,
	    section = regionSection
    )
    default boolean feldip() {return true;}

    @ConfigItem(
	    keyName = RegionKeyName.KEY_FOSSIL,
	    name = "Fossil Island/Mos Le Harmless",
	    description = "Show or hide this region.",
	    position = 22,
	    section = regionSection
    )
    default boolean fossil() {return true;}

    @ConfigItem(
	    keyName = RegionKeyName.KEY_FREMMENIK,
	    name = "Fremmenik/Lunar Isle",
	    description = "Show or hide this region.",
	    position = 23,
	    section = regionSection
    )
    default boolean fremmenik() {return true;}

    @ConfigItem(
	    keyName = RegionKeyName.KEY_KOUREND,
	    name = "Kourend",
	    description = "Show or hide this region.",
	    position = 24,
	    section = regionSection
    )
    default boolean kourend() {return true;}

    @ConfigItem(
	    keyName = RegionKeyName.KEY_KANDARIN,
	    name = "Kandarin",
	    description = "Show or hide this region.",
	    position = 25,
	    section = regionSection
    )
    default boolean kandarin() {return true;}

    @ConfigItem(
	    keyName = RegionKeyName.KEY_KEBOS,
	    name = "Kebos Lowlands",
	    description = "Show or hide this region.",
	    position = 26,
	    section = regionSection
    )
    default boolean kebos() {return true;}

    @ConfigItem(
	    keyName = RegionKeyName.KEY_DESERT,
	    name = "Desert",
	    description = "Show or hide this region.",
	    position = 27,
	    section = regionSection
    )
    default boolean desert() {return true;}

    @ConfigItem(
	    keyName = RegionKeyName.KEY_MISTHALIN,
	    name = "Misthalin",
	    description = "Show or hide this region.",
	    position = 28,
	    section = regionSection
    )
    default boolean misthalin() {return true;}

    @ConfigItem(
	    keyName = RegionKeyName.KEY_MORYTANIA,
	    name = "Morytania",
	    description = "Show or hide this region.",
	    position = 29,
	    section = regionSection
    )
    default boolean morytania() {return true;}

    @ConfigItem(
	    keyName = RegionKeyName.KEY_GNOME,
	    name = "Piscatoris/Gnome Stronghold",
	    description = "Show or hide this region.",
	    position = 30,
	    section = regionSection
    )
    default boolean gnome() {return true;}

    @ConfigItem(
	    keyName = RegionKeyName.KEY_TIRANNWN,
	    name = "Tirannwn",
	    description = "Show or hide this region.",
	    position = 31,
	    section = regionSection
    )
    default boolean tirannwn() {return true;}

    @ConfigItem(
	    keyName = RegionKeyName.KEY_WILDERNESS,
	    name = "Wilderness",
	    description = "Show or hide this region.",
	    position = 32,
	    section = regionSection
    )
    default boolean wilderness() {return true;}

    @ConfigItem(
	    keyName = RegionKeyName.KEY_VARLAMORE,
	    name = "Varlamore",
	    description = "Show or hide this region.",
	    position = 33,
	    section = regionSection
    )
    default boolean varlamore() {return true;}

    @ConfigItem(
	    keyName = RegionKeyName.KEY_UNKNOWN,
	    name = "Unknown/Unscoped",
	    description = "Show or hide stars where the region is unscoped.",
	    position = 34,
	    section = regionSection
    )
    default boolean unknown() {return true;}
}
