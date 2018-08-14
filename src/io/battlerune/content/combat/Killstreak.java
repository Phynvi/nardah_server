package io.battlerune.content.combat;

import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.mob.player.PlayerRight;
import io.battlerune.util.Utility;

public class Killstreak {
	private final Player player;
	public int streak;
	
	public Killstreak(Player player) {
		this.player = player;
	}
	
	public void add() {
		streak++;
		
		if(announcementNeeded()) {
			reward();
			announce();
		}
	}
	
	public void end(String killer) {
		if(announcementNeeded()) {
			String icon = "";
			String name = PlayerRight.getCrown(player) + " " + player.getName();
			World.sendMessage("" + icon + " <col=FF0000>" + name + "</col> has lost their kill streak of <col=FF0000>" + streak + "</col> to <col=FF0000>" + killer + "</col>!");
		}
		
		streak = 0;
	}
	
	public void reward() {
		int coins = streak * 50_000;
		player.bankVault.add(coins);
		player.message("<col=FF0000>" + Utility.formatDigits(coins) + " coins were added into your bank vault for that kill streak!");
	}
	
	private void announce() {
		String icon = "";
		String name = PlayerRight.getCrown(player) + " " + player.getName();
		World.sendMessage("<col=FF0000>" + icon + " " + name + " </col>is now on a killstreak of <col=FF0000>" + streak + "</col>.");
	}
	
	private boolean announcementNeeded() {
		return streak >= 5;
	}
}
