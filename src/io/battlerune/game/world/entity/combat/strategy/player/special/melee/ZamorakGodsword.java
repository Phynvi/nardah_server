package io.battlerune.game.world.entity.combat.strategy.player.special.melee;

import io.battlerune.game.Animation;
import io.battlerune.game.Graphic;
import io.battlerune.game.UpdatePriority;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.strategy.player.PlayerMeleeStrategy;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.data.LockType;
import io.battlerune.game.world.entity.actor.player.Player;

/**
 * Handles the zamorak sword weapon special attack.
 * @author Daniel
 */
public class ZamorakGodsword extends PlayerMeleeStrategy {

	// ZGS(normal): 7638, ZGS(OR): 7639
	private static final Animation ANIMATION = new Animation(7638, UpdatePriority.HIGH);
	private static final Graphic GRAPHIC = new Graphic(1210, UpdatePriority.HIGH);
	private static final Graphic FREEZE_GRAPHIC = new Graphic(369, UpdatePriority.HIGH);
	private static final ZamorakGodsword INSTANCE = new ZamorakGodsword();

	@Override
	public void attack(Player attacker, Actor defender, Hit hit) {
		super.attack(attacker, defender, hit);
		attacker.graphic(GRAPHIC);

		if(hit.getDamage() > 0) {
			if(defender.locking.locked())
				return;
			defender.graphic(FREEZE_GRAPHIC);
			defender.locking.lock(20, LockType.FREEZE);
		}
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
		return 2 * roll;
	}

	@Override
	public int modifyDamage(Player attacker, Actor defender, int damage) {
		return damage * 11 / 10;
	}

	public static ZamorakGodsword get() {
		return INSTANCE;
	}

}