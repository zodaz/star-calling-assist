package com.starcallingassist.enums;

import com.starcallingassist.objects.StarLocationTransport;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import static net.runelite.api.Constants.CHUNK_SIZE;
import net.runelite.api.Quest;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

/**
 * Enum specifying all star locations and the static data associated with each location.
 */
@Getter
@AllArgsConstructor
public enum StarLocationDetails
{
	//
 	// ASGARNIA
 	//
	RIMMINGTON_MINE("Rimmington mine", Region.ASGARNIA, new Point(2974, 3241), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Rimmington POH", TransportType.HOUSE_PORTAL),
			new StarLocationTransport("Port Sarim rat pits", TransportType.MINIGAME_TELEPORT),
			new StarLocationTransport("Air Altar", TransportType.RING_OF_THE_ELEMENTS),
			new StarLocationTransport("Crafting guild", TransportType.SKILLS_NECKLACE)
		}),
	CRAFTING_GUILD("Crafting guild", Region.ASGARNIA, new Point(2940, 3280), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Crafting guild", TransportType.SKILLS_NECKLACE),
			new StarLocationTransport("Air Altar", TransportType.RING_OF_THE_ELEMENTS),
			new StarLocationTransport("Rimmington POH", TransportType.HOUSE_PORTAL),
			new StarLocationTransport("Falador", TransportType.FALADOR_TELEPORT)
		}),
	WEST_FALADOR_MINE("West Falador mine", Region.ASGARNIA, new Point(2906, 3355), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Falador", TransportType.FALADOR_TELEPORT),
			new StarLocationTransport("Crafting Guild", TransportType.SKILLS_NECKLACE)
		}),
	EAST_FALADOR_BANK("East Falador bank", Region.ASGARNIA, new Point(3030, 3348), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Falador Park", TransportType.RING_OF_WEALTH),
			new StarLocationTransport("Mining Guild", TransportType.SKILLS_NECKLACE),
			new StarLocationTransport("Falador", TransportType.FALADOR_TELEPORT)
		}),
	DWARVEN_MINE_ENTRANCE("North Dwarven Mine entrance", Region.ASGARNIA, new Point(3018, 3443), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Lassar", TransportType.LASSAR_TELEPORT),
			new StarLocationTransport("Edgeville", TransportType.AMULET_OF_GLORY),
			new StarLocationTransport("Falador", TransportType.FALADOR_TELEPORT)
		}),
	TAVERLEY_HOUSE_PORTAL("Taverley house portal", Region.ASGARNIA, new Point(2882, 3474), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Taverley POH", TransportType.HOUSE_PORTAL),
			new StarLocationTransport("Burthorpe Games room", TransportType.GAMES_NECKLACE),
			new StarLocationTransport("Falador", TransportType.FALADOR_TELEPORT)
		}),

	//
 	// KARAMJA
	//
	BRIMHAVEN_GOLD_MINE("Brimhaven northwest gold mine", Region.KARAMJA, new Point(2736, 3221), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Brimhaven POH", TransportType.HOUSE_PORTAL),
			new StarLocationTransport("Brimhaven", TransportType.SPIRIT_TREE_FARMING),
			new StarLocationTransport("Brimhaven (30gp)", TransportType.SHIP_FROM_ARDOUGNE),
			new StarLocationTransport("Tai Bwo Wannai", TransportType.SCROLL_TELEPORT)
		}),
	BRIMHAVEN_POH("Southwest of Brimhaven Poh", Region.KARAMJA, new Point(2742, 3143), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Brimhaven POH", TransportType.HOUSE_PORTAL),
			new StarLocationTransport("Brimhaven", TransportType.SPIRIT_TREE_FARMING),
			new StarLocationTransport("Brimhaven (30gp)", TransportType.SHIP_FROM_ARDOUGNE),
			new StarLocationTransport("Tai Bwo Wannai", TransportType.SCROLL_TELEPORT)
		}),
	NATURE_ALTAR_MINE("Nature Altar mine north of Shilo", Region.KARAMJA, new Point(2845, 3037), null,
		new StarLocationTransport[]{
			new StarLocationTransport("CKR", TransportType.FAIRY_RING),
			new StarLocationTransport("Kaleb Paramaya (79 Agility)", TransportType.ACHIEVEMENT_DIARY_CAPE),
			new StarLocationTransport("Tai Bwo Wannai", TransportType.SCROLL_TELEPORT),
			new StarLocationTransport("Gem Mine (79 Agility)", TransportType.KARAMJA_GLOVES_3)
		}),
	SHILO_VILLAGE("Shilo Village gem mine", Region.KARAMJA, new Point(2827, 2999), Quest.SHILO_VILLAGE,
		new StarLocationTransport[]{
			new StarLocationTransport("Gem Mine", TransportType.KARAMJA_GLOVES_3),
			new StarLocationTransport("Kaleb Paramaya", TransportType.ACHIEVEMENT_DIARY_CAPE),
			new StarLocationTransport("Shilo Village (10 - 200gp)", TransportType.BRIMHAVEN_CART),
			new StarLocationTransport("CKR", TransportType.FAIRY_RING)
		}),
	NORTH_CRANDOR("North Crandor", Region.KARAMJA, new Point(2835, 3296), Quest.DRAGON_SLAYER_I,
		new StarLocationTransport[]{
			new StarLocationTransport("BLP", TransportType.FAIRY_RING),
			new StarLocationTransport("Tzhaar Fight pits", TransportType.MINIGAME_TELEPORT),
			new StarLocationTransport("Karamja", TransportType.AMULET_OF_GLORY)
		}),
	SOUTH_CRANDOR("South Crandor", Region.KARAMJA, new Point(2822, 3238), Quest.DRAGON_SLAYER_I,
		new StarLocationTransport[]{
			new StarLocationTransport("BLP", TransportType.FAIRY_RING),
			new StarLocationTransport("Tzhaar Fight pits", TransportType.MINIGAME_TELEPORT),
			new StarLocationTransport("Karamja", TransportType.AMULET_OF_GLORY)
		}),

	//
 	// DESERT
	//
	AL_KHARID_MINE("Al Kharid mine", Region.DESERT, new Point(3296, 3298), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Fire Altar", TransportType.RING_OF_THE_ELEMENTS),
			new StarLocationTransport("Al Kharid PvP Arena", TransportType.RING_OF_DUELING)
		}),
	AL_KHARID_BANK("Al Kharid bank", Region.DESERT, new Point(3276, 3164), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Al Kharid", TransportType.AMULET_OF_GLORY),
			new StarLocationTransport("Al Kharid PvP Arena", TransportType.RING_OF_DUELING)
		}),
	PVP_ARENA("North of Al Kharid PvP Arena", Region.DESERT, new Point(3351, 3281), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Fire Altar", TransportType.RING_OF_THE_ELEMENTS),
			new StarLocationTransport("Mage Training Arena", TransportType.MINIGAME_TELEPORT),
			new StarLocationTransport("Al Kharid PvP Arena", TransportType.RING_OF_DUELING)
		}),
	UZER("Nw of Uzer (Eagle's Eyrie)", Region.DESERT, new Point(3424, 3160), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Desert heat", TransportType.WARNING),
			new StarLocationTransport("Eagle's Eyrie", TransportType.NECKLACE_OF_PASSAGE),
			new StarLocationTransport("DLQ", TransportType.FAIRY_RING),
			new StarLocationTransport("Uzer (0 - 200gp)", TransportType.MAGIC_CARPET_FROM_SHANTAY_PASS)
		}),
	NARDAH("Nardah bank", Region.DESERT, new Point(3434, 2889), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Nardah", TransportType.DESERT_AMULET),
			new StarLocationTransport("Nardah", TransportType.SCROLL_TELEPORT),
			new StarLocationTransport("DLQ", TransportType.FAIRY_RING),
			new StarLocationTransport("Nardah (0 - 200gp)", TransportType.MAGIC_CARPET_FROM_POLLNIVNEACH)
		}),
	AGILITY_PYRAMID("Agility Pyramid mine", Region.DESERT, new Point(3316, 2867), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Desert heat", TransportType.WARNING),
			new StarLocationTransport("Jaleustrophos", TransportType.PHARAOS_SCEPTRE),
			new StarLocationTransport("Nardah", TransportType.DESERT_AMULET),
			new StarLocationTransport("Nardah", TransportType.SCROLL_TELEPORT),
			new StarLocationTransport("Sophanem (0 - 200gp)", TransportType.MAGIC_CARPET_FROM_POLLNIVNEACH)
		}),
	DESERT_QUARRY("Desert Quarry mine", Region.DESERT, new Point(3171, 2910), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Desert heat", TransportType.WARNING),
			new StarLocationTransport("Bandit Camp Quarry", TransportType.CAMULET_HARD_DESERT_DIARY),
			new StarLocationTransport("Enakhra's Temple", TransportType.CAMULET),
			new StarLocationTransport("Jaldraocht", TransportType.PHARAOS_SCEPTRE),
			new StarLocationTransport("Ruins of Unkah", TransportType.FERRY_FROM_AL_KHARID)
		}),

	//
 	// FELDIP HILLS
	//
	CORSAIR_COVE_BANK("Corsair Cove bank", Region.FELDIP, new Point(2567, 2858), Quest.THE_CORSAIR_CURSE,
		new StarLocationTransport[]{
			new StarLocationTransport("Feldip Hills", TransportType.SPIRIT_TREE),
			new StarLocationTransport("Feldip Hunter area", TransportType.HUNTER_CAPE),
			new StarLocationTransport("Myths' Guild", TransportType.MYTHICAL_CAPE),
			new StarLocationTransport("Corsair Cove", TransportType.SHIP_FROM_RIMMINGTON)
		}),
	CORSAIR_RESOURCE_AREA("Corsair Resource Area", Region.FELDIP, new Point(2483, 2886), Quest.DRAGON_SLAYER_I,
		new StarLocationTransport[]{
			new StarLocationTransport("Feldip Hills", TransportType.SPIRIT_TREE),
			new StarLocationTransport("Myths' Guild", TransportType.MYTHICAL_CAPE),
			new StarLocationTransport("Feldip Hunter area", TransportType.HUNTER_CAPE),
			new StarLocationTransport("Corsair Cove", TransportType.SHIP_FROM_RIMMINGTON)
		}),
	MYTHS_GUILD("Myths' Guild", Region.FELDIP, new Point(2468, 2842), Quest.DRAGON_SLAYER_II,
		new StarLocationTransport[]{
			new StarLocationTransport("Myths' Guild", TransportType.MYTHICAL_CAPE),
			new StarLocationTransport("Feldip Hills", TransportType.SPIRIT_TREE)
		}),
	FELDIP_HILLS_AKS("Feldip Hills (aks fairy ring)", Region.FELDIP, new Point(2571, 2964), null,
		new StarLocationTransport[]{
			new StarLocationTransport("AKS", TransportType.FAIRY_RING),
			new StarLocationTransport("Lemantolly Undri", TransportType.GNOME_GLIDER),
			new StarLocationTransport("Feldip Hunter area", TransportType.HUNTER_CAPE)
		}),
	RANTZ_CAVE("Rantz cave", Region.FELDIP, new Point(2630, 2993), null,
		new StarLocationTransport[]{
			new StarLocationTransport("AKS", TransportType.FAIRY_RING),
			new StarLocationTransport("Lemantolly Undri", TransportType.GNOME_GLIDER)
		}),
	SOUL_WARS("Soul Wars", Region.FELDIP, new Point(2200, 2792), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Soul Wars", TransportType.MINIGAME_TELEPORT),
			new StarLocationTransport("Soul Wars", TransportType.EDGEVILLE_SOUL_WARS_PORTAL)
		}),

	//
 	// FOSIL ISLAND / MOS LE' HARMLESS
	//
	VOLCANIC_MINE("Fossil Island Volcanic Mine entrance", Region.FOSSIL, new Point(3818, 3801), Quest.BONE_VOYAGE,
		new StarLocationTransport[]{
			new StarLocationTransport("Fossil Island and then Magic Mushtree to Verdant Valley", TransportType.DIGSITE_PENDANT),
			new StarLocationTransport("Digsite and then travel with Barge guard", TransportType.DIGSITE_PENDANT)
		}),
	FOSSIL_ISLAND_RUNE_ROCKS("Fossil Island rune rocks", Region.FOSSIL, new Point(3774, 3814), Quest.BONE_VOYAGE,
		new StarLocationTransport[]{
			new StarLocationTransport("Digsite and then travel with Barge guard", TransportType.DIGSITE_PENDANT),
			new StarLocationTransport("Fossil Island and then Magic Mushtree to Verdant Valley", TransportType.DIGSITE_PENDANT)
		}),
	MOS_LE_HARMLESS("Mos Le'Harmless west bank", Region.FOSSIL, new Point(3686, 2969), Quest.CABIN_FEVER,
		new StarLocationTransport[]{
			new StarLocationTransport("Mos Le'Harmless", TransportType.SCROLL_TELEPORT),
			new StarLocationTransport("Mos Le'Harmless", TransportType.CHARTER_SHIP),
			new StarLocationTransport("Trouble Brewing", TransportType.MINIGAME_TELEPORT)
		}),

	//
 	// FREMENNIK
	//
	KELDAGRIM_ENTRANCE_MINE("Keldagrim entrance mine", Region.FREMMENIK, new Point(2727, 3683), null,
		new StarLocationTransport[]{
			new StarLocationTransport("DKS", TransportType.FAIRY_RING),
			new StarLocationTransport("Rellekka (57 Agility)", TransportType.FREMENNIK_SEA_BOOTS),
			new StarLocationTransport("Rellekka", TransportType.ENCHANTED_LYRE),
			new StarLocationTransport("Rellekka", TransportType.HOUSE_PORTAL),
			new StarLocationTransport("Fremennik Slayer Dungeon", TransportType.SLAYER_RING)
		}),
	RELLEKKA_MINE("Rellekka mine", Region.FREMMENIK, new Point(2683, 3699), Quest.THE_FREMENNIK_TRIALS,
		new StarLocationTransport[]{
			new StarLocationTransport("Rellekka", TransportType.FREMENNIK_SEA_BOOTS),
			new StarLocationTransport("Rellekka", TransportType.ENCHANTED_LYRE),
			new StarLocationTransport("Rellekka", TransportType.HOUSE_PORTAL),
			new StarLocationTransport("DKS (57 Agility)", TransportType.FAIRY_RING)
		}),
	JATIZSO("Jatizso mine entrance", Region.FREMMENIK, new Point(2393, 3814), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Jatizso (Fremennik Elite Diary)", TransportType.ENCHANTED_LYRE),
			new StarLocationTransport("Jatizso", TransportType.RELLEKKA_SHIP)
		}),
	NEITIZNOT("Neitiznot south of rune rock", Region.FREMMENIK, new Point(2375, 3832), Quest.THE_FREMENNIK_ISLES,
		new StarLocationTransport[]{
			new StarLocationTransport("Aggressive Ice Trolls will attack you", TransportType.WARNING),
			new StarLocationTransport("Neitiznot (Fremennik Elite Diary)", TransportType.ENCHANTED_LYRE),
			new StarLocationTransport("Neitiznot", TransportType.RELLEKKA_SHIP)
		}),
	MISCELLANIA("Miscellania mine (cip fairy ring)", Region.FREMMENIK, new Point(2528, 3887), Quest.THE_FREMENNIK_TRIALS,
		new StarLocationTransport[]{
			new StarLocationTransport("CIP", TransportType.FAIRY_RING),
			new StarLocationTransport("Miscellania", TransportType.RING_OF_WEALTH),
			new StarLocationTransport("Miscellania (After Throne of Miscellania)", TransportType.RELLEKKA_SHIP)
		}),
	LUNAR_ISLE("Lunar Isle mine entrance", Region.FREMMENIK, new Point(2139, 3938), Quest.LUNAR_DIPLOMACY,
		new StarLocationTransport[]{
			new StarLocationTransport("Aggressive Suqahs will attack you", TransportType.WARNING),
			new StarLocationTransport("Moonclan/Lunar Isle", TransportType.LUNAR_ISLE_TELEPORT),
			new StarLocationTransport("Lunar Isle", TransportType.SCROLL_TELEPORT),
			new StarLocationTransport("Pirates' Cove and then travel with Captain Bentley to Lunar Isle", TransportType.RELLEKKA_SHIP)
		}),

	//
 	// KANDARIN
	//
	YANILLE_BANK("Yanille bank", Region.KANDARIN, new Point(2602, 3086), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Watchtower (Yanille)", TransportType.WATCH_TOWER),
			new StarLocationTransport("Nightmare Zone", TransportType.MINIGAME_TELEPORT),
			new StarLocationTransport("Yanille", TransportType.HOUSE_PORTAL),
			new StarLocationTransport("Watchtower", TransportType.WATCH_TOWER)
		}),
	PORT_KHAZARD_MINE("Port Khazard mine", Region.KANDARIN, new Point(2624, 3141), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Khazard", TransportType.KHAZARD_TELEPORT),
			new StarLocationTransport("Nightmare Zone", TransportType.MINIGAME_TELEPORT),
			new StarLocationTransport("Watchtower (Yanille)", TransportType.WATCH_TOWER)
		}),
	ARDOUGNE_MONASTERY("Ardougne Monastery", Region.KANDARIN, new Point(2608, 3233), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Monastery", TransportType.ARDOUGNE_CLOAK),
			new StarLocationTransport("DJP", TransportType.FAIRY_RING),
			new StarLocationTransport("Battlefield of Khazard", TransportType.SPIRIT_TREE)
		}),
	SOUTH_OF_LEGENDS_GUILD("South of Legends' Guild", Region.KANDARIN, new Point(2705, 3333), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Legends' Guild", TransportType.QUEST_CAPE),
			new StarLocationTransport("BLR", TransportType.FAIRY_RING),
			new StarLocationTransport("Ardougne", TransportType.ARDOUGNE_TELEPORT),
			new StarLocationTransport("Farm", TransportType.ARDOUGNE_CLOAK)
		}),
	CATHERBY_BANK("Catherby bank", Region.KANDARIN, new Point(2804, 3434), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Catherby", TransportType.CATHERBY_TELEPORT),
			new StarLocationTransport("Camelot", TransportType.CAMELOT_TELEPORT),
			new StarLocationTransport("Flax Keeper", TransportType.ACHIEVEMENT_DIARY_CAPE)
		}),
	COAL_TRUCKS("Coal Trucks west of Seers'", Region.KANDARIN, new Point(2589, 3478), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Ranging Guild (20 Agility)", TransportType.COMBAT_BRACELET),
			new StarLocationTransport("ALS (20 Agility)", TransportType.FAIRY_RING),
			new StarLocationTransport("Fishing Guild", TransportType.SKILLS_NECKLACE)
		}),

	//
 	// KOUREND
	//
	HOSIDIUS_MINE("Hosidius mine", Region.KOUREND, new Point(1778, 3493), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Tithe Farm", TransportType.MINIGAME_TELEPORT),
			new StarLocationTransport("Hosidius", TransportType.HOUSE_PORTAL),
			new StarLocationTransport("AKR", TransportType.FAIRY_RING),
			new StarLocationTransport("Hosidius South", TransportType.MINECART_NETWORK)
		}),
	PORT_PISCARILIUS_MINE("Port Piscarilius mine in Kourend", Region.KOUREND, new Point(1769, 3709), null,
		new StarLocationTransport[]{
			new StarLocationTransport("The Fisher's Flute", TransportType.KHAREDSTS_MEMOIRS),
			new StarLocationTransport("Port Piscarilius", TransportType.MINECART_NETWORK),
			new StarLocationTransport("Kourend Castle", TransportType.KOUREND_TELEPORT),
			new StarLocationTransport("Port Piscarilius", TransportType.SHIP_FROM_PORT_SARIM)
		}),
	SHAYZIEN_MINE("Shayzien mine south of Kourend Castle", Region.KOUREND, new Point(1597, 3648), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Xeric's Heart", TransportType.XERICS_TALISMAN),
			new StarLocationTransport("Kourend Castle", TransportType.KOUREND_TELEPORT),
			new StarLocationTransport("Shayzien East", TransportType.MINECART_NETWORK)
		}),
	SOUTH_LOVAKENGJ_BANK("South Lovakengj bank", Region.KOUREND, new Point(1534, 3747), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Xeric's Inferno", TransportType.XERICS_TALISMAN),
			new StarLocationTransport("Jewellery of Jubilation", TransportType.KHAREDSTS_MEMOIRS),
			new StarLocationTransport("Lovakengj", TransportType.MINECART_NETWORK),
			new StarLocationTransport("Kourend Castle", TransportType.KOUREND_TELEPORT)
		}),
	LOVAKITE_MINE("Lovakite mine", Region.KOUREND, new Point(1437, 3840), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Xeric's Inferno", TransportType.XERICS_TALISMAN),
			new StarLocationTransport("Jewellery of Jubilation", TransportType.KHAREDSTS_MEMOIRS),
			new StarLocationTransport("Battlefront", TransportType.BATTLEFRONT_TELEPORT)
		}),
	ARCEUUS_DENSE_ESSENCE_MINE("Arceuus dense essence mine", Region.KOUREND, new Point(1760, 3853), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Home teleport", TransportType.ARCEUUS_HOME_TELEPORT),
			new StarLocationTransport("CIS", TransportType.FAIRY_RING),
			new StarLocationTransport("Arceuus Library", TransportType.ARCEUUS_LIBRARY_TELEPORT),
			new StarLocationTransport("Wintertodt Camp", TransportType.GAMES_NECKLACE)
		}),

	//
 	// KEBOS
	//
	MOUNT_KARUULM_BANK("Mount Karuulm bank", Region.KEBOS, new Point(1322, 3816), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Mount Karuulm", TransportType.RADAS_BLESSING_3),
			new StarLocationTransport("CIR", TransportType.FAIRY_RING),
			new StarLocationTransport("Battlefront", TransportType.BATTLEFRONT_TELEPORT),
			new StarLocationTransport("Farming Guild", TransportType.SKILLS_NECKLACE)
		}),
	MOUNT_KARUULM_MINE("Mount Karuulm mine", Region.KEBOS, new Point(1279, 3817), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Mount Karuulm", TransportType.RADAS_BLESSING_3),
			new StarLocationTransport("CIR", TransportType.FAIRY_RING),
			new StarLocationTransport("Battlefront", TransportType.BATTLEFRONT_TELEPORT),
			new StarLocationTransport("Farming Guild", TransportType.SKILLS_NECKLACE)
		}),
	KEBOS_SWAMP_MINE("Kebos Swamp mine", Region.KEBOS, new Point(1210, 3651), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Farming Guild", TransportType.SKILLS_NECKLACE),
			new StarLocationTransport("CIR", TransportType.FAIRY_RING),
			new StarLocationTransport("Battlefront", TransportType.BATTLEFRONT_TELEPORT)
		}),
	COX_BANK("Chambers of Xeric bank", Region.KEBOS, new Point(1258, 3564), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Xeric's Honour", TransportType.XERICS_TALISMAN),
			new StarLocationTransport("Mount Quidamortem", TransportType.MINECART_NETWORK),
			new StarLocationTransport("BLS", TransportType.FAIRY_RING)
		}),

	//
	// MISTHALIN
	//
	VARROCK_EAST_BANK("Varrock east bank", Region.MISTHALIN, new Point(3258, 3408), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Toby", TransportType.ACHIEVEMENT_DIARY_CAPE),
			new StarLocationTransport("Varrock", TransportType.VARROCK_TELEPORT),
			new StarLocationTransport("Varrock rat pits", TransportType.MINIGAME_TELEPORT)
		}),
	SOUTHEAST_VARROCK_MINE("Southeast Varrock mine", Region.MISTHALIN, new Point(3290, 3353), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Senntisten", TransportType.SENNTISTEN_TELEPORT),
			new StarLocationTransport("Champions' Guild", TransportType.CHRONICLE),
			new StarLocationTransport("Champions' Guild", TransportType.COMBAT_BRACELET),
			new StarLocationTransport("Varrock", TransportType.VARROCK_TELEPORT),
		}),
	CHAMPIONS_GUILD_MINE("Champions' Guild mine", Region.MISTHALIN, new Point(3175, 3362), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Champions' Guild", TransportType.COMBAT_BRACELET),
			new StarLocationTransport("Champions' Guild", TransportType.CHRONICLE),
			new StarLocationTransport("Varrock", TransportType.VARROCK_TELEPORT)
		}),
	DRAYNOR_VILLAGE("Draynor Village", Region.MISTHALIN, new Point(3094, 3235), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Twiggy O'Korn", TransportType.ACHIEVEMENT_DIARY_CAPE),
			new StarLocationTransport("Draynor Village", TransportType.AMULET_OF_GLORY),
			new StarLocationTransport("Wizards' Tower", TransportType.NECKLACE_OF_PASSAGE)
		}),
	WEST_LUMBRIDGE_SWAMP_MINE("West Lumbridge Swamp mine", Region.MISTHALIN, new Point(3153, 3150), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Water Altar", TransportType.RING_OF_THE_ELEMENTS),
			new StarLocationTransport("Twiggy O'Korn", TransportType.ACHIEVEMENT_DIARY_CAPE),
			new StarLocationTransport("Draynor Village", TransportType.AMULET_OF_GLORY),
			new StarLocationTransport("Wizards' Tower", TransportType.NECKLACE_OF_PASSAGE),
			new StarLocationTransport("Lumbridge", TransportType.LUMBRIDGE_TELEPORT)
		}),
	EAST_LUMBRIDGE_SWAMP_MINE("East Lumbridge Swamp mine", Region.MISTHALIN, new Point(3230, 3155), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Water Altar", TransportType.RING_OF_THE_ELEMENTS),
			new StarLocationTransport("Hatius Cosaintus", TransportType.ACHIEVEMENT_DIARY_CAPE),
			new StarLocationTransport("Lumbridge", TransportType.LUMBRIDGE_TELEPORT)
		}),

	//
 	// MORYTANIA
	//
	DARKMEYER("Darkmeyer ess. mine entrance", Region.MORYTANIA, new Point(3635, 3340), Quest.SINS_OF_THE_FATHER,
		new StarLocationTransport[]{
			new StarLocationTransport("Darkmeyer", TransportType.DRAKANS_MEDALLION)
		}),
	TOB_BANK("Theatre of Blood bank", Region.MORYTANIA, new Point(3650, 3214), Quest.PRIEST_IN_PERIL,
		new StarLocationTransport[]{
			new StarLocationTransport("Ver Sinhaza", TransportType.DRAKANS_MEDALLION),
			new StarLocationTransport("Sleepe (10k gp)", TransportType.ANDRAS_ROWBOAT_FROM_ECTOFUNTUS)
		}),
	CANIFIS_BANK("Canifis bank", Region.MORYTANIA, new Point(3505, 3485), Quest.PRIEST_IN_PERIL,
		new StarLocationTransport[]{
			new StarLocationTransport("Kharyrll", TransportType.KHARYRLL_TELEPORT),
			new StarLocationTransport("CKS", TransportType.FAIRY_RING),
			new StarLocationTransport("Salve Graveyard", TransportType.SALVE_GRAVEYARD_TELEPORT),
			new StarLocationTransport("Fenkenstrain's Castle", TransportType.FENKENSTRAINS_CASTLE_TELEPORT),
		}),
	BURGH_DE_ROTT_BANK("Burgh de Rott bank", Region.MORYTANIA, new Point(3500, 3219), Quest.IN_AID_OF_THE_MYREQUE,
		new StarLocationTransport[]{
			new StarLocationTransport("Burgh de Rott", TransportType.MORYTANIA_LEGS_3),
			new StarLocationTransport("Mort'ton", TransportType.SCROLL_TELEPORT),
			new StarLocationTransport("Shades of Mort'ton", TransportType.MINIGAME_TELEPORT)
		}),
	ABANDONED_MINE("Abandoned Mine west of Burgh", Region.MORYTANIA, new Point(3451, 3233), Quest.PRIEST_IN_PERIL,
		new StarLocationTransport[]{
			new StarLocationTransport("Burgh de Rott", TransportType.MORYTANIA_LEGS_3),
			new StarLocationTransport("BIP (50 Agility)", TransportType.FAIRY_RING),
			new StarLocationTransport("Mort'ton", TransportType.SCROLL_TELEPORT),
			new StarLocationTransport("Shades of Mort'ton", TransportType.MINIGAME_TELEPORT)
		}),

	//
 	// GNOME
	//
	WEST_OF_GRAND_TREE("West of Grand Tree", Region.GNOME, new Point(2444, 3490), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Grand Tree", TransportType.ROYAL_SEED_POD),
			new StarLocationTransport("Elder Gnome Child", TransportType.ACHIEVEMENT_DIARY_CAPE),
			new StarLocationTransport("Gnome Stronghold", TransportType.SPIRIT_TREE)
		}),
	GONOME_STRONGHOLD_SPIRIT_TREE("Gnome Stronghold spirit tree", Region.GNOME, new Point(2448, 3436), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Gnome Stronghold", TransportType.SPIRIT_TREE),
			new StarLocationTransport("Elder Gnome Child", TransportType.ACHIEVEMENT_DIARY_CAPE),
			new StarLocationTransport("Stronghold Slayer Cave", TransportType.SLAYER_RING),
			new StarLocationTransport("Grand Tree", TransportType.ROYAL_SEED_POD)
		}),
	PISCATORIS("Piscatoris (akq fairy ring)", Region.GNOME, new Point(2341, 3635), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Piscatoris", TransportType.SCROLL_TELEPORT),
			new StarLocationTransport("AKQ", TransportType.FAIRY_RING),
			new StarLocationTransport("Piscatoris Fishing Colony", TransportType.WESTERN_BANNER_3)
		}),

	//
 	// TIRANNWN
	//
	LLETYA("Lletya", Region.TIRANNWN, new Point(2329, 3163), Quest.MOURNINGS_END_PART_I,
		new StarLocationTransport[]{
			new StarLocationTransport("Lletya", TransportType.TELEPORT_CRYSTAL),
			new StarLocationTransport("Iorwerth Camp", TransportType.SCROLL_TELEPORT),
			new StarLocationTransport("Port Tyras", TransportType.CHARTER_SHIP)
		}),
	ISAFDAR_RUNITE_ROCKS("Isafdar runite rocks", Region.TIRANNWN, new Point(2269, 3158), Quest.REGICIDE,
		new StarLocationTransport[]{
			new StarLocationTransport("Lletya", TransportType.TELEPORT_CRYSTAL),
			new StarLocationTransport("Iorwerth Camp", TransportType.SCROLL_TELEPORT),
			new StarLocationTransport("Port Tyras", TransportType.CHARTER_SHIP)
		}),
	PRIFDDINAS("Prifddinas Zalcano entrance", Region.TIRANNWN, new Point(3274, 6055), Quest.SONG_OF_THE_ELVES,
		new StarLocationTransport[]{
			new StarLocationTransport("Prifddinas", TransportType.TELEPORT_CRYSTAL),
			new StarLocationTransport("Prifddinas", TransportType.HOUSE_PORTAL)
		}),
	ARANDAR_MINE("Arandar mine north of Lletya", Region.TIRANNWN, new Point(2318, 3269), Quest.REGICIDE,
		new StarLocationTransport[]{
			new StarLocationTransport("Prifddinas", TransportType.TELEPORT_CRYSTAL),
			new StarLocationTransport("Prifddinas", TransportType.HOUSE_PORTAL),
			new StarLocationTransport("Iorwerth Camp", TransportType.SCROLL_TELEPORT),
			new StarLocationTransport("Lletya", TransportType.TELEPORT_CRYSTAL),
			new StarLocationTransport("Jorral's Outpost and go through Arandar pass", TransportType.NECKLACE_OF_PASSAGE)
		}),
	MYNYDD("Mynydd nw of Prifddinas", Region.TIRANNWN, new Point(2173, 3409), Quest.SONG_OF_THE_ELVES,
		new StarLocationTransport[]{
			new StarLocationTransport("Prifddinas", TransportType.TELEPORT_CRYSTAL),
			new StarLocationTransport("Prifddinas", TransportType.HOUSE_PORTAL)
		}),

	//
 	// WILDERNESS
	//
	MAGE_OF_ZAMORAK_MINE("Mage of Zamorak mine (lvl 7 Wildy)", Region.WILDERNESS, new Point(3108, 3569), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Other players can attack you", TransportType.WARNING),
			new StarLocationTransport("Lesser Fanatic", TransportType.ACHIEVEMENT_DIARY_CAPE),
			new StarLocationTransport("Edgeville", TransportType.AMULET_OF_GLORY),
			new StarLocationTransport("Ferox Enclave", TransportType.RING_OF_DUELING),
			new StarLocationTransport("Clan Wars", TransportType.MINIGAME_TELEPORT)
		}),
	SKELETON_MINE("Skeleton mine (lvl 10 Wildy)", Region.WILDERNESS, new Point(3018, 3593), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Other players can attack you", TransportType.WARNING),
			new StarLocationTransport("Bandit Camp", TransportType.BURNING_AMULET),
			new StarLocationTransport("Mind Altar", TransportType.MIND_ALTAR_TELEPORT),
			new StarLocationTransport("Edgeville", TransportType.AMULET_OF_GLORY),
			new StarLocationTransport("Ferox Enclave", TransportType.RING_OF_DUELING),
			new StarLocationTransport("Clan Wars", TransportType.MINIGAME_TELEPORT)
		}),
	HOBGOBLIN_MINE("Hobgoblin mine (lvl 30 Wildy)", Region.WILDERNESS, new Point(3093, 3756), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Other players can attack you", TransportType.WARNING),
			new StarLocationTransport("Black Chinchompa Area", TransportType.HUNTER_CAPE),
			new StarLocationTransport("Level 35", TransportType.WILDERNESS_OBELISK),
			new StarLocationTransport("Revenant Caves", TransportType.SCROLL_TELEPORT),
			new StarLocationTransport("Lava Maze", TransportType.BURNING_AMULET)
		}),
	LAVA_MAZE_RUNITE_MINE("Lava maze runite mine (lvl 46 Wildy)", Region.WILDERNESS, new Point(3057, 3887), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Other players can attack you", TransportType.WARNING),
			new StarLocationTransport("Ghorrock", TransportType.GHORROCK_TELEPORT),
			new StarLocationTransport("Level 44", TransportType.WILDERNESS_OBELISK),
			new StarLocationTransport("Lava Maze", TransportType.BURNING_AMULET),
			new StarLocationTransport("Revenant Caves", TransportType.SCROLL_TELEPORT)
		}),
	PIRATES_HIDEOUT("Pirates' Hideout (lvl 53 Wildy)", Region.WILDERNESS, new Point(3049, 3940), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Other players can attack you", TransportType.WARNING),
			new StarLocationTransport("Ice Plateau", TransportType.ICE_PLATEAU_TELEPORT),
			new StarLocationTransport("Deserted Keep", TransportType.ARDOUGNE_EDGEVILLE_WILDERNESS_LEVER),
			new StarLocationTransport("Level 44", TransportType.WILDERNESS_OBELISK)
		}),
	MAGE_ARENA_BANK("Mage Arena bank (lvl 56 Wildy)", Region.WILDERNESS, new Point(3091, 3962), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Other players can attack you", TransportType.WARNING),
			new StarLocationTransport("Deserted Keep", TransportType.ARDOUGNE_EDGEVILLE_WILDERNESS_LEVER),
			new StarLocationTransport("Ice Plateau", TransportType.ICE_PLATEAU_TELEPORT),
			new StarLocationTransport("Level 44", TransportType.WILDERNESS_OBELISK)
		}),
	WILDERNESS_RESOURCE_AREA("Wilderness Resource Area", Region.WILDERNESS, new Point(3188, 3932), null,
		new StarLocationTransport[]{
			new StarLocationTransport("Other players can attack you", TransportType.WARNING),
			new StarLocationTransport("Deserted Keep", TransportType.ARDOUGNE_EDGEVILLE_WILDERNESS_LEVER),
			new StarLocationTransport("Level 50", TransportType.WILDERNESS_OBELISK)
		}),

	//
 	// VARLAMORE
	//
	VARLAMORE_SOUTH_EAST_MINE("Varlamore South East mine", Region.VARLAMORE, new Point(1742, 2954), Quest.CHILDREN_OF_THE_SUN,
		new StarLocationTransport[]{
			new StarLocationTransport("AJP", TransportType.FAIRY_RING),
			new StarLocationTransport("Colossal Wyrm Remains", TransportType.QUETZAL_TRANSPORT_SYSTEM),
			new StarLocationTransport("Colossal Wyrm", TransportType.SCROLL_TELEPORT),
			new StarLocationTransport("Fortis Colosseum", TransportType.RING_OF_DUELING),
			new StarLocationTransport("Civitas illa Fortis", TransportType.CIVITAS_ILLA_FORTIS_TELEPORT)
		}),
	VARLAMORE_COLOSSEUM("Varlamore colosseum entrance bank", Region.VARLAMORE, new Point(1771, 3102), Quest.CHILDREN_OF_THE_SUN,
		new StarLocationTransport[]{
			new StarLocationTransport("Fortis Colosseum", TransportType.RING_OF_DUELING),
			new StarLocationTransport("Fortis Colosseum", TransportType.QUETZAL_TRANSPORT_SYSTEM),
			new StarLocationTransport("Civitas illa Fortis", TransportType.CIVITAS_ILLA_FORTIS_TELEPORT)
		}),
	NORTHWEST_OF_HUNTER_GUILD("Mine north-west of hunter guild", Region.VARLAMORE, new Point(1486, 3089), Quest.CHILDREN_OF_THE_SUN,
		new StarLocationTransport[]{
			new StarLocationTransport("Cam Torum entrance", TransportType.QUETZAL_TRANSPORT_SYSTEM),
			new StarLocationTransport("Hunter Guild", TransportType.QUETZAL_WHISTLE),
			new StarLocationTransport("Hunter Guild", TransportType.HUNTER_CAPE),
			new StarLocationTransport("Civitas illa Fortis", TransportType.CIVITAS_ILLA_FORTIS_TELEPORT)
		}),
	SALVAGER_OVERLOOK("Salvager Overlook in Varlamore", Region.VARLAMORE, new Point(1625, 3275), Quest.CHILDREN_OF_THE_SUN,
		new StarLocationTransport[]{
			new StarLocationTransport("Salvager Overlook", TransportType.QUETZAL_TRANSPORT_SYSTEM),
			new StarLocationTransport("Twilight Temple", TransportType.PENDANT_OF_ATES)
		}),
	ALDARIN_MINE("Aldarin mine in Varlamore", Region.VARLAMORE, new Point(1421, 2873), Quest.CHILDREN_OF_THE_SUN,
		new StarLocationTransport[]{
			new StarLocationTransport("Aldarin", TransportType.QUETZAL_TRANSPORT_SYSTEM),
			new StarLocationTransport("CKQ", TransportType.FAIRY_RING),
			new StarLocationTransport("Aldarin", TransportType.HOUSE_PORTAL)
		})
	;

	// Maps for quick lookup by name and coordinates.
	private static final Map<String, StarLocationDetails> NAME_MAP = new HashMap<>();
	private static final Map<Point, StarLocationDetails> COORDINATES_MAP = new HashMap<>();

	static
	{
		// Create name and coordinates mappings.
		for (StarLocationDetails starLocationDetails : StarLocationDetails.values())
		{
			NAME_MAP.put(starLocationDetails.getName(), starLocationDetails);
			COORDINATES_MAP.put(starLocationDetails.getCoordinates(), starLocationDetails);
		}
	}

	/**
	 * Get the {@link StarLocationDetails} by location name.
	 *
	 * @param locationName The location name of the star.
	 * @return             The {@link StarLocationDetails} or null if not found.
	 */
	public static StarLocationDetails getByName(String locationName)
	{
		return NAME_MAP.get(locationName);
	}

	/**
	 * Get the {@link StarLocationDetails} by coordinates.
	 *
	 * @param point The coordinates of the star.
	 * @return      The {@link StarLocationDetails} or null if not found.
	 */
	public static StarLocationDetails getByCoordinates(Point point)
	{
		return COORDINATES_MAP.get(point);
	}

	/**
	 * Location name of this star.
	 */
	private final String name;

	/**
	 * The world region this star resides in.
	 */
	private final Region region;

	/**
	 * World coordinates of this star.
	 */
	private final Point coordinates;

	/**
	 * Quest requirement of this star. Null = no quest requirement.
	 */
	private final Quest questRequirement;

	/**
	 * List of the best transportation options to get to this star.
	 */
	private final StarLocationTransport[] transportList;

	public WorldPoint getWorldPoint()
	{
		return new WorldPoint(coordinates.x, coordinates.y, 0);
	}

	/**
	 * Get the world area the star occupies in the world.
	 *
	 * @return The 2 x 2 {@link WorldArea}
	 */
	public WorldArea getWorldArea()
	{
		return new WorldArea(getWorldPoint(), 2, 2);
	}

	/**
	 * Get the world area from which within it is possible the star can be scouted.
	 *
	 * @return The 7 x 7 chunk (1 chunk  = 8 x 8 tile) area.
	 */
	public WorldArea getScoutableBounds()
	{
		final WorldPoint worldPoint = getWorldPoint();

		return new WorldArea(
			(worldPoint.getX() & -CHUNK_SIZE) - (3 * CHUNK_SIZE),
			(worldPoint.getY() & -CHUNK_SIZE) - (3 * CHUNK_SIZE),
			CHUNK_SIZE * 7,
			CHUNK_SIZE * 7,
			0
		);
	}
}