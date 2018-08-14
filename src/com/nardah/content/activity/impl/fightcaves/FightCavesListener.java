package com.nardah.content.activity.impl.fightcaves;

import com.nardah.content.activity.ActivityListener;
import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.util.Utility;

/**
 * The {@link FightCaves} combat listener for all mobs in the activity.
 * @author Michael | Chex
 */
public class FightCavesListener extends ActivityListener<FightCaves> {

	/**
	 * Constructs a new {@code FightCavesListener} object for a specific
	 * {@link FightCaves} activity.
	 */
	public FightCavesListener(FightCaves minigame) {
		super(minigame);
	}

	@Override
	public void block(Actor attacker, Actor defender, Hit hit, CombatType combatType) {
		if(!defender.isNpc())
			return;
		if(defender.id != 3127)
			return;
		if(Utility.getPercentageAmount(defender.getCurrentHealth(), defender.getMaximumHealth()) > 49)
			return;
		for(Mob mob : activity.mobs) {
			if(mob.id == 3128 && (mob.getCombat().inCombatWith(attacker) || Utility.withinDistance(defender, mob, 5))) {
				defender.heal(1);
			}
		}
	}

	@Override
	public void onDeath(Actor attacker, Actor defender, Hit hit) {
		activity.handleDeath(defender);
	}
}
