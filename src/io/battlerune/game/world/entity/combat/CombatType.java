package io.battlerune.game.world.entity.combat;

import io.battlerune.game.world.entity.combat.formula.MagicFormula;
import io.battlerune.game.world.entity.combat.formula.MeleeFormula;
import io.battlerune.game.world.entity.combat.formula.RangedFormula;
import io.battlerune.game.world.entity.mob.Mob;

public enum CombatType {
	MELEE(new MeleeFormula()), RANGED(new RangedFormula()), MAGIC(new MagicFormula());
	
	final FormulaModifier<Mob> formula;
	
	CombatType(FormulaModifier<Mob> formula) {
		this.formula = formula;
	}
	
	public FormulaModifier<Mob> getFormula() {
		return formula;
	}
}
