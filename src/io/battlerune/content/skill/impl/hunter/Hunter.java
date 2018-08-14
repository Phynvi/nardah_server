package io.battlerune.content.skill.impl.hunter;

import io.battlerune.Config;
import io.battlerune.content.event.impl.ItemInteractionEvent;
import io.battlerune.content.event.impl.NpcInteractionEvent;
import io.battlerune.content.skill.impl.hunter.net.HunterNetting;
import io.battlerune.content.skill.impl.hunter.net.ImplingReward;
import io.battlerune.content.skill.impl.hunter.net.impl.Butterfly;
import io.battlerune.content.skill.impl.hunter.net.impl.Impling;
import io.battlerune.content.skill.impl.hunter.trap.BoxTrapping;
import io.battlerune.content.skill.impl.hunter.trap.Traps;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.mob.npc.Npc;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.skill.Skill;
import io.battlerune.game.world.items.Item;
import io.battlerune.game.world.position.Position;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.RandomUtils;
import io.battlerune.util.Utility;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles the hunter skill.
 * @author Daniel
 */
public class Hunter extends Skill {

	/* Holds all the hunter spawns. */
	public static Map<Integer, Position> SPAWNS = new HashMap<>();

	/**
	 * Creates a new <code>Hunter<code>
	 */
	public Hunter(int level, double experience) {
		super(Skill.HUNTER, level, experience);
	}

	@Override
	protected double modifier() {
		return Config.HUNTER_MODIFICATION;
	}

	@Override
	protected boolean clickNpc(Player player, NpcInteractionEvent event) {
		if(event.getOpcode() != 0) {
			return false;
		}

		Npc npc = event.getNpc();
		Position original = npc.getPosition();

		if(Impling.forId(npc.id).isPresent()) {
			HunterNetting.catchImpling(player, npc, Impling.forId(npc.id).get(), original);
			return true;
		} else if(Butterfly.forId(npc.id).isPresent()) {
			HunterNetting.catchButterfly(player, npc, Butterfly.forId(npc.id).get(), original);
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected boolean clickItem(Player player, ItemInteractionEvent event) {
		if(event.getOpcode() != 0) {
			return false;
		}
		Item item = event.getItem();

		for(Traps trap : Traps.values()) {
			if(trap.getItems()[0].getId() != item.getId())
				continue;
			player.action.execute(new BoxTrapping(player, trap), true);
			return true;
		}

		if(!ImplingReward.forId(item.getId()).isPresent()) {
			return false;
		}
		ImplingReward impling = ImplingReward.forId(item.getId()).get();
		Item reward = Utility.randomElement(impling.getLootation());
		if(!player.inventory.hasCapacityFor(reward)) {
			player.send(new SendMessage("You do not have enough inventory space to enter this."));
			return true;
		}
		player.inventory.remove(item);
		boolean shatter = Utility.random(10) == 1;
		if(!shatter) {
			player.inventory.add(11260, 1);
			player.send(new SendMessage("You successfully open the jar."));
		} else {
			player.send(new SendMessage("The jar breaks as you open the jar, cutting you a bit."));
			player.damage(new Hit(Utility.random(3)));
		}
		player.inventory.add(reward);

		return true;
	}

	public static boolean calculateSuccess(Player player, int requiredLevel) {
		int level = player.skills.getLevel(Skill.HUNTER);
		int basePercentage = 30 - requiredLevel + ((level) / 2);
		if(basePercentage > 80)
			basePercentage = 80;
		else if(basePercentage < 30)
			basePercentage = 30;
		if(RandomUtils.random(100) < basePercentage)
			return true;
		return false;
	}
}
