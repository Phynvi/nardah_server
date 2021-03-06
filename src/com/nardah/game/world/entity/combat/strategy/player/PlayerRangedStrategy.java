package com.nardah.game.world.entity.combat.strategy.player;

import com.nardah.content.prestige.PrestigePerk;
import com.nardah.game.world.position.Area;
import com.nardah.game.world.position.Position;
import com.nardah.util.RandomUtils;
import com.nardah.game.Animation;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.combat.CombatImpact;
import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.effect.impl.CombatPoisonEffect;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.ranged.RangedAmmunition;
import com.nardah.game.world.entity.combat.ranged.RangedWeaponType;
import com.nardah.game.world.entity.combat.strategy.basic.RangedStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.items.containers.equipment.Equipment;
import com.nardah.game.world.items.ground.GroundItem;
import com.nardah.net.packet.out.SendMessage;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class PlayerRangedStrategy extends RangedStrategy<Player> {
	
	private static final PlayerRangedStrategy INSTANCE = new PlayerRangedStrategy();
	private int hasRecursed = 0;
	
	protected PlayerRangedStrategy() {
	}
	
	@Override
	public boolean canAttack(Player attacker, Actor defender) {
		Item weapon = attacker.equipment.get(Equipment.WEAPON_SLOT);
		
		if(weapon == null) {
			attacker.getCombat().reset();
			return false;
		}
		
		if(attacker.rangedDefinition == null && hasRecursed == 0) {
			hasRecursed = 1;
			System.out.println("RangedDef is null, updating");
			attacker.equipment.updateRangedEquipment();
			return canAttack(attacker, defender);
		}
		
		hasRecursed = 0;
		
		Item ammo = attacker.equipment.get(attacker.rangedDefinition.getSlot());
		if(ammo != null && attacker.rangedAmmo != null && ammo.getAmount() >= attacker.rangedAmmo.getRemoval()) {
			if(attacker.rangedDefinition.isValid(attacker.rangedAmmo)) {
				return true;
			}
			attacker.send(new SendMessage("You can't use this ammunition with this weapon."));
		} else {
			attacker.send(new SendMessage("You need some ammunition to use this weapon!"));
		}
		
		attacker.getCombat().reset();
		return false;
	}
	
	protected void sendStuff(Player attacker, Actor defender) {
		int id = attacker.equipment.get(attacker.rangedDefinition.getSlot()).getId();
		Animation animation = attacker.rangedAmmo.getAnimation(id).orElse(getAttackAnimation(attacker, defender));
		attacker.animate(animation);
		attacker.rangedAmmo.getStart(id).ifPresent(attacker::graphic);
		attacker.rangedAmmo.sendProjectile(attacker, defender);
	}
	
	@Override
	public void start(Player attacker, Actor defender, Hit[] hits) {
		if(attacker.isSpecialActivated()) {
			attacker.getCombatSpecial().drain(attacker);
		}
		
		if(attacker.getCombat().getDefender() == defender) {
			sendStuff(attacker, defender);
			
			int id = attacker.equipment.get(attacker.rangedDefinition.getSlot()).getId();
			if(attacker.rangedAmmo.getEffect(id).isPresent()) {
				List<Hit> extra = new LinkedList<>();
				for(Hit hit : hits) {
					Predicate<CombatImpact> filter = effect -> effect.canAffect(attacker, defender, hit);
					Consumer<CombatImpact> execute = effect -> effect.impact(attacker, defender, hit, extra);
					attacker.rangedAmmo.getEffect(id).filter(filter).ifPresent(execute);
				}
				if(!defender.isPlayer() || !PlayerRight.isIronman(attacker)) {
					if(extra.isEmpty()) {
						Collections.addAll(extra, hits);
						addCombatExperience(attacker, extra.toArray(new Hit[0]));
					} else {
						addCombatExperience(attacker, hits);
					}
				}
			} else if(!defender.isPlayer() || !PlayerRight.isIronman(attacker)) {
				addCombatExperience(attacker, hits);
			}
		}
	}
	
	@Override
	public void attack(Player attacker, Actor defender, Hit hit) {
		removeAmmunition(attacker, defender, attacker.rangedDefinition.getType());
		if(hit.getDamage() > 1 && attacker.rangedDefinition != null) {
			Item item = attacker.equipment.get(attacker.rangedDefinition.getSlot());
			CombatPoisonEffect.getPoisonType(item).ifPresent(defender::poison);
		}
	}
	
	@Override
	public void hit(Player attacker, Actor defender, Hit hit) {
		if(attacker.rangedAmmo != null && attacker.rangedDefinition != null) {
			int id = attacker.equipment.retrieve(attacker.rangedDefinition.getSlot()).map(Item::getId).orElse(-1);
			attacker.rangedAmmo.getEnd(id).ifPresent(defender::graphic);
		}
	}
	
	@Override
	public Animation getAttackAnimation(Player attacker, Actor defender) {
		int animation = attacker.getCombat().getFightType().getAnimation();
		
		if(attacker.equipment.hasShield()) {
			Item weapon = attacker.equipment.getShield();
			FightType fightType = attacker.getCombat().getFightType();
			animation = weapon.getAttackAnimation(fightType).orElse(animation);
		}
		
		if(attacker.equipment.hasWeapon()) {
			Item weapon = attacker.equipment.getWeapon();
			FightType fightType = attacker.getCombat().getFightType();
			animation = weapon.getAttackAnimation(fightType).orElse(animation);
		}
		
		return new Animation(animation, UpdatePriority.HIGH);
	}
	
	@Override
	public int getAttackDelay(Player attacker, Actor defender, FightType fightType) {
		return attacker.getCombat().getFightType().getDelay();
	}
	
	@Override
	public int getAttackDistance(Player attacker, FightType fightType) {
		return fightType.getDistance();
	}
	
	@Override
	public CombatHit[] getHits(Player attacker, Actor defender) {
		RangedAmmunition ammo = attacker.rangedAmmo;
		CombatHit[] hits = new CombatHit[ammo.getRemoval()];
		for(int index = 0; index < hits.length; index++) {
			hits[index] = nextRangedHit(attacker, defender);
		}
		return hits;
	}
	
	@Override
	public CombatType getCombatType() {
		return CombatType.RANGED;
	}
	
	private void removeAmmunition(Player attacker, Actor defender, RangedWeaponType type) {
		Item next = attacker.equipment.get(type.getSlot());
		if(next == null)
			return;
		
		if(attacker.rangedAmmo.isDroppable()) {
			Item dropItem = new Item(next.getId());
			
			boolean canBreak = !dropItem.getName().contains("arrow") || !attacker.prestige.hasPerk(PrestigePerk.ARROWHEAD);
			
			if(!canBreak || RandomUtils.success(0.75)) {
				if(Equipment.hasAva(attacker) && RandomUtils.success(0.96)) {
					return;
				}
				
				Position dropPoisition = defender.getPosition();
				
				if(Area.inKraken(attacker) || Area.inZulrah(attacker)) {
					dropPoisition = attacker.getPosition();
				}
				
				GroundItem.create(attacker, dropItem, dropPoisition);
			}
		}
		
		next.decrementAmount();
		if(next.getAmount() == 0) {
			attacker.send(new SendMessage("That was the last of your ammunition!"));
			next = null;
		}
		attacker.equipment.set(type.getSlot(), next, true);
	}
	
	public static PlayerRangedStrategy get() {
		return INSTANCE;
	}
	
}
