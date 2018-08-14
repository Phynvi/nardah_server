package io.battlerune.content.combat.cannon;

import io.battlerune.game.Animation;
import io.battlerune.game.Projectile;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.actor.npc.Npc;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.region.Region;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.Utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CannonManager {
	
	static Map<String, Cannon> ACTIVE_CANNONS = new HashMap<>();
	
	public enum Setup {
		NO_CANNON, BASE, STAND, BARRELS, FURNACE, COMPLETE_CANNON
	}
	
	public enum Rotation {
		NORTH, NORTH_EAST, EAST, SOUTH_EAST, SOUTH, SOUTH_WEST, WEST, NORTH_WEST
	}
	
	public static void test(Player player) {
		drop(player, new Cannon(player.getName(), player.getPosition()));
	}
	
	public static void drop(Player player, Cannon cannon) {
		if(ACTIVE_CANNONS.containsKey(player.getName())) {
			player.send(new SendMessage("You already have a cannon active!"));
			return;
		}
		
		if(cannon.getStage().ordinal() != 0) {
			player.send(new SendMessage("You have already started setting up a cannon!"));
			return;
		}
		
		for(Cannon other : ACTIVE_CANNONS.values()) {
			if(other.getPosition().isWithinDistance(player.getPosition(), 5)) {
				player.send(new SendMessage("You are trying to build too close to another cannon!"));
				return;
			}
		}
		
		World.schedule(new CannonBuild(player, cannon));
	}
	
	public static void pickup(Player player) {
		Cannon cannon = ACTIVE_CANNONS.get(player.getName());
		
		if(cannon == null) {
			player.send(new SendMessage("This is not your cannon!"));
			return;
		}
		
		if(!cannon.getOwner().equalsIgnoreCase(player.getName())) {
			player.send(new SendMessage("This is not your cannon!"));
			return;
		}
		
		player.animate(new Animation(827));
		cannon.unregister();
		ACTIVE_CANNONS.remove(player.getName());
	}
	
	public static void load(Player player) {
		Cannon cannon = ACTIVE_CANNONS.get(player.getName());
		
		if(cannon == null) {
			player.send(new SendMessage("This is not your cannon!"));
			return;
		}
		
		if(!cannon.getOwner().equalsIgnoreCase(player.getName())) {
			player.send(new SendMessage("This is not your cannon!"));
			return;
		}
		
		if(!player.inventory.contains(2)) {
			player.send(new SendMessage("You do not have any Cannon balls."));
			return;
		}
		
		int needed = 30 - cannon.getAmmunition();
		
		if(needed == 0) {
			player.send(new SendMessage("Your cannon is full."));
			return;
		}
		
		int cannon_balls = player.inventory.computeAmountForId(2);
		
		if(cannon_balls <= needed) {
			player.inventory.remove(2, cannon_balls);
			player.send(new SendMessage("You load the last of your cannon balls"));
			cannon.setAmmunition(cannon.getAmmunition() + cannon_balls);
		} else {
			player.inventory.remove(2, needed);
			player.send(new SendMessage("You load " + needed + " balls into the cannon."));
			cannon.setAmmunition(cannon.getAmmunition() + needed);
		}
		
		if(cannon.isFiring()) {
			player.send(new SendMessage("The cannon is already firing!"));
			return;
		}
		
		cannon.setFiring(true);
		World.schedule(new CannonFireAction(player, cannon));
	}
	
	public static Projectile getCannonFire() {
		Projectile p = new Projectile(53);
		p.setStartHeight(50);
		p.setEndHeight(50);
		return p;
	}
	
	public static Npc[] getNpc(Cannon cannon) {
		ArrayList<Npc> attack = new ArrayList<>();
		
		for(Npc npc : World.getNpcs()) {
			if(npc == null) {
				continue;
			}
			
			if(!Utility.withinDistance(npc, cannon, Region.VIEW_DISTANCE)) {
				continue;
			}
			
			if(!npc.definition.isAttackable()) {
				continue;
			}
			
			attack.add(npc);
		}
		
		Npc[] npc = new Npc[attack.size()];
		
		for(int i = 0; i < npc.length; i++) {
			npc[i] = attack.get(i);
		}
		
		return npc;
	}
	
	public static void rotate(Cannon cannon) {
		switch(cannon.getRotation()) {
			case NORTH:
				World.sendObjectAnimation(516, cannon.getObject());
				break;
			case NORTH_EAST:
				World.sendObjectAnimation(517, cannon.getObject());
				break;
			case EAST:
				World.sendObjectAnimation(518, cannon.getObject());
				break;
			case SOUTH_EAST:
				World.sendObjectAnimation(519, cannon.getObject());
				break;
			case SOUTH:
				World.sendObjectAnimation(520, cannon.getObject());
				break;
			case SOUTH_WEST:
				World.sendObjectAnimation(521, cannon.getObject());
				break;
			case WEST:
				World.sendObjectAnimation(514, cannon.getObject());
				break;
			case NORTH_WEST:
				World.sendObjectAnimation(515, cannon.getObject());
				break;
		}
	}
	
}
