package com.nardah.game.world.entity.combat.strategy.npc.boss.skotizo;

import com.nardah.game.world.position.Position;
import com.nardah.util.Utility;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.Direction;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.player.Player;

/**
 * Created by Daniel on 2017-12-20.
 */
public class SkotizoUtility {
	
	static Mob generateSpawn() {
		SpawnData spawn = SpawnData.generate();
		Mob skotizo = new Mob(7286, spawn.position, 10, Direction.NORTH);
		World.sendMessage("<col=8714E6> Skotizo has just spawned! He is located at " + spawn.location + "!", "<col=8714E6> First clan to kill him will be rewarded handsomely!");
		skotizo.register();
		skotizo.definition.setRespawnTime(-1);
		skotizo.definition.setAggressive(true);
		skotizo.speak("Darkness is here to penetrate your souls!");
		return skotizo;
	}
	
	public static void defeated(Mob skotizo, Player player) {
		boolean hasClan = player.clanChannel != null;
		
		if(hasClan) {
			player.clanChannel.getDetails().points += 5;
			player.clanChannel.addExperience(10000);
			World.sendMessage("<col=8714E6> Skotizo has been defeated by " + player.getName() + ", a clan member of " + player.clanChannel.getName() + "!");
			player.clanChannel.message("Hell yeah boys! We just killed Skotizo!! We earned 10,000 EXP & 5 CP.");
		} else {
			World.sendMessage("<col=8714E6> Skotizo has been defeated by " + player.getName() + ", a solo individual with balls of steel!");
		}
		
		skotizo.unregister();
	}
	
	public enum SpawnData {
		LEVEL_18("lvl 18 wild near east dragons", new Position(3307, 3668, 0)),
		LEVEL_19("lvl 19 wild near obelisk", new Position(3222, 3658, 0)),
		LEVEL_28("lvl 28 wild near venenatis", new Position(3308, 3737, 0)),
		LEVEL_41("lvl 41 wild near callisto", new Position(3270, 3843, 0)),
		LEVEL_52("lvl 52 wild near obelisk", new Position(3304, 3929, 0)),
		LEVEL_53("lvl 53 wild near scorpia's cave entrance", new Position(3211, 3944, 0));
		
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
