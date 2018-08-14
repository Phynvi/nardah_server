package io.battlerune.content.skill.impl.agility.obstacle;

import io.battlerune.Config;
import io.battlerune.content.clanchannel.content.ClanTaskKey;
import io.battlerune.content.skill.impl.agility.Agility;
import io.battlerune.game.Animation;
import io.battlerune.game.task.Task;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.mob.MobAnimation;
import io.battlerune.game.world.entity.mob.UpdateFlag;
import io.battlerune.game.world.entity.mob.data.LockType;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.skill.Skill;
import io.battlerune.game.world.items.Item;
import io.battlerune.game.world.items.ground.GroundItem;
import io.battlerune.game.world.position.Area;
import io.battlerune.game.world.position.Position;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.Utility;

import java.util.concurrent.TimeUnit;

public interface ObstacleInteraction {

	int getAnimation();

	String getPreMessage();

	String getPostMessage();

	void start(Player player);

	void onExecution(Player player, Position start, Position end);

	void onCancellation(Player player);

	default void execute(Player player, Obstacle next, Position start, Position end, int level, float experience, int ordinal) {
		if(!canExecute(player, level))
			return;

		if(player.locking.locked(LockType.MASTER))
			return;

		player.getCombat().reset();
		player.locking.lock();

		World.schedule(new Task(true, 1) {
			private final MobAnimation ANIMATION = player.mobAnimation.copy();
			private final boolean RUNNING = player.movement.isRunning();
			private boolean started = false;
			private Obstacle nextObstacle = next;

			@Override
			protected void onSchedule() {
				player.movement.setRunningToggled(false);
				if(getPreMessage() != null)
					player.send(new SendMessage(getPreMessage()));
				start(player);
				attach(player);
			}

			@Override
			protected void execute() {
				if(nextObstacle != null && player.getPosition().equals(nextObstacle.getEnd())) {
					if(canExecute(player, level)) {
						nextObstacle.getType().getInteraction().start(player);
						nextObstacle.getType().getInteraction().onExecution(player, nextObstacle.getStart(), nextObstacle.getEnd());
						if(nextObstacle.getType().getInteraction().getPreMessage() != null)
							player.send(new SendMessage(nextObstacle.getType().getInteraction().getPreMessage()));
					}
					if(nextObstacle.getNext() == null) {
						this.cancel();
						return;
					}
					nextObstacle.getType().getInteraction().onCancellation(player);
					nextObstacle = nextObstacle.getNext();
				} else if(player.getPosition().equals(end)) {
					if(nextObstacle != null) {
						if(canExecute(player, level)) {
							nextObstacle.getType().getInteraction().start(player);
							nextObstacle.getType().getInteraction().onExecution(player, nextObstacle.getStart(), nextObstacle.getEnd());
							if(nextObstacle.getType().getInteraction().getPreMessage() != null) {
								player.send(new SendMessage(nextObstacle.getType().getInteraction().getPreMessage()));
							}
						}
						if(nextObstacle.getNext() != null) {
							nextObstacle.getType().getInteraction().onCancellation(player);
							nextObstacle = nextObstacle.getNext();
						} else {
							this.cancel();
							return;
						}
					} else {
						this.cancel();
						return;
					}
				}
				if(!started) {
					started = true;
					onExecution(player, start, end);
					if(ordinal > -1 && Utility.random(50) == 0) {

						if(!Area.inWildernessResource(player) || !Area.inBarbarianCourse(player) || !Area.inGnomeCourse(player)) {
							GroundItem.create(player, new Item(11849, 1));
							player.send(new SendMessage("<col=C60DDE>There appears to be a wild Grace mark near you."));
						}
					}
				}
			}

			@Override
			protected void onCancel(boolean logout) {
				if(logout) {
					player.move(start);
					return;
				}
				if(getPostMessage() != null)
					player.send(new SendMessage(getPostMessage()));
				if(experience > 0)
					player.skills.addExperience(Skill.AGILITY, experience * Config.AGILITY_MODIFICATION);
				if(ordinal > -1) {
					if(ordinal == 0) {
						player.attributes.set("AGILITY_FLAGS", 1 << ordinal);
					} else {
						int i = player.attributes.get("AGILITY_FLAGS", Integer.class) | (1 << ordinal);
						player.attributes.set("AGILITY_FLAGS", i);
					}
				}
				if(Area.inGnomeCourse(player)) {
					if(courseRewards(player, "Gnome Agility Course", Agility.GNOME_FLAGS, 39))
						;
					//                        AchievementHandler.activateAchievement(player, AchievementList.COMPLETE_100_GNOME_COURSES, 1);
				}
				if(Area.inBarbarianCourse(player)) {
					if(courseRewards(player, "Barbarian Agility Course", Agility.BARBARIAN_FLAGS, 46.5f))
						;
					//                        AchievementHandler.activateAchievement(player, AchievementList.COMPLETE_250_BARB_COURSES, 1);
				}
				if(Area.inWildernessCourse(player)) {
					if(courseRewards(player, "Wilderness Agility Course", Agility.WILDERNESS_FLAGS, 498.9f))
						;
					//                        AchievementHandler.activateAchievement(player, AchievementList.COMPLETE_500_WILD_COURSES, 1);
				}
				player.mobAnimation = ANIMATION;
				player.animate(new Animation(65535));
				player.updateFlags.add(UpdateFlag.APPEARANCE);
				player.movement.setRunningToggled(RUNNING);
				Position nextPosition = nextObstacle != null ? nextObstacle.getEnd() : end;
				if(!player.getPosition().equals(nextPosition)) {
					player.move(nextPosition);
				} else {
					int time = player.attributes.get("AGILITY_TYPE", ObstacleType.class) == ObstacleType.WILDERNESS_COURSE ? 1199 : 599;
					player.locking.lock(time, TimeUnit.MILLISECONDS, LockType.MASTER);
				}
				onCancellation(player);
			}
		});
	}

	default boolean canExecute(Player player, int level) {
		if(player.skills.getLevel(Skill.AGILITY) < level) {
			player.dialogueFactory.sendStatement("You need an agility level of " + level + " to do this!").execute();
			return false;
		}
		return true;
	}

	default boolean courseRewards(Player player, String course, int flags, float bonus) {
		int flag = player.attributes.get("AGILITY_FLAGS", Integer.class);
		if((flag & flags) != flags)
			return false;

		player.skills.addExperience(Skill.AGILITY, bonus * Config.AGILITY_MODIFICATION);
		player.send(new SendMessage("You have completed the " + course + " and receive 5 tickets."));
		player.forClan(channel -> channel.activateTask(ClanTaskKey.AGILITY_COURSE, player.getName()));
		player.inventory.addOrDrop(new Item(2996, 5));
		player.attributes.set("AGILITY_FLAGS", 0);
		return true;
	}

}