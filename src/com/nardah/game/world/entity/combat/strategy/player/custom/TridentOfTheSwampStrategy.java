package com.nardah.game.world.entity.combat.strategy.player.custom;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.util.RandomUtils;
import com.nardah.Config;
import com.nardah.game.Animation;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.strategy.basic.MagicStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.items.containers.equipment.Equipment;
import com.nardah.net.packet.out.SendMessage;

public class TridentOfTheSwampStrategy extends MagicStrategy<Player> {
	private static final TridentOfTheSwampStrategy INSTANCE = new TridentOfTheSwampStrategy();
	private static CombatProjectile PROJECTILE;

	static {
		try {
			PROJECTILE = CombatProjectile.getDefinition("Trident of the Swamp");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean canAttack(Player attacker, Actor defender) {
		if(defender.isPlayer()) {
			attacker.send(new SendMessage("You can't attack players with this weapon."));
			return false;
		}

		Item weapon = attacker.equipment.get(Equipment.WEAPON_SLOT);

		if(weapon == null) {
			attacker.getCombat().reset();
			return false;
		}

		if(weapon.getId() == 12899 && attacker.tridentSwampCharges < 1) {
			attacker.send(new SendMessage("Your trident is out of charges!"));
			attacker.getCombat().reset();
			return false;
		}

		return true;
	}

	@Override
	public void start(Player attacker, Actor defender, Hit[] hits) {
		PROJECTILE.getAnimation().ifPresent(attacker::animate);
		PROJECTILE.getStart().ifPresent(attacker::graphic);
		PROJECTILE.getProjectile().ifPresent(projectile -> projectile.send(attacker, defender));

		if(!defender.isPlayer() || !PlayerRight.isIronman(attacker)) {
			for(Hit hit : hits) {
				int exp = 2 * hit.getDamage();
				attacker.skills.addExperience(Skill.MAGIC, exp * Config.COMBAT_MODIFICATION);
				attacker.skills.addExperience(Skill.HITPOINTS, exp / 3 * Config.COMBAT_MODIFICATION);
			}
		}
	}

	@Override
	public void attack(Player attacker, Actor defender, Hit hit) {
		attacker.tridentSwampCharges--;
	}

	@Override
	public void hit(Player attacker, Actor defender, Hit hit) {
		if(RandomUtils.success(0.25)) {
			defender.venom();
		}
		PROJECTILE.getEnd().ifPresent(defender::graphic);
	}

	@Override
	public Animation getAttackAnimation(Player attacker, Actor defender) {
		int animation = attacker.getCombat().getFightType().getAnimation();
		return new Animation(animation, UpdatePriority.HIGH);
	}

	@Override
	public int getAttackDelay(Player attacker, Actor defender, FightType fightType) {
		return attacker.getCombat().getFightType().getDelay();
	}

	@Override
	public int getAttackDistance(Player attacker, FightType fightType) {
		return 10;
	}

	@Override
	public CombatHit[] getHits(Player attacker, Actor defender) {
		int max = 23;
		int level = attacker.skills.getLevel(Skill.MAGIC);
		if(level - 75 > 0) {
			max += (level - 75) / 3;
		}
		return new CombatHit[]{nextMagicHit(attacker, defender, max, PROJECTILE)};
	}

	@Override
	public CombatType getCombatType() {
		return CombatType.MAGIC;
	}

	public static TridentOfTheSwampStrategy get() {
		return INSTANCE;
	}

}
