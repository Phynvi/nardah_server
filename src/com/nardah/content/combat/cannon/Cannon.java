package com.nardah.content.combat.cannon;

import com.nardah.game.world.entity.Entity;
import com.nardah.game.world.entity.EntityType;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.object.CustomGameObject;
import com.nardah.game.world.position.Position;
import com.nardah.game.world.region.Region;
import com.nardah.net.packet.out.SendAddObject;
import com.nardah.net.packet.out.SendRemoveObject;

/**
 * Handles the dwarf cannon.
 * @author Daniel
 */
public class Cannon extends Entity {
	
	private final String owner;
	
	private final Position position;
	
	private CustomGameObject object;
	
	private int ammunition;
	
	private boolean firing;
	
	private CannonManager.Setup stage;
	
	private CannonManager.Rotation rotation;
	
	Cannon(String owner, Position position) {
		super(position);
		this.owner = owner;
		this.position = position;
		this.ammunition = 0;
		this.firing = false;
		this.stage = CannonManager.Setup.NO_CANNON;
		this.rotation = CannonManager.Rotation.NORTH;
		this.object = new CustomGameObject(8, position);
	}
	
	public String getOwner() {
		return owner;
	}
	
	public Position getPosition() {
		return position;
	}
	
	public int getAmmunition() {
		return ammunition;
	}
	
	public void setAmmunition(int ammunition) {
		this.ammunition = ammunition;
	}
	
	public boolean isFiring() {
		return firing;
	}
	
	public void setFiring(boolean firing) {
		this.firing = firing;
	}
	
	public CannonManager.Setup getStage() {
		return stage;
	}
	
	public void setStage(CannonManager.Setup stage) {
		this.stage = stage;
	}
	
	public CustomGameObject getObject() {
		return object;
	}
	
	public void setObject(CustomGameObject object) {
		this.object = object;
	}
	
	public CannonManager.Rotation getRotation() {
		return rotation;
	}
	
	public void setRotation(CannonManager.Rotation rotation) {
		this.rotation = rotation;
	}
	
	@Override
	public void register() {
		if(!isRegistered()) {
			Region region = getRegion();
			setRegistered(true);
			
			if(region == null) {
				setPosition(getPosition());
			} else if(!region.containsObject(getHeight(), object)) {
				addToRegion(region);
			}
		}
	}
	
	@Override
	public void unregister() {
		if(isRegistered()) {
			destroy();
		}
	}
	
	@Override
	public void addToRegion(Region region) {
		region.getPlayers(getHeight()).stream().filter(Player::isValid).forEach(player -> player.send(new SendAddObject(object)));
	}
	
	@Override
	public void removeFromRegion(Region region) {
		region.getPlayers(getHeight()).stream().filter(Player::isValid).forEach(player -> player.send(new SendRemoveObject(object)));
	}
	
	@Override
	public String getName() {
		return "Dwarf cannon";
	}
	
	@Override
	public EntityType getType() {
		return EntityType.DWARF_CANNON;
	}
	
	@Override
	public boolean equals(Object obj) {
		return false;
	}
	
	@Override
	public int hashCode() {
		return 0;
	}
}
