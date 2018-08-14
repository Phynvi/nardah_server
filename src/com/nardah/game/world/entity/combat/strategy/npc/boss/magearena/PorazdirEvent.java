package com.nardah.game.world.entity.combat.strategy.npc.boss.magearena;

import com.nardah.util.Stopwatch;
import com.nardah.game.task.Task;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.mob.Mob;

import java.util.concurrent.TimeUnit;

/**
 * Created by Adam_#6732
 */

public class PorazdirEvent extends Task {
	private Mob Porazdir;
	private boolean initial;
	private final Stopwatch stopwatch = Stopwatch.start();
	
	public PorazdirEvent() {
		super(false, 100);
		this.Porazdir = null;
		this.initial = true;
	}
	
	@Override
	public void execute() {
		if((Porazdir == null || !Porazdir.isRegistered()) && !initial) {
			initial = true;
			stopwatch.reset();
		}
		
		if(initial) {
			if(stopwatch.elapsedTime(TimeUnit.MINUTES) == 75) {
				Porazdir = PorazdirUtility.generatePorazdirSpawn();
				initial = false;
				stopwatch.reset();
			}
			return;
		}
		
		if(stopwatch.elapsedTime(TimeUnit.MINUTES) == 35) {// 15 minutes
			initial = true;
			stopwatch.reset();
			if(Porazdir != null) {
				Porazdir.speak("Pathetic humans could not kill me! Muhahaha");
				World.schedule(2, () -> Porazdir.unregister());
			}
			World.sendMessage("<col=ff0000>Porazdir has disappeared! He will return in 60 minutes.");
		} else if(stopwatch.elapsedTime(TimeUnit.MINUTES) == 10) {// 10 minutes
			World.sendMessage("<col=ff0000>Porazdir will disappear in 5 minutes!");
		} else if(stopwatch.elapsedTime(TimeUnit.MINUTES) == 5) {// 5 minutes
			World.sendMessage("<col=ff0000>Porazdir will disappear in 10 minutes!");
		}
	}
}
