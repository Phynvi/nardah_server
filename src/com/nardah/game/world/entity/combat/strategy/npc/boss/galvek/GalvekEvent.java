package com.nardah.game.world.entity.combat.strategy.npc.boss.galvek;

import com.nardah.util.Stopwatch;
import com.nardah.game.task.Task;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.mob.Mob;

import java.util.concurrent.TimeUnit;

public class GalvekEvent extends Task {
	private Mob galvek;
	private boolean initial;
	private final Stopwatch stopwatch = Stopwatch.start();
	
	public GalvekEvent() {
		super(false, 100);
		this.galvek = null;
		this.initial = true;
	}
	
	@Override
	public void execute() {
		if((galvek == null || !galvek.isRegistered()) && !initial) {
			initial = true;
			stopwatch.reset();
		}
		
		if(initial) {
			if(stopwatch.elapsedTime(TimeUnit.MINUTES) == 60) {
				galvek = GalvekUtility.generateSpawn();
				initial = false;
				stopwatch.reset();
			}
			return;
		}
		
		if(stopwatch.elapsedTime(TimeUnit.MINUTES) == 15) {// 15 minutes
			initial = true;
			stopwatch.reset();
			if(galvek != null) {
				galvek.speak("Pathetic humans could not kill me! Muhahaha");
				World.schedule(2, () -> galvek.unregister());
				World.sendMessage("[GALVEK] You have failed to kill me! Muhahaha");
			}
			World.sendMessage("<col=ff0000>Galvek has disappeared! He will return in 30 minutes.");
		} else if(stopwatch.elapsedTime(TimeUnit.MINUTES) == 10) {// 10 minutes
			World.sendMessage("<col=ff0000>Galvek will disappear in 5 minutes!");
		} else if(stopwatch.elapsedTime(TimeUnit.MINUTES) == 5) {// 5 minutes
			World.sendMessage("<col=ff0000>Galvek will disappear in 10 minutes!");
		}
	}
}
