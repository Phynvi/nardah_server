package io.battlerune.game.world.entity.combat.strategy.npc.boss.galvek;

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
 * Created by Adam_#6723 Galvek Utility Class, handles the spawning &
 * generates// i dont even wanna type my name under such shit the location of
 * the spawning
 */
public class GalvekUtility {
	
	public static SpawnData1 spawn;
	
	public static boolean activated = false;
	
	public static boolean galvekbutton;
	
	public static Npc generateSpawn() {
		activated = true;
		spawn = SpawnData1.generate();
		Npc Galvek = new Npc(8095, spawn.position, 10, Direction.NORTH);
		World.sendBroadcast(1, "Galvek has just spawned! He is located at " + spawn.location + "!", true);
		Galvek.register();
		Galvek.definition.setRespawnTime(-1);
		Galvek.definition.setAggressive(true);
		Galvek.speak("Darkness is here to penetrate your souls!");
		activated = true;
		return Galvek;
	}
	
	/**
	 * Identification of all loot, it selects the loot
	 */
	
	public static int[] ALWAYSLOOT = {1, 1, 1, 1, 1, 2, 3, 4, 5};
	public static int[] COMMONLOOT = {6199, 989, 3140, 4087, 11732, 989, 12878, 6585, 4675};
	public static int[] RARELOOT = {11834, 11832, 11828, 11830, 11836, 11773, 13239, 13237, 13235, 11772, 11771, 11770, 20143, 20002};
	public static int[] SUPERRARELOOT = {11862, 21225, 12817, 12825, 12821, 20997, 13652, 11802, 13576, 11785, 19481, 11791, 12904,};
	
	public static void defeated(Npc Galvek, Player player) {
		
		boolean hasClan = player.clanChannel != null;
		
		if(hasClan) {
			player.clanChannel.getDetails().points += 5;
			player.clanChannel.addExperience(10000);
			World.sendMessage("<col=8714E6>Galvek has been defeated by " + player.getName() + ", a clan member of " + player.clanChannel.getName() + "!");
			player.clanChannel.message("Hell yeah boys! We just killed Galvek!! We earned 10,000 EXP & 5 CP.");
		}
		World.sendMessage("<col=8714E6>Galvek has been defeated by " + player.getName() + ", the legend.");
		
		/**
		 * Constructs a new object for the ground item method, uses utility random, to
		 * randomise a number between the upper bound and lower bound of a number.
		 *
		 *
		 **/
		
		int random = Utility.random(100);
		
		if(random <= 50) {
			for(int i = 0; i < COMMONLOOT.length; i++) {
				GroundItem.createGlobal(player, new Item(COMMONLOOT[i], 1), new Position(spawn.getPosition().getX() + Utility.random(10), spawn.getPosition().getY() + Utility.random(10), 0));
			}
		}
		if(random <= 25) {
			for(int i = 0; i < RARELOOT.length; i++) {
				GroundItem.createGlobal(player, new Item(RARELOOT[i], 1), new Position(spawn.getPosition().getX() + Utility.random(10), spawn.getPosition().getY() + Utility.random(10), 0));
			}
		}
		if(random <= 5) {
			for(int i = 0; i < SUPERRARELOOT.length; i++) {
				GroundItem.createGlobal(player, new Item(SUPERRARELOOT[i], 1), new Position(spawn.getPosition().getX() + Utility.random(10), spawn.getPosition().getY() + Utility.random(10), 0));
				World.sendMessage("<img=10><col=FF0000>Galvek has dropped Bank Loot!");
			}
		}
		
		for(int i = 0; i < ALWAYSLOOT.length; i++) {
			GroundItem.createGlobal(player, new Item(ALWAYSLOOT[i], 1), new Position(spawn.getPosition().getX() + Utility.random(10), spawn.getPosition().getY() + Utility.random(10), 0));
		}
		
		player.send(new SendMessage("Galvek drop's lootation all over the map.", MessageColor.RED));
		Galvek.unregister();
	}
	
	public enum SpawnData1 {
		LEVEL_46("lvl 46 wild near Spider Hill", new Position(3135, 3888, 0), new Position(3132, 3881, 0)), LEVEL_16("lvl 16 wild near Bone Yard", new Position(3273, 3648, 0), new Position(3267, 3654, 0)), LEVEL_51("lvl 51 wild near Rogues Castle", new Position(3266, 3924, 0), new Position(3266, 3927, 0)), LEVEL_41("lvl 41 wild near 19 Portal", new Position(3197, 3670, 0), new Position(3194, 3666, 0)), LEVEL_47("lvl 47 wild near obelisk", new Position(3308, 3892, 0), new Position(3305, 3888, 0)), LEVEL_53("lvl 53 wild near scorpia's cave entrance", new Position(3211, 3944, 0), new Position(3208, 3940, 0));
		
		public final String location;
		public final Position position;
		public final Position tsunami;
		
		SpawnData1(String location, Position position, Position tsunami) {
			this.location = location;
			this.position = position;
			this.tsunami = tsunami;
		}
		
		public static SpawnData1 generate() {
			return Utility.randomElement(values());
		}
		
		public String getLocation() {
			return location;
		}
		
		public Position getPosition() {
			return position;
		}
		
		public Position getTsunami() {
			return tsunami;
		}
		
	}
	
}