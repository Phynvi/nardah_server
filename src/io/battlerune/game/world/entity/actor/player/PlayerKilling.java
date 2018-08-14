package io.battlerune.game.world.entity.actor.player;

import io.battlerune.content.clanchannel.content.ClanAchievement;
import io.battlerune.content.writer.InterfaceWriter;
import io.battlerune.content.writer.impl.InformationWriter;
import io.battlerune.game.world.World;
import io.battlerune.util.Utility;

import static io.battlerune.content.clanchannel.content.ClanTaskKey.BOT_KILLING;
import static io.battlerune.content.clanchannel.content.ClanTaskKey.PLAYER_KILLING;

public class PlayerKilling {
	
	public static void handle(Player killer, Player victim) {
		if(killer.isBot) {
			
			return;
		}
		
		if(killer.lastHost.equalsIgnoreCase(victim.lastHost)) {
			killer.message("<col=FF0019>You were not rewarded since you killed someone with your same IP.");
			return;
		}
		
		if(killer.clanChannel != null && victim.clanChannel != null && killer.clanChannel.getName().equalsIgnoreCase(victim.clanChannel.getName())) {
			killer.message("<col=FF0019>You were not rewarded since you killed someone from your clan.");
			return;
		}
		
		int reward = PlayerRight.getPkPoints(killer);
		killer.kill++;
		victim.death++;
		killer.pkPoints += reward;
		killer.message("<col=295EFF>You were rewarded with " + reward + " points for that kill. You now have: " + Utility.formatDigits(killer.pkPoints) + ".");
		killer.killstreak.add();
		
		/** User has a 1/100 Chance of recieveing a blood key. **/
		int Random_Chance = Utility.random(1, 100);
		if(Random_Chance == 5 && PlayerRight.isDonator(killer)) {
			killer.inventory.add(6640, 1);
			killer.message("The gods have looked down upon you and smiled! They have rewarded you with" + "@red@ 1x Blood Key!");
			World.sendMessage("<col=8714E6> A blood crystal was given to " + killer.getName() + ", a clan member of " + killer.clanChannel.getName() + "!");
			World.sendMessage("<col=8714E6> To get a chance of obtaining a blood key, you must be a donator or higher!");
			
		}
		
		killer.forClan(channel -> {
			channel.activateTask(victim.isBot ? BOT_KILLING : PLAYER_KILLING, killer.getName());
			channel.activateAchievement(ClanAchievement.PLAYER_KILLER_I);
			channel.activateAchievement(ClanAchievement.PLAYER_KILLER_II);
			channel.activateAchievement(ClanAchievement.PLAYER_KILLER_III);
		});
		
		InterfaceWriter.write(new InformationWriter(killer));
		add(killer, victim.lastHost);
	}
	
	public static void add(Player player, String host) {
		if(host == null || host.isEmpty()) {
			return;
		}
		if(player.lastKilled.contains(host)) {
			return;
		}
		if(player.lastKilled.size() >= 3) {
			player.lastKilled.removeFirst();
		}
		player.lastKilled.add(host);
	}
	
	public static boolean remove(Player player, String host) {
		return player.lastKilled.remove(host);
	}
	
	public static boolean contains(Player player, String host) {
		return player.lastKilled != null && player.lastKilled.contains(host);
	}
}
