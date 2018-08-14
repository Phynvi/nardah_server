package io.battlerune.content.activity.impl.warriorguild;

import io.battlerune.content.activity.ActivityListener;
import io.battlerune.game.world.entity.actor.Actor;

/**
 * Handles the warrior guild activity combat listener.
 * @author Daniel.
 */
public class WarriorGuildActivityListener extends ActivityListener<WarriorGuild> {

	/**
	 * Constructs a new <code>WarriorGuildActivityListener</code>.
	 */
	WarriorGuildActivityListener(WarriorGuild minigame) {
		super(minigame);
	}

	@Override
	public boolean canAttack(Actor attacker, Actor defender) {
		boolean cyclop = false;

		for(int id : WarriorGuildUtility.CYCLOPS) {
			if(id == defender.id) {
				cyclop = true;
				break;
			}
		}

		if(cyclop && activity.state == WarriorGuildState.ANIMATOR) {
			return false;
		}

		return super.canAttack(attacker, defender);
	}
}
