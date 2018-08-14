package io.battlerune.content.activity.impl.zulrah;

import io.battlerune.content.activity.Activity;
import io.battlerune.content.activity.ActivityListener;
import io.battlerune.content.activity.ActivityType;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.hit.Hitsplat;
import io.battlerune.game.world.entity.mob.Mob;
import io.battlerune.game.world.entity.mob.npc.Npc;
import io.battlerune.game.world.entity.mob.npc.NpcDeath;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.object.CustomGameObject;
import io.battlerune.game.world.position.Area;
import io.battlerune.game.world.position.Position;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.RandomUtils;
import io.battlerune.util.Utility;

import java.util.*;

public class ZulrahActivity extends Activity {
	public final Player player;
	public ZulrahState state;
	boolean attackable;
	private boolean completed;
	public Position target;
	public Npc zulrah;
	public int count;
	List<Npc> snakes = new ArrayList<>();
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
	public void onDeath(Mob mob) {
		if(mob.isNpc() && mob.getNpc().id != 2045) {
			World.schedule(new NpcDeath(mob.getNpc()));
			completed = true;
			finish();
			return;
		}
		if(mob.isNpc() && snakes.contains(mob.getNpc())) {
			World.schedule(new NpcDeath(mob.getNpc(), () -> {
				snakes.remove(mob.getNpc());
				remove(mob);
			}));
			return;
		}
		super.onDeath(mob);
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
