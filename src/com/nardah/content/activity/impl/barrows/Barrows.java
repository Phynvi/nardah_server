package com.nardah.content.activity.impl.barrows;

import com.nardah.content.activity.panel.ActivityPanel;
import com.nardah.content.activity.Activity;
import com.nardah.content.activity.ActivityType;
import com.nardah.content.event.impl.ObjectInteractionEvent;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.mob.MobDeath;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.position.Area;
import com.nardah.game.world.position.Position;
import com.nardah.net.packet.out.SendEntityHintArrow;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.Utility;

public class Barrows extends Activity {

	private Player player;

	private Mob brotherMob;

	private Barrows(Player player) {
		super(10, Actor.DEFAULT_INSTANCE_HEIGHT);
		this.player = player;
	}

	public static Barrows create(Player player) {
		Barrows minigame = new Barrows(player);
		minigame.add(player);
		return minigame;
	}

	private void summon(BrotherData brother) {
		if(player.barrowKills[brother.ordinal()]) {
			player.dialogueFactory.sendPlayerChat("I have already killed this brother.").execute();
			return;
		}
		if(brotherMob != null) {
			player.dialogueFactory.sendPlayerChat("Maybe I should finish killing the other one first.").execute();
			return;
		}
		if(player.hiddenBrother == brother && player.barrowsKillCount != 5) {
			player.dialogueFactory.sendPlayerChat("I should return when I've killed the others.").execute();
			return;
		} else if(player.hiddenBrother == brother && player.barrowsKillCount == 5) {
			player.dialogueFactory.sendOption("Enter the tunnel!", () -> {
				player.move(new Position(3551, 9691, 0));
			}, "No, I'm not read yet! I'm still a virgin.", () -> {
				player.dialogueFactory.clear();
			}).execute();
			return;
		}
		brotherMob = new Mob(brother.getNpcId(), player.getPosition());
		add(brotherMob);
		brotherMob.speak("How dare you disturb my rest!");
		brotherMob.getCombat().attack(player);
		brotherMob.owner = player;
		player.send(new SendEntityHintArrow(brotherMob));
	}

	private void move(Position position) {
		if(brotherMob != null && !brotherMob.isDead()) {
			remove(brotherMob);
			brotherMob = null;
		}
		player.move(position);
	}

	@Override
	public void onDeath(Actor actor) {
		if(actor.isPlayer() && actor.equals(player)) {
			finish();
			return;
		}
		if(actor.isNpc()) {
			BrotherData bro = BrotherData.getBarrowsBrother(actor.getNpc());
			if(bro != null) {
				brotherMob = null;
				player.barrowsKillCount += 1;
				player.barrowKills[bro.ordinal()] = true;
				if(player.barrowsKillCount == 1) {
					player.hiddenBrother = BarrowsUtility.getHiddenBrother(player);
				}
				World.schedule(new MobDeath(actor.getNpc()));
			}
		}
	}

	@Override
	protected void start() {

	}

	@Override
	public void finish() {
		cleanup();
		remove(player);
		ActivityPanel.clear(player);
	}

	@Override
	public void cleanup() {
		if(brotherMob != null) {
			remove(brotherMob);
		}
	}

	@Override
	public void onLogout(Player player) {
		finish();
	}

	@Override
	public void onRegionChange(Player player) {
		if(!Area.inBarrows(player)) {
			finish();
		}
	}

	@Override
	public void update() {
		int killed = player.barrowsKillCount;
		int total = BrotherData.values().length;
		int percentage = (int) Utility.getPercentageAmount(killed, total);
		String ahrim = player.barrowKills[BrotherData.AHRIM.ordinal()] ? "@red@Ahrim the Blighted" : "@gre@Ahrim the Blighted";
		String dharok = player.barrowKills[BrotherData.DHAROK.ordinal()] ? "@red@Dharok the Wretched" : "@gre@Dharok the Wretched";
		String guthan = player.barrowKills[BrotherData.GUTHAN.ordinal()] ? "@red@Guthan the Infested" : "@gre@Guthan the Infested";
		String karil = player.barrowKills[BrotherData.KARIL.ordinal()] ? "@red@Karil the Tainted" : "@gre@Karil the Tainted";
		String torag = player.barrowKills[BrotherData.TORAG.ordinal()] ? "@red@Torag the Corrupted" : "@gre@Torag the Corrupted";
		String verac = player.barrowKills[BrotherData.VERAC.ordinal()] ? "@red@Verac the Defiled" : "@gre@Verac the Defiled";
		ActivityPanel.update(player, percentage, "Barrows", new Item(19629, 0), "Killed: <col=FF5500>" + killed + "</col> - Remaining: <col=FF5500>" + (total - killed) + "</col>", ahrim, dharok, guthan, karil, torag, verac);
	}

	@Override
	public boolean canTeleport(Player player) {
		return true;
	}

	@Override
	protected boolean clickObject(Player player, ObjectInteractionEvent event) {
		int id = event.getObject().getId();
		switch(id) {
			case 20973:
				if(player.barrowsKillCount == 6) {
					if(player.inventory.getFreeSlots() < 5) {
						player.dialogueFactory.sendPlayerChat("I should free up some inventory slots first.").execute();
						return true;
					}
					BarrowsUtility.generateRewards(player);
					player.barrowsKillCount = 0;
					player.barrowKills = new boolean[BrotherData.values().length];
					player.hiddenBrother = null;
					player.send(new SendMessage("You have completed the barrows minigame, well done!"));
					finish();
				} else if(player.barrowsKillCount == 5) {
					if(brotherMob == null) {
						brotherMob = new Mob(player.hiddenBrother.getNpcId(), player.getPosition());
						add(brotherMob);
						brotherMob.speak("How dare you disturb my slumber!");
						brotherMob.getCombat().attack(player);
						brotherMob.owner = player;
						player.send(new SendEntityHintArrow(brotherMob));
					}
					return true;
				}
				break;
			case 20667:
				move(BrotherData.AHRIM.getHillPosition());
				return true;
			case 20770:
				summon(BrotherData.AHRIM);
				return true;
			case 20672:
				move(BrotherData.VERAC.getHillPosition());
				return true;
			case 20772:
				summon(BrotherData.VERAC);
				return true;
			case 20668:
				move(BrotherData.DHAROK.getHillPosition());
				return true;
			case 20720:
				summon(BrotherData.DHAROK);
				return true;
			case 20671:
				move(BrotherData.TORAG.getHillPosition());
				return true;
			case 20721:
				summon(BrotherData.TORAG);
				return true;
			case 20669:
				move(BrotherData.GUTHAN.getHillPosition());
				return true;
			case 20722:
				summon(BrotherData.GUTHAN);
				return true;
			case 20670:
				move(BrotherData.KARIL.getHillPosition());
				return true;
			case 20771:
				summon(BrotherData.KARIL);
				return true;
		}
		return false;
	}

	@Override
	public boolean safe() {
		return false;
	}

	@Override
	public ActivityType getType() {
		return ActivityType.BARROWS;
	}
}
