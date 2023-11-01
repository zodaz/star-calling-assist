package com.starcallingassist;

import com.starcallingassist.constants.RegionKeyName;
import com.starcallingassist.enums.ChatLogLevel;
import com.starcallingassist.modules.sidepanel.enums.TotalLevelType;
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
		name = "Scout Settings",
		description = "Settings to configure the scouting part of the plugin.",
		position = 2,
		closedByDefault = true
	)
	String scoutingSection = "Scout Settings";

	@ConfigItem(
		keyName = "includeIgn",
		name = "Send in-game name",
		description = "Includes your in-game name with your calls. This is required if you want the stars you find " +
			"to count towards your called stars total.",
		position = 3,
		section = scoutingSection
	)
	default boolean includeIgn()
	{
		return false;
	}

	@ConfigItem(
		keyName = "autoCall",
		name = "Auto call stars",
		description = "Automatically call stars as they appear or fully depletes",
		position = 4,
		section = scoutingSection
	)
	default boolean autoCall()
	{
		return true;
	}

	@ConfigItem(
		keyName = "updateStar",
		name = "Auto update stars",
		description = "Posts a new call when the tier of a star changes (Auto call must be enabled)",
		position = 5,
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
		position = 6,
		section = scoutingSection
	)
	default boolean callHorn()
	{
		return false;
	}

	@ConfigSection(
		name = "Display Settings",
		description = "Settings to configure the layout of active stars within the side panel.",
		position = 7,
		closedByDefault = true
	)
	String displaySection = "Display Settings";

	@ConfigItem(
		keyName = "showTier",
		name = "Show Tier",
		description = "Displays the current tier of the star in the list.",
		position = 8,
		section = displaySection
	)
	default boolean showTier()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showFoundBy",
		name = "Show Found By",
		description = "Displays the name of the player who first discovered the star in the list.",
		position = 9,
		section = displaySection
	)
	default boolean showFoundBy()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showDeadTime",
		name = "Show Death Time Estimate",
		description = "Displays the estimated time (in minutes) until the star is dead.",
		position = 10,
		section = displaySection
	)
	default boolean showDeadTime()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showWorldType",
		name = "Show World Type",
		description = "Displays the world type (e.g. PvP, 2200 total) in the list.",
		position = 11,
		section = displaySection
	)
	default boolean showWorldType()
	{
		return true;
	}

	@ConfigItem(
		keyName = "logLevel",
		name = "Chat Log Level",
		description = "To what extent you want to see log messages from the plugin in the game chat.",
		position = 12,
		section = displaySection
	)
	default ChatLogLevel logLevel()
	{
		return ChatLogLevel.NORMAL;
	}

	@ConfigItem(
		keyName = "estimateTier",
		name = "Use estimated tiers",
		description = "Estimates the current tier of stars in the list, instead of showing the last known tier.",
		position = 13,
		section = displaySection
	)
	default boolean estimateTier()
	{
		return true;
	}

	@ConfigSection(
		name = "Star Filters",
		description = "Settings to filter out certain stars.",
		position = 14,
		closedByDefault = true
	)
	String starSection = "Star Filters";

	@ConfigItem(
		keyName = "minTier",
		name = "Minimum Tier",
		description = "Lowest tier of stars to be displayed in the side-panel.",
		position = 15,
		section = starSection
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
		position = 16,
		section = starSection
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
		keyName = "minDeadTime",
		name = "Minimum Dead Time",
		description = "Hides stars that are estimated to be depleted in less than the specified amount of minutes",
		position = 17,
		section = starSection
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

	@ConfigSection(
		name = "World Filters",
		description = "Settings to filter out certain worlds.",
		position = 18,
		closedByDefault = true
	)
	String worldSection = "World Filters";

	@ConfigItem(
		keyName = "showF2P",
		name = "Show Free-to-Play",
		description = "Show or hide f2p worlds.",
		position = 19,
		section = worldSection
	)
	default boolean showF2P()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showMembers",
		name = "Show Members",
		description = "Show or hide members worlds.",
		position = 20,
		section = worldSection
	)
	default boolean showMembers()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showPvp",
		name = "Show PvP",
		description = "Show or hide PvP worlds.",
		position = 21,
		section = worldSection
	)
	default boolean showPvp()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showHighRisk",
		name = "Show High-Risk PvP",
		description = "Show or hide high-risk PvP worlds.",
		position = 22,
		section = worldSection
	)
	default boolean showHighRisk()
	{
		return false;
	}

	@ConfigItem(
		keyName = "totalLevelType",
		name = "Max total world",
		description = "Hides worlds with a total level requirement higher than this.",
		position = 23,
		section = worldSection
	)
	default TotalLevelType totalLevelType()
	{
		return TotalLevelType.TOTAL_2200;
	}

	@ConfigSection(
		name = "Region Filters",
		description = "Settings to filter out stars in certain regions.",
		position = 24,
		closedByDefault = true
	)
	String regionSection = "Region Filters";

	@ConfigItem(
		keyName = RegionKeyName.KEY_ASGARNIA,
		name = "Asgarnia",
		description = "Show or hide this region.",
		position = 25,
		section = regionSection
	)
	default boolean asgarnia()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_KARAMJA,
		name = "Crandor/Karamja",
		description = "Show or hide this region.",
		position = 26,
		section = regionSection
	)
	default boolean karamja()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_FELDIP,
		name = "Feldip Hills/Isle Of Souls",
		description = "Show or hide this region.",
		position = 27,
		section = regionSection
	)
	default boolean feldip()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_FOSSIL,
		name = "Fossil Island/Mos Le Harmless",
		description = "Show or hide this region.",
		position = 28,
		section = regionSection
	)
	default boolean fossil()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_FREMMENIK,
		name = "Fremmenik/Lunar Isle",
		description = "Show or hide this region.",
		position = 29,
		section = regionSection
	)
	default boolean fremmenik()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_KOUREND,
		name = "Kourend",
		description = "Show or hide this region.",
		position = 30,
		section = regionSection
	)
	default boolean kourend()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_KANDARIN,
		name = "Kandarin",
		description = "Show or hide this region.",
		position = 31,
		section = regionSection
	)
	default boolean kandarin()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_KEBOS,
		name = "Kebos Lowlands",
		description = "Show or hide this region.",
		position = 32,
		section = regionSection
	)
	default boolean kebos()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_DESERT,
		name = "Desert",
		description = "Show or hide this region.",
		position = 33,
		section = regionSection
	)
	default boolean desert()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_MISTHALIN,
		name = "Misthalin",
		description = "Show or hide this region.",
		position = 34,
		section = regionSection
	)
	default boolean misthalin()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_MORYTANIA,
		name = "Morytania",
		description = "Show or hide this region.",
		position = 35,
		section = regionSection
	)
	default boolean morytania()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_GNOME,
		name = "Piscatoris/Gnome Stronghold",
		description = "Show or hide this region.",
		position = 36,
		section = regionSection
	)
	default boolean gnome()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_TIRANNWN,
		name = "Tirannwn",
		description = "Show or hide this region.",
		position = 37,
		section = regionSection
	)
	default boolean tirannwn()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_WILDERNESS,
		name = "Wilderness",
		description = "Show or hide this region.",
		position = 38,
		section = regionSection
	)
	default boolean wilderness()
	{
		return true;
	}

	@ConfigItem(
		keyName = RegionKeyName.KEY_UNKNOWN,
		name = "Unknown / Unconfirmed",
		description = "Show or hide stars that haven't been confirmed / mapped to a region yet.",
		position = 39,
		section = regionSection
	)
	default boolean unknown()
	{
		return false;
	}
}
