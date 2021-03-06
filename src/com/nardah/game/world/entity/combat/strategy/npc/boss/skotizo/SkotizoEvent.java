package com.nardah.game.world.entity.combat.strategy.npc.boss.skotizo;

import com.nardah.util.Stopwatch;
import com.nardah.game.task.Task;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.mob.Mob;

import java.util.concurrent.TimeUnit;

public class SkotizoEvent extends Task {
	private Mob skotizo;
	private boolean initial;
	private final Stopwatch stopwatch = Stopwatch.start();
	
	public SkotizoEvent() {
		super(false, 100);
		this.skotizo = null;
		this.initial = true;
	}
	
	@Override
	public void execute() {
		if((skotizo == null || !skotizo.isRegistered()) && !initial) {
			initial = true;
			stopwatch.reset();
		}
		
		if(initial) {
			if(stopwatch.elapsedTime(TimeUnit.MINUTES) == 30) {
				skotizo = SkotizoUtility.generateSpawn();
				initial = false;
				stopwatch.reset();
			}
			return;
		}
		
		if(stopwatch.elapsedTime(TimeUnit.MINUTES) == 15) {// 15 minutes
			initial = true;
			stopwatch.reset();
			if(skotizo != null) {
				skotizo.speak("Pathetic humans could not kill me! Muhahaha");
				World.schedule(2, () -> skotizo.unregister());
			}
			World.sendMessage("<col=8714E6>Skotizo has disappeared! He will return in 30 minutes.");
		} else if(stopwatch.elapsedTime(TimeUnit.MINUTES) == 10) {// 10 minutes
			World.sendMessage("<col=8714E6>Skotizo will disappear in 5 minutes!");
		} else if(stopwatch.elapsedTime(TimeUnit.MINUTES) == 5) {// 5 minutes
			World.sendMessage("<col=8714E6>Skotizo will disappear in 10 minutes!");
		}
	}
}
