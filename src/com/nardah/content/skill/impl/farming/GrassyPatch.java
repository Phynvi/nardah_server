package com.nardah.content.skill.impl.farming;

import com.nardah.game.Animation;
import com.nardah.game.action.Action;
import com.nardah.game.action.policy.WalkablePolicy;
import com.nardah.game.task.Task;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.Utility;

public class GrassyPatch {

	public byte stage = 0;
	long time = System.currentTimeMillis();

	public void setTime() {
		time = System.currentTimeMillis();
	}

	public boolean isRaked() {
		return stage == 3;
	}

	public void process(Player player, int index) {
		if(stage == 0)
			return;
		long elapsed = (System.currentTimeMillis() - time) / 60_000;
		int grow = 1;

		if(elapsed >= grow) {
			for(int i = 0; i < elapsed / grow; i++) {
				if(stage == 0) {
					player.getFarming().doConfig();
					return;
				}

				stage = ((byte) (stage - 1));
			}
			player.getFarming().doConfig();
			setTime();
		}
	}

	public void click(Player player, int option, int index) {
		if(option == 1) {
			player.action.execute(raking(player, index));
		}
	}

	boolean raking = false;

	public Action<Player> raking(Player p, final int index) {
		return new Action<Player>(p, 3, true) {
			int delay = 0;

			@Override
			protected void execute() {
				if(isRaked()) {
					p.send(new SendMessage("This plot is fully raked. Try planting a seed."));
					return;
				}
				if(!p.inventory.contains(5341)) {
					p.send(new SendMessage("This patch needs to be raked before anything can grow in it."));
					p.send(new SendMessage("You do not have a rake in your inventory."));
					return;
				}
				raking = true;
				p.animate(new Animation(2273));
				if(!p.inventory.contains(5341)) {
					p.send(new SendMessage("This patch needs to be raked before anything can grow in it."));
					p.send(new SendMessage("You do not have a rake in your inventory."));
					cancel();
					return;
				}
				if(p.inventory.getFreeSlots() == 0) {
					cancel();
					return;
				}
				p.animate(new Animation(2273));
				if(delay >= 2 + Utility.random(2)) {
					setTime();
					GrassyPatch grassyPatch = GrassyPatch.this;
					grassyPatch.stage = ((byte) (grassyPatch.stage + 1));
					grassyPatch.doConfig(p, index);
					p.skills.addExperience(Skill.FARMING, Utility.random(2));
					p.inventory.add(6055, 1);
					if(isRaked()) {
						p.send(new SendMessage("The plot is now fully raked."));
						cancel();
					}
					delay = 0;
				}
				delay++;
			}

			@Override
			protected void onSchedule() {
				p.skills.get(Skill.FARMING).setDoingSkill(true);
			}

			@Override
			protected void onCancel(boolean logout) {
				p.skills.get(Skill.FARMING).setDoingSkill(false);
			}

			@Override
			public String getName() {
				return "Raking patch";
			}

			@Override
			public boolean prioritized() {
				return false;
			}

			@Override
			public WalkablePolicy getWalkablePolicy() {
				return WalkablePolicy.NON_WALKABLE;
			}
		};
	}

	public void rake(final Player p, final int index) {
		if(raking)
			return;
		if(isRaked()) {
			p.send(new SendMessage("This plot is fully raked. Try planting a seed."));
			return;
		}
		if(!p.inventory.contains(5341)) {
			p.send(new SendMessage("This patch needs to be raked before anything can grow in it."));
			p.send(new SendMessage("You do not have a rake in your inventory."));
			return;
		}
		raking = true;
		//		p.getSkillManager().stopSkilling();
		p.animate(new Animation(2273));
		World.schedule(new Task(1) {
			int delay = 0;

			@Override
			public void execute() {
				if(!p.inventory.contains(5341)) {
					p.send(new SendMessage("This patch needs to be raked before anything can grow in it."));
					p.send(new SendMessage("You do not have a rake in your inventory."));
					cancel();
					return;
				}
				if(p.inventory.getFreeSlots() == 0) {
					//					p.inventory.full();
					cancel();
					return;
				}
				p.animate(new Animation(2273));
				if(delay >= 2 + Utility.random(2)) {
					setTime();
					GrassyPatch grassyPatch = GrassyPatch.this;
					grassyPatch.stage = ((byte) (grassyPatch.stage + 1));
					grassyPatch.doConfig(p, index);
					p.skills.addExperience(Skill.FARMING, Utility.random(2));
					p.inventory.add(6055, 1);
					if(isRaked()) {
						p.send(new SendMessage("The plot is now fully raked."));
						cancel();
					}
					delay = 0;
				}
				delay++;
			}

			@Override
			protected void onCancel(boolean logout) {
				super.onCancel(false);
				raking = false;
				//				setEventRunning(false);
				p.animate(new Animation(65535));
			}

		});
		//		TaskManager.submit(p.getCurrentTask());
	}

	public void doConfig(Player p, int index) {
		p.getFarming().doConfig();
	}

	public int getConfig(int index) {
		return stage * FarmingPatches.values()[index].mod;
	}
}
