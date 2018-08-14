package io.battlerune.content.activity.impl.vorkath;

import io.battlerune.Config;
import io.battlerune.content.ActivityLog;
import io.battlerune.content.achievement.AchievementHandler;
import io.battlerune.content.achievement.AchievementKey;
import io.battlerune.content.activity.Activity;
import io.battlerune.content.activity.ActivityDeathType;
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

import java.util.Optional;

public class VorkathActivity extends Activity {

	private final Player player;
	public Npc vorkath = null;
	private final VorkathActivityListener listener = new VorkathActivityListener(this);

	private static final int VORKATH = 8060;
	private static final Position VORKATH_POS = new Position(2269, 4062);

	private VorkathActivity(Player player, int instance) {
		super(1, instance);
		this.player = player;
	}

	public static VorkathActivity create(Player player) {
		VorkathActivity minigame = new VorkathActivity(player, player.playerAssistant.instance());
		minigame.add(player);
		player.gameRecord.start();
		return minigame;
	}

	public static void CreatePaidInstance(Player player) {
		if(player.bank.contains(995, 100000)) {
			player.bank.remove(995, 100000);
			Teleportation.teleport(player, new Position(2272, 4055, 0), 20, () -> create(player));
			player.send(new SendMessage("You have teleported to the Instanced Version of Vorkath"));
			player.send(new SendMessage("100,000 coins has been taken out of your bank, as a fee."));

		} else {
			DialogueFactory factory = player.dialogueFactory;
			Teleportation.teleport(player, new Position(2271, 4046, 0));
			factory.sendNpcChat(5608, Expression.HAPPY, "You need to have 100,000 in your bank!");
			player.message("You need to have 100,000 in your bank!");
		}
	}

	public static void CreateUnPaidInstance(Player player) {
		player.send(new SendMessage("You have teleported to the Non-Instanced Version of Vorkath"));
		Teleportation.teleport(player, new Position(2271, 4046, 0));
	}

	@Override
	public void add(Actor actor) {
		super.add(actor);
		if(actor.isNpc()) {
			if(actor.getNpc().id == VORKATH) {
				vorkath = actor.getNpc();
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
		if(id == VORKATH) {
			vorkath = null;
			Teleportation.teleport(player, Config.DEFAULT_POSITION, 20, () -> {
				player.send(new SendMessage("Get yo ass back home boi, " + player.getName() + "!"));
			});
		}
		super.remove(actor);
	}

	@Override
	protected void start() {
		Npc npc = new Npc(VORKATH, VORKATH_POS);
		npc.face(player);
		npc.owner = player;
		add(npc);
		player.face(vorkath.getPosition());
		pause();
	}

	@Override
	public boolean canTeleport(Player player) {
		return true;
	}

	@Override
	public void finish() {
		boolean successfull = vorkath.isDead();
		cleanup();
		remove(player);
		if(successfull) {
			player.activityLogger.add(ActivityLog.VORKATH);
			AchievementHandler.activate(player, AchievementKey.VORKATH);
			player.message("Congratulations, you have killed the Vorkath. Fight duration: @red@" + Utility.getTime(player.gameRecord.end(ActivityType.VORKATH)) + "</col>.");
			restart(10, () -> {
				if(Area.inVorkath(player)) {
					create(player);
				} else {
					remove(player);
				}
			});
		}
	}

	@Override
	public void cleanup() {
		if(vorkath != null && vorkath.isRegistered())
			vorkath.unregister();
	}

	@Override
	protected boolean clickObject(Player player, ObjectInteractionEvent event) {
		return true;
	}

	@Override
	protected Optional<VorkathActivityListener> getListener() {
		return Optional.of(listener);
	}

	@Override
	public void onLogout(Player player) {
		cleanup();
		remove(player);
	}

	@Override
	public void onDeath(Actor actor) {
		if(actor.isNpc() && actor.getNpc().equals(vorkath)) {
			World.schedule(new NpcDeath(actor.getNpc(), this::finish));
			return;
		}

		super.onDeath(actor);
	}

	@Override
	public void onRegionChange(Player player) {
		if(!Area.inVorkath(player)) {
			cleanup();
			remove(player);
		}
	}

	@Override
	public ActivityDeathType deathType() {
		return ActivityDeathType.PURCHASE;
	}

	@Override
	public ActivityType getType() {
		return ActivityType.VORKATH;
	}
}
