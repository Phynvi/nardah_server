package io.battlerune.game.world.entity.combat.strategy.player.special.melee;

import io.battlerune.game.Animation;
import io.battlerune.game.Graphic;
import io.battlerune.game.UpdatePriority;
import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.strategy.player.PlayerMeleeStrategy;
import io.battlerune.game.world.entity.mob.Mob;
import io.battlerune.game.world.entity.mob.player.Player;

/**
 * @author Michael | Chex
 * @editor adam Fixed several alarming issue's with the AGS. Adjusted the damage
 * ratio too.
 */

public class ArmadylGodsword extends PlayerMeleeStrategy {

	// AGS(normal): 7644, AGS(OR): 7645
	private static final Animation ANIMATION = new Animation(7644, UpdatePriority.HIGH);
	private static final Graphic GRAPHIC = new Graphic(1211);

	private static final ArmadylGodsword INSTANCE = new ArmadylGodsword();

	private ArmadylGodsword() {
	}

	@Override
	public void start(Player attacker, Mob defender, Hit[] hits) {
		super.start(attacker, defender, hits);
		attacker.animate(ANIMATION);
		attacker.graphic(GRAPHIC);
	}

	@Override
	public int getAttackDelay(Player attacker, Mob defender, FightType fightType) {
		return 4;
	}

	@Override
	public Animation getAttackAnimation(Player attacker, Mob defender) {
		return ANIMATION;
	}

	@Override
	public int modifyAccuracy(Player attacker, Mob defender, int roll) {
		return 3 * roll;
	}

	@Override
	public int modifyDamage(Player attacker, Mob defender, int damage) {
		return (int) (damage * 1.575);
	}

	public static ArmadylGodsword get() {
		return INSTANCE;
	}

}