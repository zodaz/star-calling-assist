package com.starcallingassist.enums;

import com.starcallingassist.constants.SpriteConstants;
import com.starcallingassist.modules.spriteutil.SpriteUtilModule;
import java.awt.image.BufferedImage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.worldmap.WorldMapPlugin;
import net.runelite.client.util.ImageUtil;

/**
 * Enum representing different modes of transport by name and icon.
 */
@RequiredArgsConstructor
public enum TransportType
{
	MINIGAME_TELEPORT("Minigame teleport", SpriteConstants.MINIGAME_TELEPORT),
	HOUSE_PORTAL("House portal", SpriteConstants.HOUSE_PORTAL),
	SPIRIT_TREE("Spirit tree", SpriteConstants.GENERIC_TRANSPORT),
	SPIRIT_TREE_FARMING("Spirit tree (farming)", SpriteConstants.GENERIC_TRANSPORT),
	SHIP_FROM_ARDOUGNE("Ship from Ardougne", SpriteConstants.GENERIC_TRANSPORT),
	BRIMHAVEN_CART("Cart from Brimhaven", SpriteConstants.GENERIC_TRANSPORT),
	WARNING("WARNING", SpriteConstants.WARNING_MAP_ICON),
	MAGIC_CARPET_FROM_SHANTAY_PASS("Magic carpet from Shantay Pass", SpriteConstants.GENERIC_TRANSPORT),
	MAGIC_CARPET_FROM_POLLNIVNEACH("Magic carpet from Pollnivneach", SpriteConstants.GENERIC_TRANSPORT),
	FERRY_FROM_AL_KHARID("Ferry from Al Kharid", SpriteConstants.GENERIC_TRANSPORT),
	SHIP_FROM_RIMMINGTON("Ship from Rimmington", SpriteConstants.GENERIC_TRANSPORT),
	GNOME_GLIDER("Gnome glider", SpriteConstants.GENERIC_TRANSPORT),
	EDGEVILLE_SOUL_WARS_PORTAL("Edgeville Soul Wars portal", SpriteConstants.GENERIC_TRANSPORT),
	CHARTER_SHIP("Charter ship", SpriteConstants.GENERIC_TRANSPORT),
	RELLEKKA_SHIP("Ship from rellekka", SpriteConstants.GENERIC_TRANSPORT),
	SHIP_FROM_PORT_SARIM("Ship from Port Sarim", SpriteConstants.GENERIC_TRANSPORT),
	ANDRAS_ROWBOAT_FROM_ECTOFUNTUS("Andras rowboat from Ectofuntus", SpriteConstants.GENERIC_TRANSPORT),
	QUETZAL_TRANSPORT_SYSTEM("Quetzal transport system", SpriteConstants.GENERIC_TRANSPORT),
	QUETZAL_WHISTLE("Quetzal whistle", SpriteConstants.GENERIC_TRANSPORT),
	MINECART_NETWORK("Lovakengj minecart network", SpriteConstants.GENERIC_TRANSPORT),
	ARDOUGNE_EDGEVILLE_WILDERNESS_LEVER("Ardougne/Edgeville wilderness lever", SpriteConstants.GENERIC_TRANSPORT),

	GAMES_NECKLACE("Games necklace", WorldMapPlugin.class, "games_necklace_teleport_icon.png"),
	SKILLS_NECKLACE("Skills necklace", WorldMapPlugin.class, "skills_necklace_teleport_icon.png"),
	RING_OF_WEALTH("Ring of wealth", WorldMapPlugin.class, "ring_of_wealth_teleport_icon.png"),
	AMULET_OF_GLORY("Amulet of glory", WorldMapPlugin.class, "amulet_of_glory_teleport_icon.png"),
	COMBAT_BRACELET("Combat bracelet", WorldMapPlugin.class, "combat_bracelet_teleport_icon.png"),
	RING_OF_DUELING("Ring of dueling", WorldMapPlugin.class, "ring_of_dueling_teleport_icon.png"),
	DIGSITE_PENDANT("Digsite pendant", WorldMapPlugin.class, "digsite_pendant_teleport_icon.png"),
	FAIRY_RING("Fairy ring", WorldMapPlugin.class, "fairy_ring_travel.png"),
	FALADOR_TELEPORT("Normal spellbook", WorldMapPlugin.class, "falador_teleport_icon.png"),
	RING_OF_THE_ELEMENTS("Ring of the elements", WorldMapPlugin.class, "ring_of_the_elements_teleport_icon.png"),
	LASSAR_TELEPORT("Ancient spellbook", WorldMapPlugin.class, "lassar_teleport_icon.png"),
	SCROLL_TELEPORT("Teleport scroll", WorldMapPlugin.class, "scroll_teleport_icon.png"),
	ACHIEVEMENT_DIARY_CAPE("Achievement diary cape", WorldMapPlugin.class, "achievement_cape_icon.png"),
	KARAMJA_GLOVES_3("Karamja gloves 3", WorldMapPlugin.class, "karamja_gloves_icon.png"),
	NECKLACE_OF_PASSAGE("Necklace of passage", WorldMapPlugin.class, "necklace_of_passage_teleport_icon.png"),
	DESERT_AMULET("Desert amulet", WorldMapPlugin.class, "desert_amulet_icon.png"),
	PHARAOS_SCEPTRE("Pharaoh's sceptre", WorldMapPlugin.class, "pharaohs_sceptre_teleport_icon.png"),
	CAMULET("Camulet", WorldMapPlugin.class, "camulet_teleport_icon.png"),
	CAMULET_HARD_DESERT_DIARY("Camulet (Hard desert diary)", WorldMapPlugin.class, "camulet_teleport_icon.png"),
	HUNTER_CAPE("Hunter cape", WorldMapPlugin.class, "hunter_cape_icon.png"),
	MYTHICAL_CAPE("Mythical cape", WorldMapPlugin.class, "mythical_cape_teleport_icon.png"),
	FREMENNIK_SEA_BOOTS("Fremennik sea boots", WorldMapPlugin.class, "fremennik_boots_icon.png"),
	ENCHANTED_LYRE("Enchanted lyre", WorldMapPlugin.class, "enchanted_lyre_teleport_icon.png"),
	SLAYER_RING("Slayer ring", WorldMapPlugin.class, "slayer_ring_teleport_icon.png"),
	LUNAR_ISLE_TELEPORT("Lunar spellbook", WorldMapPlugin.class, "moonclan_teleport_icon.png"),
	WATCH_TOWER("Normal spellbook", WorldMapPlugin.class, "watchtower_teleport_icon.png"),
	KHAZARD_TELEPORT("Lunar spellbook", WorldMapPlugin.class, "khazard_teleport_icon.png"),
	ARDOUGNE_CLOAK("Ardougne cloak", WorldMapPlugin.class, "ardougne_cloak_icon.png"),
	QUEST_CAPE("Quest cape", WorldMapPlugin.class, "quest_cape_icon.png"),
	ARDOUGNE_TELEPORT("Normal spellbook", WorldMapPlugin.class, "ardougne_teleport_icon.png"),
	CATHERBY_TELEPORT("Lunar spellbook", WorldMapPlugin.class, "catherby_teleport_icon.png"),
	CAMELOT_TELEPORT("Normal spellbook", WorldMapPlugin.class, "camelot_teleport_icon.png"),
	KHAREDSTS_MEMOIRS("Kharedst's memoirs", WorldMapPlugin.class, "kharedsts_memoirs_teleport_icon.png"),
	KOUREND_TELEPORT("Normal spellbook", WorldMapPlugin.class, "kourend_teleport_icon.png"),
	XERICS_TALISMAN("Xeric's talisman", WorldMapPlugin.class, "xerics_talisman_teleport_icon.png"),
	BATTLEFRONT_TELEPORT("Arceuus spellbook", WorldMapPlugin.class, "battlefront_teleport_icon.png"),
	ARCEUUS_HOME_TELEPORT("Arceuus spellbook", WorldMapPlugin.class, "arceuus_library_teleport_icon_arceuus.png"),
	ARCEUUS_LIBRARY_TELEPORT("Arceuus spellbook", WorldMapPlugin.class, "arceuus_library_teleport_icon_arceuus.png"),
	RADAS_BLESSING_3("Rada's blessing 3", WorldMapPlugin.class, "radas_blessing_icon.png"),
	VARROCK_TELEPORT("Normal spellbook", WorldMapPlugin.class, "varrock_teleport_icon.png"),
	SENNTISTEN_TELEPORT("Ancient spellbook", WorldMapPlugin.class, "senntisten_teleport_icon.png"),
	CHRONICLE("Chronicle", WorldMapPlugin.class, "chronicle_teleport_icon.png"),
	LUMBRIDGE_TELEPORT("Normal spellbook", WorldMapPlugin.class, "lumbridge_teleport_icon.png"),
	DRAKANS_MEDALLION("Drakan's medallion", WorldMapPlugin.class, "drakans_medallion_teleport_icon.png"),
	KHARYRLL_TELEPORT("Ancient spellbook", WorldMapPlugin.class, "kharyrll_teleport_icon.png"),
	SALVE_GRAVEYARD_TELEPORT("Arceuus spellbook", WorldMapPlugin.class, "salve_graveyard_teleport_icon.png"),
	FENKENSTRAINS_CASTLE_TELEPORT("Arceuus spellbook", WorldMapPlugin.class, "fenkenstrains_castle_teleport_icon.png"),
	MORYTANIA_LEGS_3("Morytania legs 3", WorldMapPlugin.class, "morytania_legs_icon.png"),
	ROYAL_SEED_POD("Royal seed pod", WorldMapPlugin.class, "royal_seed_pod_teleport_icon.png"),
	WESTERN_BANNER_3("Western banner 3", WorldMapPlugin.class, "western_banner_icon.png"),
	TELEPORT_CRYSTAL("Teleport crystal", WorldMapPlugin.class, "teleport_crystal_icon.png"),
	BURNING_AMULET("Burning amulet", WorldMapPlugin.class, "burning_amulet_teleport_icon.png"),
	MIND_ALTAR_TELEPORT("Arceuus spellbook", WorldMapPlugin.class, "mind_altar_teleport_icon.png"),
	WILDERNESS_OBELISK("Wilderness obelisk", WorldMapPlugin.class, "obelisk_icon.png"),
	GHORROCK_TELEPORT("Ancient spellbook", WorldMapPlugin.class, "ghorrock_teleport_icon.png"),
	ICE_PLATEAU_TELEPORT("Lunar spellbook", WorldMapPlugin.class, "ice_plateau_teleport_icon.png"),
	CIVITAS_ILLA_FORTIS_TELEPORT("Normal spellbook", WorldMapPlugin.class, "civitas_illa_fortis_teleport_icon.png"),
	PENDANT_OF_ATES("Pendant of ates", WorldMapPlugin.class, "pendant_of_ates_icon.png");

	private int spriteId;

	@Getter
	private final String name;

	@Getter
	private BufferedImage icon;

	static
	{
		SpriteUtilModule.registerLoadSpritesCallback(TransportType::loadSprites);
	}

	/**
	 * @param name     The display name of the transportation type.
	 * @param spriteId The sprite id of the sprite to use the image from.
	 */
	TransportType(final String name, final int spriteId)
	{
		this.name = name;
		this.spriteId = spriteId;
	}

	/**
	 * @param name      The display name of the transportation type.
	 * @param c         The class to be referenced for the package path.
	 * @param imagePath The path to the image, relative to the given class.
	 */
	TransportType(final String name, final Class<?> c, final String imagePath)
	{
		this.name = name;
		this.spriteId = Integer.MIN_VALUE;

		try
		{
			icon = ImageUtil.loadImageResource(c, imagePath);
		}
		catch (Exception ignored)
		{
			// Use the generic transport sprite if loadImageResource fails
			this.spriteId = SpriteConstants.GENERIC_TRANSPORT;
		}
	}

	/**
	 * Loads the icon image from cache for all enum values that were defined with a sprite id.
	 * @param spriteManager The {@link SpriteManager} instance to use when loading sprites.
	 */
	public static void loadSprites(SpriteManager spriteManager)
	{
		for (TransportType transportType : TransportType.values())
		{
			if (transportType.spriteId != Integer.MIN_VALUE)
			{
				spriteManager.getSpriteAsync(transportType.spriteId, 0, transportType::setIcon);
			}
		}
	}

	private void setIcon(BufferedImage icon)
	{
		this.icon = icon;
	}
}
