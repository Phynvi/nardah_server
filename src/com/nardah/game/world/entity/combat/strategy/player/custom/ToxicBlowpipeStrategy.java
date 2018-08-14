package com.nardah.game.world.entity.combat.strategy.player.custom;

import com.nardah.game.world.position.Area;
import com.nardah.game.world.position.Position;
import com.nardah.util.RandomUtils;
import com.nardah.game.Animation;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.effect.impl.CombatPoisonEffect;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.ranged.RangedAmmunition;
import com.nardah.game.world.entity.combat.strategy.basic.RangedStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.items.containers.equipment.Equipment;
import com.nardah.game.world.items.ground.GroundItem;
import com.nardah.net.packet.out.SendMessage;

public class ToxicBlowpipeStrategy extends RangedStrategy<Player> {
	
	private static final ToxicBlowpipeStrategy INSTANCE = new ToxicBlowpipeStrategy();
	
	@Override
	public boolean canAttack(Player attacker, Actor defender) {
		Item weapon = attacker.equipment.get(Equipment.WEAPON_SLOT);
		
		if(weapon == null) {
			attacker.getCombat().reset();
			return false;
		}
		
		Item ammo = attacker.blowpipeDarts;
		
		if(ammo == null) {
			return false;
		}
		
		RangedAmmunition ammu = RangedAmmunition.find(null, ammo);
		if(ammu == null || !ammu.isDart()) {
			attacker.message("Your blowpipe is not using darts for ammunition!");
			return false;
		}
		
		if(ammo.getAmount() >= 1) {
			if(attacker.blowpipeScales >= 3) {
				return true;
			}
			attacker.send(new SendMessage("Your blowpipe requires more scales!"));
		} else {
			attacker.send(new SendMessage("You need some ammunition to use this weapon!"));
		}
		
		attacker.getCombat().reset();
		return false;
	}
	
	@Override
	public void start(Player attacker, Actor defender, Hit[] hits) {
		if(attacker.isSpecialActivated()) {
			attacker.getCombatSpecial().drain(attacker);
		}
		
		if(attacker.getCombat().getDefender() == defender) {
			attacker.animate(getAttackAnimation(attacker, defender));
			attacker.rangedAmmo.sendProjectile(attacker, defender);
			
			if(!defender.isPlayer() || !PlayerRight.isIronman(attacker)) {
				addCombatExperience(attacker, hits);
			}
		}
	}
	
	@Override
	public void attack(Player attacker, Actor defender, Hit hit) {
		Item darts = attacker.blowpipeDarts;
		removeAmmunition(attacker, defender);
		
		if(hit.getDamage() > 1) {
			if(RandomUtils.success(0.25)) {
				defender.venom();
			} else {
				CombatPoisonEffect.getPoisonType(darts).ifPresent(defender::poison);
			}
		}
	}
	
	@Override
	public Animation getAttackAnimation(Player attacker, Actor defender) {
		int animation = attacker.getCombat().getFightType().getAnimation();
		return new Animation(animation, UpdatePriority.HIGH);
	}
	
	@Override
	public int getAttackDelay(Player attacker, Actor defender, FightType fightType) {
		int delay = attacker.getCombat().getFightType().getDelay();
		if(defender.isNpc())
			return delay - 1;
		return delay;
	}
	
	@Override
	public int getAttackDistance(Player attacker, FightType fightType) {
		return fightType.getDistance();
	}
	
	@Override
	public CombatHit[] getHits(Player attacker, Actor defender) {
		return new CombatHit[]{nextRangedHit(attacker, defender)};
	}
	
	@Override
	public CombatType getCombatType() {
		return CombatType.RANGED;
	}
	
	private void removeAmmunition(Player attacker, Actor defender) {
		Item darts = attacker.blowpipeDarts;
		darts.decrementAmount();
		attacker.blowpipeScales -= 1.5f;
		
		if(RandomUtils.success(0.75)) {
			if(Equipment.hasAva(attacker) && RandomUtils.success(0.96)) {
				return;
			}
			
			Position dropPoisition = defender.getPosition();
			
			if(Area.inKraken(attacker) || Area.inZulrah(attacker)) {
				dropPoisition = attacker.getPosition();
			}
			
			GroundItem.create(attacker, darts.createWithAmount(1), dropPoisition);
		}
		
		if(darts.getAmount() == 0) {
			attacker.send(new SendMessage("That was the last of your ammunition!"));
			attacker.blowpipeDarts = null;
		}
		
		if(attacker.blowpipeScales == 0) {
			attacker.send(new SendMessage("Your blowpipe has run out of charges!"));
		}
	}
	
	public static ToxicBlowpipeStrategy get() {
		return INSTANCE;
	}
	
}
