package com.nardah.game.world.entity.actor;

import com.nardah.game.world.entity.actor.data.LockType;
import com.nardah.game.world.entity.actor.data.PacketType;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Locking {
	private final Actor actor;
	private LockType lock = null;
	private long lockTime = -1;
	
	Locking(Actor actor) {
		this.actor = actor;
	}
	
	/**
	 * Locks the actor indefinitely.
	 */
	public void lock() {
		lock(Integer.MAX_VALUE, LockType.MASTER);
	}
	
	public void lock(int time) {
		lock(time, LockType.MASTER);
	}
	
	public void lock(LockType type) {
		lock(Integer.MAX_VALUE, type);
	}
	
	/**
	 * Locks the actor for a certain amount of time.
	 */
	public void lock(int time, LockType type) {
		lock(time, TimeUnit.SECONDS, type);
	}
	
	/**
	 * Locks the actor for a certain amount of time.
	 */
	public void lock(int time, TimeUnit gUnit, LockType type) {
		long start = System.currentTimeMillis();
		long timer = TimeUnit.MILLISECONDS.convert(time, gUnit);
		if(Long.MAX_VALUE - start <= timer)
			timer = (Long.MAX_VALUE - start);
		type.execute(actor, time);
		actor.movement.reset();
		lock = type;
		lockTime = start + timer;
	}
	
	/**
	 * Checks if the actor is locked.
	 */
	public boolean locked() {
		if(actor.isDead())
			return true;
		boolean state = lock != null && lockTime - System.currentTimeMillis() >= 0;
		if(!state)
			unlock();
		return state;
	}
	
	public void status() {
		System.out.println();
		System.out.println("Lock Status");
		System.out.println("Locked: " + locked(LockType.WALKING));
		System.out.println("Lock Time: " + lockTime);
		System.out.println("Lock: " + lock);
		if(lock == null) {
			System.out.println("Lock Packet: null");
		} else {
			System.out.println("Lock Packet: ");
			Arrays.stream(lock.packets).forEach(System.out::println);
		}
		
		System.out.println();
		
	}
	
	public void addToTime(int timeInMs) {
		System.out.println("Locktime was: " + lockTime);
		lockTime += timeInMs;
		System.out.println("Locktime is: " + lockTime);
	}
	
	/**
	 * Checks if the actor is locked by a certain type.
	 */
	public boolean locked(LockType type) {
		return locked() && lock == type;
	}
	
	public boolean locked(PacketType packet) {
		return locked() && lock.isLocked(packet);
	}
	
	public boolean locked(PacketType packet, Object object) {
		return locked() && lock.isLocked(packet, actor, object);
	}
	
	/**
	 * Unlocks the actor.
	 */
	public void unlock() {
		lock = null;
		lockTime = 0;
	}
	
	public LockType getLock() {
		return lock;
	}
}
