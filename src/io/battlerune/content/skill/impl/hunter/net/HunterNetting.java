package io.battlerune.content.skill.impl.hunter.net;

import io.battlerune.Config;
import io.battlerune.content.activity.randomevent.RandomEventHandler;
import io.battlerune.content.skill.impl.hunter.Hunter;
import io.battlerune.content.skill.impl.hunter.net.impl.Butterfly;
import io.battlerune.content.skill.impl.hunter.net.impl.Impling;
import io.battlerune.game.Animation;
import io.battlerune.game.action.Action;
import io.battlerune.game.action.impl.ImplingTeleportAction;
import io.battlerune.game.action.policy.WalkablePolicy;
import io.battlerune.game.world.entity.mob.npc.Npc;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.skill.Skill;
import io.battlerune.game.world.items.Item;
import io.battlerune.game.world.position.Position;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.Utility;

public class HunterNetting {

	public static void catchImpling(Player player, Npc npc, Impling impling, Position original) {
		if(!player.equipment.contains(10010) && !player.inventory.contains(10010)) {
			player.send(new SendMessage("You need a butterfly net to capture this impling."));
			return;
		}

		if(!player.inventory.contains(11260)) {
			player.send(new SendMessage("You need a impling jar to hold this impling."));
			return;
		}

		if(player.skills.getLevel(Skill.HUNTER) < impling.getLevel()) {
			player.send(new SendMessage("You need a hunter level of " + impling.getLevel() + " to catch this impling."));
			return;
		}

		Item item = new Item(impling.getReward(), 1);

		if(!player.inventory.hasCapacityFor(item)) {
			player.send(new SendMessage("You do not have enough inventory space to hold this impling."));
			return;
		}

		if(player.skills.getLevel(Skill.STRENGTH) >= 95) {
			player.animate(new Animation(6606));
			player.action.execute(catchImpling(player, npc, original, new Item(11260), item, impling.getExperience()), true);
		}
	}

	public static void catchButterfly(Player player, Npc npc, Butterfly butterfly, Position original) {
		if(!player.equipment.contains(10010) && !player.inventory.contains(10010)) {
			player.send(new SendMessage("You need a butterfly net to capture this butterfly."));
			return;
		}

		if(!player.inventory.contains(10012)) {
			player.send(new SendMessage("You need a butterfly jar to hold this impling."));
			return;
		}

		if(player.skills.getLevel(Skill.HUNTER) < butterfly.getLevel()) {
			player.send(new SendMessage("You need a hunter level of " + butterfly.getLevel() + " to catch this butterfly."));
			return;
		}

		Item item = new Item(butterfly.getItem(), 1);

		if(!player.inventory.hasCapacityFor(item)) {
			player.send(new SendMessage("You do not have enough inventory space to hold this butterfly."));
			return;
		}

		if(player.skills.getLevel(Skill.STRENGTH) >= 95) {
			player.animate(new Animation(6606));
			player.action.execute(catchImpling(player, npc, original, new Item(11260), item, butterfly.getExperience()), true);
		}
	}

	private static Action<Player> catchImpling(Player player, Npc npc, Position original, Item first, Item second, int experience) {
		return new Action<Player>(player, 1) {

			@Override
			public void execute() {
				boolean success = false;

				if(npc.getPosition().equals(original)) {
					success = true;
				}

				if(Utility.random(3) == 1) {
					success = true;
				}

				if(success) {
					npc.locking.lock();
					npc.move(new Position(1, 1));
					Hunter.SPAWNS.remove(npc.id, npc.spawnPosition);
					npc.action.execute(new ImplingTeleportAction(npc), true);
					player.inventory.replace(first.getId(), second.getId(), true);
					player.skills.addExperience(Skill.HUNTER, experience * Config.HUNTER_MODIFICATION);
					player.send(new SendMessage("You catch the " + npc.getName() + " and place it in the jar."));
					RandomEventHandler.trigger(player);
				} else {
					player.send(new SendMessage("You fail to catch the " + npc.getName() + "."));
				}
				cancel();
			}

			@Override
			public String getName() {
				return "Catch impling";
			}

			@Override
			public boolean prioritized() {
				return false;
			}

			@Override
			public WalkablePolicy getWalkablePolicy() {
				return WalkablePolicy.NON_WALKABLE;
			}
		};
	}
}
