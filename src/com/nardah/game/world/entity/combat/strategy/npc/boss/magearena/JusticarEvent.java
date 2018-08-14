package com.nardah.game.world.entity.combat.strategy.npc.boss.magearena;

import com.nardah.util.Stopwatch;
import com.nardah.game.task.Task;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.mob.Mob;

import java.util.concurrent.TimeUnit;

/**
 * Created by Adam_#6732
 */

public class JusticarEvent extends Task {
	private Mob jusiticar;
	private boolean initial;
	private final Stopwatch stopwatch = Stopwatch.start();
	
	public JusticarEvent() {
		super(false, 100);
		this.jusiticar = null;
		this.initial = true;
	}
	
	@Override
	public void execute() {
		if((jusiticar == null || !jusiticar.isRegistered()) && !initial) {
			initial = true;
			stopwatch.reset();
		}
		
		if(initial) {
			if(stopwatch.elapsedTime(TimeUnit.MINUTES) == 60) {
				jusiticar = JusticarUtility.generatejusiticarSpawn();
				initial = false;
				stopwatch.reset();
			}
			return;
		}
		
		if(stopwatch.elapsedTime(TimeUnit.MINUTES) == 30) {// 15 minutes
			initial = true;
			stopwatch.reset();
			if(jusiticar != null) {
				jusiticar.speak("Pathetic humans could not kill me! Muhahaha");
				World.schedule(2, () -> jusiticar.unregister());
			}
			World.sendMessage("<col=ff0000>Jusiticar has disappeared! He will return in 60 minutes.");
		} else if(stopwatch.elapsedTime(TimeUnit.MINUTES) == 10) {// 10 minutes
			World.sendMessage("<col=ff0000>Jusiticar will disappear in 5 minutes!");
		} else if(stopwatch.elapsedTime(TimeUnit.MINUTES) == 5) {// 5 minutes
			World.sendMessage("<col=ff0000>Jusiticar will disappear in 10 minutes!");
		}
	}
}
