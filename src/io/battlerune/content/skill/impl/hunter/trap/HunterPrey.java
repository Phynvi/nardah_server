package io.battlerune.content.skill.impl.hunter.trap;

import io.battlerune.game.world.entity.actor.Direction;
import io.battlerune.game.world.entity.actor.npc.Npc;
import io.battlerune.game.world.object.GameObject;
import io.battlerune.game.world.position.Position;
import io.battlerune.game.world.region.Region;

/**
 * @author Ethan Kyle Millard <skype:pumpklins>
 * @since Tue, August 07, 2018 @ 11:03 PM
 */
public class HunterPrey extends Npc {
	/**
	 * The process hash.
	 */
	private int hash;

	/**
	 * The hunter definition.
	 */
	private Animals definition;

	/**
	 * Hunter pulse.
	 */
	private int tick;

	/**
	 * The trap definitions.
	 */
	private Traps trapDefinition;

	/**
	 * Constructs a new Hunter prey.
	 * @param id
	 * @param position
	 */
	public HunterPrey(int id, Position position) {
		super(id, position, 5, Direction.getRandomDirection());
	}

	@Override
	public void sequence() {

		for(GameObject object : Region.ACTIVE_OBJECT.values()) {

			if(getRegion().containsObject(0, object)) {

			}

		}

		super.sequence();
	}
}
