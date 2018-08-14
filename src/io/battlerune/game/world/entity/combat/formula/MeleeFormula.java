package io.battlerune.game.world.entity.combat.formula;

import io.battlerune.game.world.entity.combat.FormulaModifier;
import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.skill.Skill;
import io.battlerune.game.world.items.containers.equipment.Equipment;

/**
 * @edited Adam_#6723
 */
public final class MeleeFormula implements FormulaModifier<Actor> {
	
	@Override
	public int modifyAccuracy(Actor attacker, Actor defender, int roll) {
		FightType fightType = attacker.getCombat().getFightType();
		int level = attacker.skills.getLevel(Skill.ATTACK);
		int effectiveAccuracy = attacker.getCombat().modifyAttackLevel(defender, level);
		return 9 + effectiveAccuracy + fightType.getStyle().getAccuracyIncrease();
	}
	
	@Override
	public int modifyAggressive(Actor attacker, Actor defender, int roll) {
		FightType fightType = attacker.getCombat().getFightType();
		int level = attacker.skills.getLevel(Skill.STRENGTH);
		int effectiveStrength = attacker.getCombat().modifyStrengthLevel(defender, level);
		return 9 + effectiveStrength + fightType.getStyle().getStrengthIncrease();
	}
	
	@Override
	public int modifyDefensive(Actor attacker, Actor defender, int roll) {
		FightType fightType = defender.getCombat().getFightType();
		int level = defender.skills.getLevel(Skill.DEFENCE);
		int effectiveDefence = defender.getCombat().modifyDefenceLevel(attacker, level);
		return 9 + effectiveDefence + fightType.getStyle().getDefensiveIncrease();
	}
	
	@Override
	public int modifyOffensiveBonus(Actor attacker, Actor defender, int bonus) {
		FightType fightType = attacker.getCombat().getFightType();
		bonus = attacker.getBonus(fightType.getBonus());
		return attacker.getCombat().modifyOffensiveBonus(defender, bonus);
	}
	
	@Override
	public int modifyAggressiveBonus(Actor attacker, Actor defender, int bonus) {
		bonus = attacker.getBonus(Equipment.STRENGTH_BONUS);
		return attacker.getCombat().modifyAggresiveBonus(defender, bonus);
	}
	
	@Override
	public int modifyDefensiveBonus(Actor attacker, Actor defender, int bonus) {
		FightType fightType = attacker.getCombat().getFightType();
		bonus = defender.getBonus(fightType.getCorrespondingBonus());
		return defender.getCombat().modifyDefensiveBonus(attacker, bonus);
	}
	
}
