package io.battlerune.game.world.entity.actor.movement.waypoint;

import io.battlerune.game.task.Task;
import io.battlerune.game.world.Interactable;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.data.PacketType;
import io.battlerune.game.world.entity.actor.npc.Npc;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.position.Position;
import io.battlerune.game.world.region.Region;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.Utility;

import java.util.Objects;

public abstract class Waypoint extends Task {
	protected final Actor actor;
	protected Interactable target;
	private Position lastPosition;
	
	protected Waypoint(Actor actor, Interactable target) {
		super(true, 0);
		this.actor = actor;
		this.target = target;
	}
	
	protected abstract void onDestination();
	
	protected int getRadius() {
		return 1;
	}
	
	protected boolean withinDistance() {
		return Utility.getDistance(actor, target) <= getRadius() && !actor.movement.needsPlacement();
	}
	
	@Override
	protected void onSchedule() {
		if(target instanceof Actor) {
			Actor other = (Actor) target;
			other.attributes.set("actor-following", this);
			actor.interact(other);
		}
		
		if(actor.locking.locked(PacketType.MOVEMENT)) {
			return;
		}
		
		if(!withinDistance()) {
			findRoute();
		}
	}
	
	@Override
	public void execute() {
		if(Utility.inside(actor, target) && target instanceof Actor) {
			if(!actor.locking.locked(PacketType.MOVEMENT) && !actor.movement.needsPlacement())
				Utility.fixInsidePosition(actor, target);
			return;
		}
		
		if(withinDistance()) {
			onDestination();
			return;
		}
		
		if(target.getPosition().equals(lastPosition)) {
			return;
		}
		
		if(actor.locking.locked(PacketType.MOVEMENT)) {
			return;
		}
		
		lastPosition = target.getPosition();
		findRoute();
	}
	
	private void findRoute() {
		if(target instanceof Player && actor.equals(((Player) target).pet)) {
			int distance = Utility.getDistance(actor, target);
			if(distance > Region.VIEW_DISTANCE) {
				Npc pet = actor.getNpc();
				pet.move(target.getPosition());
				World.schedule(1, () -> pet.interact((Player) target));
			}
		}
		
		//        if (this instanceof CombatWaypoint) {
		//            System.out.println(actor.getPosition());
		//            System.out.println(target.getPosition());
		//            System.out.println(Utility.getDelta(actor, target));
		//            System.out.println();
		//        }
		
		boolean smart = actor.isPlayer() || (actor.isNpc() && !(this instanceof CombatWaypoint));
		
		if(smart && actor.movement.dijkstraPath(target)) {
			return;
		}
		
		if(actor.movement.simplePath(target)) {
			return;
		}
		
		if(actor.isPlayer())
			actor.getPlayer().send(new SendMessage("I can't reach that!"));
		
		/* No path can be found, lets get out of here!!!! */
		cancel();
	}
	
	@Override
	protected void onCancel(boolean logout) {
		actor.resetFace();
		
		if(target instanceof Actor) {
			Actor other = (Actor) target;
			other.attributes.remove("actor-following");
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		if(obj instanceof Waypoint) {
			Waypoint other = (Waypoint) obj;
			return Objects.equals(actor, other.actor) && Objects.equals(target, other.target);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "{" + "target=" + target + '}';
	}
	
	public Interactable getTarget() {
		return target;
	}
	
	public void onChange() {
		execute();
		//        actor.movement.processNextMovement();
	}
}
