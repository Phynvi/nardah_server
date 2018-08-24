package com.nardah.game.world.entity.combat.strategy.npc.boss.magearena;

import com.nardah.game.world.position.Position;
import com.nardah.util.Utility;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.Direction;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.player.Player;

/**
 * Created by Adam_#6732
 */
public class PorazdirUtility {
	
	public static Mob generatePorazdirSpawn() {
		SpawnData spawn = SpawnData.generate();
		Mob Porazdir = new Mob(7860, spawn.position, 10, Direction.NORTH);
		World.sendMessage("<col=8714E6> Porazdir has just spawned! He is located at " + spawn.location + "!");
		World.sendBroadcast(1, "The Porazdir boss has spawned!" + spawn.location + "!", true);
//		for(int x = 0; x < 500; x++) {
//			System.out.println("Porazdir Spawned!");
//		}
		Porazdir.register();
		Porazdir.definition.setRespawnTime(-1);
		Porazdir.definition.setAggressive(true);
		Porazdir.speak("Darkness is here to penetrate your souls!");
		return Porazdir;
	}
	
	public static void defeated(Mob jusiticar, Player player) {
		
		boolean hasClan = player.clanChannel != null;
		
		if(hasClan) {
			player.clanChannel.getDetails().points += 5;
			player.clanChannel.addExperience(10000);
			World.sendMessage("<col=8714E6> jusiticar has been defeated by " + player.getName() + ", a clan member of " + player.clanChannel.getName() + "!");
			player.clanChannel.message("Hell yeah boys! We just killed jusiticar!! We earned 10,000 EXP & 5 CP!");
		} else {
			World.sendMessage("<col=8714E6> jusiticar has been defeated by " + player.getName() + ", a solo individual with balls of steel!");
		}
		
		jusiticar.unregister();
	}
	
	public enum SpawnData {
		LEVEL_19("lvl 15 wild west dark warrior's fortress", new Position(2988, 3636, 0)), LEVEL_28("lvl 19 wild near wilderness ruins", new Position(2964, 3670, 0)), LEVEL_41("lvl 10 wild south of dark Warriors", new Position(3016, 3591, 0)), LEVEL_52("North of edgeville in the wilderness", new Position(3100, 3528, 0)), LEVEL_53("lvl 19 wild west of graveyard of shadows", new Position(3138, 3670, 0));
		
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
