package io.battlerune.content.consume;

import io.battlerune.game.Animation;
import io.battlerune.game.UpdatePriority;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.skill.Skill;
import io.battlerune.net.packet.out.SendMessage;

/**
 * @author Adam_#6723 Handles the overhealing of anglerfish, don't ask why i
 * created seperate class...didn't wanna touch the aids they call
 * "eat.java".
 */

public class Anglerfish {

	/**
	 * The method that executes the Angler fish actionn.
	 * @param player the player to do this action for. This method only executes
	 * after they've elapsed the food delay tier.
	 */
	public static void onAnglerEffect(Player player) {
		if(player.foodDelay.elapsed(1300)) {
			modifySkill(player, Skill.HITPOINTS, 0.21, 2);
			player.animate(new Animation(829, UpdatePriority.LOW));
			player.getCombat().reset();
			player.getCombat().cooldown(2);
			player.foodDelay.reset();
			player.inventory.remove(13441, 1);
			player.skills.refresh(Skill.HITPOINTS);
			player.send(new SendMessage("You eat the " + "Anglerfish" + "."));
		}

	}

	/**
	 * The method that executes the basic effect food action that will append the
	 * level of {@code skill}.
	 * @param player the player to do this action for.
	 */
	public static void modifySkill(Player player, int skill, double percentage, int base) {
		Skill s = player.skills.get(skill);
		int realLevel = s.getMaxLevel();

		final int boostLevel = (int) (realLevel * percentage + base);

		int cap = s.getLevel();
		if(cap < realLevel + boostLevel) {
			cap = realLevel + boostLevel;
		}

		if(skill == Skill.HITPOINTS && boostLevel < 0) {
			int damage = boostLevel;
			if(player.getCurrentHealth() + damage <= 0)
				damage = -player.getCurrentHealth() + 1;
			player.damage(new Hit(-damage));
		} else {
			player.skills.get(skill).modifyLevel(level -> level + boostLevel, 0, cap);
		}

		player.skills.refresh(skill);
	}

}
