package io.battlerune.game.world.entity.combat;

import io.battlerune.game.world.entity.combat.formula.MagicFormula;
import io.battlerune.game.world.entity.combat.formula.MeleeFormula;
import io.battlerune.game.world.entity.combat.formula.RangedFormula;
import io.battlerune.game.world.entity.actor.Actor;

public enum CombatType {
	MELEE(new MeleeFormula()), RANGED(new RangedFormula()), MAGIC(new MagicFormula());
	
	final FormulaModifier<Actor> formula;
	
	CombatType(FormulaModifier<Actor> formula) {
		this.formula = formula;
	}
	
	public FormulaModifier<Actor> getFormula() {
		return formula;
	}
}
