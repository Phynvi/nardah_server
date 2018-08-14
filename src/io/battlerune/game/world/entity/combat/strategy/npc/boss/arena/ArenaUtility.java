package io.battlerune.game.world.entity.combat.strategy.npc.boss.arena;

import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.actor.Direction;
import io.battlerune.game.world.entity.actor.npc.Npc;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.items.Item;
import io.battlerune.game.world.items.ground.GroundItem;
import io.battlerune.game.world.position.Position;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.MessageColor;
import io.battlerune.util.Utility;

/**
 * Created by Daniel on 2017-12-20.
 */
public class ArenaUtility {
	
	public static boolean activated = false;
	
	public static Npc generateSpawn() {
		activated = true;
		SpawnData spawn = SpawnData.generate();
		Npc arena = new Npc(5129, spawn.position, 10, Direction.NORTH);
		World.sendMessage("<col=8714E6> arena has just spawned! He is located at " + spawn.location + "!", "<col=8714E6> First clan to kill him will be rewarded handsomely!");
		World.sendMessage("to enter the arena do ::arena and rid this beast from the world of NR!");
		World.sendBroadcast(1, "The Arena boss has spawned enter by doing ::arena", true);
		arena.register();
		arena.definition.setRespawnTime(-1);
		arena.definition.setAggressive(true);
		arena.speak("Darkness is here to penetrate your souls!");
		return arena;
	}
	
	/**
	 * Identification of all loot, it selects the loot
	 */
	public static int[] COMMONLOOT = {6199, 15501, 989, 3140, 4087, 11732, 989};
	public static int[] RARELOOT = {20000, 20001, 20002, 15220, 15018, 15020, 15019, 6585, 4151, 11283, 11846, 11848, 11850, 11852, 11854, 11856};
	public static int[] SUPERRARELOOT = {13887, 13893, 13899, 13905, 13884, 13890, 13896, 13902, 13858, 13861, 13864, 13867, 13870, 13873, 13876};
	
	public static void defeated(Npc arena, Player player) {
		
		/*
		 * int superrare = SUPERRARELOOT[Utility.random(SUPERRARELOOT.length - 1)]; int
		 * rare = RARELOOT[Utility.random(SUPERRARELOOT.length - 1)]; int common =
		 * COMMONLOOT[Utility.random(SUPERRARELOOT.length - 1)];
		 */
		
		boolean hasClan = player.clanChannel != null;
		
		if(hasClan) {
			player.clanChannel.getDetails().points += 5;
			player.clanChannel.addExperience(10000);
			World.sendMessage("<col=8714E6> arena has been defeated by " + player.getName() + ", a clan member of " + player.clanChannel.getName() + "!");
			player.clanChannel.message("Hell yeah boys! We just killed arena!! We earned 10,000 EXP & 5 CP.");
		}
		World.sendMessage("<col=8714E6> arena has been defeated by " + player.getName() + ", a solo individual with balls of steel!");
		
		/** Generates a random item from the int array list. **/
		
		/** Generates a random item from the integer array list. **/
		
		Item item = new Item(Utility.randomElement(COMMONLOOT));
		
		Item item1 = new Item(Utility.randomElement(RARELOOT));
		
		Item item2 = new Item(Utility.randomElement(SUPERRARELOOT));
		
		/**
		 * Constructs a new object for the ground item method, uses utility random, to
		 * randomise a number between the upper bound and lower bound of a number.
		 **/
		
		Position position = new Position(2269 + Utility.random(1, 2), 5342 + Utility.random(2, 3), 0);
		Position position1 = new Position(2264 + Utility.random(1, 4), 5336 + Utility.random(2, 5), 0);
		Position position2 = new Position(2271 + Utility.random(2, 3), 5345 + Utility.random(3, 4), 0);
		
		Position position3 = new Position(2281 + Utility.random(1, 6), 5354 + Utility.random(2, 7), 0);
		Position position4 = new Position(2278 + Utility.random(2, 7), 5335 + Utility.random(3, 8), 0);
		Position position5 = new Position(2258 + Utility.random(3, 8), 5341 + Utility.random(4, 9), 0);
		
		Position position6 = new Position(2270 + Utility.random(1, 6), 5342 + Utility.random(2, 7), 0);
		Position position7 = new Position(2256 + Utility.random(2, 7), 5345 + Utility.random(3, 8), 0);
		Position position8 = new Position(2265 + Utility.random(3, 8), 5339 + Utility.random(4, 9), 0);
		
		GroundItem.createGlobal(player, item, position);
		
		GroundItem.createGlobal(player, item1, position1);
		
		GroundItem.createGlobal(player, item2, position2);
		
		GroundItem.createGlobal(player, item, position3);
		
		GroundItem.createGlobal(player, item1, position4);
		
		GroundItem.createGlobal(player, item2, position5);
		
		GroundItem.createGlobal(player, item2, position6);
		GroundItem.createGlobal(player, item2, position7);
		GroundItem.createGlobal(player, item2, position8);
		
		player.send(new SendMessage("Glod drop's lootation all over the map.", MessageColor.RED));
		
		arena.unregister();
	}
	
	public enum SpawnData {
		LEVEL_18("do ::arena To Enter his evil lair!", new Position(2273, 5341, 0)),
		;
		/*
		 * LEVEL_19("lvl 19 wild near obelisk", new Position(3222, 3658, 0)),
		 * LEVEL_28("lvl 28 wild near vennenatis", new Position(3308, 3737, 0)),
		 * LEVEL_41("lvl 41 wild near callisto", new Position(3270, 3843, 0)),
		 * LEVEL_52("lvl 52 wild near obelisk", new Position(3304, 3929, 0)),
		 * LEVEL_53("lvl 53 wild near scorpia's cave entrance", new Position(3211, 3944,
		 * 0));
		 */
		
		public final String location;
		public final Position position;
		
		SpawnData(String location, Position position) {
			this.location = location;
			this.position = position;
		}
		
		public static SpawnData generate() {
			return Utility.randomElement(values());
		}
	}
}
