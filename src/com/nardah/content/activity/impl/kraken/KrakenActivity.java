package com.nardah.content.activity.impl.kraken;

import com.nardah.content.achievement.AchievementHandler;
import com.nardah.content.achievement.AchievementKey;
import com.nardah.Config;
import com.nardah.content.activity.Activity;
import com.nardah.content.activity.ActivityType;
import com.nardah.content.event.impl.ObjectInteractionEvent;
import com.nardah.content.skill.impl.magic.teleport.Teleportation;
import com.nardah.game.Animation;
import com.nardah.game.UpdatePriority;
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

/**
 * Handles the Kraken boss.
 * @author Daniel
 */
public class KrakenActivity extends Activity {

	/**
	 * The player fighting the Kraken.
	 */
	private final Player player;

	/**
	 * The Kraken mob.
	 */
	public Mob kraken = null;

	/**
	 * The whirlpool activated count.
	 */
	public int count = 0;

	/**
	 * Flag if kraken has been defeated.
	 */
	private boolean completed;

	/**
	 * Set of mob tentacles.
	 */
	private Set<Mob> tentacles = new HashSet<>();

	/**
	 * The Kraken activity listener.
	 */
	private final KrakenActivityListener listener = new KrakenActivityListener(this);

	/**
	 * Holds all the monster spawn information.
	 */
	private static final int[][] SPAWN = {{493, 2276, 10038}, {493, 2276, 10034}, {496, 2278, 10035}, {493, 2283, 10038}, {493, 2283, 10034},};

	/**
	 * Constructs a new <code>KrakenActivity</code>.
	 */
	private KrakenActivity(Player player, int instance) {
		super(1, instance);
		this.player = player;
	}

	/**
	 * Creates a new Kraken activity.
	 */
	public static KrakenActivity create(Player player) {
		KrakenActivity minigame = new KrakenActivity(player, player.playerAssistant.instance());
		minigame.add(player);
		player.gameRecord.start();
		minigame.count = 0;
		return minigame;
	}

	/**
	 * Handles transforming a Mob.
	 */
	public void transform(Mob mob, int transform) {
		mob.transform(transform);
		mob.mobAssistant.login();
		if(transform == 5535)
			count++;
		if(transform == 494)
			mob.animate(new Animation(7135, UpdatePriority.HIGH));
		mob.getCombat().attack(player);
	}

	@Override
	public void onDeath(Actor actor) {
		if(actor.isPlayer() && actor.equals(player)) {
			player.send(new SendMessage("Better luck next time old chap!"));
			cleanup();
			remove(player);
			return;
		}
		if(actor.isNpc() && actor.getNpc().id == 494) {
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
			if(actor.getNpc().id == 496) {
				kraken = actor.getNpc();
			} else {
				tentacles.add(actor.getNpc());
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
		if(id == 496) {
			kraken = null;
		} else {
			tentacles.remove(actor.getNpc());
		}
		super.remove(actor);
	}

	@Override
	protected void start() {
		for(int[] aSPAWN : SPAWN) {
			Mob mob = new Mob(aSPAWN[0], new Position(aSPAWN[1], aSPAWN[2]));
			mob.owner = player;
			add(mob);
		}
		player.face(kraken.getPosition());
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
		if(!Area.inKraken(player)) {
			cleanup();
			finish();
		}
	}

	@Override
	public boolean canTeleport(Player player) {
		return true;
	}

	@Override
	public void finish() {
		cleanup();

		if(completed) {
			AchievementHandler.activate(player, AchievementKey.KILL_KRAKEN);
			player.send(new SendMessage("Congratulations, you have killed the Kraken. Fight duration: @red@" + Utility.getTime(player.gameRecord.end(ActivityType.KRAKEN)) + "</col>."));
		} else {
			player.gameRecord.end(ActivityType.KRAKEN, false);
		}

		remove(player);
		player.message("Please teleport back to kraken to fight him again!");
	}

	@Override
	public void cleanup() {
		count = 0;
		if(kraken != null && kraken.isRegistered())
			kraken.unregister();
		Iterator<Mob> it = tentacles.iterator();
		while(it.hasNext()) {
			Mob mob = it.next();
			mob.animate(mob.definition.getDeathAnimation());
			World.schedule(3, mob::unregister);
			it.remove();
		}
	}

	@Override
	protected boolean clickObject(Player player, ObjectInteractionEvent event) {
		if(event.getObject().getId() != 538)
			return false;
		player.dialogueFactory.sendOption("Restart Kraken instance", () -> {
			remove(player);
			Teleportation.teleport(player, new Position(2280, 10031, 0), Teleportation.TeleportationData.CREVICE, () -> KrakenActivity.create(player));
		}, "Leave Kraken Instance", () -> {
			remove(player);
			Teleportation.teleportNoChecks(player, Config.DEFAULT_POSITION, Teleportation.TeleportationData.CREVICE);
		}).execute();
		return true;
	}

	@Override
	public ActivityType getType() {
		return ActivityType.KRAKEN;
	}

	@Override
	protected Optional<KrakenActivityListener> getListener() {
		return Optional.of(listener);
	}
}