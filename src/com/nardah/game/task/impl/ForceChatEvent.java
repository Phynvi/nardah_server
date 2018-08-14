package com.nardah.game.task.impl;

import com.nardah.game.world.World;
import com.nardah.util.parser.impl.NpcForceChatParser;
import com.nardah.game.task.Task;
import com.nardah.game.world.entity.actor.mob.Mob;

/**
 * An randomevent which starts the forced message randomevent.
 * @author Daniel | Obey
 */
public class ForceChatEvent extends Task {

	private final Mob mob;

	private final NpcForceChatParser.ForcedMessage forcedMessage;

	public ForceChatEvent(Mob mob, NpcForceChatParser.ForcedMessage forcedMessage) {
		super(forcedMessage.getInterval());
		this.mob = mob;
		this.forcedMessage = forcedMessage;
	}

	@Override
	public void execute() {
		if(mob == null || !World.getNpcs().contains(mob)) {
			cancel();
			return;
		}

		mob.speak(forcedMessage.nextMessage());
	}

}
