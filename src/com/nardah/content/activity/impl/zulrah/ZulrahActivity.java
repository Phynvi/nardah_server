package com.nardah.content.activity.impl.zulrah;

import com.nardah.content.activity.Activity;
import com.nardah.content.activity.ActivityListener;
import com.nardah.content.activity.ActivityType;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.hit.Hitsplat;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.mob.MobDeath;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.object.CustomGameObject;
import com.nardah.game.world.position.Area;
import com.nardah.game.world.position.Position;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.RandomUtils;
import com.nardah.util.Utility;

import java.util.*;

public class ZulrahActivity extends Activity {
	public final Player player;
	public ZulrahState state;
	boolean attackable;
	private boolean completed;
	public Position target;
	public Mob zulrah;
	public int count;
	List<Mob> snakes = new ArrayList<>();
	List<CustomGameObject> clouds = new ArrayList<>();
	private final ActivityListener<? extends Activity> ZULRAH_LISTENER = new ZulrahListener(this);

	private ZulrahActivity(Player player, int instance) {
		super(1, instance);
		this.count = 0;
		this.player = player;
		this.state = ZulrahState.INITIALIZE;
	}

	public static ZulrahActivity create(Player player) {
		ZulrahActivity activity = new ZulrahActivity(player, player.playerAssistant.instance());
		activity.add(player);
		player.send(new SendMessage("Welcome to Zulrah's shrine."));
		player.dialogueFactory.sendStatement("Welcome to Zulrah's shrine.").execute();
		player.gameRecord.start();
		activity.start();
		return activity;
	}

	ZulrahState getNextState() {
		if(zulrah.id == 2043)
			return ZulrahState.ATTACKING;
		Set<ZulrahState> states = new HashSet<>();
		states.add(ZulrahState.ATTACKING);
		if(clouds.size() <= ZulrahConstants.MAXIMUM_CLOUDS)
			states.add(ZulrahState.POISONOUS_CLOUD);
		if(snakes.size() <= ZulrahConstants.MAXIMUM_SNAKELINGS)
			states.add(ZulrahState.SNAKELINGS);
		return Utility.randomElement(states);
	}

	Position getSnakelingPosition() {
		Set<Position> positions = new HashSet<>(Arrays.asList(new Position(2263, 3075), new Position(2263, 3071), new Position(2268, 3069), new Position(2273, 3071), new Position(2273, 3077)));
		snakes.forEach(snake -> {
			if(positions.contains(snake.getPosition())) {
				positions.remove(snake.getPosition());
			}
		});
		return Utility.randomElement(positions);
	}

	Position getCloudPosition() {
		Set<Position> positions = new HashSet<>(Arrays.asList(ZulrahConstants.CLOUD_POSITIONS));
		clouds.forEach(cloud -> {
			if(positions.contains(cloud.getPosition())) {
				positions.remove(cloud.getPosition());
			}
		});
		return Utility.randomElement(positions);
	}

	@Override
	protected void start() {
		if(!clouds.isEmpty()) {
			for(CustomGameObject cloud : clouds) {
				Position[] boundaries = Utility.getInnerBoundaries(cloud.getPosition().transform(+1, +1), 2, 2);
				for(Position position : boundaries) {
					System.out.println("Checking if " + player.getName() + "s position(" + player.getPosition() + " is in " + position);
					if(player.getPosition().equals(position))
						player.damage(new Hit(RandomUtils.inclusive(1, 5), Hitsplat.POISON));
				}
			}
		}
		state.execute(this);
	}

	@Override
	public void cleanup() {
		remove(zulrah);
		snakes.forEach(this::remove);
		clouds.forEach(CustomGameObject::unregister);
		clouds.clear();
		snakes.clear();
	}

	@Override
	public void finish() {
		cleanup();
		remove(player);
		if(completed) {
			player.send(new SendMessage("Fight duration: @red@" + Utility.getTime(player.gameRecord.end(ActivityType.ZULRAH)) + "</col>."));
		}
	}

	@Override
	public void onLogout(Player player) {
		finish();
	}

	@Override
	public void onRegionChange(Player player) {
		if(!Area.inZulrah(player))
			finish();
	}

	@Override
	public void onDeath(Actor actor) {
		if(actor.isNpc() && actor.getNpc().id != 2045) {
			World.schedule(new MobDeath(actor.getNpc()));
			completed = true;
			finish();
			return;
		}
		if(actor.isNpc() && snakes.contains(actor.getNpc())) {
			World.schedule(new MobDeath(actor.getNpc(), () -> {
				snakes.remove(actor.getNpc());
				remove(actor);
			}));
			return;
		}
		super.onDeath(actor);
	}

	@Override
	public boolean safe() {
		return true;
	}

	@Override
	protected Optional<? extends ActivityListener<? extends Activity>> getListener() {
		return Optional.of(ZULRAH_LISTENER);
	}

	@Override
	public boolean canTeleport(Player player) {
		return true;
	}

	@Override
	public ActivityType getType() {
		return ActivityType.ZULRAH;
	}
}
