package com.nardah.game.world.entity.combat.strategy.npc.boss;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.pathfinding.path.SimplePathChecker;
import com.nardah.game.world.position.Position;
import com.nardah.util.Utility;
import com.nardah.game.Animation;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.combat.FormulaModifier;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.strategy.npc.MultiStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcRangedStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.data.LockType;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.items.containers.equipment.Equipment;

/**
 * @author Daniel
 */
public class Zulrah extends MultiStrategy {
	private static final MagicAttack MAGIC = new MagicAttack();
	private static final RangedAttack RANGED = new RangedAttack();
	private static final MeleeAttack MELEE = new MeleeAttack();
	
	private static final FormulaModifier<Mob> MODIFIER = new FormulaModifier<Mob>() {
		@Override
		public int modifyAttackLevel(Mob attacker, Actor defender, int level) {
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
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public boolean withinDistance(Mob attacker, Actor defender) {
			return Utility.within(attacker, defender, getAttackDistance(attacker, attacker.getCombat().getFightType())) && SimplePathChecker.checkProjectile(attacker, defender);
		}
		
		@Override
		public void start(Mob attacker, Actor defender, Hit[] hits) {
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
		public void hit(Mob attacker, Actor defender, Hit hit) {
			/* No super call because of splash gfx */
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit hit = nextMagicHit(attacker, defender, 0);
			hit.setAccurate(false);
			return new CombatHit[]{hit};
		}
		
		@Override
		public int getAttackDelay(Mob attacker, Actor defender, FightType fightType) {
			return 8;
		}
		
		@Override
		public int getAttackDistance(Mob attacker, FightType fightType) {
			return 3;
		}
		
		@Override
		public int modifyOffensiveBonus(Mob attacker, Actor defender, int bonus) {
			FightType fightType = attacker.getCombat().getFightType();
			bonus = attacker.getBonus(fightType.getBonus());
			return attacker.getCombat().modifyOffensiveBonus(defender, bonus);
		}
		
		@Override
		public int modifyAggressiveBonus(Mob attacker, Actor defender, int bonus) {
			bonus = attacker.getBonus(Equipment.STRENGTH_BONUS);
			return attacker.getCombat().modifyAggresiveBonus(defender, bonus);
		}
		
		@Override
		public int modifyDefensiveBonus(Actor attacker, Mob defender, int bonus) {
			FightType fightType = attacker.getCombat().getFightType();
			bonus = defender.getBonus(fightType.getCorrespondingBonus());
			return defender.getCombat().modifyDefensiveBonus(attacker, bonus);
		}
		
	}
	
	private static class RangedAttack extends NpcRangedStrategy {
		private RangedAttack() {
			super(CombatProjectile.getDefinition("Zulrah Ranged"));
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{nextRangedHit(attacker, defender, 41)};
		}
		
		@Override
		public int getAttackDistance(Mob attacker, FightType fightType) {
			return 10;
		}
		
	}
	
	private static class MagicAttack extends NpcMagicStrategy {
		private MagicAttack() {
			super(CombatProjectile.getDefinition("Zulrah Magic"));
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit combatHit = nextMagicHit(attacker, defender, 41);
			combatHit.setAccurate(true);
			return new CombatHit[]{combatHit};
		}
		
		@Override
		public int getAttackDistance(Mob attacker, FightType fightType) {
			return 10;
		}
		
	}
	
}
