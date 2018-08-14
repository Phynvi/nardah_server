package com.nardah.content.skill.impl.thieving;

import com.nardah.Config;
import com.nardah.game.Animation;
import com.nardah.game.action.Action;
import com.nardah.game.action.policy.WalkablePolicy;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.data.LockType;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.game.world.position.Area;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.Utility;

/**
 * Handles the pickpocketing action.
 * @author Daniel
 */
public final class PickpocketAction extends Action<Player> {
	/**
	 * The pickpocket data.
	 */
	private final PickpocketData pickpocket;

	/**
	 * The mob being pickpocketed.
	 */
	private final Mob mob;

	/**
	 * Constructs a new <code>PickpocketData</code>.
	 */
	public PickpocketAction(Player player, Mob mob, PickpocketData pickpocket) {
		super(player, 3);
		this.mob = mob;
		this.pickpocket = pickpocket;
	}

	/**
	 * The failure rate for pickpocketing.
	 */
	private int failureRate(Player player) {
		double f1 = pickpocket.getLevel() / 10;
		double f2 = 100 / ((player.skills.getMaxLevel(Skill.THIEVING) + 1) - pickpocket.getLevel());
		return (int) Math.floor((f2 + f1) / 2);
	}

	@Override
	public void execute() {
		boolean failed = Utility.random(100) < failureRate(getMob());

		if(failed) {
			mob.interact(getMob());
			mob.face(mob.faceDirection);
			mob.animate(new Animation(422));
			mob.speak("What do you think you're doing?");
			getMob().action.clearNonWalkableActions();
			getMob().damage(new Hit(Utility.random(pickpocket.getDamage())));
			getMob().locking.lock(pickpocket.getStun(), LockType.STUN);
			getMob().send(new SendMessage("You failed to pickpocketing the " + mob.getName() + "."));
			cancel();
			return;
		}

		double experience = Area.inDonatorZone(getMob()) ? pickpocket.getExperience() * 2 : pickpocket.getExperience();
		getMob().skills.addExperience(Skill.THIEVING, experience * Config.THIEVING_MODIFICATION);
		getMob().send(new SendMessage("You have successfully pickpocket the " + mob.getName() + "."));
		getMob().inventory.add(Utility.randomElement(pickpocket.getLoot()));
		getMob().locking.unlock();
		cancel();
	}

	@Override
	public String getName() {
		return "Thieving pickpocket";
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