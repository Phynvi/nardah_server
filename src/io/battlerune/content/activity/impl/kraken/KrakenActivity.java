package io.battlerune.content.activity.impl.kraken;

import io.battlerune.Config;
import io.battlerune.content.achievement.AchievementHandler;
import io.battlerune.content.achievement.AchievementKey;
import io.battlerune.content.activity.Activity;
import io.battlerune.content.activity.ActivityType;
import io.battlerune.content.event.impl.ObjectInteractionEvent;
import io.battlerune.content.skill.impl.magic.teleport.Teleportation;
import io.battlerune.game.Animation;
import io.battlerune.game.UpdatePriority;
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
	 * The Kraken npc.
	 */
	public Npc kraken = null;

	/**
	 * The whirlpool activated count.
	 */
	public int count = 0;

	/**
	 * Flag if kraken has been defeated.
	 */
	private boolean completed;

	/**
	 * Set of npc tentacles.
	 */
	private Set<Npc> tentacles = new HashSet<>();

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
	 * Handles transforming a Npc.
	 */
	public void transform(Npc npc, int transform) {
		npc.transform(transform);
		npc.npcAssistant.login();
		if(transform == 5535)
			count++;
		if(transform == 494)
			npc.animate(new Animation(7135, UpdatePriority.HIGH));
		npc.getCombat().attack(player);
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
			Npc npc = new Npc(aSPAWN[0], new Position(aSPAWN[1], aSPAWN[2]));
			npc.owner = player;
			add(npc);
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
		Iterator<Npc> it = tentacles.iterator();
		while(it.hasNext()) {
			Npc npc = it.next();
			npc.animate(npc.definition.getDeathAnimation());
			World.schedule(3, npc::unregister);
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