package io.battlerune.game.world.entity.combat.attack;

import io.battlerune.game.world.entity.combat.CombatType;
import io.battlerune.game.world.entity.combat.FormulaModifier;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.hit.HitIcon;
import io.battlerune.game.world.entity.combat.hit.Hitsplat;
import io.battlerune.game.world.entity.mob.Mob;
import io.battlerune.util.RandomUtils;

/**
 * Supplies factory methods useful for combat.
 * @author Michael | Chex Chex Updated this class.
 */
public final class FormulaFactory {
	
	public static Hit nextMeleeHit(Mob attacker, Mob defender) {
		int max = getMaxHit(attacker, defender, CombatType.MELEE);
		return nextHit(attacker, defender, max, Hitsplat.NORMAL, HitIcon.MELEE);
	}
	
	public static Hit nextMeleeHit(Mob attacker, Mob defender, int max) {
		return nextHit(attacker, defender, max, Hitsplat.NORMAL, HitIcon.MELEE);
	}
	
	public static Hit nextRangedHit(Mob attacker, Mob defender, int max) {
		return nextHit(attacker, defender, max, Hitsplat.NORMAL, HitIcon.RANGED);
	}
	
	public static Hit nextMagicHit(Mob attacker, Mob defender, int max) {
		return nextHit(attacker, defender, max, Hitsplat.NORMAL, HitIcon.MAGIC);
	}
	
	private static Hit nextHit(Mob attacker, Mob defender, int max, Hitsplat hitsplat, HitIcon icon) {
		Hit hit = new Hit(max < 0 ? -1 : 0, hitsplat, icon, false);
		
		/*
		 * Use attacker's strategy for combat type since formulas are dependent on the
		 * main combat type of the attack. This allows for melee-based magic attacks,
		 * ranged-based melee attacks, etc.
		 */
		CombatType type = attacker.getStrategy().getCombatType();
		
		if(isAccurate(attacker, defender, type)) {
			if(max > 0) {
				max = type.getFormula().modifyDamage(attacker, defender, max);
				int verdict = RandomUtils.inclusive(0, max);
				
				if(verdict > defender.getCurrentHealth()) {
					verdict = defender.getCurrentHealth();
				}
				
				hit.setDamage(verdict);
			}
			
			hit.setAccurate(true);
		}
		
		return hit;
	}
	
	private static boolean isAccurate(Mob attacker, Mob defender, CombatType type) {
		double fetchattackRoll = rollOffensive(attacker, defender, type.getFormula());
		double fetchdefenceRoll = rollDefensive(attacker, defender, type.getFormula());
		return RandomUtils.success(fetchattackRoll / (fetchattackRoll + fetchdefenceRoll));
	}
	
	public static int rollOffensive(Mob attacker, Mob defender, FormulaModifier<Mob> formula) {
		int fetchroll = formula.modifyAccuracy(attacker, defender, 0);
		int fetchbonus = formula.modifyOffensiveBonus(attacker, defender, 0);
		return attacker.getCombat().modifyAccuracy(defender, fetchroll * (fetchbonus + 64));
	}
	
	public static int rollDefensive(Mob attacker, Mob defender, FormulaModifier<Mob> formula) {
		int fetchroll = formula.modifyDefensive(attacker, defender, 0);
		int fetchbonus = formula.modifyDefensiveBonus(attacker, defender, 0);
		return attacker.getCombat().modifyDefensive(defender, fetchroll * (fetchbonus + 64));
	}
	
	public static int getMaxHit(Mob attacker, Mob defender, CombatType type) {
		FormulaModifier<Mob> formula = type.getFormula();
		int fetchlevel = formula.modifyAggressive(attacker, defender, 0);
		int fetchbonus = formula.modifyAggressiveBonus(attacker, defender, 0);
		return maxHit(fetchlevel, fetchbonus);
	}
	
	/**
	 * Handles accuracy bonuses, takes into account players weapon accuracy - adam. MAKE SUREE TO REMEMBER TO USET HIS AND CALL UPON THIS!
	 * Tested and accuracy seems more real.
	 **/
	public static int getStatsBonuses(Mob attacker, Mob defender, CombatType type) {
		FormulaModifier<Mob> formula = type.getFormula();
		int fetchlevel = formula.modifyAggressive(attacker, defender, 0);
		int fetchbonus = formula.modifyAggressiveBonus(attacker, defender, 0);
		int determine = formula.modifyDamage(attacker, defender, 0);
		int fetchroll = formula.modifyDefensive(attacker, defender, 0);
		determine = fetchroll;
		FormulaModifier<Mob> strategy = type.getFormula();
		
		return strategy.modifyDamage(attacker, defender, maxHit(fetchlevel, fetchbonus));
	}
	
	public static int getModifiedMaxHit(Mob attacker, Mob defender, CombatType type) {
		FormulaModifier<Mob> strategy = type.getFormula();
		int fetchlevel = strategy.modifyAggressive(attacker, defender, 0);
		int fetchbonus = strategy.modifyAggressiveBonus(attacker, defender, 0);
		return strategy.modifyDamage(attacker, defender, maxHit(fetchlevel, fetchbonus));
	}
	
	private static int maxHit(int level, int bonus) {
		return (320 + level * (bonus + 64)) / 640;
	}
	
}
