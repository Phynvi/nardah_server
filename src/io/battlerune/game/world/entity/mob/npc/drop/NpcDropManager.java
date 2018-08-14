package io.battlerune.game.world.entity.mob.npc.drop;

import io.battlerune.content.CrystalChest;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.mob.npc.Npc;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.items.Item;
import io.battlerune.game.world.items.ItemDefinition;
import io.battlerune.game.world.items.ground.GroundItem;
import io.battlerune.game.world.position.Position;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.RandomGen;
import io.battlerune.util.Utility;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The manager class which holds the static entries of drop tables and has a
 * method to execute a drop table from the specified npc.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 * @since 29-1-2017.
 */
public final class NpcDropManager {

	/**
	 * The collection of npc ids by their representative drop tables.
	 */
	public static final Map<Integer, NpcDropTable> NPC_DROPS = new HashMap<>();

	/**
	 * Alternative drop positions for mobs (i.e kraken, zulrah)
	 */
	private static final int[] ALTERNATIVE_POSITION = {2044, 2043, 2042, 494};

	/**
	 * Attempts to drop the drop table which belongs to {@code npc#id}.
	 */
	public static void drop(Player killer, Npc npc) {
		if(killer == null) {
			return;
		}

		if(npc == null) {
			return;
		}

		NpcDropTable table = NPC_DROPS.get(npc.id);

		if(table == null) {
			return;
		}

		RandomGen gen = new RandomGen();
		List<NpcDrop> npc_drops = table.generate();
		Position dropPosition = npc.getPosition().copy();
		/*
		 * if(killer.equipment.contains(1995)) {
		 * killer.message("Your loot is auto picked up"); } else {
		 * killer.message("I'm a moron and did something stupid. FK"); }
		 */
		// special drop positions
		if(checkAlternativePosition(npc.id)) {
			dropPosition = killer.getPosition().copy();
		}

		// crystal key drop
		if(npc.getMaximumHealth() > 50 && Utility.random(150) <= 5) {
			Item crystal_key = Utility.randomElement(CrystalChest.KEY_HALVES);
			GroundItem.create(killer, crystal_key, dropPosition);
			killer.send(new SendMessage("<col=BA383E>Rare Drop Notification: </col>" + crystal_key.getName()));
		}

		// casket drop
		if(npc.getMaximumHealth() > 10 && Utility.random(200) <= 10) {
			Item casket = new Item(405);
			GroundItem.create(killer, casket, dropPosition);
			killer.send(new SendMessage("<col=BA383E>Rare Drop Notification: </col>" + casket.getName()));
		}

		// drop table
		for(NpcDrop drop : npc_drops) {
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
			//                killer.forClan(channel -> channel.splitLoot(killer, npc, item));
			//                return;
			//            }

			if(killer.settings.dropNotification && item.getValue() > 1_000_000) {
				String name = item.getName();
				killer.send(new SendMessage("<col=BA383E>Rare Drop Notification: </col>" + name + " (" + Utility.formatDigits(item.getValue()) + " coins)"));
				World.sendMessage("<col=BA383E>Runity: <col=" + killer.right.getColor() + ">" + killer.getName() + " </col>has just received " + Utility.getAOrAn(name) + " <col=BA383E>" + name + " </col>from <col=BA383E>" + npc.getName() + "</col>!");
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
