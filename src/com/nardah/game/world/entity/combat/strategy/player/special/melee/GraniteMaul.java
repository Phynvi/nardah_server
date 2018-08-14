package com.nardah.game.world.entity.combat.strategy.player.special.melee;

import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.strategy.player.PlayerMeleeStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;

public class GraniteMaul extends PlayerMeleeStrategy {
	private static final Animation ANIMATION = new Animation(1667, UpdatePriority.HIGH);
	private static final Graphic GRAPHIC = new Graphic(340);
	private static final GraniteMaul INSTANCE = new GraniteMaul();

	private GraniteMaul() {
	}

	@Override
	public void start(Player attacker, Actor defender, Hit[] hits) {
		super.start(attacker, defender, hits);

		if(attacker.attributes.is("granite-maul-spec")) {
			CombatHit damage = nextMeleeHit(attacker, defender);
			defender.damage(damage);
			defender.getCombat().getDamageCache().add(attacker, damage);
			attacker.getCombatSpecial().drain(attacker);
			attacker.attributes.remove("granite-maul-spec");
		}

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
	public int getAttackDelay(Player attacker, Actor defender, FightType fightType) {
		return 3;
	}

	public static GraniteMaul get() {
		return INSTANCE;
	}

}