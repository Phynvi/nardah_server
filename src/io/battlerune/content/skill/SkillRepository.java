package io.battlerune.content.skill;

import io.battlerune.Config;
import io.battlerune.content.skill.impl.agility.Agility;
import io.battlerune.content.skill.impl.crafting.impl.Gem;
import io.battlerune.content.skill.impl.crafting.impl.Hide;
import io.battlerune.content.skill.impl.fishing.Fishable;
import io.battlerune.content.skill.impl.fishing.FishingSpot;
import io.battlerune.content.skill.impl.fishing.FishingTool;
import io.battlerune.content.skill.impl.fletching.impl.*;
import io.battlerune.content.skill.impl.hunter.Hunter;
import io.battlerune.content.skill.impl.hunter.net.impl.Butterfly;
import io.battlerune.content.skill.impl.hunter.net.impl.Impling;
import io.battlerune.content.skill.impl.mining.OreData;
import io.battlerune.content.skill.impl.woodcutting.TreeData;
import io.battlerune.game.world.entity.actor.Direction;
import io.battlerune.game.world.entity.actor.npc.Npc;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.position.Position;
import io.battlerune.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class SkillRepository {
	
	/**
	 * Holds all the impling spawns.
	 */
	public static final List<Integer> HUNTER_SPAWNS = new ArrayList<>();
	
	public static final List<Integer> SKILLING_ITEMS = new ArrayList<>();
	
	/**
	 * Holds all the impling onSpawn locations.
	 */
	private static final Position[] HUNTER_SPAWN_POSITION = {new Position(3081, 3484), new Position(3101, 3484), new Position(3115, 3506), new Position(3094, 3453), new Position(3134, 3511), new Position(3180, 3422), new Position(3240, 3440), new Position(3287, 3351), new Position(3257, 3247), new Position(3221, 3213), new Position(2803, 3445), new Position(2721, 3472), new Position(3349, 3271), new Position(3283, 3196), new Position(3574, 3322), new Position(2659, 2660), new Position(3377, 3168), new Position(2660, 3706), new Position(2318, 3813), new Position(3054, 3514), new Position(2572, 4329), new Position(2583, 4339), new Position(2593, 4333), new Position(2610, 4308), new Position(2595, 4297), new Position(2598, 4312), new Position(2570, 4344), new Position(2585, 4342), new Position(2604, 4336), new Position(2594, 4322)};
	
	/**
	 * Spawns all the skilling npcs.
	 */
	public static void spawn() {
		for(Position position : HUNTER_SPAWN_POSITION) {
			if(!Hunter.SPAWNS.containsValue(position)) {
				int identification = Utility.randomElement(HUNTER_SPAWNS);
				Hunter.SPAWNS.put(identification, position);
				new Npc(identification, position, Config.NPC_WALKING_RADIUS, Direction.NORTH).register();
			}
		}
	}
	
	/**
	 * Loads all the skilling data.
	 */
	public static void load() {
		Gem.load();
		Hide.load();
		Arrow.load();
		Carvable.load();
		Bolt.load();
		Crossbow.load();
		Featherable.load();
		Stringable.load();
		Impling.addList();
		Butterfly.addList();
		Agility.declare();
		FishingTool.declare();
		Fishable.declare();
		FishingSpot.declare();
		spawn();
		declareItems();
	}
	
	private static void declareItems() {
		for(TreeData tree : TreeData.values()) {
			SKILLING_ITEMS.add(tree.item);
		}
		for(OreData ore : OreData.values()) {
			SKILLING_ITEMS.add(ore.ore);
		}
		for(Fishable fish : Fishable.values()) {
			SKILLING_ITEMS.add(fish.getRawFishId());
		}
	}
	
	public static boolean isSkillingItem(int item) {
		for(int id : SKILLING_ITEMS) {
			if(item == id)
				return true;
		}
		return false;
	}
	
	public static boolean isSuccess(int skill, int levelRequired) {
		double successChance = Math.ceil(((double) skill * 50.0D - (double) levelRequired * 15.0D) / (double) levelRequired / 3.0D * 4.0D);
		int roll = Utility.random(99);
		return successChance >= roll;
	}
	
	public static boolean isSuccess(Player p, int skillId, int levelRequired) {
		double level = p.skills.getMaxLevel(skillId);
		double successChance = Math.ceil((((level * 50.0D) - ((double) levelRequired * 15.0D)) / (double) levelRequired / 3.0D) * 4.0D);
		int roll = Utility.random(99);
		return successChance >= roll;
	}
	
	public static boolean isSuccess(Player p, int skill, int levelRequired, int toolLevelRequired) {
		double level = (p.skills.getMaxLevel(skill) + toolLevelRequired) / 2.0D;
		double successChance = Math.ceil((((level * 50.0D) - ((double) levelRequired * 15.0D)) / (double) levelRequired / 3.0D) * 4.0D);
		int roll = Utility.random(99);
		return successChance >= roll;
	}
}
