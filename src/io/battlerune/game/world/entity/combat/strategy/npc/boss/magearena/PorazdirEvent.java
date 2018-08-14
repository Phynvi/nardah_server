package io.battlerune.game.world.entity.combat.strategy.npc.boss.magearena;

import io.battlerune.game.task.Task;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.mob.npc.Npc;
import io.battlerune.util.Stopwatch;

import java.util.concurrent.TimeUnit;

/**
 * Created by Adam_#6732
 */

public class PorazdirEvent extends Task {
	private Npc Porazdir;
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
