package io.battlerune.game.world.entity.actor;

import io.battlerune.content.activity.Activity;
import io.battlerune.content.activity.ActivityType;
import io.battlerune.game.Animation;
import io.battlerune.game.Graphic;
import io.battlerune.game.action.ActionManager;
import io.battlerune.game.task.impl.ForceMovementTask;
import io.battlerune.game.world.Interactable;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.Entity;
import io.battlerune.game.world.entity.EntityType;
import io.battlerune.game.world.entity.combat.Combat;
import io.battlerune.game.world.entity.combat.CombatUtil;
import io.battlerune.game.world.entity.combat.PoisonType;
import io.battlerune.game.world.entity.combat.effect.CombatEffectType;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.strategy.CombatStrategy;
import io.battlerune.game.world.entity.actor.data.LockType;
import io.battlerune.game.world.entity.actor.movement.Movement;
import io.battlerune.game.world.entity.actor.movement.waypoint.CombatWaypoint;
import io.battlerune.game.world.entity.actor.movement.waypoint.FollowWaypoint;
import io.battlerune.game.world.entity.actor.movement.waypoint.WalkToWaypoint;
import io.battlerune.game.world.entity.actor.movement.waypoint.Waypoint;
import io.battlerune.game.world.entity.actor.npc.Npc;
import io.battlerune.game.world.entity.actor.npc.definition.NpcDefinition;
import io.battlerune.game.world.entity.actor.player.ForceMovement;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.actor.player.relations.ChatMessage;
import io.battlerune.game.world.entity.actor.prayer.PrayerBook;
import io.battlerune.game.world.entity.skill.Skill;
import io.battlerune.game.world.entity.skill.SkillManager;
import io.battlerune.game.world.object.GameObject;
import io.battlerune.game.world.position.Position;
import io.battlerune.net.packet.out.SendPoison;
import io.battlerune.util.MutableNumber;
import io.battlerune.util.Utility;
import io.battlerune.util.generic.BooleanInterface;
import io.battlerune.util.generic.GenericAttributes;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static io.battlerune.game.world.entity.combat.CombatConstants.EMPTY_BONUSES;

/**
 * Handles the actor class.
 * @author Daniel
 * @author Chex
 */
public abstract class Actor extends Entity {
	
	public boolean brutalMode;
	private int listIndex;
	public int id = -1;
	private int transformId;
	private boolean dead;
	public boolean regionChange;
	public boolean positionChange;
	public boolean forceWalking;
	public boolean teleporting;
	public boolean inTeleport;
	public boolean teleportRegion;
	public boolean blockFace;
	public String forceChat;
	public Actor interactingWith;
	public Hit firstHit;
	public Hit secondHit;
	public Position lastPosition;
	public Position teleportTarget;
	public Position facePosition;
	public Activity activity;
	private Optional<Animation> animation = Optional.empty();
	private Optional<Graphic> graphic = Optional.empty();
	public List<Actor> followers = new LinkedList<>();
	public ForceMovement forceMovement;
	public final EnumSet<UpdateFlag> updateFlags = EnumSet.noneOf(UpdateFlag.class);
	public final GenericAttributes attributes = new GenericAttributes();
	public final SkillManager skills = new SkillManager(this);
	public final Movement movement = new Movement(this);
	public ActorAnimation actorAnimation = new ActorAnimation(updateFlags);
	public ActionManager action = new ActionManager();
	protected Waypoint cachedWaypoint;
	public PrayerBook prayer = new PrayerBook();
	private int[] bonuses = EMPTY_BONUSES;
	private final MutableNumber poisonDamage = new MutableNumber();
	private final MutableNumber venomDamage = new MutableNumber();
	public final Locking locking = new Locking(this);
	private PoisonType poisonType;
	public boolean inBattleRealm = false;
	public boolean abortBot = true;
	
	/**
	 * Constructs a new <code>Actor</code>.
	 */
	public Actor(Position position) {
		super(position);
		this.lastPosition = position.copy();
	}
	
	public Actor(Position position, boolean visible) {
		super(position, visible);
		this.lastPosition = position.copy();
	}
	
	/**
	 * Sets the actor's forced chat.
	 */
	public void speak(String forceChat) {
		if(forceChat == null || forceChat.isEmpty() || forceChat.length() > ChatMessage.CHARACTER_LIMIT)
			return;
		this.forceChat = forceChat;
		this.updateFlags.add(UpdateFlag.FORCED_CHAT);
	}
	
	public void animate(int animation) {
		animate(new Animation(animation));
	}
	
	/**
	 * Plays an animation.
	 */
	public void animate(Animation animation) {
		animate(animation, false);
	}
	
	public void graphic(int graphic) {
		graphic(new Graphic(graphic));
	}
	
	/**
	 * Plays a graphic.
	 */
	public void graphic(Graphic graphic) {
		graphic(graphic, false);
	}
	
	/**
	 * Plays an animation.
	 */
	public void animate(Animation animation, boolean override) {
		Optional<Animation> result = Optional.ofNullable(animation);
		animation = result.orElse(Animation.RESET);
		
		if(!this.animation.isPresent() || override || this.animation.get().compareTo(animation) > 0) {
			this.animation = result;
			this.updateFlags.add(UpdateFlag.ANIMATION);
		}
	}
	
	/**
	 * Plays a graphic.
	 */
	public void graphic(Graphic graphic, boolean override) {
		Optional<Graphic> result = Optional.ofNullable(graphic);
		graphic = result.orElse(Graphic.RESET);
		
		if(!this.graphic.isPresent() || override || this.graphic.get().compareTo(graphic) > 0) {
			this.graphic = result;
			this.updateFlags.add(UpdateFlag.GRAPHICS);
		}
	}
	
	/**
	 * Transforms the actor.
	 */
	public void transform(int transformId) {
		this.transformId = transformId;
		this.id = transformId;
		this.updateFlags.add(UpdateFlag.TRANSFORM);
		this.updateFlags.add(UpdateFlag.APPEARANCE);
		
		if(isNpc()) {
			NpcDefinition definition = NpcDefinition.get(id);
			getNpc().definition = definition;
			setWidth(definition.getSize());
			setLength(definition.getSize());
			setBonuses(definition.getBonuses());
			actorAnimation.setNpcAnimations(definition);
		}
	}
	
	/**
	 * Resets the actor after an update.
	 */
	public final void reset() {
		resetAnimation();
		resetGraphic();
	}
	
	/**
	 * Resets the waypoint
	 */
	public final void resetWaypoint() {
		if(cachedWaypoint != null && cachedWaypoint.isRunning()) {
			cachedWaypoint.cancel();
		}
	}
	
	public void forceMove(int delay, int animation, int startSpeed, int endSpeed, Position offset, Direction direction) {
		forceMove(delay, 0, animation, startSpeed, endSpeed, offset, direction);
	}
	
	public void forceMove(int delay, int delay2, int animation, int startSpeed, int endSpeed, Position offset, Direction direction) {
		forceMove(delay, delay2, animation, 0, startSpeed, endSpeed, offset, direction);
	}
	
	/**
	 * Creates a force movement action for an entity.
	 */
	public void forceMove(int delay, int delay2, int animation, int animationDelay, int startSpeed, int endSpeed, Position offset, Direction direction) {
		ForceMovement movement = new ForceMovement(getPosition(), offset, startSpeed, endSpeed, direction);
		World.schedule(new ForceMovementTask(this, delay, delay2, movement, new Animation(animation, animationDelay)));
	}
	
	/**
	 * Sets the actor interacting with another actor.
	 */
	public void interact(Actor actor) {
		if(blockFace)
			return;
		this.interactingWith = actor;
		this.updateFlags.add(UpdateFlag.INTERACT);
	}
	
	/**
	 * Sets the client update flag to face a certain direction.
	 */
	public void face(GameObject object) {
		if(blockFace)
			return;
		if(object == null || object.getPosition().equals(facePosition))
			return;
		this.facePosition = object.getPosition();
		this.updateFlags.add(UpdateFlag.FACE_COORDINATE);
	}
	
	public void face(Actor actor) {
		face(actor.getPosition());
	}
	
	/**
	 * Sets the client update flag to face a certain direction.
	 */
	public void face(Position position) {
		if(blockFace)
			return;
		if(!position.equals(facePosition)) {
			this.facePosition = position;
			this.updateFlags.add(UpdateFlag.FACE_COORDINATE);
		}
	}
	
	/**
	 * Sets the client update flag to face a certain direction.
	 */
	public void face(Direction direction) {
		if(blockFace)
			return;
		Position position = getPosition().transform(direction.getFaceLocation());
		if(!position.equals(facePosition)) {
			this.facePosition = position;
			this.updateFlags.add(UpdateFlag.FACE_COORDINATE);
		}
	}
	
	/**
	 * Resets the actor's face location.
	 */
	public void resetFace() {
		if(blockFace || interactingWith == null)
			return;
		interactingWith = null;
		this.updateFlags.add(UpdateFlag.INTERACT);
	}
	
	/**
	 * Moves the actor to a set position.
	 */
	public void move(Position position) {
		if(regionChange)
			return;
		if(isPlayer() && !getPlayer().interfaceManager.isClear())
			getPlayer().interfaceManager.close(false);
		setPosition(position);
		if(Utility.isRegionChange(position, lastPosition)) {
			regionChange = true;
		} else {
			positionChange = true;
		}
		teleportRegion = true;
		getCombat().reset();
		resetFace();
		locking.lock(599, TimeUnit.MILLISECONDS, LockType.MASTER);
		onStep();
	}
	
	public void walk(Position position) {
		walk(position, false);
	}
	
	public void walk(Position destination, boolean ignoreClip) {
		if(ignoreClip) {
			movement.walk(destination);
		} else {
			movement.simplePath(destination);
		}
	}
	
	public void runTo(Position destination) {
		movement.dijkstraPath(destination);
	}
	
	public void walkTo(Position position) {
		getCombat().reset();
		walkTo(position, () -> {
			/* Do nothing on arrival */
		});
	}
	
	public void walkTo(Position position, Runnable onDestination) {
		Interactable interactable = Interactable.create(position);
		walkTo(interactable, onDestination);
	}
	
	public void walkExactlyTo(Position position) {
		walkExactlyTo(position, () -> {
		});
	}
	
	public void walkExactlyTo(Position position, Runnable onDestination) {
		Interactable interactable = Interactable.create(position, 0, 0);
		walkTo(interactable, onDestination);
	}
	
	public void walkTo(Interactable target, Runnable onDestination) {
		walkTo(target, true, onDestination);
	}
	
	public void walkTo(Interactable target, boolean clearAction, Runnable onDestination) {
		Waypoint waypoint = new WalkToWaypoint(this, target, onDestination);
		
		if(cachedWaypoint == null || (!cachedWaypoint.isRunning() || !waypoint.equals(cachedWaypoint))) {
			resetWaypoint();
			getCombat().reset();
			movement.reset();
			
			if(clearAction) {
				action.clearNonWalkableActions();
			}
			
			World.schedule(cachedWaypoint = waypoint);
		}
	}
	
	public void follow(Actor target) {
		Waypoint waypoint = new FollowWaypoint(this, target);
		if(cachedWaypoint == null || (!cachedWaypoint.isRunning() || !waypoint.equals(cachedWaypoint))) {
			resetWaypoint();
			movement.reset();
			action.clearNonWalkableActions();
			World.schedule(cachedWaypoint = waypoint);
		}
	}
	
	public void attack(Actor target) {
		Waypoint waypoint = new CombatWaypoint(this, target);
		if(cachedWaypoint == null || (!cachedWaypoint.isRunning() || !waypoint.equals(cachedWaypoint))) {
			resetWaypoint();
			movement.reset();
			action.clearNonWalkableActions();
			World.schedule(cachedWaypoint = waypoint);
		}
	}
	
	protected void setWaypoint(Waypoint waypoint) {
		if(cachedWaypoint == null || (!cachedWaypoint.isRunning() || !waypoint.equals(cachedWaypoint))) {
			resetWaypoint();
			movement.reset();
			action.clearNonWalkableActions();
			World.schedule(cachedWaypoint = waypoint);
		}
	}
	
	public void damage(Hit... hits) {
		for(Hit hit : hits)
			getCombat().queueDamage(hit);
	}
	
	public void writeFakeDamage(Hit hit) {
		if(!updateFlags.contains(UpdateFlag.FIRST_HIT)) {
			firstHit = hit;
			updateFlags.add(UpdateFlag.FIRST_HIT);
		} else {
			secondHit = hit;
			updateFlags.add(UpdateFlag.SECOND_HIT);
		}
	}
	
	public void writeDamage(Hit hit) {
		if(!updateFlags.contains(UpdateFlag.FIRST_HIT)) {
			firstHit = decrementHealth(hit);
			updateFlags.add(UpdateFlag.FIRST_HIT);
		} else {
			secondHit = decrementHealth(hit);
			updateFlags.add(UpdateFlag.SECOND_HIT);
		}
	}
	
	public Hit decrementHealth(Hit hit) {
		if(getCurrentHealth() - hit.getDamage() < 0)
			hit.modifyDamage(damage -> getCurrentHealth());
		skills.modifyLevel(level -> level - hit.getDamage(), Skill.HITPOINTS, 0, getCurrentHealth());
		skills.refresh(Skill.HITPOINTS);
		if(getCurrentHealth() < 1)
			appendDeath();
		
		return hit;
	}
	
	public void heal(int amount) {
		int health = getCurrentHealth();
		if(health >= getMaximumHealth())
			return;
		skills.modifyLevel(hp -> health + amount, Skill.HITPOINTS, 0, getMaximumHealth());
		skills.refresh(Skill.HITPOINTS);
	}
	
	/**
	 * Applies poison with an intensity of {@code type} to the entity.
	 */
	public void poison(PoisonType type) {
		poisonType = type;
		CombatUtil.effect(this, CombatEffectType.POISON);
	}
	
	/**
	 * Applies venom to the entity.
	 */
	public void venom() {
		CombatUtil.effect(this, CombatEffectType.VENOM);
	}
	
	public void setForceMovement(ForceMovement forceMovement) {
		this.forceMovement = forceMovement;
		if(forceMovement != null)
			this.updateFlags.add(UpdateFlag.FORCE_MOVEMENT);
	}
	
	public boolean inActivity() {
		return activity != null;
	}
	
	public boolean inActivity(ActivityType type) {
		return inActivity() && activity.getType() == type;
	}
	
	public void setActivity(Activity activity) {
		if(this.activity != null)
			this.activity.cleanup();
		this.activity = activity;
	}
	
	/**
	 * Resets the teleport target.
	 */
	public void clearTeleportTarget() {
		this.teleportTarget = null;
	}
	
	/**
	 * Checks if actor requires an update.
	 */
	public boolean isUpdateRequired() {
		return !updateFlags.isEmpty();
	}
	
	/**
	 * Check if an entity is an npc.
	 */
	public final boolean isNpc() {
		return getType() == EntityType.NPC;
	}
	
	/**
	 * Check if an entity is an npc.
	 */
	public final boolean isNpc(BooleanInterface<Npc> condition) {
		return getType() == EntityType.NPC && condition.activated(getNpc());
	}
	
	/**
	 * Check if an entity is a player
	 */
	public final boolean isPlayer() {
		return getType() == EntityType.PLAYER;
	}
	
	public final Npc getNpc() {
		return (Npc) this;
	}
	
	/**
	 * Check if an entity is a player
	 */
	public final boolean isPlayer(Function<Player, Boolean> condition) {
		return getType() == EntityType.PLAYER && condition.apply(getPlayer());
	}
	
	public ForceMovement getForceMovement() {
		return forceMovement;
	}
	
	public void unpoison() {
		poisonDamage.set(0);
		poisonType = null;
		
		if(this instanceof Player) {
			Player player = (Player) this;
			player.send(new SendPoison(SendPoison.PoisonType.NO_POISON));
		}
	}
	
	public void unvenom() {
		venomDamage.set(0);
		
		if(this instanceof Player) {
			Player player = (Player) this;
			player.send(new SendPoison(SendPoison.PoisonType.NO_POISON));
		}
	}
	
	public final boolean isPoisoned() {
		return poisonDamage.get() > 0;
	}
	
	public final boolean isVenomed() {
		return venomDamage.get() > 0;
	}
	
	public final MutableNumber getPoisonDamage() {
		return poisonDamage;
	}
	
	public MutableNumber getVenomDamage() {
		return venomDamage;
	}
	
	public PoisonType getPoisonType() {
		return poisonType;
	}
	
	public boolean isDead() {
		return dead;
	}
	
	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	public final Player getPlayer() {
		return (Player) this;
	}
	
	public int getCurrentHealth() {
		return skills.getLevel(Skill.HITPOINTS);
	}
	
	public int getMaximumHealth() {
		return skills.getMaxLevel(Skill.HITPOINTS);
	}
	
	public int[] getBonuses() {
		return bonuses;
	}
	
	public int getBonus(int index) {
		return bonuses[index];
	}
	
	public void setBonuses(int[] bonuses) {
		this.bonuses = bonuses;
	}
	
	public void appendBonus(int index, int amount) {
		if(bonuses == EMPTY_BONUSES)
			bonuses = new int[EMPTY_BONUSES.length];
		bonuses[index] += amount;
	}
	
	public void setBonus(int equipSlot, int bonus) {
		if(bonuses == EMPTY_BONUSES)
			bonuses = new int[EMPTY_BONUSES.length];
		bonuses[equipSlot] = bonus;
	}
	
	public int getListIndex() {
		return listIndex;
	}
	
	public void setListIndex(int listIndex) {
		this.listIndex = listIndex;
	}
	
	public Optional<Animation> getAnimation() {
		return animation;
	}
	
	public Optional<Graphic> getGraphic() {
		return graphic;
	}
	
	public void resetAnimation() {
		this.animation = Optional.empty();
	}
	
	public void resetGraphic() {
		this.graphic = Optional.empty();
	}
	
	/**
	 * The method which is invoked every tick.
	 */
	public abstract void sequence();
	
	/**
	 * State of the actor's auto retaliate.
	 */
	public abstract boolean isAutoRetaliate();
	
	/**
	 * Handles the actor death.
	 */
	protected abstract void appendDeath();
	
	/**
	 * The combat strategy of the actor.
	 */
	public abstract <T extends Actor> CombatStrategy<? super T> getStrategy();
	
	/**
	 * The combat of the actor.
	 */
	public abstract Combat<? extends Actor> getCombat();
	
	/* Calculates if you're within distance i of the position */
	public boolean near(Position calc, int i) {
		return Math.sqrt((Math.pow(calc.getX(), 2) - Math.pow(this.getX(), 2)) - (Math.pow(calc.getY(), 2) - Math.pow(this.getY(), 2))) < i;
	}
	
}