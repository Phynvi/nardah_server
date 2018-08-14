package io.battlerune.game.world.entity.combat.strategy.player.special.melee;

import io.battlerune.game.Animation;
import io.battlerune.game.Graphic;
import io.battlerune.game.UpdatePriority;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.strategy.player.PlayerMeleeStrategy;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.Direction;
import io.battlerune.game.world.entity.actor.data.LockType;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.position.Position;
import io.battlerune.util.RandomUtils;

/**
 * Handles the dragon spear weapon special attack.
 * @author Daniel
 */
public class DragonSpear extends PlayerMeleeStrategy {
	private static final DragonSpear INSTANCE = new DragonSpear();
	private static final Animation ANIMATION = new Animation(1064, UpdatePriority.HIGH);
	private static final Graphic GRAPHIC = new Graphic(253, true, UpdatePriority.HIGH);

	@Override
	public boolean canAttack(Player attacker, Actor defender) {
		// Let them have some fun.
		/*
		 * if (defender.isPlayer() && defender.width() > 1 && defender.length() > 1) {
		 * attacker.send(new SendMessage("That creature is too large to knock back!"));
		 * return false; }
		 */

		Direction direction = attacker.movement.lastDirection;
		Direction opposite = Direction.getOppositeDirection(direction);
		Position position = defender.getPosition().transform(opposite.getFaceLocation());

		/*
		 * if (!TraversalMap.isTraversable(position, direction, false)) {
		 * attacker.send(new
		 * SendMessage("That entity can not be knocked back as something is blocking it!"
		 * )); return false; }
		 */

		return super.canAttack(attacker, defender);
	}

	@Override
	public void attack(Player attacker, Actor defender, Hit hit) {
		super.attack(attacker, defender, hit);

		Direction direction = attacker.movement.lastDirection;
		Direction opposite = Direction.getOppositeDirection(direction);
		Position position = defender.getPosition().transform(opposite.getFaceLocation());

		if(hit.getDamage() == 0) {
			hit.setDamage(RandomUtils.inclusive(0, 20)); // Fuck it
		}
		attacker.graphic(GRAPHIC);
		defender.movement.reset();
		World.schedule(1, () -> {
			System.out.println(defender.getName() + " is being pushed to " + position);
			defender.move(position);
		});
		World.schedule(2, () -> defender.locking.lock(6, LockType.STUN));
	}

	@Override
	public CombatHit[] getHits(Player attacker, Actor defender) {
		return new CombatHit[]{nextMeleeHit(attacker, defender)};
	}

	@Override
	public Animation getAttackAnimation(Player attacker, Actor defender) {
		return ANIMATION;
	}

	public static DragonSpear get() {
		return INSTANCE;
	}

}