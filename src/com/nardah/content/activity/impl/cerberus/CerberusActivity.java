package com.nardah.content.activity.impl.cerberus;

import com.nardah.content.dialogue.Expression;
import com.nardah.Config;
import com.nardah.content.ActivityLog;
import com.nardah.content.activity.Activity;
import com.nardah.content.activity.ActivityType;
import com.nardah.content.dialogue.DialogueFactory;
import com.nardah.content.event.impl.ObjectInteractionEvent;
import com.nardah.content.skill.impl.magic.teleport.Teleportation;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.mob.MobDeath;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.position.Area;
import com.nardah.game.world.position.Position;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.Utility;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

public class CerberusActivity extends Activity {

	private final Player player;
	public Mob cerberus = null;
	private boolean completed;
	public Set<Mob> ghosts = new HashSet<>();
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
			World.schedule(new MobDeath(actor.getNpc(), () -> {
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
		Mob mob = new Mob(CERBERUS, CERBERUS_POS);
		mob.face(player);
		mob.owner = player;
		add(mob);
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
		Iterator<Mob> it = ghosts.iterator();
		while(it.hasNext()) {
			Mob mob = it.next();
			mob.animate(mob.definition.getDeathAnimation());
			World.schedule(3, mob::unregister);
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
