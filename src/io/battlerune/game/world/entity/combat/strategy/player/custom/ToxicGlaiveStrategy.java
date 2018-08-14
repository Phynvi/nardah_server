package io.battlerune.game.world.entity.combat.strategy.player.custom;

import io.battlerune.game.Animation;
import io.battlerune.game.Graphic;
import io.battlerune.game.UpdatePriority;
import io.battlerune.game.world.entity.combat.CombatType;
import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.strategy.player.PlayerMeleeStrategy;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.player.Player;

/**
 * @author Adam_#6723 Handles the animation of Toxic glaive and gfx with it
 * aswell as effects.
 */

public class ToxicGlaiveStrategy extends PlayerMeleeStrategy {
	
	private static final ToxicGlaiveStrategy INSTANCE = new ToxicGlaiveStrategy();
	
	public String name() {
		return "Icy Glaive";
	}
	
	@Override
	public CombatType getCombatType() {
		return CombatType.MELEE;
	}
	
	/**
	 * Atack delay.
	 **/
	
	@Override
	public int getAttackDelay(Player attacker, Actor defender, FightType fightType) {
		return 3;
	}
	
	/**
	 * Instane's the class to be called upon,and applied to an item.
	 **/
	public static ToxicGlaiveStrategy get() {
		return INSTANCE;
	}
	
	private static final Animation ANIMATION = new Animation(439, UpdatePriority.HIGH);
	
	@Override
	public void attack(Player attacker, Actor defender, Hit hit) {
		defender.graphic(new Graphic(402, UpdatePriority.HIGH));
		super.attack(attacker, defender, hit);
	}
	
	@Override
	public void start(Player attacker, Actor defender, Hit[] hits) {
		super.start(attacker, defender, hits);
		// TODO EFFECT
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
		return roll * 5 / 3;
	}
	
}
