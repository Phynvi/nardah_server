package io.battlerune.game.world.entity.combat.strategy.npc.boss;

import io.battlerune.game.Animation;
import io.battlerune.game.UpdatePriority;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.combat.FormulaModifier;
import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.strategy.npc.MultiStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcRangedStrategy;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.data.LockType;
import io.battlerune.game.world.entity.actor.npc.Npc;
import io.battlerune.game.world.items.containers.equipment.Equipment;
import io.battlerune.game.world.pathfinding.path.SimplePathChecker;
import io.battlerune.game.world.position.Position;
import io.battlerune.util.Utility;

import static io.battlerune.game.world.entity.combat.projectile.CombatProjectile.getDefinition;

/**
 * @author Daniel
 */
public class Zulrah extends MultiStrategy {
	private static final MagicAttack MAGIC = new MagicAttack();
	private static final RangedAttack RANGED = new RangedAttack();
	private static final MeleeAttack MELEE = new MeleeAttack();
	
	private static final FormulaModifier<Npc> MODIFIER = new FormulaModifier<Npc>() {
		@Override
		public int modifyAttackLevel(Npc attacker, Actor defender, int level) {
			return 1;
		}
	};
	
	public Zulrah() {
		setRanged();
	}
	
	public void setMelee() {
		currentStrategy = MELEE;
	}
	
	public void setMagic() {
		currentStrategy = MAGIC;
	}
	
	public void setRanged() {
		currentStrategy = RANGED;
	}
	
	private static class MeleeAttack extends NpcMagicStrategy {
		
		MeleeAttack() {
			super(getDefinition("EMPTY"));
		}
		
		@Override
		public boolean withinDistance(Npc attacker, Actor defender) {
			return Utility.within(attacker, defender, getAttackDistance(attacker, attacker.getCombat().getFightType())) && SimplePathChecker.checkProjectile(attacker, defender);
		}
		
		@Override
		public void start(Npc attacker, Actor defender, Hit[] hits) {
			Position end = defender.getPosition();
			attacker.face(end);
			attacker.animate(new Animation(5807, UpdatePriority.HIGH));
			World.schedule(6, () -> {
				attacker.face(end);
				attacker.animate(new Animation(5806, UpdatePriority.HIGH));
				if(defender.getPosition().equals(end)) {
					attacker.getCombat().addModifier(MODIFIER);
					Hit hit = nextMeleeHit(attacker, defender, 41);
					attacker.getCombat().removeModifier(MODIFIER);
					defender.damage(hit);
					defender.getCombat().getDamageCache().add(attacker, hit);
					defender.locking.lock(2, LockType.STUN);
				}
			});
		}
		
		@Override
		public void hit(Npc attacker, Actor defender, Hit hit) {
			/* No super call because of splash gfx */
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			CombatHit hit = nextMagicHit(attacker, defender, 0);
			hit.setAccurate(false);
			return new CombatHit[]{hit};
		}
		
		@Override
		public int getAttackDelay(Npc attacker, Actor defender, FightType fightType) {
			return 8;
		}
		
		@Override
		public int getAttackDistance(Npc attacker, FightType fightType) {
			return 3;
		}
		
		@Override
		public int modifyOffensiveBonus(Npc attacker, Actor defender, int bonus) {
			FightType fightType = attacker.getCombat().getFightType();
			bonus = attacker.getBonus(fightType.getBonus());
			return attacker.getCombat().modifyOffensiveBonus(defender, bonus);
		}
		
		@Override
		public int modifyAggressiveBonus(Npc attacker, Actor defender, int bonus) {
			bonus = attacker.getBonus(Equipment.STRENGTH_BONUS);
			return attacker.getCombat().modifyAggresiveBonus(defender, bonus);
		}
		
		@Override
		public int modifyDefensiveBonus(Actor attacker, Npc defender, int bonus) {
			FightType fightType = attacker.getCombat().getFightType();
			bonus = defender.getBonus(fightType.getCorrespondingBonus());
			return defender.getCombat().modifyDefensiveBonus(attacker, bonus);
		}
		
	}
	
	private static class RangedAttack extends NpcRangedStrategy {
		private RangedAttack() {
			super(getDefinition("Zulrah Ranged"));
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			return new CombatHit[]{nextRangedHit(attacker, defender, 41)};
		}
		
		@Override
		public int getAttackDistance(Npc attacker, FightType fightType) {
			return 10;
		}
		
	}
	
	private static class MagicAttack extends NpcMagicStrategy {
		private MagicAttack() {
			super(getDefinition("Zulrah Magic"));
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			CombatHit combatHit = nextMagicHit(attacker, defender, 41);
			combatHit.setAccurate(true);
			return new CombatHit[]{combatHit};
		}
		
		@Override
		public int getAttackDistance(Npc attacker, FightType fightType) {
			return 10;
		}
		
	}
	
}
