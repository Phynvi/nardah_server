package io.battlerune.game.world.entity.combat.strategy.npc.boss.magearena;

import io.battlerune.game.task.Task;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.actor.npc.Npc;
import io.battlerune.util.Stopwatch;

import java.util.concurrent.TimeUnit;

/**
 * Created by Adam_#6732
 */

public class DerwenEvent extends Task {
	private Npc Derwen;
	private boolean initial;
	private final Stopwatch stopwatch = Stopwatch.start();
	
	public DerwenEvent() {
		super(false, 100);
		this.Derwen = null;
		this.initial = true;
	}
	
	@Override
	public void execute() {
		if((Derwen == null || !Derwen.isRegistered()) && !initial) {
			initial = true;
			stopwatch.reset();
		}
		
		if(initial) {
			if(stopwatch.elapsedTime(TimeUnit.MINUTES) == 90) {
				Derwen = DerwenUtility.generatederwenSpawn();
				initial = false;
				stopwatch.reset();
			}
			return;
		}
		
		if(stopwatch.elapsedTime(TimeUnit.MINUTES) == 45) {// 15 minutes
			initial = true;
			stopwatch.reset();
			if(Derwen != null) {
				Derwen.speak("Pathetic humans could not kill me! Muhahaha");
				World.schedule(2, () -> Derwen.unregister());
			}
			World.sendMessage("<col=ff0000> Derwen has disappeared! He will return in 60 minutes.");
		} else if(stopwatch.elapsedTime(TimeUnit.MINUTES) == 10) {// 10 minutes
			World.sendMessage("<col=ff0000> Derwen will disappear in 5 minutes!");
		} else if(stopwatch.elapsedTime(TimeUnit.MINUTES) == 5) {// 5 minutes
			World.sendMessage("<col=ff0000> Derwen will disappear in 10 minutes!");
		}
	}
}
