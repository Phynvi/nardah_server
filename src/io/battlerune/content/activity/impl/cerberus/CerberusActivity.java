package io.battlerune.content.activity.impl.cerberus;

import io.battlerune.Config;
import io.battlerune.content.ActivityLog;
import io.battlerune.content.activity.Activity;
import io.battlerune.content.activity.ActivityType;
import io.battlerune.content.dialogue.DialogueFactory;
import io.battlerune.content.dialogue.Expression;
import io.battlerune.content.event.impl.ObjectInteractionEvent;
import io.battlerune.content.skill.impl.magic.teleport.Teleportation;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.npc.Npc;
import io.battlerune.game.world.entity.actor.npc.NpcDeath;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.position.Area;
import io.battlerune.game.world.position.Position;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.Utility;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public class CerberusActivity extends Activity {

	private final Player player;
	public Npc cerberus = null;
	private boolean completed;
	public Set<Npc> ghosts = new HashSet<>();
	private final CerberusActivityListener listener = new CerberusActivityListener(this);

	private static final int CERBERUS = 5862;
	private static final Position CERBERUS_POS = new Position(1238, 1250);

	private CerberusActivity(Player player, int instance) {
		super(1, instance);
		this.player = player;
	}

	public static CerberusActivity create(Player player) {
		CerberusActivity minigame = new CerberusActivity(player, player.playerAssistant.instance());
		minigame.add(player);
		player.gameRecord.start();
		return minigame;
	}

	public static void CreatePaidInstance(Player player) {
		if(player.bank.contains(995, 75000)) {
			player.bank.remove(995, 75000);
			Teleportation.teleport(player, new Position(1240, 1226, 0), 20, () -> CerberusActivity.create(player));
			player.send(new SendMessage("You have teleported to the Instanced Version of Cerberus"));
			player.send(new SendMessage("75,000 coins has been taken out of your bank, as a fee."));

		} else {
			DialogueFactory factory = player.dialogueFactory;
			Teleportation.teleport(player, new Position(1240, 1226, 0));
			factory.sendNpcChat(5608, Expression.HAPPY, "You need to have 75,000 in your bank!");
			player.message("You need to have 75,000 in your bank!");
		}
	}

	public static void CreateUnPaidInstance(Player player) {
		player.send(new SendMessage("You have teleported to the Non-Instanced Version of Cerberus"));
		Teleportation.teleport(player, new Position(1240, 1226, 0));
	}

	@Override
	public void onDeath(Actor actor) {
		if(actor.isPlayer() && actor.equals(player)) {
			player.send(new SendMessage("Better luck next time!"));
			cleanup();
			remove(player);
			return;
		}
		if(actor.isNpc() && actor.getNpc().id == CERBERUS) {
			World.schedule(new NpcDeath(actor.getNpc(), () -> {
				completed = true;
				finish();
			}));
			return;
		}
		super.onDeath(actor);
	}

	@Override
	public void add(Actor actor) {
		super.add(actor);
		if(actor.isNpc()) {
			if(actor.getNpc().id == CERBERUS) {
				cerberus = actor.getNpc();
			} else {
				ghosts.add(actor.getNpc());
			}
			actor.locking.lock();
		}
	}

	@Override
	public void remove(Actor actor) {
		if(!actor.isNpc()) {
			super.remove(actor);
			return;
		}
		int id = actor.getNpc().id;
		if(id == CERBERUS) {
			cerberus = null;
			Teleportation.teleport(player, Config.DEFAULT_POSITION, 20, () -> {
			});
		} else {
			ghosts.remove(actor.getNpc());
		}
		super.remove(actor);
	}

	@Override
	protected void start() {
		Npc npc = new Npc(CERBERUS, CERBERUS_POS);
		npc.face(player);
		npc.owner = player;
		add(npc);
		player.face(cerberus.getPosition());
		pause();
	}

	@Override
	public void onLogout(Player player) {
		player.move(Config.DEFAULT_POSITION);
		cleanup();
		finish();
	}

	@Override
	public void onRegionChange(Player player) {
		if(!Area.inCerberus(player)) {
			cleanup();
			finish();
		}
	}

	@Override
	public boolean canTeleport(Player player) {
		return true;
	}

	// new method = good code.
	@Override
	public void finish() {
		boolean successfull = cerberus.isDead();
		cleanup();
		remove(player);
		if(successfull) {
			player.activityLogger.add(ActivityLog.CERBERUS);
			player.message("Congratulations, you have killed the Cerberus. Fight duration: @red@" + Utility.getTime(player.gameRecord.end(ActivityType.CERBERUS)) + "</col>.");
			restart(10, () -> {
				if(Area.inCerberus(player)) {
					create(player);
				} else {
					remove(player);
				}
			});
		}
	}

	/*
	 * @Override public void finish() { //old method, shit code cleanup();
	 *
	 * if (completed) { player.send(new
	 * SendMessage("Congratulations, you have killed the Cerberus.")); //+
	 * Utility.getTime(player.gameRecord.end(ActivityType.CERBERUS)) + "</col>."));
	 * } else { player.gameRecord.end(ActivityType.CERBERUS, false); }
	 *
	 * remove(player);
	 * player.message("Please teleport back to Cerberus to fight him again!"); }
	 */

	@Override
	public void cleanup() {
		if(cerberus != null && cerberus.isRegistered())
			cerberus.unregister();
		Iterator<Npc> it = ghosts.iterator();
		while(it.hasNext()) {
			Npc npc = it.next();
			npc.animate(npc.definition.getDeathAnimation());
			World.schedule(3, npc::unregister);
			it.remove();
		}
	}

	@Override
	protected boolean clickObject(Player player, ObjectInteractionEvent event) {
		return true;
	}

	@Override
	public ActivityType getType() {
		return ActivityType.CERBERUS;
	}

	@Override
	protected Optional<CerberusActivityListener> getListener() {
		return Optional.of(listener);
	}
}
