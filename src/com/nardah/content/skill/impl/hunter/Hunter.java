package com.nardah.content.skill.impl.hunter;

import com.nardah.content.skill.impl.hunter.net.HunterNetting;
import com.nardah.content.skill.impl.hunter.net.ImplingReward;
import com.nardah.content.skill.impl.hunter.net.impl.Butterfly;
import com.nardah.content.skill.impl.hunter.net.impl.Impling;
import com.nardah.content.skill.impl.hunter.trap.BoxTrapping;
import com.nardah.content.skill.impl.hunter.trap.Traps;
import com.nardah.Config;
import com.nardah.content.event.impl.ItemInteractionEvent;
import com.nardah.content.event.impl.NpcInteractionEvent;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.position.Position;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.RandomUtils;
import com.nardah.util.Utility;

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

		Mob mob = event.getMob();
		Position original = mob.getPosition();

		if(Impling.forId(mob.id).isPresent()) {
			HunterNetting.catchImpling(player, mob, Impling.forId(mob.id).get(), original);
			return true;
		} else if(Butterfly.forId(mob.id).isPresent()) {
			HunterNetting.catchButterfly(player, mob, Butterfly.forId(mob.id).get(), original);
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

//		for(Traps trap : Traps.values()) {
//			if(trap.getItems()[0].getId() != item.getId())
//				continue;
//			player.action.execute(new BoxTrapping(player, trap), true);
//			return true;
//		}

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
