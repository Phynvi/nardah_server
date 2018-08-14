package com.nardah.game.world.entity.combat.formula;

import com.nardah.game.world.entity.combat.FormulaModifier;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.game.world.items.containers.equipment.Equipment;

public final class RangedFormula implements FormulaModifier<Actor> {
	
	@Override
	public int modifyAccuracy(Actor attacker, Actor defender, int roll) {
		FightType fightType = attacker.getCombat().getFightType();
		int level = attacker.skills.getLevel(Skill.RANGED);
		int effectiveAccuracy = attacker.getCombat().modifyRangedLevel(defender, level);
		return 11 + effectiveAccuracy + fightType.getStyle().getAccuracyIncrease();
	}
	
	@Override
	public int modifyAggressive(Actor attacker, Actor defender, int roll) {
		int level = attacker.skills.getLevel(Skill.RANGED);
		return 13 + attacker.getCombat().modifyRangedLevel(defender, level);
	}
	
	@Override
	public int modifyDefensive(Actor attacker, Actor defender, int roll) {
		FightType fightType = defender.getCombat().getFightType();
		int level = defender.skills.getLevel(Skill.DEFENCE);
		int effectiveDefence = defender.getCombat().modifyDefenceLevel(attacker, level);
		return 13 + effectiveDefence + fightType.getStyle().getDefensiveIncrease();
	}
	
	@Override
	public int modifyOffensiveBonus(Actor attacker, Actor defender, int bonus) {
		bonus = attacker.getBonus(Equipment.RANGED_OFFENSE);
		return attacker.getCombat().modifyOffensiveBonus(defender, bonus);
	}
	
	@Override
	public int modifyAggressiveBonus(Actor attacker, Actor defender, int bonus) {
		bonus = attacker.getBonus(Equipment.RANGED_STRENGTH);
		return attacker.getCombat().modifyAggresiveBonus(defender, bonus);
	}
	
	@Override
	public int modifyDefensiveBonus(Actor attacker, Actor defender, int bonus) {
		bonus = defender.getBonus(Equipment.RANGED_DEFENSE);
		return defender.getCombat().modifyDefensiveBonus(attacker, bonus);
	}
	
}
