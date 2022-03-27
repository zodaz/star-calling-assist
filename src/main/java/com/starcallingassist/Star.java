package com.starcallingassist;

import net.runelite.api.GameObject;
import net.runelite.api.coords.WorldPoint;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Star
{
    private static Star CURRENT_STAR = null;

    public final GameObject starObject;
    public final int tier;
    public final WorldPoint location;
    public final int world;

    Star(GameObject starObject, int tier, WorldPoint location, int world)
    {
	this.starObject = starObject;
	this.tier = tier;
	this.location = location;
	this.world = world;
    }

    public static Star GET_STAR(){ return CURRENT_STAR; }

    public static void REMOVE_STAR() { CURRENT_STAR = null; }

    public static void SET_STAR(GameObject star, int tier, int world)
    {
	CURRENT_STAR = new Star(star, tier, star.getWorldLocation(), world);
    }

    public static final Map<Point, String> LOCATION_NAMES = new HashMap<Point, String>()
    {
	{
	    /*
	    ASGARNIA
	     */
	    put(new Point(2974, 3241), "Rimmington mine");
	    put(new Point(2940, 3280), "Crafting guild");
	    put(new Point(2906, 3355), "West falador mine");
	    put(new Point(3030, 3348), "East falador bank");
	    put(new Point(3018, 3443), "North Dwarven Mine Entrance");
	    put(new Point(2882, 3474), "Taverley house portal");
	    /*
	    CRANDOR/KARAMJA
	     */
	    put(new Point(2736, 3221), "Brimhaven north horseshoe mine");
	    put(new Point(2742, 3143), "Brimhaven south dungeon entrance");
	    put(new Point(2845, 3037), "Nature altar mine north of shilo");
	    put(new Point(2827, 2999), "Shilo village gem mine");
	    put(new Point(2835, 3296), "North crandor");
	    put(new Point(2822, 3238), "South crandor");
	    /*
	    DESERT
	     */
	    put(new Point(3296, 3298), "Al kharid mine");
	    put(new Point(3276, 3164), "Al kharid bank");
	    put(new Point(3341, 3267), "Duel arena");
	    put(new Point(3424, 3160), "Northwest of uzer");
	    put(new Point(3434, 2889), "Nardah bank");
	    put(new Point(3316, 2867), "Agility pyramid mine");
	    put(new Point(3171, 2910), "Desert quarry mine");
	    /*
	    FELDIP HILLS/ISLE OF SOULS
	     */
	    put(new Point(2567, 2858), "Corsair cove bank");
	    put(new Point(2483, 2886), "Corsair resource area");
	    put(new Point(2468, 2842), "Myths guild");
	    put(new Point(2571, 2964), "Feldip hills (aks fairy ring)");
	    put(new Point(2630, 2993), "Rantz cave");
	    put(new Point(2200, 2792), "Soul wars south mine");
	    /*
	    FOSSIL ISLAND/MOS LE HARMLESS
	     */
	    put(new Point(3818, 3801), "Fossil Island Volcanic Mine Entrance");
	    put(new Point(3774, 3814), "Fossil Island rune rocks");
	    put(new Point(3686, 2969), "Mos le harmless");
	    /*
	    FREMENNIK/LUNAR ISLE
	     */
	    put(new Point(2727, 3683), "Keldagrim entrance mine");
	    put(new Point(2683, 3699), "Rellekka mine");
	    put(new Point(2393, 3814), "Jatizso Mine Entrance");
	    put(new Point(2375, 3832), "Neitiznot Islands South of Rune Rocks");
	    put(new Point(2528, 3887), "Miscellania Mine (cip fairy ring)");
	    put(new Point(2139, 3938), "Lunar isle mine entrance");
	    /*
	    KANDARIN
	     */
	    put(new Point(2602, 3086), "Yanille bank");
	    put(new Point(2624, 3141), "Port Khazard mine");
	    put(new Point(2608, 3233), "Ardougne monestary");
	    put(new Point(2705, 3333), "South of legends guild");
	    put(new Point(2804, 3434), "Catherby bank");
	    put(new Point(2589, 3478), "Coal Trucks West of Seers");
	    /*
	    KOUREND
	     */
	    put(new Point(1778, 3493), "Hosidius mine");
	    put(new Point(1769, 3709), "Port Piscarilius Mine in Kourend");
	    put(new Point(1597, 3648), "Shayzien Mine south of kourend castle");
	    put(new Point(1534, 3747), "South lovakengj bank");
	    put(new Point(1437, 3840), "Lovakite mine");
	    put(new Point(1760, 3853), "Arceuus dense essence mine");
	    /*
	    KEBOS LOWLANDS
	     */
	    put(new Point(1322, 3816), "Mount karuulm bank");
	    put(new Point(1279, 3817), "Mount karuulm mine");
	    put(new Point(1210, 3651), "Kebos swamp mine");
	    put(new Point(1258, 3564), "Chambers of xeric bank");
	    /*
	    MISTHALIN
	     */
	    put(new Point(3258, 3408), "Varrock east bank");
	    put(new Point(3290, 3353), "Southeast varrock mine");
	    put(new Point(3175, 3362), "Champions guild mine");
	    put(new Point(3094, 3235), "Draynor village");
	    put(new Point(3153, 3150), "Southwest lumbridge swamp mine");
	    put(new Point(3230, 3155), "Southeast lumbridge swamp mine");
	    /*
	    MORYTANIA
	     */
	    put(new Point(3635, 3340), "Darkmeyer ess. mine entrance");
	    put(new Point(3650, 3214), "Theatre of blood bank");
	    put(new Point(3505, 3485), "Canifis bank");
	    put(new Point(3500, 3219), "Burgh de rott bank");
	    put(new Point(3451, 3233), "Abandoned mine west of burgh de rott");
	    /*
	    PISCATORIS/GNOME STRONGHOLD
	     */
	    put(new Point(2444, 3490), "West of grand tree");
	    put(new Point(2448, 3436), "Gnome stronghold spirit tree");
	    put(new Point(2341, 3635), "Piscatoris (akq fairy ring)");
	    /*
	    TIRANNWN
	     */
	    put(new Point(2329, 3163), "Lletya");
	    put(new Point(2269, 3158), "Isafdar runite rocks");
	    put(new Point(3274, 6055), "Priffdinas zalcano entrance");
	    put(new Point(2318, 3269), "Arandar mine");
	    put(new Point(2173, 3409), "Mynydd");
	    /*
	    WILDERNESS
	     */
	    put(new Point(3108, 3569), "Mage of zamorak mine");
	    put(new Point(3018, 3593), "Wilderness skeleton mine");
	    put(new Point(3093, 3756), "Wilderness hobgoblin mine");
	    put(new Point(3057, 3887), "Lava maze runite rocks");
	    put(new Point(3049, 3940), "Pirates hideout");
	    put(new Point(3091, 3962), "Mage bank");
	    put(new Point(3188, 3932), "Wilderness resource area");
	}
    };
}
