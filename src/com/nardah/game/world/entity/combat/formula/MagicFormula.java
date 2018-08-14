package com.nardah.game.world.entity.combat.formula;

import com.nardah.game.world.entity.combat.FormulaModifier;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.game.world.items.containers.equipment.Equipment;

public final class MagicFormula implements FormulaModifier<Actor> {
	
	@Override
	public int modifyAccuracy(Actor attacker, Actor defender, int roll) {
		int level = attacker.skills.getLevel(Skill.MAGIC);
		return 12 + attacker.getCombat().modifyMagicLevel(defender, level);
	}
	
	@Override
	public int modifyAggressive(Actor attacker, Actor defender, int roll) {
		int level = attacker.skills.getLevel(Skill.MAGIC);
		return 12 + attacker.getCombat().modifyMagicLevel(defender, level);
	}
	
	@Override
	public int modifyDefensive(Actor attacker, Actor defender, int roll) {
		FightType fightType = defender.getCombat().getFightType();
		
		int magic = defender.skills.getLevel(Skill.MAGIC);
		magic = defender.getCombat().modifyMagicLevel(attacker, magic);
		
		int defence = defender.skills.getLevel(Skill.DEFENCE);
		defence = defender.getCombat().modifyDefenceLevel(attacker, defence);
		
		int effectiveLevel = 11 + fightType.getStyle().getDefensiveIncrease();
		effectiveLevel += 0.50 * magic + 0.20 * defence;
		
		return effectiveLevel;
	}
	
	@Override
	public int modifyDamage(Actor attacker, Actor defender, int damage) {
		int bonus = attacker.getBonus(Equipment.MAGIC_STRENGTH);
		return damage + damage * bonus / 100;
	}
	
	@Override
	public int modifyOffensiveBonus(Actor attacker, Actor defender, int bonus) {
		bonus = attacker.getBonus(Equipment.MAGIC_OFFENSE);
		return attacker.getCombat().modifyOffensiveBonus(defender, bonus);
	}
	
	@Override
	public int modifyAggressiveBonus(Actor attacker, Actor defender, int bonus) {
		return attacker.getCombat().modifyAggresiveBonus(defender, bonus);
	}
	
	@Override
	public int modifyDefensiveBonus(Actor attacker, Actor defender, int bonus) {
		bonus = attacker.getBonus(Equipment.MAGIC_DEFENSE);
		return attacker.getCombat().modifyDefensiveBonus(defender, bonus);
	}
	
}
