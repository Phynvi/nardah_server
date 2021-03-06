package com.nardah.content.activity.impl.magearena;

import com.nardah.content.activity.panel.ActivityPanel;
import com.nardah.content.ActivityLog;
import com.nardah.content.activity.Activity;
import com.nardah.content.activity.ActivityType;
import com.nardah.content.quest.QuestManager;
import com.nardah.content.skill.impl.magic.teleport.Teleportation;
import com.nardah.game.Graphic;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.Direction;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.mob.MobDeath;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.pathfinding.TraversalMap;
import com.nardah.game.world.position.Area;
import com.nardah.game.world.position.Position;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.Utility;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * The Kolodion's mage arena activity.
 * @author Daniel
 */
public class MageArena extends Activity {
	/**
	 * The spawn boundaries.
	 */
	private static final Position[] BOUNDARIES = Utility.getInnerBoundaries(new Position(3105, 3934, 0), 8, 8);

	/**
	 * The stages of the activity.
	 */
	private enum Stage {
		INITIALIZE, FIGHTING, FINISH
	}

	/**
	 * The player instance.
	 */
	private final Player player;

	/**
	 * The amount of mobs the player has killed.
	 */
	private int killed;

	/**
	 * The amount of points the player has been awarded.
	 */
	private int points;

	/**
	 * Activity completion flag.
	 */
	private Stage stage = Stage.INITIALIZE;

	/**
	 * Holds all the monsters of the activity.
	 */
	private Map<Integer, Mob> monsters = new HashMap<>();

	/**
	 * Constructs a new <code>MageArena</code>.
	 */
	private MageArena(Player player, int instance) {
		super(5, instance);
		this.player = player;
	}

	/**
	 * Creates a new <code>MageArena</code> activity.
	 */
	public static MageArena create(Player player) {
		MageArena activity = new MageArena(player, player.playerAssistant.instance());
		player.move(new Position(3105, 3934, 0));
		player.gameRecord.start();
		activity.add(player);
		activity.resetCooldown();
		return activity;
	}

	/**
	 * Gets the next best available position.
	 */
	private Position getAvailablePosition() {
		Position available = Utility.randomElement(BOUNDARIES);
		while(!TraversalMap.isTraversable(available, Direction.NORTH, false)) {
			available = Utility.randomElement(BOUNDARIES);
		}
		return available;
	}

	/**
	 * Handles the meteors.
	 */
	private void meteors() {
		if(Utility.random(1, 3) == 2) {
			Position center = new Position(player.getX() - 2, player.getY() - 2, player.getHeight());
			Position[] bounds = Utility.getInnerBoundaries(center, 5, 5);
			Set<Position> meteors = new HashSet<>();
			for(Position bound : bounds) {
				if(Utility.random(100) > 55) {
					meteors.add(bound);
				}
			}
			for(Position meteor : meteors) {
				World.sendGraphic(new Graphic(659), meteor);
				World.schedule(1, () -> {
					if(player.getPosition().equals(meteor))
						player.damage(new Hit(Utility.random(5, 12)));
				});
			}
		}
	}

	@Override
	protected void start() {
		switch(stage) {
			case INITIALIZE:
				for(int index = 0; index < 3; index++) {
					int id = index == 0 ? 1157 : (index == 1 ? 1160 : 1158);
					Mob monster = new Mob(id, getAvailablePosition());
					monster.owner = player;
					monsters.put(id, monster);
					add(monster);
				}
				stage = Stage.FIGHTING;
				cooldown(1);
				break;
			case FIGHTING:
				for(Mob monster : monsters.values()) {
					if(!monster.getCombat().isAttacking(player)) {
						monster.getCombat().attack(player);
						continue;
					}
					if(monster.getCombat().isUnderAttackBy(player) && Utility.random(5, 15) == 5) {
						monster.speak("ARGHHH!");
						World.sendGraphic(new Graphic(1213), player.getPosition());
						player.damage(new Hit(Utility.random(1, 5)));
						Teleportation.teleport(monster, getAvailablePosition(), 20, () -> monster.getCombat().attack(player));
					}
				}
				meteors();
				resetCooldown();
				break;
			case FINISH:
				finish();
				break;
		}
	}

	@Override
	public void finish() {
		long time = player.gameRecord.end(ActivityType.KOLODION_ARENA);

		cleanup();
		remove(player);
		ActivityPanel.clear(player);
		player.move(new Position(2540, 4715, 0));
		String[] dialogue;
		if(stage != Stage.FINISH) {
			dialogue = new String[]{"Muhahaha!", "What did I say?! Pathetic!!", "Pay me and try again."};
		} else {
			// points += 15;
			player.setkolodionPoints(player.getkolodionPoints() + 15);
			player.message("<img=14>You now have @red@" + player.getkolodionPoints() + " Kolodion Points!");
			dialogue = new String[]{"Well I'll be a goblin's ballsack!", "You actually did it! You have proven me wrong.", "Jump into that pool and earn claim your reward."};
			player.send(new SendMessage("You have completed Kolodion's arena. Final time: @red@" + Utility.getTime(time) + "</col>. Earned Points: @red@" + player.getkolodionPoints() + "</col>."));
			player.activityLogger.add(ActivityLog.KOLODIONS_MINIGAME);
			if(player.quest.getStage()[QuestManager.KOLODIONS_ARENA] == 1) {
				player.quest.getStage()[QuestManager.KOLODIONS_ARENA] = 2;
			}
		}

		player.dialogueFactory.sendNpcChat(1605, dialogue).execute();
	}

	@Override
	public void cleanup() {
		for(Mob monster : monsters.values()) {
			monsters.remove(monster);
			remove(monster);
		}
	}

	@Override
	public void onLogout(Player player) {
		finish();
	}

	@Override
	public void onRegionChange(Player player) {
		if(!Area.inkolodionArena(player))
			finish();
	}

	@Override
	public boolean canTeleport(Player player) {
		player.send(new SendMessage("You can not teleport out of this activity."));
		return true;
	}

	@Override
	public void update() {
		int progress = (int) Utility.getPercentageAmount(killed, 3);
		String clock = "Time: <col=FF5500>" + Utility.getTime(System.currentTimeMillis() - player.gameRecord.time) + "</col>";
		String remain = "Monsters Left: <col=FF5500>" + (3 - killed) + "</col>";
		String gained = "Points Gained: <col=FF5500>" + points + "</col>";
		Item[] items = {new Item(2412), new Item(2413), new Item(2414)};
		ActivityPanel.update(player, progress, "Kolodion's Arena", "Progress:", Utility.randomElement(items), clock, remain, gained);
	}

	@Override
	public void onDeath(Actor actor) {
		if(actor.isPlayer()) {
			finish();
			return;
		}
		if(monsters.containsKey(actor.id)) {
			killed++;
			points += Utility.random(5, 10);
			monsters.remove(actor.id);
			World.schedule(new MobDeath(actor.getNpc()));
		}
		if(monsters.isEmpty()) {
			stage = Stage.FINISH;
			cooldown(10);
			return;
		}
		resetCooldown();
	}

	@Override
	public ActivityType getType() {
		return ActivityType.KOLODION_ARENA;
	}
}
