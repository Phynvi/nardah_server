package com.nardah.game.world.entity.combat.strategy.npc.boss.gano;

import com.nardah.util.Stopwatch;
import com.nardah.game.task.Task;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.mob.Mob;

import java.util.concurrent.TimeUnit;

public class GanoEvent extends Task {
	private Mob gano;
	private boolean initial;
	private final Stopwatch stopwatch = Stopwatch.start();
	
	public GanoEvent() {
		super(false, 100);
		this.gano = null;
		this.initial = true;
	}
	
	@Override
	public void execute() {
		if((gano == null || !gano.isRegistered()) && !initial) {
			initial = true;
			stopwatch.reset();
		}
		
		if(initial) {
			if(stopwatch.elapsedTime(TimeUnit.MINUTES) == 120) {
				gano = GanoUtility.generateSpawn();
				initial = false;
				stopwatch.reset();
			}
			return;
		}
		
		if(stopwatch.elapsedTime(TimeUnit.MINUTES) == 15) {// 15 minutes
			initial = true;
			stopwatch.reset();
			if(gano != null) {
				gano.speak("Pathetic humans could not kill me! Muhahaha");
				World.schedule(2, () -> gano.unregister());
			}
			World.sendMessage("<col=ff0000>Gano has disappeared! He will return in 30 minutes.");
		} else if(stopwatch.elapsedTime(TimeUnit.MINUTES) == 10) {// 10 minutes
			World.sendMessage("<col=ff0000>Gano will disappear in 5 minutes!");
		} else if(stopwatch.elapsedTime(TimeUnit.MINUTES) == 5) {// 5 minutes
			World.sendMessage("<col=ff0000>Gano will disappear in 10 minutes!");
		}
	}
}
