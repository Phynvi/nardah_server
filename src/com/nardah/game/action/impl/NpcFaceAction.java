package com.nardah.game.action.impl;

import com.nardah.content.pet.PetData;
import com.nardah.game.action.policy.WalkablePolicy;
import com.nardah.game.action.Action;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.position.Position;

import java.util.Arrays;

/**
 * An action that faces an mob, but resets their facing to their default facing
 * direction after 15 seconds.
 * @author Daniel
 * @author Michael | Chex
 */
public final class NpcFaceAction extends Action<Mob> {
	/**
	 * The mob identifications to check if face action is allowed.
	 */
	private int[] identification;

	/**
	 * The array list of all the first option mobs that can not face.
	 */
	private final int[] FIRST_OPTION = {};

	/**
	 * The array list of all the second option mobs that can not face.
	 */
	private final int[] SECOND_OPTION = {3080, 3010};

	/**
	 * The array list of all the third option mobs that can not face.
	 */
	private final int[] THIRD_OPTION = {};

	private final int[] FOURTH_OPTION = {};

	/**
	 * Constructs a new <code>NpcFaceAction</code>.
	 * @param mob The mob.
	 * @param face The face position.
	 * @param option The option id.
	 */
	public NpcFaceAction(Mob mob, Position face, int option) {
		super(mob, 25);
		if(option == 0)
			identification = FIRST_OPTION;
		else if(option == 1)
			identification = SECOND_OPTION;
		else if(option == 2)
			identification = THIRD_OPTION;
		else if(option == 3)
			identification = FOURTH_OPTION;

		if(Arrays.stream(PetData.values()).anyMatch(p -> mob.id == p.getNpc())) {
			cancel();
			return;
		}

		if(identification != null && Arrays.stream(identification).anyMatch($it -> mob.id == $it)) {
			cancel();
			return;
		}

		getMob().face(face);
	}

	@Override
	public void execute() {
		Mob mob = getMob().getNpc();

		if(mob.walk) {
			getMob().face(getMob().faceDirection);
		}

		cancel();
	}

	@Override
	public String getName() {
		return "Mob face";
	}

	@Override
	public boolean prioritized() {
		return false;
	}

	@Override
	public WalkablePolicy getWalkablePolicy() {
		return WalkablePolicy.NON_WALKABLE;
	}

}
