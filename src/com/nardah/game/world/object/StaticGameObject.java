package com.nardah.game.world.object;

import com.nardah.game.world.pathfinding.TraversalMap;
import com.nardah.game.world.position.Position;
import com.nardah.util.Utility;
import com.nardah.util.generic.GenericAttributes;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.region.Region;
import com.nardah.net.packet.out.SendAddObject;
import com.nardah.net.packet.out.SendRemoveObject;

import java.util.Objects;

import static com.nardah.game.world.object.ObjectDirection.NORTH;
import static com.nardah.game.world.object.ObjectDirection.SOUTH;

/**
 * Represents a static game object loaded from the map fs.
 * @author Michael | Chex
 */
public class StaticGameObject implements GameObject {
	
	/**
	 * The object definition.
	 */
	public ObjectDefinition definition;
	
	/**
	 * The generic attributes.
	 */
	public GenericAttributes genericAttributes;
	
	/**
	 * The object position coordinates.
	 */
	public final Position position;
	
	/**
	 * The object type.
	 */
	public ObjectType type;
	
	/**
	 * A byte holding the object rotation.
	 */
	public ObjectDirection direction;
	
	/**
	 * Creates the game object.
	 */
	public StaticGameObject(ObjectDefinition definition, Position position, ObjectType type, ObjectDirection direction) {
		this.definition = definition;
		this.position = position;
		this.type = type;
		this.direction = direction;
	}
	
	@Override
	public GenericAttributes getGenericAttributes() {
		if(genericAttributes == null) {
			genericAttributes = new GenericAttributes();
		}
		return genericAttributes;
	}
	
	@Override
	public ObjectDefinition getDefinition() {
		return definition;
	}
	
	@Override
	public Position getPosition() {
		return position;
	}
	
	@Override
	public int width() {
		if(direction == NORTH || direction == SOUTH) {
			return definition.length;
		}
		return definition.width;
	}
	
	@Override
	public int length() {
		if(direction == NORTH || direction == SOUTH) {
			return definition.width;
		}
		return definition.length;
	}
	
	@Override
	public ObjectType getObjectType() {
		return type;
	}
	
	@Override
	public ObjectDirection getDirection() {
		return direction;
	}
	
	@Override
	public void register() {
		Region objectRegion = getPosition().getRegion();
		if(!objectRegion.containsObject(getHeight(), this)) {
			TraversalMap.markObject(objectRegion, this, true, true);
			for(Region region : World.getRegions().getSurroundingRegions(getPosition())) {
				for(Player other : region.getPlayers(getHeight())) {
					if(other.instance != getInstancedHeight())
						continue;
					if(Utility.withinViewingDistance(this, other, Region.VIEW_DISTANCE)) {
						other.send(new SendAddObject(this));
					}
				}
			}
		}
	}
	
	@Override
	public void unregister() {
		Region objectRegion = getPosition().getRegion();
		if(objectRegion.containsObject(getHeight(), this)) {
			TraversalMap.markObject(objectRegion, this, false, true);
			for(Region region : World.getRegions().getSurroundingRegions(getPosition())) {
				for(Player other : region.getPlayers(getHeight())) {
					if(other.instance != getInstancedHeight())
						continue;
					if(Utility.withinViewingDistance(this, other, Region.VIEW_DISTANCE)) {
						other.send(new SendRemoveObject(this));
					}
				}
			}
		}
	}
	
	@Override
	public void transform(int id) {
		unregister();
		definition = ObjectDefinition.lookup(id);
		register();
	}
	
	@Override
	public void rotate(ObjectDirection direction) {
		unregister();
		this.direction = direction;
		register();
	}
	
	@Override
	public boolean active() {
		return getPosition().getRegion().containsObject(getHeight(), this);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(definition.getId(), position);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == this)
			return true;
		if(obj instanceof StaticGameObject) {
			StaticGameObject other = (StaticGameObject) obj;
			return definition == other.definition && position.equals(other.position);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return String.format("StaticGameObject[id=%s, loc=%s, width=%s, len=%s, rot=%s, type=%s]", getId(), getPosition(), width(), length(), getDirection(), getObjectType());
	}
}
