package com.nardah.content.teleport;

import com.nardah.content.skill.impl.magic.teleport.TeleportType;
import com.nardah.game.world.position.Position;

/**
 * The teleport data.
 * @author Daniel/ Adam_#6723
 */
public enum Teleport {
	/* Minigames */
	ARENA("Mage Arena", TeleportType.MINIGAMES, new Position(2540, 4717, 0), false, new int[]{2412, 2413, 2414}, false, "You will be teleported in a safe area", ""), DUEL_ARENA("Duel Arena", TeleportType.MINIGAMES, new Position(3365, 3265, 0), false, new int[]{-1, 995}, true, "The place to be if you want to lose bank", "Stake at your own risk!"), GAMBLE("Gamble", TeleportType.MINIGAMES, new Position(2459, 3094, 0), false, new int[]{-1, 995}, true, "The place to be if you want to lose bank", "Here comes the money!"), BARROWS("Barrows", TeleportType.MINIGAMES, new Position(3565, 3315, 0), true, new int[]{-1, 7462}, true, "43 Prayer is highly recommended", "Don't forget your spade!"), FIGHT_CAVE("Fight Caves", TeleportType.MINIGAMES, new Position(2438, 5168, 0), false, new int[]{-1, 6570}, true, "43 Prayer & High Range is recommended", ""), PEST_CONTROL("Pest Control", TeleportType.MINIGAMES, new Position(2662, 2655, 0), false, new int[]{11663, 11664, 11665}, true, "Having a group is highly recommended", "Make sure to pack your pesticides!"), WARRIOR_GUILD("Warrior Guild", TeleportType.MINIGAMES, new Position(2846, 3541, 0), true, new int[]{-1, 8850}, true, "A level of 130 (strength + attack) is needed", ""), KOLODIONS_ARENA("Kolodion's Arena", TeleportType.MINIGAMES, new Position(2540, 4717, 0), false, new int[]{6918, 6916, 6924}, true, "You will be teleported in a safe area", ""),
	
	/* Skilling */
	AGILITY("Agility", TeleportType.SKILLING, new Position(3293, 3182, 0), true, new int[]{9773, 9771, 9772}, true, "Want to be flexible and agile in bed?", "This is how you get there"), CRAFTING("Crafting", TeleportType.SKILLING, new Position(2747, 3444, 0), false, new int[]{9782, 9780, 9781}, true, "Trying to make some sick crafts?", "Level up that crafting!"), FISHING("Fishing", TeleportType.SKILLING, new Position(2809, 3435, 0), false, new int[]{9800, 9798, 9799}, true, "Want to impress the girl next door?", "Get her some premium trout!"), HUNTER("Hunter", TeleportType.SKILLING, new Position(3039, 4836, 0), true, new int[]{9950, 9948, 9949}, true, "Looking to hunt the biggest game?", "Go pking, cause this shits too easy"), MINING("Mining", TeleportType.SKILLING, new Position(3039, 4836, 1), true, new int[]{9794, 9792, 9793}, true, "Let your natural slave roots take over...", "Mine those damn rocks!"), THIEVING("Thieving", TeleportType.SKILLING, new Position(3046, 4969, 1), false, new int[]{9779, 9777, 9778}, true, "Working hard? What's that?", "Let's just steal our gold!"), RUNECRAFTING("Runecrafting", TeleportType.SKILLING, new Position(3039, 4836, 0), false, new int[]{9767, 9765, 9766}, true, "There's no feeling that can compare with", "crafting runes out of your ass!"), WOODCUTTING("Woodcutting", TeleportType.SKILLING, new Position(2727, 3484, 0), true, new int[]{9809, 9807, 9808}, true, "HOW MUCH WOOD WOULD A WOODCHUCK CHUCK IF A", "WOODCHUCK COULD CHUCK WOOD?"), WILDERNESS_RESOURCE("Wilderness Resource", TeleportType.SKILLING, new Position(3184, 3947, 0), false, new int[]{11934, 451, 1513}, false, "This teleport is in the lvl 50+ wilderness!", "YOLO tho, am I right??"),
	
	/* Monster Killing */
	GOBLINS("Training Island", TeleportType.MONSTER_KILLING, new Position(2524, 4776, 0), false, new int[]{-1, 288}, true, "", ""), ROCK_CRABS("Rock Crabs", TeleportType.MONSTER_KILLING, new Position(2676, 3711, 0), false, new int[]{-1, 411}, true, "", ""), SAND_CRABS("Sand Crabs", TeleportType.MONSTER_KILLING, new Position(1726, 3463, 0), false, new int[]{-1, 411}, true, "", ""), AL_KHARID_WARRIORS("Al-Kharid Warriors", TeleportType.MONSTER_KILLING, new Position(3293, 3182, 0), false, new int[]{}, true, "", ""), HILL_GIANTS("Hill Giants", TeleportType.MONSTER_KILLING, new Position(3117, 9856, 0), false, new int[]{}, true, "", ""), YAKS("Yaks", TeleportType.MONSTER_KILLING, new Position(2321, 3804, 0), false, new int[]{}, true, "", ""), ANKOUS("Ankous", TeleportType.MONSTER_KILLING, new Position(2359, 5236, 0), false, new int[]{}, true, "", ""), SLAYER_TOWER("Slayer Tower", TeleportType.MONSTER_KILLING, new Position(3429, 3538, 0), false, new int[]{}, true, "", ""), TAVERLY_DUNGEON("Taverly Dungeon", TeleportType.MONSTER_KILLING, new Position(2884, 9800, 0), false, new int[]{}, true, "", ""), RELEKKA_DUNGEON("Rellekka Dungeon", TeleportType.MONSTER_KILLING, new Position(2792, 10019, 0), false, new int[]{}, true, "", ""), BRIMHAVEN_DUNGEON("Brimhaven Dungeon", TeleportType.MONSTER_KILLING, new Position(2681, 9563, 0), false, new int[]{}, true, "", ""), SMOKE_DEVILS("Smoke Devils", TeleportType.MONSTER_KILLING, new Position(2404, 9415, 0), false, new int[]{-1, 12002}, true, "Looks like somebody has been vaping in this dungeon", "Bring a facemask!"), DUST_DEVILS("Dust Devils", TeleportType.MONSTER_KILLING, new Position(3239, 9364, 0), false, new int[]{20736, 2513, 20736}, true, "Looks like somebody has been farting in this dungeon", "Bring a facemask!"), DARK_BEAST("Dark Beasts", TeleportType.MONSTER_KILLING, new Position(2018, 4639, 0), false, new int[]{-1, 11235}, true, "What these fellas lack in their downstairs area", "make it up with they're 'long' horn"), DEMONIC_GORILLAS("Demonic Gorillas", TeleportType.MONSTER_KILLING, new Position(2130, 5647, 0), false, new int[]{-1, 19529}, true, "Some people say that these gorillas", "are haramabe, back to haunt the living!"), SKELETAL_WYVERNS("Skeletal Wyverns", TeleportType.MONSTER_KILLING, new Position(3055, 9564, 0), false, new int[]{-1, 11286}, true, "Ice cold, baby.", ""), MITHRIL_DRAGONS("Mithril Dragons", TeleportType.MONSTER_KILLING, new Position(1748, 5326, 0), false, new int[]{-1, 11286}, true, "", ""),
	
	/* Player Killing */
	MAGE_BANK("Mage Bank", TeleportType.PLAYER_KILLING, new Position(2540, 4717, 0), false, new int[]{2412, 2413, 2414}, false, "You will be teleported in a safe area", ""), EDGEVILLE("Edgeville", TeleportType.PLAYER_KILLING, new Position(3087, 3517, 0), false, new int[]{}, true, "You will be teleported in a safe area", "Time to destroy some people!"), CASTLE("Castle", TeleportType.PLAYER_KILLING, new Position(3002, 3626, 0), false, new int[]{}, false, "This teleport is in 14 Wilderness", ""), EAST_DRAGONS("East Dragons", TeleportType.PLAYER_KILLING, new Position(3333, 3666, 0), false, new int[]{-1, 1540}, false, "This teleport is in 19 Wilderness", "Beware of Dragons!"), WEST_DRAGONS("West Dragons", TeleportType.PLAYER_KILLING, new Position(2976, 3597, 0), false, new int[]{-1, 1540}, false, "This teleport is in 10 Wilderness", "Beware of Dragons!"), GRAVES("Graveyard", TeleportType.PLAYER_KILLING, new Position(3180, 3671, 0), false, new int[]{-1, 6722}, false, "This teleport is in 19 Wilderness", "Don't let the undead eat your nuts!"), LAVA_DRAGONS("Lava Dragons", TeleportType.PLAYER_KILLING, new Position(3195, 3865, 0), false, new int[]{-1, 11286}, false, "This teleport is in the wilderness lvl 44", "It is also in multi!"), WILDERNESS_RESOURCE2("Wilderness Resource", TeleportType.PLAYER_KILLING, new Position(3184, 3947, 0), false, new int[]{11934, 451, 1513}, false, "This teleport is in the lvl 50+ wilderness!", "YOLO tho, am I right??"),
	
	/* CITIES */
	VARROCK("Varrock", TeleportType.CITIES, new Position(3210, 3424, 0), false, new int[]{8007, 8007, 8007}, true, "Takes you to the Capital of Runity", "Where the markets are filled with goods"), LUMBRIDGE("Lumbridge", TeleportType.CITIES, new Position(3222, 3218, 0), false, new int[]{8008, 8008, 8008}, true, "A once dangerous land", " but now home to the king of lumbridge!"), ALKHARID("Al Kharid", TeleportType.CITIES, new Position(3275, 3166, 0), false, new int[]{1833, 1835, 1837}, true, "Home to the arabs and their spices!", ""), CAMELOT("Camelot", TeleportType.CITIES, new Position(2757, 3477, 0), false, new int[]{8010, 8010, 8010}, true, "Camelot, alot to do, alot to accomplish in this area.", ""), CATHERBY("Catherby", TeleportType.CITIES, new Position(2804, 3435, 0), false, new int[]{-1, 11995}, true, "Catherby is considered the fishing town of runity", ""), DRAYNOR("Draynor", TeleportType.CITIES, new Position(3104, 3248, 0), false, new int[]{19615, 19615, 19615}, true, "Draynor is a town that is extremely useful for you crafters!", ""), PORTSARIM("Port Sarim", TeleportType.CITIES, new Position(3019, 3217, 0), false, new int[]{272, 307, 10136}, true, "The capital of all fishing villages", " has the largest exports of exotic rare fishes!"), TREEGNOMESTRONGHOLD("Gnome Stronghold", TeleportType.CITIES, new Position(2440, 3403, 0), false, new int[]{3257, 9470, 13655}, true, "The gnome's hold these lands", ""),
	
	//    CERBERUS("Cerberus", TeleportType.CITIES, new Position(2872, 9847, 0), 1500, false, "", ""),
	//    LIZARDMAN_SHAMAN("Lizardman Shaman", TeleportType.CITIES, new Position(1495, 3700, 0), 1500, false, "", ""),
	//    GIANT_MOLE("Giant Mole", TeleportType.CITIES, new Position(1761, 5186, 0), 1500, false, "", ""),;;
	;
	
	/**
	 * The name of the teleport.
	 */
	private final String name;
	
	/**
	 * The type of the teleport.
	 */
	private final TeleportType type;
	
	/**
	 * The position of the teleport.
	 */
	private final Position position;
	
	/**
	 * If the teleport is a special case.
	 */
	private final boolean special;
	
	/**
	 * The item display of the teleport.
	 */
	private final int[] items;
	
	private final boolean customsAllowed;
	
	public boolean getcustomsAllowed() {
		return customsAllowed;
	}
	
	/**
	 * Creates a new <code>Teleport<code>.
	 */
	Teleport(String name, TeleportType type, Position position, boolean special, int[] items, boolean customsAllowed, String... strings) {
		this.name = name;
		this.type = type;
		this.position = position;
		this.special = special;
		this.items = items;
		this.customsAllowed = customsAllowed;
		this.strings = strings;
	}
	
	/**
	 * The information strings of the teleport.
	 */
	private final String[] strings;
	
	/**
	 * Gets the name of the teleport.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Gets the type of the teleport.
	 */
	public TeleportType getType() {
		return type;
	}
	
	/**
	 * Gets the position of the teleport.
	 */
	public Position getPosition() {
		return position;
	}
	
	/**
	 * Gets the mob display of the itemcontainer.
	 */
	public int[] getDisplay() {
		return items;
	}
	
	/**
	 * If the teleport is a special case.
	 */
	public boolean isSpecial() {
		return special;
	}
	
	/**
	 * Gets the information strings of the teleport.
	 */
	public String[] getStrings() {
		return strings;
	}
	
	@Override
	public String toString() {
		return "Name: " + name + " - Type: " + type + " - Special: " + special;
	}
}
