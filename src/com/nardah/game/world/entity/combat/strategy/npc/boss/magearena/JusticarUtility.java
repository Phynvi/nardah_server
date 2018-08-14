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
public class JusticarUtility {
	
	public static boolean activated = false;
	public static SpawnData spawn;
	
	public static Mob generatejusiticarSpawn() {
		activated = true;
		SpawnData spawn = SpawnData.generate();
		Mob jusiticar = new Mob(7858, spawn.position, 10, Direction.NORTH);
		World.sendMessage("<col=8714E6> Justicar has just spawned! He is located at " + spawn.location + "!");
		World.sendBroadcast(1, "The Justicar boss has spawned!" + spawn.location + "!", true);
		jusiticar.register();
		jusiticar.definition.setRespawnTime(-1);
		jusiticar.definition.setAggressive(true);
		jusiticar.speak("Darkness is here to penetrate your souls!");
		return jusiticar;
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
		LEVEL_19("lvl 15 wild near dark warrior's fortress", new Position(3009, 3637, 0)), LEVEL_28("lvl 54 wild near wilderness resource area", new Position(3184, 3948, 0)), LEVEL_41("lvl 52 wild near Rouges Castle", new Position(3270, 3933, 0)), LEVEL_52("lvl 1 wild North of edgeville", new Position(3100, 3528, 0)), LEVEL_53("lvl 19 wild near graveyard of shadows", new Position(3146, 3672, 0));
		
		public final String location;
		public final Position position;
		
		SpawnData(String location, Position position) {
			this.location = location;
			this.position = position;
		}
		
		public Position getPosition() {
			return position;
		}
		
		public static SpawnData generate() {
			return Utility.randomElement(values());
		}
	}
}
