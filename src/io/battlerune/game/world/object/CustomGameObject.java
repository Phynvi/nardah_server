package io.battlerune.game.world.object;

import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.Entity;
import io.battlerune.game.world.entity.EntityType;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.pathfinding.TraversalMap;
import io.battlerune.game.world.position.Position;
import io.battlerune.game.world.region.Region;
import io.battlerune.net.packet.out.SendAddObject;
import io.battlerune.net.packet.out.SendRemoveObject;
import io.battlerune.util.generic.GenericAttributes;

import java.util.Objects;

import static io.battlerune.game.world.object.ObjectDirection.NORTH;
import static io.battlerune.game.world.object.ObjectDirection.SOUTH;

/**
 * Represents a static game object loaded from the map fs.
 * @author Michael | Chex
 */
public class CustomGameObject extends Entity implements GameObject {
	
	/**
	 * The object definition.
	 */
	private ObjectDefinition definition;
	
	/**
	 * The object direction.
	 */
	private ObjectDirection direction;
	
	/**
	 * The object type.
	 */
	private ObjectType type;
	
	/**
	 * The generic attributes.
	 */
	private GenericAttributes genericAttributes;
	
	public CustomGameObject(int id, int instance, Position position, ObjectDirection direction, ObjectType type) {
		super(position);
		this.definition = ObjectDefinition.lookup(id);
		this.instance = instance;
		this.direction = direction;
		this.type = type;
	}
	
	public CustomGameObject(int id, Position position, ObjectDirection direction, ObjectType type) {
		this(id, Actor.DEFAULT_INSTANCE_HEIGHT, position, direction, type);
	}
	
	public CustomGameObject(int id, int instance, Position position) {
		this(id, instance, position, NORTH, ObjectType.INTERACTABLE);
	}
	
	public CustomGameObject(int id, Position position) {
		this(id, Actor.DEFAULT_INSTANCE_HEIGHT, position, NORTH, ObjectType.INTERACTABLE);
	}
	
	@Override
	public int getInstancedHeight() {
		return instance;
	}
	
	@Override
	public GenericAttributes getGenericAttributes() {
		if(genericAttributes == null)
			genericAttributes = new GenericAttributes();
		return genericAttributes;
	}
	
	@Override
	public ObjectDefinition getDefinition() {
		return definition;
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
		if(!isRegistered()) {
			Region region = getRegion();
			setRegistered(true);
			
			if(region == null) {
				setPosition(getPosition());
			} else if(!region.containsObject(getHeight(), this)) {
				addToRegion(region);
			}
			
			Region.ACTIVE_OBJECT.put(getPosition(), this);
		}
	}
	
	@Override
	public void unregister() {
		if(isRegistered()) {
			Region.ACTIVE_OBJECT.remove(getPosition(), this);
			removeFromRegion(getRegion());
			destroy();
		}
	}
	
	@Override
	public boolean active() {
		return isRegistered();
	}
	
	@Override
	public void addToRegion(Region objectRegion) {
		if(!objectRegion.containsObject(getHeight(), this)) {
			TraversalMap.markObject(objectRegion, this, true, true);
			for(Region region : World.getRegions().getSurroundingRegions(getPosition())) {
				for(Player other : region.getPlayers(getHeight())) {
					if(other.instance != getInstancedHeight())
						continue;
					other.send(new SendAddObject(this));
				}
			}
		}
	}
	
	@Override
	public void removeFromRegion(Region objectRegion) {
		if(objectRegion.containsObject(getHeight(), this)) {
			TraversalMap.markObject(objectRegion, this, false, true);
			for(Region region : World.getRegions().getSurroundingRegions(getPosition())) {
				for(Player other : region.getPlayers(getHeight())) {
					if(other.instance != getInstancedHeight()) {
						continue;
					}
					other.send(new SendRemoveObject(this));
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
	public String getName() {
		return definition.name;
	}
	
	@Override
	public EntityType getType() {
		return EntityType.CUSTOM_OBJECT;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(definition.getId(), getPosition());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof CustomGameObject) {
			CustomGameObject other = (CustomGameObject) obj;
			return definition == other.definition && getPosition().equals(other.getPosition()) && getInstancedHeight() == other.getInstancedHeight();
		}
		return obj == this;
	}
	
	@Override
	public String toString() {
		return String.format("CustomGameObject[id=%s, loc=%s, width=%s, len=%s, rot=%s, type=%s]", getId(), getPosition(), width(), length(), getDirection(), getObjectType());
	}
}
