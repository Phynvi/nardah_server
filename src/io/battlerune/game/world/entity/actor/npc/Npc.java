package io.battlerune.game.world.entity.actor.npc;

import io.battlerune.content.activity.Activity;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.EntityType;
import io.battlerune.game.world.entity.combat.Combat;
import io.battlerune.game.world.entity.combat.strategy.CombatStrategy;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.Direction;
import io.battlerune.game.world.entity.actor.npc.definition.NpcDefinition;
import io.battlerune.game.world.position.Area;
import io.battlerune.game.world.position.Position;
import io.battlerune.game.world.region.Region;
import io.battlerune.util.RandomUtils;
import io.battlerune.util.Utility;
import io.battlerune.util.log.LogManager;
import io.battlerune.util.log.Logger;
import io.battlerune.util.log.LoggerType;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a non-player character in the in-game world.
 * @author Daniel | Obey
 * @author Michael | Chex
 */
public class Npc extends Actor {
	
	private static final Logger npcSequenceLogger = LogManager.getLogger(LoggerType.UPDATING);
	private int sequence;
	public boolean walk;
	public boolean attackNpc;
	public Actor owner;
	public final Direction faceDirection;
	public final Position spawnPosition;
	public NpcDefinition definition;
	private CombatStrategy<Npc> strategy;
	public final NpcAssistant npcAssistant = new NpcAssistant(this);
	private final Combat<Npc> combat = new Combat<>(this);
	private int walkCooldown;
	public Position[] boundaries;
	private int walkingRadius;
	
	/**
	 * The amount of players in this viewport, this is used for npcs
	 **/
	public final AtomicInteger atomicPlayerCount = new AtomicInteger(0);
	
	public Npc(int id, Position position, int walkingRadius, Direction direction) {
		super(position);
		this.id = id;
		this.faceDirection = direction;
		this.spawnPosition = position.copy();
		this.walkingRadius = walkingRadius;
	}
	
	public Npc(int id, Position position, int walkingRadius) {
		this(id, position, walkingRadius, Direction.SOUTH);
	}
	
	public Npc(int id, Position position, Direction direction) {
		this(id, position, 0, direction);
	}
	
	public Npc(Actor entity, int id, Position position) {
		this(id, position, 0, Direction.SOUTH);
		owner = entity;
	}
	
	public Npc(int id, Position position) {
		this(id, position, 0, Direction.SOUTH);
	}
	
	@Override
	public void setPosition(Position position) {
		//        Region.removeMobOnTile(width(), getPosition());
		super.setPosition(position);
		//        Region.setMobOnTile(width(), position);
	}
	
	@Override
	public void sequence() {
		try {
			action.sequence();
			
			if(sequence % 110 == 0) {
				for(int index = 0; index <= 6; index++) {
					skills.regress(index);
				}
			}
			
			if(definition.isAttackable() && (definition.isAggressive() || Area.inWilderness(this))) {
				getCombat().checkAggression(definition.getCombatLevel(), spawnPosition);
			}
			
			if(combat.getDefender() == null && !combat.inCombat() && walk) {
				if(walkCooldown > 0) {
					walkCooldown--;
				} else {
					Position target = Utility.randomElement(boundaries);
					walk(target);
					walkCooldown = RandomUtils.inclusive(10, 30);
				}
			}
			
			if(definition.isAttackable()) {
				getCombat().tick();
			}
		} catch(Exception ex) {
			npcSequenceLogger.info("Error sequencing " + this);
			ex.printStackTrace();
		}
		sequence++;
	}
	
	@Override
	public void register() {
		if(!isRegistered() && !World.getNpcs().contains(this)) {
			int w = walkingRadius * width();
			int l = walkingRadius * length();
			
			walk = walkingRadius != 0;
			boundaries = Utility.getInnerBoundaries(spawnPosition.transform(-w, -l), w * 2, l * 2);
			setRegistered(World.getNpcs().add(this));
			setPosition(getPosition());
			npcAssistant.login();
		}
	}
	
	@Override
	public void unregister() {
		if(isRegistered()) {
			World.getNpcs().remove((Npc) destroy());
		}
	}
	
	@Override
	public void addToRegion(Region region) {
		region.addNpc(this);
	}
	
	@Override
	public void removeFromRegion(Region region) {
		region.removeNpc(this);
	}
	
	@Override
	public void onStep() {
	}
	
	@Override
	public void appendDeath() {
		if(inActivity()) {
			Activity.forActivity(this, activity -> activity.onDeath(this));
			return;
		}
		World.schedule(new NpcDeath(this));
	}
	
	@Override
	public Combat<Npc> getCombat() {
		return combat;
	}
	
	@Override
	public CombatStrategy<Npc> getStrategy() {
		return strategy;
	}
	
	@Override
	public String getName() {
		return definition == null ? "Unknown" : definition.getName();
	}
	
	@Override
	public int getBonus(int index) {
		return definition.getBonuses()[index];
	}
	
	@Override
	public int[] getBonuses() {
		return definition.getBonuses();
	}
	
	@Override
	public EntityType getType() {
		return EntityType.NPC;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(getIndex(), definition);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Npc) {
			Npc other = (Npc) obj;
			return id == other.id && getIndex() == other.getIndex() && spawnPosition.equals(other.spawnPosition);
		}
		return obj == this;
	}
	
	@Override
	public String toString() {
		return String.format("Npc[index=%d id=%d %s]", getIndex(), id, getPosition());
	}
	
	int getDeathTime() {
		return definition.getDeathTimer();
	}
	
	public boolean isAutoRetaliate() {
		return definition.isRetaliate();
	}
	
	public void setStrategy(CombatStrategy<Npc> strategy) {
		this.strategy = strategy;
	}
	
}
