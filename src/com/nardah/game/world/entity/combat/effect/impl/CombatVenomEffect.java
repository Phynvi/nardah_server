package com.nardah.game.world.entity.combat.effect.impl;

import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.effect.CombatEffect;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.hit.HitIcon;
import com.nardah.game.world.entity.combat.hit.Hitsplat;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.items.containers.equipment.Equipment;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.net.packet.out.SendPoison;

/**
 * The combat effect applied when a character needs to be venomed.
 * @author lare96 <http://github.com/lare96>
 */
public final class CombatVenomEffect extends CombatEffect {
	
	/**
	 * Creates a new {@link CombatVenomEffect}.
	 */
	public CombatVenomEffect() {
		super(30);
	}
	
	@Override
	public boolean apply(Actor actor) {
		if(actor.isVenomed()) {
			return false;
		}
		
		if(actor.isNpc() && actor.getNpc().definition.hasVenomImmunity()) {
			return false;
		}
		
		if(actor.isPlayer() && actor.getPlayer().equipment.retrieve(Equipment.HELM_SLOT).filter(helm -> helm.getId() == 13197 || helm.getId() == 13199 || helm.getId() == 12931).isPresent()) {
			return false;
		}
		
		if(actor.isPlayer()) {
			Player player = actor.getPlayer();
			if(player.getVenomImmunity().get() > 0 || actor.isDead())
				return false;
			player.send(new SendMessage("You have been venomed!"));
			player.send(new SendPoison(SendPoison.PoisonType.VENOM));
		}
		actor.getVenomDamage().set(6);
		return true;
	}
	
	@Override
	public boolean removeOn(Actor actor) {
		boolean remove = !actor.isVenomed() || actor.isDead();
		if(remove && actor.isPlayer()) {
			Player player = (Player) actor;
			player.send(new SendPoison(SendPoison.PoisonType.NO_POISON));
		}
		return remove;
	}
	
	@Override
	public void process(Actor actor) {
		if(actor.getVenomDamage().get() < 20)
			actor.damage(new Hit(actor.getVenomDamage().getAndIncrement(2), Hitsplat.DISEASE, HitIcon.NONE));
	}
	
	@Override
	public boolean onLogin(Actor actor) {
		if(actor.isVenomed() && actor.isPlayer()) {
			actor.getPlayer().send(new SendPoison(SendPoison.PoisonType.REGULAR));
		}
		return actor.isVenomed();
	}
	
	public static boolean isVenomous(Mob mob) {
		return mob.id == 2042 || mob.id == 2043 || mob.id == 2044;
	}
	
	/**
	 * Added a quick null check as the weapon was throwing errors and nulling out.
	 **/
	public static boolean isVenomous(Item weapon) {
		
		if(weapon == null) {
			return true;
		}
		
		return weapon.matchesId(12_926) || weapon.matchesId(12_904) || weapon.matchesId(12_899);
		
	}
	
}
