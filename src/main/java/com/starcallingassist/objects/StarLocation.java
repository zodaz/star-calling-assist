package com.starcallingassist.objects;

import com.starcallingassist.enums.Region;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;

public class StarLocation
{
	@AllArgsConstructor
	@Data
	private static class StarLocationDetails
	{
		private String name;
		private Region region;
	}

	private static final Map<Point, StarLocationDetails> LOCATION_NAMES = new HashMap<Point, StarLocationDetails>()
	{
		{
			put(new Point(2974, 3241), new StarLocationDetails("Rimmington mine", Region.ASGARNIA));
			put(new Point(2940, 3280), new StarLocationDetails("Crafting guild", Region.ASGARNIA));
			put(new Point(2906, 3355), new StarLocationDetails("West Falador mine", Region.ASGARNIA));
			put(new Point(3030, 3348), new StarLocationDetails("East Falador bank", Region.ASGARNIA));
			put(new Point(3018, 3443), new StarLocationDetails("North Dwarven Mine entrance", Region.ASGARNIA));
			put(new Point(2882, 3474), new StarLocationDetails("Taverley house portal", Region.ASGARNIA));

			put(new Point(2736, 3221), new StarLocationDetails("Brimhaven northwest gold mine", Region.KARAMJA));
			put(new Point(2742, 3143), new StarLocationDetails("Southwest of Brimhaven Poh", Region.KARAMJA));
			put(new Point(2845, 3037), new StarLocationDetails("Nature Altar mine north of Shilo", Region.KARAMJA));
			put(new Point(2827, 2999), new StarLocationDetails("Shilo Village gem mine", Region.KARAMJA));
			put(new Point(2835, 3296), new StarLocationDetails("North Crandor", Region.KARAMJA));
			put(new Point(2822, 3238), new StarLocationDetails("South Crandor", Region.KARAMJA));

			put(new Point(3296, 3298), new StarLocationDetails("Al Kharid mine", Region.DESERT));
			put(new Point(3276, 3164), new StarLocationDetails("Al Kharid bank", Region.DESERT));
			put(new Point(3351, 3281), new StarLocationDetails("North of Al Kharid PvP Arena", Region.DESERT));
			put(new Point(3424, 3160), new StarLocationDetails("Nw of Uzer (Eagle's Eyrie)", Region.DESERT));
			put(new Point(3434, 2889), new StarLocationDetails("Nardah bank", Region.DESERT));
			put(new Point(3316, 2867), new StarLocationDetails("Agility Pyramid mine", Region.DESERT));
			put(new Point(3171, 2910), new StarLocationDetails("Desert Quarry mine", Region.DESERT));

			put(new Point(2567, 2858), new StarLocationDetails("Corsair Cove bank", Region.FELDIP));
			put(new Point(2483, 2886), new StarLocationDetails("Corsair Resource Area", Region.FELDIP));
			put(new Point(2468, 2842), new StarLocationDetails("Myths' Guild", Region.FELDIP));
			put(new Point(2571, 2964), new StarLocationDetails("Feldip Hills (aks fairy ring)", Region.FELDIP));
			put(new Point(2630, 2993), new StarLocationDetails("Rantz cave", Region.FELDIP));
			put(new Point(2200, 2792), new StarLocationDetails("Soul Wars south mine", Region.FELDIP));

			put(new Point(3818, 3801), new StarLocationDetails("Fossil Island Volcanic Mine entrance", Region.FOSSIL));
			put(new Point(3774, 3814), new StarLocationDetails("Fossil Island rune rocks", Region.FOSSIL));
			put(new Point(3686, 2969), new StarLocationDetails("Mos Le'Harmless west bank", Region.FOSSIL));

			put(new Point(2727, 3683), new StarLocationDetails("Keldagrim entrance mine", Region.FREMMENIK));
			put(new Point(2683, 3699), new StarLocationDetails("Rellekka mine", Region.FREMMENIK));
			put(new Point(2393, 3814), new StarLocationDetails("Jatizso mine entrance", Region.FREMMENIK));
			put(new Point(2375, 3832), new StarLocationDetails("Neitiznot south of rune rock", Region.FREMMENIK));
			put(new Point(2528, 3887), new StarLocationDetails("Miscellania mine (cip fairy ring)", Region.FREMMENIK));
			put(new Point(2139, 3938), new StarLocationDetails("Lunar Isle mine entrance", Region.FREMMENIK));

			put(new Point(2602, 3086), new StarLocationDetails("Yanille bank", Region.KANDARIN));
			put(new Point(2624, 3141), new StarLocationDetails("Port Khazard mine", Region.KANDARIN));
			put(new Point(2608, 3233), new StarLocationDetails("Ardougne Monastery", Region.KANDARIN));
			put(new Point(2705, 3333), new StarLocationDetails("South of Legends' Guild", Region.KANDARIN));
			put(new Point(2804, 3434), new StarLocationDetails("Catherby bank", Region.KANDARIN));
			put(new Point(2589, 3478), new StarLocationDetails("Coal Trucks west of Seers'", Region.KANDARIN));

			put(new Point(1778, 3493), new StarLocationDetails("Hosidius mine", Region.KOUREND));
			put(new Point(1769, 3709), new StarLocationDetails("Port Piscarilius mine in Kourend", Region.KOUREND));
			put(new Point(1597, 3648), new StarLocationDetails("Shayzien mine south of Kourend Castle", Region.KOUREND));
			put(new Point(1534, 3747), new StarLocationDetails("South Lovakengj bank", Region.KOUREND));
			put(new Point(1437, 3840), new StarLocationDetails("Lovakite mine", Region.KOUREND));
			put(new Point(1760, 3853), new StarLocationDetails("Arceuus dense essence mine", Region.KOUREND));

			put(new Point(1322, 3816), new StarLocationDetails("Mount Karuulm bank", Region.KEBOS));
			put(new Point(1279, 3817), new StarLocationDetails("Mount Karuulm mine", Region.KEBOS));
			put(new Point(1210, 3651), new StarLocationDetails("Kebos Swamp mine", Region.KEBOS));
			put(new Point(1258, 3564), new StarLocationDetails("Chambers of Xeric bank", Region.KEBOS));

			put(new Point(3258, 3408), new StarLocationDetails("Varrock east bank", Region.MISTHALIN));
			put(new Point(3290, 3353), new StarLocationDetails("Southeast Varrock mine", Region.MISTHALIN));
			put(new Point(3175, 3362), new StarLocationDetails("Champions' Guild mine", Region.MISTHALIN));
			put(new Point(3094, 3235), new StarLocationDetails("Draynor Village", Region.MISTHALIN));
			put(new Point(3153, 3150), new StarLocationDetails("West Lumbridge Swamp mine", Region.MISTHALIN));
			put(new Point(3230, 3155), new StarLocationDetails("East Lumbridge Swamp mine", Region.MISTHALIN));

			put(new Point(3635, 3340), new StarLocationDetails("Darkmeyer ess. mine entrance", Region.MORYTANIA));
			put(new Point(3650, 3214), new StarLocationDetails("Theatre of Blood bank", Region.MORYTANIA));
			put(new Point(3505, 3485), new StarLocationDetails("Canifis bank", Region.MORYTANIA));
			put(new Point(3500, 3219), new StarLocationDetails("Burgh de Rott bank", Region.MORYTANIA));
			put(new Point(3451, 3233), new StarLocationDetails("Abandoned Mine west of Burgh", Region.MORYTANIA));

			put(new Point(2444, 3490), new StarLocationDetails("West of Grand Tree", Region.GNOME));
			put(new Point(2448, 3436), new StarLocationDetails("Gnome Stronghold spirit tree", Region.GNOME));
			put(new Point(2341, 3635), new StarLocationDetails("Piscatoris (akq fairy ring)", Region.GNOME));

			put(new Point(2329, 3163), new StarLocationDetails("Lletya", Region.TIRANNWN));
			put(new Point(2269, 3158), new StarLocationDetails("Isafdar runite rocks", Region.TIRANNWN));
			put(new Point(3274, 6055), new StarLocationDetails("Prifddinas Zalcano entrance", Region.TIRANNWN));
			put(new Point(2318, 3269), new StarLocationDetails("Arandar mine north of Lletya", Region.TIRANNWN));
			put(new Point(2173, 3409), new StarLocationDetails("Mynydd nw of Prifddinas", Region.TIRANNWN));

			put(new Point(3108, 3569), new StarLocationDetails("Mage of Zamorak mine (lvl 7 Wildy)", Region.WILDERNESS));
			put(new Point(3018, 3593), new StarLocationDetails("Skeleton mine (lvl 10 Wildy)", Region.WILDERNESS));
			put(new Point(3093, 3756), new StarLocationDetails("Hobgoblin mine (lvl 30 Wildy)", Region.WILDERNESS));
			put(new Point(3057, 3887), new StarLocationDetails("Lava maze runite mine (lvl 46 Wildy)", Region.WILDERNESS));
			put(new Point(3049, 3940), new StarLocationDetails("Pirates' Hideout (lvl 53 Wildy)", Region.WILDERNESS));
			put(new Point(3091, 3962), new StarLocationDetails("Mage Arena bank (lvl 56 Wildy)", Region.WILDERNESS));
			put(new Point(3188, 3932), new StarLocationDetails("Wilderness Resource Area", Region.WILDERNESS));
		}
	};

	private Point point;

	private String originalInputName;

	public StarLocation(String location)
	{
		if (location == null)
		{
			return;
		}

		originalInputName = location;
		point = LOCATION_NAMES.entrySet()
			.stream()
			.filter(entry -> entry.getValue().getName().equalsIgnoreCase(location))
			.map(Map.Entry::getKey)
			.findFirst()
			.orElse(null);
	}

	public StarLocation(WorldPoint location)
	{
		if (location == null)
		{
			return;
		}

		point = new Point(
			location.getX(),
			location.getY()
		);
	}

	public WorldPoint getWorldPoint()
	{
		if (point == null)
		{
			return null;
		}

		return new WorldPoint(
			(int) point.getX(),
			(int) point.getY(),
			0
		);
	}

	public WorldArea getWorldArea()
	{
		if (point == null)
		{
			return null;
		}

		return new WorldArea(
			getWorldPoint(),
			2,
			2
		);
	}

	public String getName()
	{
		if (point == null && originalInputName != null)
		{
			return originalInputName;
		}

		if (point == null)
		{
			return null;
		}

		StarLocationDetails location = LOCATION_NAMES.get(point);
		if (location == null)
		{
			return this.point.x + "," + this.point.y;
		}

		return location.getName();
	}

	public Region getRegion()
	{
		if (point == null)
		{
			return Region.UNKNOWN;
		}

		StarLocationDetails location = LOCATION_NAMES.get(point);
		if (location == null)
		{
			return Region.UNKNOWN;
		}

		return location.getRegion();
	}
}
