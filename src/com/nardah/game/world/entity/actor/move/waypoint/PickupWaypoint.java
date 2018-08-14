package com.nardah.game.world.entity.actor.move.waypoint;

import com.nardah.game.world.position.Position;
import com.nardah.game.world.Interactable;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.items.ground.GroundItem;

public class PickupWaypoint extends Waypoint {
	
	private final Player player;
	private final Item item;
	private final Position position;
	
	public PickupWaypoint(Player player, Item item, Position position) {
		super(player, Interactable.create(position, 0, 0));
		this.player = player;
		this.item = item;
		this.position = position;
	}
	
	@Override
	public void onDestination() {
		actor.movement.reset();
		GroundItem.pickup(player, item, position);
		cancel();
	}
	
	@Override
	protected int getRadius() {
		return 0;
	}
	
}
