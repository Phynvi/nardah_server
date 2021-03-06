package com.nardah.content.activity.impl.pestcontrol;

import com.nardah.content.activity.ActivityType;
import com.nardah.content.activity.GroupActivity;
import com.nardah.content.event.impl.ObjectInteractionEvent;
import com.nardah.game.world.entity.actor.Direction;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.pathfinding.TraversalMap;
import com.nardah.game.world.position.Area;
import com.nardah.game.world.position.Position;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.Utility;

import java.util.*;

/**
 * A {@code PestControl} object is an {@link GroupActivity} that handles the
 * pest control minigame.
 * @author Michael | Chex
 */
public class PestControl extends GroupActivity {
	
	/**
	 * The lobby cooldown timer in ticks.
	 */
	protected static final int PLAYER_CAPACITY = 25;
	
	/**
	 * The lobby cooldown timer in ticks.
	 */
	private static final int LOBBY_COOLDOWN = 30; // 15 seconds for testing
	
	/**
	 * The game cooldown timer in ticks.
	 */
	private static final int GAME_COOLDOWN = 250; // 2 minutes for testing
	
	/**
	 * The portal names.
	 */
	protected static final String[] PORTAL_NAMES = {"Red Portal", "Blue Portal", "Purple Portal", "Yellow Portal"};
	
	/**
	 * The position of the pest control boat.
	 */
	protected static final Position BOAT = new Position(2656, 2609);
	
	/**
	 * The position of the pest control boat.
	 */
	protected static final Position END_POSITION = new Position(2657, 2639);
	
	/**
	 * A list of all the active pest control minigames.
	 */
	private static final List<PestControl> ACTIVE = new LinkedList<>();
	
	/**
	 * The Pest control activity listener.
	 */
	private final PestControlListener listener = new PestControlListener(this);
	
	/**
	 * The void knight.
	 */
	protected final Mob voidKnight = new Mob(1756, Position.create(2656, 2592));
	
	/**
	 * The portals.
	 */
	protected final Portal[] portals = new Portal[]{ /* Red */ new Portal(1742, Position.create(2645, 2569)),
			/* Blue */ new Portal(1740, Position.create(2680, 2588)),
			/* Purple */ new Portal(1739, Position.create(2628, 2591)),
			/* Yellow */ new Portal(1741, Position.create(2669, 2570))};
	
	private static final int[] RAVAGERS = {1704, 1705, 1706, 1707, 1708};
	public static final int[] SPLATTERS = {1691, 1692};
	public static final int[] SHIFTERS = {1698, 1699, 1700, 1701};
	public static final int[] DEFILERS = {1728, 1729};
	public static final int[] TORCHERS = {1728, 1729};
	
	/**
	 * The active monsters.
	 */
	protected Set<Mob> monsters = new HashSet<>();
	
	protected Set<Mob> portalSet = new HashSet<>();
	
	/**
	 * The lobby state.
	 */
	protected boolean lobby;
	
	/**
	 * The void knight messages that he will chant.
	 */
	private static final String[] VOID_KNIGHT_MESSAGES = {"We must not fail!", "Take down the portals", "The Void Knights will not fall!", "Hail the Void Knights!", "We are beating these scum!"};
	
	/**
	 * Constructs a new {@link PestControl} minigame object.
	 */
	private PestControl() {
		super(1, PLAYER_CAPACITY);
		cooldown(LOBBY_COOLDOWN);
		lobby = true;
	}
	
	/**
	 * Adds a player to the pest control boat.
	 */
	public static void enter(Player player) {
		PestControl pestControl = getLobby();
		pestControl.addActivity(player, pestControl.createNode(player));
		player.move(new Position(2661, 2639));
		player.send(new SendMessage("You have entered the pest control boat."));
	}
	
	/**
	 * Sequences all the active pest control minigames.
	 */
	public static void sequenceMinigame() {
		Iterator<PestControl> iterator = ACTIVE.iterator();
		
		while(iterator.hasNext()) {
			PestControl pestControl = iterator.next();
			
			if(!pestControl.lobby) {
				if(pestControl.getTicks() == START || pestControl.getActiveSize() == 0 || pestControl.portalSet.isEmpty()) {
					pestControl.finish();
					pestControl.removeAll();
					pestControl.cleanup();
					iterator.remove();
					continue;
				}
				
				if(pestControl.getTicks() % 10 == 0 && pestControl.voidKnight != null) {
					pestControl.voidKnight.speak(Utility.randomElement(VOID_KNIGHT_MESSAGES));
				}
				
				if(pestControl.getTicks() % 5 == 0) {
					pestControl.agressiveMonsters();
				}
			}
			
			pestControl.sequence();
		}
	}
	
	@Override
	public void sequence() {
		super.sequence();
		forEachActivity((mob, activity) -> activity.getPanel().ifPresent(panel -> ((PestControlPanel) panel).update((PestControlNode) activity)));
	}
	
	@Override
	protected void start() {
		if(!lobby) {
			return;
		}
		
		if(getActiveSize() < 1) {
			groupMessage("There needs to be at least 1 player to start a game.");
			cooldown(LOBBY_COOLDOWN);
			return;
		}
		
		add(voidKnight);
		voidKnight.blockFace = true;
		for(Mob portal : portals) {
			add(portal);
			portalSet.add(portal);
		}
		
		super.start();
		groupMessage("Protect the void knight at all costs, good luck!");
		spawnMonsters();
		cooldown(GAME_COOLDOWN);
		lobby = false;
	}
	
	private final Position[] blue_bounds = Utility.getInnerBoundaries(new Position(2678, 2589), 8, 8);
	private final Position[] red_bounds = Utility.getInnerBoundaries(new Position(2646, 2573), 8, 8);
	private final Position[] yellow_bounds = Utility.getInnerBoundaries(new Position(2670, 2574), 8, 8);
	private final Position[] purple_bounds = Utility.getInnerBoundaries(new Position(2631, 2592), 8, 8);
	private final Position[] knight_bounds = Utility.getInnerBoundaries(new Position(2657, 2593), 8, 8);
	
	private void spawnMonsters() {
		for(int index = 0; index < 5; index++) {
			spawn(Utility.randomElement(RAVAGERS), blue_bounds);
		}
		for(int index = 0; index < 5; index++) {
			spawn(Utility.randomElement(RAVAGERS), red_bounds);
		}
		for(int index = 0; index < 5; index++) {
			spawn(Utility.randomElement(RAVAGERS), yellow_bounds);
		}
		for(int index = 0; index < 5; index++) {
			spawn(Utility.randomElement(RAVAGERS), purple_bounds);
		}
		for(int index = 0; index < 5; index++) {
			spawn(Utility.randomElement(RAVAGERS), knight_bounds);
		}
	}
	
	private void spawn(int id, Position[] boundaries) {
		Position target = Utility.randomElement(boundaries);
		
		while(!TraversalMap.isTraversable(new Position(target.getX(), target.getY(), 0), Direction.NORTH, false)) {
			target = Utility.randomElement(boundaries);
		}
		
		Mob monster = new Mob(id, new Position(target.getX(), target.getY(), 0));
		monsters.add(monster);
		add(monster);
	}
	
	private void agressiveMonsters() {
		if(monsters.isEmpty())
			return;
		if(voidKnight == null)
			return;
		for(Mob monster : monsters) {
			if(monster.getCombat().inCombat())
				continue;
			if(monster.getPosition().isWithinDistance(voidKnight.getPosition(), 10)) {
				monster.getCombat().attack(voidKnight);
				continue;
			}
			activities.keySet().forEach(mob -> {
				if(!mob.isPlayer())
					return;
				if(monster.getPosition().isWithinDistance(mob.getPosition(), 8))
					monster.getCombat().attack(mob);
			});
		}
	}
	
	@Override
	public void cleanup() {
		super.cleanup();
		remove(voidKnight);
		for(Mob monster : monsters)
			remove(monster);
		for(Mob portal : portals)
			remove(portal);
		monsters.clear();
	}
	
	@Override
	public boolean canTeleport(Player player) {
		return true;
	}
	
	@Override
	public void onLogout(Player player) {
		removeActivity(player);
	}
	
	@Override
	public void onRegionChange(Player player) {
		if(!Area.inPestControl(player)) {
			removeActivity(player);
		}
	}
	
	@Override
	protected boolean clickObject(Player player, ObjectInteractionEvent event) {
		if(event.getObject().getId() == 14314) {
			removeActivity(player);
			// player.send(new SendMessage("You are trapped in my MINIGAME HAHAH! You can
			// only leave once the minigame is completed."));
			return true;
		}
		return false;
	}
	
	@Override
	public boolean safe() {
		return true;
	}
	
	@Override
	public void onDeath(Actor actor) {
		if(actor.isNpc()) {
			Mob mob = actor.getNpc();
			
			if(mob.id >= 1739 && mob.id <= 1742) {
				portalSet.remove(mob);
			} else {
				monsters.remove(actor.getNpc());
			}
			
			remove(actor);
		} else {
			actor.move(BOAT.transform(Utility.random(4), Utility.random(6)));
		}
	}
	
	/**
	 * Gets the next pest control lobby.
	 */
	private static PestControl getLobby() {
		for(PestControl next : ACTIVE)
			if(next.inLobby())
				return next;
		PestControl next = new PestControl();
		ACTIVE.add(next);
		return next;
	}
	
	/**
	 * Creates a new {@link PestControlNode} for a player.
	 */
	private PestControlNode createNode(Player player) {
		PestControlNode node = new PestControlNode(this, player);
		PestControlPanel panel = new PestControlPanel(this, player);
		panel.open();
		node.setPanel(panel);
		return node;
	}
	
	/**
	 * Checks if this pest control minigame is in the lobby.
	 */
	private boolean inLobby() {
		return lobby;
	}
	
	@Override
	protected Optional<PestControlListener> getListener() {
		return Optional.of(listener);
	}
	
	@Override
	public ActivityType getType() {
		return ActivityType.PEST_CONTROL;
	}
	
}