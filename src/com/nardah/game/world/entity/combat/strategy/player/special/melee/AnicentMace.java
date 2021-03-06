package com.nardah.game.world.entity.combat.strategy.player.special.melee;

import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.strategy.player.PlayerMeleeStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.prayer.Prayer;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.net.packet.out.SendMessage;

/**
 * @author Adam_#6723
 */

public class AnicentMace extends PlayerMeleeStrategy {

	private static final AnicentMace INSTANCE = new AnicentMace();

	private static final Animation ANIMATION = new Animation(6147, UpdatePriority.HIGH);
	private static final Graphic GRAPHIC = new Graphic(1052, true, UpdatePriority.HIGH);

	@Override
	public void start(Player attacker, Actor defender, Hit[] hits) {
		super.start(attacker, defender, hits);
		AnicentMace.onRestoreEffect(attacker, true);
		int realLevel = attacker.skills.getMaxLevel(Skill.PRAYER);
		attacker.skills.get(Skill.PRAYER).modifyLevel(level -> level + (int) Math.floor(8 + (realLevel * 0.25)));
		attacker.skills.refresh(Skill.PRAYER);
		defender.prayer.deactivate(Prayer.PROTECT_FROM_MELEE);
		defender.getPlayer().send(new SendMessage("Your overhead prayers have been disabled!"));
		attacker.getPlayer().send(new SendMessage("You have disabled " + defender.getName() + "'s overhead prayers!"));
	}

	/**
	 * The method that executes the restoring for the anicentMace Special attack
	 * @param player the player to do this action for.
	 */
	private static void onRestoreEffect(Player player, boolean anicentRestore) {
		for(int index = 0; index <= 6; index++) {
			if((index == Skill.PRAYER) || (index == Skill.HITPOINTS)) {
				continue;
			}

			Skill skill = player.skills.get(index);
			int realLevel = skill.getMaxLevel();

			if(skill.getLevel() >= realLevel) {
				continue;
			}

			int formula = anicentRestore ? (int) Math.floor(8 + (realLevel * 0.25)) : (int) Math.floor(10 + (realLevel * 0.30));
			player.skills.get(index).modifyLevel(level -> level + formula);
			player.skills.refresh(index);
		}
	}

	@Override
	public void attack(Player attacker, Actor defender, Hit hit) {
		super.attack(attacker, defender, hit);
		attacker.graphic(GRAPHIC);
	}

	@Override
	public CombatHit[] getHits(Player attacker, Actor defender) {
		return new CombatHit[]{nextMeleeHit(attacker, defender)};
	}

	@Override
	public Animation getAttackAnimation(Player attacker, Actor defender) {
		return ANIMATION;
	}

	@Override
	public int modifyAccuracy(Player attacker, Actor defender, int roll) {
		return roll * 4 / 3;
	}

	@Override
	public int modifyDamage(Player attacker, Actor defender, int damage) {
		return damage * 23 / 20;
	}

	public static AnicentMace get() {
		return INSTANCE;
	}

}
