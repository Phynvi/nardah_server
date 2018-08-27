package com.nardah.game.world.items.ground;

import com.nardah.game.task.Task;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.region.Region;

/**
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 * @since 27-12-2016.
 */
public final class GroundItemEvent extends Task {

	/**
	 * A variable which indicates how many ticks a minute is.
	 */
	private static final int MINUTE = 100;

	/**
	 * The ground item this random event is running for.
	 */
	private final GroundItem groundItem;

	/**
	 * The counter of this task.
	 */
	private int minutes;

	/**
	 * Creates a new {@link Task} that doesn't execute instantly.
	 */
	GroundItemEvent(GroundItem groundItem) {
		super(MINUTE);
		this.groundItem = groundItem;
	}

	@Override
	public void execute() {
		switch(groundItem.policy) {
			case GLOBAL:
				if(++minutes < 2) {
					return;
				}

				if(groundItem.item.getId() == 12791) {
					groundItem.player.runePouch.runes.clear();
				}

				cancel();
				break;
			case ONLY_OWNER:
				if(++minutes < 1) {
					return;
				}

				groundItem.policy = GroundItemPolicy.GLOBAL;
				if(!groundItem.item.isTradeable()) {
					return;
				}

				Region[] regions = World.getRegions().getSurroundingRegions(groundItem.getPosition());
				for(Region region : regions) {
					for(Player player : region.getPlayers(groundItem.getHeight())) {
						if(!groundItem.isRegistered())
							continue;

						if(!groundItem.canSee(player))
							continue;

						if(!groundItem.player.getName().equals(player.getName())) {
//							System.out.println(groundItem.player.getName() + "'s ground item should now be seen by " + player.getName());
							groundItem.unregister();
							GroundItem.createGlobal(groundItem.player, groundItem.item, groundItem.getPosition());
						}
					}
				}
				cancel();
				break;
		}
	}

	@Override
	protected void onCancel(boolean logout) {
		groundItem.unregister();
	}

}
