package com.nardah.game.world.entity.actor.mob.drop;

import com.nardah.content.CrystalChest;
import com.nardah.game.world.position.Position;
import com.nardah.util.RandomGen;
import com.nardah.util.Utility;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.items.ItemDefinition;
import com.nardah.game.world.items.ground.GroundItem;
import com.nardah.net.packet.out.SendMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The manager class which holds the static entries of drop tables and has a
 * method to execute a drop table from the specified mob.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 * @since 29-1-2017.
 */
public final class MobDropManager {

	/**
	 * The collection of mob ids by their representative drop tables.
	 */
	public static final Map<Integer, MobDropTable> NPC_DROPS = new HashMap<>();

	/**
	 * Alternative drop positions for mobs (i.e kraken, zulrah)
	 */
	private static final int[] ALTERNATIVE_POSITION = {2044, 2043, 2042, 494};

	/**
	 * Attempts to drop the drop table which belongs to {@code mob#id}.
	 */
	public static void drop(Player killer, Mob mob) {
		if(killer == null) {
			return;
		}

		if(mob == null) {
			return;
		}

		MobDropTable table = NPC_DROPS.get(mob.id);

		if(table == null) {
			return;
		}

		RandomGen gen = new RandomGen();
		List<MobDrop> npc_drops = table.generate();
		Position dropPosition = mob.getPosition().copy();
		/*
		 * if(killer.equipment.contains(1995)) {
		 * killer.message("Your loot is auto picked up"); } else {
		 * killer.message("I'm a moron and did something stupid. FK"); }
		 */
		// special drop positions
		if(checkAlternativePosition(mob.id)) {
			dropPosition = killer.getPosition().copy();
		}

		// crystal key drop
		if(mob.getMaximumHealth() > 50 && Utility.random(150) <= 5) {
			Item crystal_key = Utility.randomElement(CrystalChest.KEY_HALVES);
			GroundItem.create(killer, crystal_key, dropPosition);
			killer.send(new SendMessage("<col=BA383E>Rare Drop Notification: </col>" + crystal_key.getName()));
		}

		// casket drop
		if(mob.getMaximumHealth() > 10 && Utility.random(200) <= 10) {
			Item casket = new Item(405);
			GroundItem.create(killer, casket, dropPosition);
			killer.send(new SendMessage("<col=BA383E>Rare Drop Notification: </col>" + casket.getName()));
		}

		// drop table
		for(MobDrop drop : npc_drops) {
			Item item = drop.toItem(gen);

			if(killer.equipment.contains(10557)) {
				killer.message("@or2@[AUTO LOOT] Your Loot has been added to your inventory.");
				killer.inventory.add(new Item(item.getId()));
				killer.message("@or2@[AUTO LOOT] Your Loot Was: " + ItemDefinition.get(item.getId()).getName());
				return;
			}

			if(item.getId() == 11941 && killer.playerAssistant.contains(item)) { // looting bag
				killer.message("You have missed out on " + Utility.getAOrAn(item.getName()) + " " + item.getName() + " since you already have on on your account.");
				continue;
			}
			if(item.getId() == 2677 && killer.playerAssistant.contains(item)) { // easy clue
				killer.message("You have missed out on " + Utility.getAOrAn(item.getName()) + " " + item.getName() + " since you already have on on your account.");
				continue;
			}
			if(item.getId() == 2801 && killer.playerAssistant.contains(item)) { // medium clue
				killer.message("You have missed out on " + Utility.getAOrAn(item.getName()) + " " + item.getName() + " since you already have on on your account.");
				continue;
			}
			if(item.getId() == 2722 && killer.playerAssistant.contains(item)) {// hard clue
				killer.message("You have missed out on " + Utility.getAOrAn(item.getName()) + " " + item.getName() + " since you already have on on your account.");
				continue;
			}
			if(item.getId() == 12073 && killer.playerAssistant.contains(item)) {// elite clue
				killer.message("You have missed out on " + Utility.getAOrAn(item.getName()) + " " + item.getName() + " since you already have on on your account.");
				continue;
			}
			//
			//            if (killer.clanChannel != null && killer.clanChannel.lootshareEnabled()) {
			//                killer.forClan(channel -> channel.splitLoot(killer, mob, item));
			//                return;
			//            }

			if(killer.settings.dropNotification && item.getValue() > 1_000_000) {
				String name = item.getName();
				killer.send(new SendMessage("<col=BA383E>Rare Drop Notification: </col>" + name + " (" + Utility.formatDigits(item.getValue()) + " coins)"));
				World.sendMessage("<col=BA383E>Nardah: <col=" + killer.right.getColor() + ">" + killer.getName() + " </col>has just received " + Utility.getAOrAn(name) + " <col=BA383E>" + name + " </col>from <col=BA383E>" + mob.getName() + "</col>!");
			} else if(killer.settings.untradeableNotification && !item.isTradeable()) {
				killer.send(new SendMessage("<col=F5424B>Untradeable Drop Notification: </col>" + item.getName()));
			}

			if(!item.isStackable()) {
				Item single = item.createWithAmount(1);
				for(int i = 0; i < item.getAmount(); i++)
					GroundItem.create(killer, single, dropPosition);
			} else {
				GroundItem.create(killer, item, dropPosition);

			}
		}
	}

	private static boolean checkAlternativePosition(int npc) {
		for(int alternative : ALTERNATIVE_POSITION) {
			if(alternative == npc)
				return true;
		}
		return false;
	}
}
