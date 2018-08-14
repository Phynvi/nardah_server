package io.battlerune.game.task.impl;

import io.battlerune.content.skill.impl.woodcutting.AxeData;
import io.battlerune.game.Animation;
import io.battlerune.game.UpdatePriority;
import io.battlerune.game.task.Task;
import io.battlerune.game.world.entity.actor.Direction;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.skill.Skill;
import io.battlerune.game.world.object.GameObject;
import io.battlerune.net.packet.out.SendMessage;

import java.util.Optional;

public class ChopVineTask extends Task {

	private int tick = 0;
	private final Player player;
	private final GameObject object;
	private final int respawn;

	public ChopVineTask(Player player, GameObject object, int respawn) {
		super(true, 0);
		this.player = player;
		this.object = object;
		this.respawn = respawn;
	}

	@Override
	protected void onSchedule() {
		if(!player.getPosition().isWithinDistance(object.getPosition(), 1)) {
			cancel();
			return;
		}

		if(player.skills.getLevel(Skill.WOODCUTTING) < 34) {
			player.send(new SendMessage("You need a woodcutting level of 34 or more to cut this."));
			cancel();
			return;
		}

		Optional<AxeData> result = AxeData.getDefinition(player);

		if(!result.isPresent() || !player.toolkit.contains(result.get().id)) {
			player.send(new SendMessage("You need an axe to cut this."));
			cancel();
			return;
		}

		AxeData data = result.get();

		player.animate(new Animation(data.animation, UpdatePriority.HIGH));
	}

	@Override
	public void execute() {
		if(tick == 1) {
			object.unregister();
		} else if(tick == respawn / 2) {
			Direction direction = Direction.getDirection(player.getPosition(), object.getPosition());
			player.walk(player.getPosition().transform(direction.getDirectionX() * 2, direction.getDirectionY() * 2), true);
		} else if(tick >= respawn) {
			object.register();
			cancel();
		}

		tick++;
	}

	@Override
	protected void onCancel(boolean logout) {

	}

}
