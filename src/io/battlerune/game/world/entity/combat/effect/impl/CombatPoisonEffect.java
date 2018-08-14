package io.battlerune.game.world.entity.combat.effect.impl;

import io.battlerune.game.world.entity.combat.PoisonType;
import io.battlerune.game.world.entity.combat.effect.CombatEffect;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.hit.HitIcon;
import io.battlerune.game.world.entity.combat.hit.Hitsplat;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.npc.definition.NpcDefinition;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.items.Item;
import io.battlerune.game.world.items.containers.equipment.Equipment;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.net.packet.out.SendPoison;

import java.util.Optional;

/**
 * The combat effect applied when a character needs to be poisoned.
 * @author lare96 <http://github.com/lare96>
 */
public final class CombatPoisonEffect extends CombatEffect {
	
	/**
	 * The amount of times this player has been hit.
	 */
	private int amount;
	
	/**
	 * Creates a new {@link CombatPoisonEffect}.
	 */
	public CombatPoisonEffect() {
		super(25);
	}
	
	@Override
	public boolean apply(Actor actor) {
		if(actor.getPoisonType() == null || actor.isPoisoned() || actor.isVenomed()) {
			return false;
		}
		
		if(actor.isNpc() && actor.getNpc().definition.hasPoisonImmunity()) {
			return false;
		}
		
		if(actor.isPlayer() && actor.getPlayer().equipment.retrieve(Equipment.HELM_SLOT).filter(helm -> helm.getId() == 13197 || helm.getId() == 13199 || helm.getId() == 12931).isPresent()) {
			return true;
		} // ADAM INCASE THIS DOESN'T WORK REFER BACK TO HERE.
		
		if(actor.isPlayer()) {
			Player player = actor.getPlayer();
			if(player.getPoisonImmunity().get() > 0 || actor.isDead())
				return false;
			player.send(new SendMessage("You have been poisoned!"));
			player.send(new SendPoison(SendPoison.PoisonType.REGULAR));
		}
		actor.getPoisonDamage().set(actor.getPoisonType().getDamage());
		amount = 4;
		return true;
	}
	
	@Override // removed the !actor.isPoisoned(); ADAM DID THIS :d
	public boolean removeOn(Actor actor) {
		boolean remove = actor.isVenomed() || /* !actor.isPoisoned() */!actor.isPoisoned() || actor.isDead();
		if(remove && actor.isPlayer()) {
			Player player = (Player) actor;
			player.send(new SendPoison(SendPoison.PoisonType.NO_POISON));
		}
		return remove;
	}
	
	@Override
	public void process(Actor actor) {
		amount--;
		actor.damage(new Hit(actor.getPoisonDamage().get(), Hitsplat.POISON, HitIcon.NONE));
		if(amount == 0) {
			amount = 4;
			actor.getPoisonDamage().decrementAndGet();
		}
	}
	
	@Override
	public boolean onLogin(Actor actor) {
		boolean poisoned = actor.isPoisoned();
		if(poisoned && actor.isPlayer()) {
			actor.getPlayer().send(new SendPoison(SendPoison.PoisonType.REGULAR));
		}
		return poisoned;
	}
	
	/**
	 * Gets the {@link PoisonType} for {@code item} wrapped in an optional. If a
	 * poison type doesn't exist for the item then an empty optional is returned.
	 * @param item the item to get the poison type for
	 * @return the poison type for this item wrapped in an optional, or an empty
	 * optional if no poison type exists
	 */
	public static Optional<PoisonType> getPoisonType(Item item) {
		if(item != null) {
			String name = item.getName();
			if(name.endsWith("(p)")) {
				return Optional.of(PoisonType.DEFAULT_MELEE);
			}
			if(name.endsWith("(p+)")) {
				return Optional.of(PoisonType.STRONG_MELEE);
			}
			if(name.endsWith("(p++)")) {
				return Optional.of(PoisonType.SUPER_MELEE);
			}
			if(name.endsWith("tentacle")) {
				return Optional.of(PoisonType.SUPER_MELEE);
			}
			
		}
		return Optional.empty();
	}
	
	/**
	 * Gets the {@link PoisonType} for {@code npc} wrapped in an optional. If a
	 * poison type doesn't exist for the NPC then an empty optional is returned.
	 * @param npc the NPC to get the poison type for
	 * @return the poison type for this NPC wrapped in an optional, or an empty
	 * optional if no poison type exists
	 */
	public static Optional<PoisonType> getPoisonType(int npc) {
		NpcDefinition def = NpcDefinition.DEFINITIONS[npc];
		
		if(def == null || !def.isAttackable() || !def.isPoisonous()) {
			return Optional.empty();
		}
		
		if(def.getCombatLevel() < 25) {
			return Optional.of(PoisonType.WEAK_NPC);
		}
		
		if(def.getCombatLevel() < 75) {
			return Optional.of(PoisonType.DEFAULT_NPC);
		}
		
		if(def.getCombatLevel() < 200) {
			return Optional.of(PoisonType.STRONG_NPC);
		}
		
		if(def.getCombatLevel() < 225) {
			return Optional.of(PoisonType.SUPER_NPC);
		}
		
		return Optional.of(PoisonType.EXTRAORDINARY_NPC);
	}
}
