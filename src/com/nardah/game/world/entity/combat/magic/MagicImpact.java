package com.nardah.game.world.entity.combat.magic;

import com.nardah.game.world.entity.combat.attack.FormulaFactory;
import com.nardah.game.world.entity.combat.effect.CombatEffectType;
import com.nardah.util.RandomUtils;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.CombatImpact;
import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.CombatUtil;
import com.nardah.game.world.entity.combat.PoisonType;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.data.LockType;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.skill.Skill;

import java.util.List;

public enum MagicImpact {
	TELEBLOCK((attacker, defender, hit, extra) -> teleblock(defender)),
	
	BIND((attacker, defender, hit, extra) -> freeze(defender, 5)), SNARE(((attacker, defender, hit, extra) -> freeze(defender, 10))), ENTANGLE(((attacker, defender, hit, extra) -> freeze(defender, 15))),
	
	CONFUSE((attacker, defender, hit, extra) -> lowerSkill(defender, Skill.ATTACK, 5)), WEAKEN((attacker, defender, hit, extra) -> lowerSkill(defender, Skill.STRENGTH, 5)), CURSE((attacker, defender, hit, extra) -> lowerSkill(defender, Skill.DEFENCE, 5)),
	
	VULNERABILITY((attacker, defender, hit, extra) -> lowerSkill(defender, Skill.DEFENCE, 10)), ENFEEBLE((attacker, defender, hit, extra) -> lowerSkill(defender, Skill.STRENGTH, 10)), STUN((attacker, defender, hit, extra) -> lowerSkill(defender, Skill.ATTACK, 10)),
	
	MAGIC_DART((attacker, defender, hit, extra) -> {
		if(attacker.isPlayer() && hit.isAccurate()) {
			Player player = attacker.getPlayer();
			int damage = 10 + player.skills.getLevel(Skill.MAGIC) / 10;
			
			if(defender.isNpc() && player.slayer.getTask() != null) {
				Mob mob = defender.getNpc();
				if(player.slayer.getTask().valid(mob.id)) {
					damage = 13 + player.skills.getLevel(Skill.MAGIC) / 6;
				}
			}
			
			hit.setDamage(RandomUtils.inclusive(damage));
		}
	}),
	
	SMOKE_RUSH((attacker, defender, hit, extra) -> poison(attacker, defender, hit, PoisonType.DEFAULT_MAGIC)), SMOKE_BURST((attacker, defender, hit, extra) -> CombatUtil.areaAction(defender, other -> smokeBurst(attacker, defender, other, hit, extra))), SMOKE_BLITZ((attacker, defender, hit, extra) -> poison(attacker, defender, hit, PoisonType.SUPER_MAGIC)), SMOKE_BARRAGE((attacker, defender, hit, extra) -> CombatUtil.areaAction(defender, other -> smokeBarrage(attacker, defender, other, hit, extra))),
	
	SHADOW_RUSH((attacker, defender, hit, extra) -> lowerSkill(defender, Skill.ATTACK, 10)), SHADOW_BURST((attacker, defender, hit, extra) -> CombatUtil.areaAction(defender, other -> shadowBurst(attacker, defender, other, extra))), SHADOW_BLITZ((attacker, defender, hit, extra) -> lowerSkill(defender, Skill.ATTACK, 15)), SHADOW_BARRAGE((attacker, defender, hit, extra) -> CombatUtil.areaAction(defender, other -> shadowBarrage(attacker, defender, other, extra))),
	
	BLOOD_RUSH((attacker, defender, hit, extra) -> heal(attacker, hit)), BLOOD_BURST((attacker, defender, hit, extra) -> CombatUtil.areaAction(defender, other -> bloodBurst(attacker, defender, other, hit, extra))), BLOOD_BLITZ((attacker, defender, hit, extra) -> heal(attacker, hit)), BLOOD_BARRAGE((attacker, defender, hit, extra) -> CombatUtil.areaAction(defender, other -> bloodBarrage(attacker, defender, other, hit, extra))),
	
	ICE_RUSH((attacker, defender, hit, extra) -> freeze(defender, 5)), ICE_BURST((attacker, defender, hit, extra) -> CombatUtil.areaAction(defender, other -> iceBurst(attacker, defender, other, extra))), ICE_BLITZ((attacker, defender, hit, extra) -> freeze(defender, 15)), ICE_BARRAGE((attacker, defender, hit, extra) -> CombatUtil.areaAction(defender, other -> iceBarrage(attacker, defender, other, extra))),
	
	KBD_FREEZE((attacker, defender, hit, extra) -> freeze(defender, 5)), KBD_POISON((attacker, defender, hit, extra) -> poison(attacker, defender, hit, PoisonType.DEFAULT_NPC)), KBD_SHOCK((attacker, defender, hit, extra) -> kbdShock(defender)),
	
	AHRIM_BLAST((attacker, defender, hit, extra) -> {
		//        if (hit.isAccurate() && Math.random() < 0.20) {
		//            defender.skills.get(Skill.STRENGTH).removeLevel(5);
		//            defender.skills.refresh(Skill.STRENGTH);
		//        }
	});
	
	private final CombatImpact effect;
	
	MagicImpact(CombatImpact effect) {
		this.effect = new CombatImpact() {
			@Override
			public boolean canAffect(Actor attacker, Actor defender, Hit hit) {
				return hit.isAccurate() && effect.canAffect(attacker, defender, hit);
			}
			
			@Override
			public void impact(Actor attacker, Actor defender, Hit hit, List<Hit> hits) {
				effect.impact(attacker, defender, hit, hits);
			}
		};
	}
	
	public CombatImpact getEffect() {
		return effect;
	}
	
	private static void kbdShock(Actor defender) {
		if(!defender.isPlayer()) {
			return;
		}
		
		Player player = defender.getPlayer();
		int id = RandomUtils.inclusiveExcludes(Skill.ATTACK, Skill.MAGIC, Skill.HITPOINTS);
		Skill skill = player.skills.get(id);
		
		if(skill.getLevel() - 1 >= 0) {
			skill.removeLevel(1);
			player.skills.refresh(id);
		}
	}
	
	private static void lowerSkill(Actor defender, int id, int percentage) {
		if(!defender.isPlayer()) {
			return;
		}
		
		Player player = defender.getPlayer();
		Skill skill = player.skills.get(id);
		
		double ratio = percentage / 100.0;
		int limit = (int) (skill.getMaxLevel() * (1 - ratio));
		int amount = (int) (skill.getLevel() * ratio);
		
		if(skill.getLevel() - amount < limit) {
			amount = skill.getLevel() - limit;
		}
		
		if(amount > 0) {
			skill.removeLevel(amount);
			player.skills.refresh(id);
		}
	}
	
	private static void poison(Actor attacker, Actor defender, Hit hit, PoisonType type) {
		if(!hit.isAccurate() || defender.isPoisoned()) {
			return;
		}
		
		if(!attacker.isNpc() && hit.getDamage() < 1) {
			return;
		}
		
		defender.poison(type);
	}
	
	private static void freeze(Actor defender, int timer) {
		if(!defender.locking.locked()) {
			defender.locking.lock(timer, LockType.FREEZE);
		}
		defender.getCombat().reset();
		defender.movement.reset();
	}
	
	private static void heal(Actor attacker, Hit hit) {
		Skill skill = attacker.skills.get(Skill.HITPOINTS);
		
		if(skill.getLevel() < skill.getMaxLevel()) {
			float heal = hit.getDamage() / 40f;
			
			if(heal + skill.getLevel() > skill.getMaxLevel()) {
				heal = skill.getMaxLevel() - skill.getLevel();
			}
			
			attacker.heal((int) (heal * 10));
		}
	}
	
	private static void smokeBurst(Actor attacker, Actor defender, Actor other, Hit hit, List<Hit> extra) {
		poison(attacker, defender, hit, PoisonType.DEFAULT_MAGIC);
		CombatHit next = hitEvent(attacker, defender, other, 18, extra);
		if(next != null && (attacker.isNpc() || (next.isAccurate() && next.getDamage() > 0))) {
			poison(attacker, other, hit, PoisonType.DEFAULT_MAGIC);
		}
	}
	
	private static void smokeBarrage(Actor attacker, Actor defender, Actor other, Hit hit, List<Hit> extra) {
		poison(attacker, defender, hit, PoisonType.SUPER_MAGIC);
		CombatHit next = hitEvent(attacker, defender, other, 27, extra);
		if(next != null && (attacker.isNpc() || (next.isAccurate() && next.getDamage() > 0))) {
			poison(attacker, other, hit, PoisonType.SUPER_MAGIC);
		}
	}
	
	private static void shadowBurst(Actor attacker, Actor defender, Actor other, List<Hit> extra) {
		lowerSkill(defender, Skill.ATTACK, 10);
		CombatHit next = hitEvent(attacker, defender, other, 19, extra);
		if(next != null && next.isAccurate()) {
			lowerSkill(other, Skill.ATTACK, 10);
		}
	}
	
	private static void shadowBarrage(Actor attacker, Actor defender, Actor other, List<Hit> extra) {
		lowerSkill(defender, Skill.ATTACK, 15);
		CombatHit next = hitEvent(attacker, defender, other, 28, extra);
		if(next != null && next.isAccurate()) {
			lowerSkill(other, Skill.ATTACK, 15);
		}
	}
	
	private static void bloodBurst(Actor attacker, Actor defender, Actor other, Hit hit, List<Hit> extra) {
		heal(attacker, hit);
		CombatHit next = hitEvent(attacker, defender, other, 21, extra);
		if(next != null && next.isAccurate()) {
			heal(attacker, next);
		}
	}
	
	private static void bloodBarrage(Actor attacker, Actor defender, Actor other, Hit hit, List<Hit> extra) {
		heal(attacker, hit);
		CombatHit next = hitEvent(attacker, defender, other, 29, extra);
		if(next != null && next.isAccurate()) {
			heal(attacker, next);
		}
	}
	
	private static void iceBurst(Actor attacker, Actor defender, Actor other, List<Hit> extra) {
		freeze(defender, 10);
		CombatHit next = hitEvent(attacker, defender, other, 22, extra);
		if(next != null && next.isAccurate()) {
			freeze(other, 10);
		}
	}
	
	private static void iceBarrage(Actor attacker, Actor defender, Actor other, List<Hit> extra) {
		freeze(defender, 20);
		CombatHit next = hitEvent(attacker, defender, other, 28, extra);
		if(next != null && next.isAccurate()) {
			freeze(other, 20);
		}
	}
	
	private static void teleblock(Actor defender) {
		if(defender.isPlayer()) {
			CombatUtil.effect(defender, CombatEffectType.TELEBLOCK);
		}
	}
	
	private static CombatHit hitEvent(Actor attacker, Actor defender, Actor other, int max, List<Hit> extra) {
		if(!CombatUtil.canBasicAttack(attacker, other)) {
			return null;
		}
		if(!attacker.equals(other) && !defender.equals(other)) {
			int hitDelay = CombatUtil.getHitDelay(attacker, defender, CombatType.MAGIC);
			CombatHit hit = new CombatHit(FormulaFactory.nextMagicHit(attacker, other, max), hitDelay, 0);
			attacker.getCombat().submitHits(other, hit);
			if(extra != null)
				extra.add(hit);
			return hit;
		}
		return null;
	}
	
}
