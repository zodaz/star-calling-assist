package com.starcallingassist;

import com.starcallingassist.constants.RegionKeyName;
import com.starcallingassist.enums.ChatLogLevel;
import com.starcallingassist.modules.sidepanel.enums.OrderBy;
import com.starcallingassist.modules.sidepanel.enums.TotalLevelType;
import com.starcallingassist.modules.worldmapoverlay.enums.WorldMapDisplayLevel;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

@ConfigGroup(StarCallingAssistConfig.CONFIG_GROUP)
public interface StarCallingAssistConfig extends Config
{
	static String CONFIG_GROUP = "starcallingassistplugin";

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

	//#region World Map Settings
	@ConfigSection(
		name = "World Map Settings",
		description = "Settings to configure the world map part of the plugin.",
		position = 2,
		closedByDefault = true
	)
	String worldMapSection = "World Map Settings";

	@ConfigItem(
		keyName = "worldMapStarDetails",
		name = "Selected star details",
		description = "Whether to display details about the star location selected on the world map. This includes " +
			"transport options, quest requirements and all worlds that have an active star at the selected location.",
		position = 0,
		section = worldMapSection
	)
	default boolean worldMapStarDetails()
	{
		return true;
	}

	@ConfigItem(
		keyName = "worldMapDisplayLevel",
		name = "Show on world map",
		description = "Choose which stars to display on the world map.",
		position = 1,
		section = worldMapSection
	)
	default WorldMapDisplayLevel worldMapDisplayLevel()
	{
		return WorldMapDisplayLevel.NONE;
	}
	//#endregion

	//#region Scout Settings
	@ConfigSection(
		name = "Scout Settings",
		description = "Settings to configure the scouting part of the plugin.",
		position = 3,
		closedByDefault = true
	)
	String scoutingSection = "Scout Settings";

	@ConfigItem(
		keyName = "includeIgn",
		name = "Send in-game name",
		description = "Includes your in-game name with your calls. This is required if you want the stars you find " +
			"to count towards your called stars total.",
		position = 0,
		section = scoutingSection
	)
	default boolean includeIgn()
	{
		return false;
	}

	@ConfigItem(
		keyName = "autoCall",
		name = "Auto call stars",
		description = "Automatically call stars as they appear or fully depletes.",
		position = 1,
		section = scoutingSection
	)
	default boolean autoCall()
	{
		return true;
	}

	@ConfigItem(
		keyName = "updateStar",
		name = "Auto update stars",
		description = "Posts a new call when the tier of a star changes (Auto call must be enabled).",
		position = 2,
		section = scoutingSection
	)
	default boolean updateStar()
	{
		return true;
	}

	@ConfigItem(
		keyName = "callHorn",
		name = "Call Button",
		description = "Enables a button which can be used to call a star.",
		position = 3,
		section = scoutingSection
	)
	default boolean callHorn()
	{
		return false;
	}

	@ConfigItem(
		keyName = "scoutOverlay",
		name = "Scout Overlay",
		description = "Enables an overlay which helps with accurately and effortlessly scouting stars.",
		position = 4,
		section = scoutingSection
	)
	default boolean scoutOverlay()
	{
		return false;
	}
	//#endregion

	//#region General Settings
	@ConfigSection(
		name = "General Settings",
		description = "Customizable settings that augment your in-game experience.",
		position = 4
	)
	String generalSection = "General Settings";

	@ConfigItem(
		keyName = "starDetailsOverlay",
		name = "Show details overlay",
		description = "Whether to display information about a nearby star.",
		position = 0,
		section = generalSection
	)
	default boolean starDetailsOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "logLevel",
		name = "Chat Log Level",
		description = "To what extent you want to see log messages from the plugin in the game chat.",
		position = 1,
		section = generalSection
	)
	default ChatLogLevel logLevel()
	{
		return ChatLogLevel.NORMAL;
	}
	//#endregion

	//#region Side Panel Layout
	@ConfigSection(
		name = "Side-Panel Layout",
		description = "Settings to configure the layout of active stars within the side-panel.",
		position = 5,
		closedByDefault = true
	)
	String sidePanelLayoutSection = "Side Panel Layout";

	@ConfigItem(
		keyName = "showTier",
		name = "Show Tier",
		description = "Displays the current tier of the star in the list.",
		position = 0,
		section = sidePanelLayoutSection
	)
	default boolean showTier()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showFoundBy",
		name = "Show Found By",
		description = "Displays the name of the player who first discovered the star in the list.",
		position = 1,
		section = sidePanelLayoutSection
	)
	default boolean showFoundBy()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showDeadTime",
		name = "Show Dead Time Estimate",
		description = "Displays the estimated time (in minutes) until the star is dead.",
		position = 2,
		section = sidePanelLayoutSection
	)
	default boolean showDeadTime()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showWorldType",
		name = "Show World Type",
		description = "Displays the world type (e.g. PvP, 2200 total) in the list.",
		position = 3,
		section = sidePanelLayoutSection
	)
	default boolean showWorldType()
	{
		return true;
	}

	@ConfigItem(
		keyName = "orderBy",
		name = "Default Sorting",
		description = "In what order you want the stars to be sorted in the side-panel by default.",
		position = 4,
		section = sidePanelLayoutSection
	)
	default OrderBy orderBy()
	{
		return OrderBy.LOCATION;
	}
	//#endregion

	//#region Star Filters
	@ConfigSection(
		name = "Star Filters",
		description = "Settings to filter out certain stars and tiers from the side-panel.",
		position = 6,
		closedByDefault = true
	)
	String starFilterSection = "Star Filters";

	@ConfigItem(
		keyName = "minTier",
		name = "Minimum Tier",
		description = "Lowest tier of stars to be displayed in the side-panel.",
		position = 0,
		section = starFilterSection
	)
	@Range(
		min = 1,
		max = 9
	)
	default int minTier()
	{
		return 1;
	}

	@ConfigItem(
		keyName = "maxTier",
		name = "Maximum Tier",
		description = "Highest tier of stars to be displayed in the side-panel.",
		position = 1,
		section = starFilterSection
	)
	@Range(
		min = 1,
		max = 9
	)
	default int maxTier()
	{
		return 9;
	}

	@ConfigItem(
		keyName = "estimateTier",
		name = "Use estimated tiers",
		description = "Estimates the current tier of stars in the list, instead of displaying the last known tier.",
		position = 2,
		section = starFilterSection
	)
	default boolean estimateTier()
	{
		return true;
	}

	@ConfigItem(
		keyName = "minDeadTime",
		name = "Minimum Dead Time",
		description = "Hides stars that are estimated to be depleted in less than the specified amount of minutes.",
		position = 3,
		section = starFilterSection
	)
	@Range(
		min = -90,
		max = 90
	)
	@Units(
		value = Units.MINUTES
	)
	default int minDeadTime()
	{
		return -5;
	}
	//#endregion

	//#region World Filters
	@ConfigSection(
		name = "World Filters",
		description = "Settings to filter out certain worlds from the side-panel.",
		position = 7,
		closedByDefault = true
	)
	String worldFilterSection = "World Filters";

	@ConfigItem(
		keyName = "showF2P",
		name = "Show Free-to-Play",
		description = "Show or hide F2P worlds.",
		position = 0,
		section = worldFilterSection
	)
	default boolean showF2P()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showMembers",
		name = "Show Members",
		description = "Show or hide members worlds.",
		position = 1,
		section = worldFilterSection
	)
	default boolean showMembers()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showPvp",
		name = "Show PvP",
		description = "Show or hide PvP worlds.",
		position = 2,
		section = worldFilterSection
	)
	default boolean showPvp()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showHighRisk",
		name = "Show High-Risk PvP",
		description = "Show or hide high-risk PvP worlds.",
		position = 3,
		section = worldFilterSection
	)
	default boolean showHighRisk()
	{
		return false;
	}

	@ConfigItem(
		keyName = "totalLevelType",
		name = "Max Total World",
		description = "Hides worlds with a total level requirement higher than this.",
		position = 4,
		section = worldFilterSection
	)
	default TotalLevelType totalLevelType()
	{
		return TotalLevelType.TOTAL_2200;
	}
	//#endregion

	//#region Region Filters
	@ConfigSection(
		name = "Region Filters",
		description = "Settings to filter out stars in certain regions from the side-panel.",
		position = 8,
		closedByDefault = true
	)
	String regionFilterSection = "Region Filters";

	@ConfigItem(
		keyName = RegionKeyName.KEY_ASGARNIA,
		name = "Asgarnia",
		description = "Show or hide this region.",
		position = 0,
		section = regionFilterSection
	)
	default boolean asgarnia()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_KARAMJA,
		name = "Crandor/Karamja",
		description = "Show or hide this region.",
		position = 1,
		section = regionFilterSection
	)
	default boolean karamja()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_FELDIP,
		name = "Feldip Hills/Isle Of Souls",
		description = "Show or hide this region.",
		position = 2,
		section = regionFilterSection
	)
	default boolean feldip()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_FOSSIL,
		name = "Fossil Island/Mos Le Harmless",
		description = "Show or hide this region.",
		position = 3,
		section = regionFilterSection
	)
	default boolean fossil()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_FREMMENIK,
		name = "Fremmenik/Lunar Isle",
		description = "Show or hide this region.",
		position = 4,
		section = regionFilterSection
	)
	default boolean fremmenik()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_KOUREND,
		name = "Kourend",
		description = "Show or hide this region.",
		position = 5,
		section = regionFilterSection
	)
	default boolean kourend()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_KANDARIN,
		name = "Kandarin",
		description = "Show or hide this region.",
		position = 6,
		section = regionFilterSection
	)
	default boolean kandarin()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_KEBOS,
		name = "Kebos Lowlands",
		description = "Show or hide this region.",
		position = 7,
		section = regionFilterSection
	)
	default boolean kebos()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_DESERT,
		name = "Desert",
		description = "Show or hide this region.",
		position = 8,
		section = regionFilterSection
	)
	default boolean desert()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_MISTHALIN,
		name = "Misthalin",
		description = "Show or hide this region.",
		position = 9,
		section = regionFilterSection
	)
	default boolean misthalin()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_MORYTANIA,
		name = "Morytania",
		description = "Show or hide this region.",
		position = 10,
		section = regionFilterSection
	)
	default boolean morytania()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_GNOME,
		name = "Piscatoris/Gnome Stronghold",
		description = "Show or hide this region.",
		position = 11,
		section = regionFilterSection
	)
	default boolean gnome()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_TIRANNWN,
		name = "Tirannwn",
		description = "Show or hide this region.",
		position = 12,
		section = regionFilterSection
	)
	default boolean tirannwn()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_VARLAMORE,
		name = "Varlamore",
		description = "Show or hide this region.",
		position = 13,
		section = regionFilterSection
	)
	default boolean varlamore()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_WILDERNESS,
		name = "Wilderness",
		description = "Show or hide this region.",
		position = 14,
		section = regionFilterSection
	)
	default boolean wilderness()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_UNKNOWN,
		name = "Unknown / Unconfirmed",
		description = "Show or hide stars that haven't been confirmed / mapped to a region yet.",
		position = 15,
		section = regionFilterSection
	)
	default boolean unknown()
	{
		return false;
	}
	//#endregion
}
