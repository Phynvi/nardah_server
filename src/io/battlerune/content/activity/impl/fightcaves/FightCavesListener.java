package io.battlerune.content.activity.impl.fightcaves;

import io.battlerune.content.activity.ActivityListener;
import io.battlerune.game.world.entity.combat.CombatType;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.mob.Mob;
import io.battlerune.game.world.entity.mob.npc.Npc;
import io.battlerune.util.Utility;

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
	public void block(Mob attacker, Mob defender, Hit hit, CombatType combatType) {
		if(!defender.isNpc())
			return;
		if(defender.id != 3127)
			return;
		if(Utility.getPercentageAmount(defender.getCurrentHealth(), defender.getMaximumHealth()) > 49)
			return;
		for(Npc npc : activity.npcs) {
			if(npc.id == 3128 && (npc.getCombat().inCombatWith(attacker) || Utility.withinDistance(defender, npc, 5))) {
				defender.heal(1);
			}
		}
	}

	@Override
	public void onDeath(Mob attacker, Mob defender, Hit hit) {
		activity.handleDeath(defender);
	}
}
