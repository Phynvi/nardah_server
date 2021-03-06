package com.nardah.game.world.entity.combat.strategy.player;

import com.nardah.util.RandomUtils;
import com.nardah.util.Utility;
import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.combat.CombatImpact;
import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.magic.CombatSpell;
import com.nardah.game.world.entity.combat.magic.MagicRune;
import com.nardah.game.world.entity.combat.strategy.basic.MagicStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.game.world.items.Item;
import com.nardah.net.packet.out.SendMessage;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.nardah.game.world.items.containers.equipment.Equipment.WEAPON_SLOT;

public class PlayerMagicStrategy extends MagicStrategy<Player> {
	
	/**
	 * The magic spell definition.
	 */
	private final CombatSpell spell;
	
	/**
	 * The spell splash graphic.
	 */
	public static final Graphic SPLASH = new Graphic(85);
	
	/**
	 * Constructs a new {@code SpellStrategy} from a {@link CombatSpell}.
	 */
	public PlayerMagicStrategy(CombatSpell spell) {
		this.spell = spell;
	}
	
	@Override
	public boolean canAttack(Player attacker, Actor defender) {
		if(defender.isPlayer() && defender.getPlayer().isBot) {
			attacker.message("You can't attack bots with magic.");
			return false;
		}
		
		if(spell == CombatSpell.TELE_BLOCK) {
			if(defender.isPlayer()) {
				if(defender.getPlayer().isTeleblocked()) {
					attacker.message("This player is already affected by this spell!");
					return false;
				}
			} else if(defender.isNpc()) {
				attacker.message("You can't teleblock a mob!");
				return false;
			}
		}
		
		if(/* PlayerRight.isDeveloper(attacker) || */ spell.canCast(attacker, defender)) {
			return true;
		}
		
		attacker.send(new SendMessage("You need some runes to cast this spell."));
		attacker.getCombat().reset();
		return false;
	}
	
	@Override
	public void start(Player attacker, Actor defender, Hit[] hits) {
		if(attacker.getCombat().getDefender() == defender) {
			Animation animation = spell.getAnimation().orElse(getAttackAnimation(attacker, defender));
			attacker.animate(animation);
			spell.getStart().ifPresent(attacker::graphic);
			spell.sendProjectile(attacker, defender);
			
			if(spell.getEffect().isPresent()) {
				List<Hit> extra = new LinkedList<>();
				for(Hit hit : hits) {
					Predicate<CombatImpact> filter = effect -> effect.canAffect(attacker, defender, hit);
					Consumer<CombatImpact> execute = effect -> effect.impact(attacker, defender, hit, extra);
					spell.getEffect().filter(filter).ifPresent(execute);
				}
				
				if(!defender.isPlayer() || !PlayerRight.isIronman(attacker)) {
					if(extra.isEmpty()) {
						Collections.addAll(extra, hits);
						addCombatExperience(attacker, spell.getBaseExperience(), extra.toArray(new Hit[0]));
					} else {
						addCombatExperience(attacker, spell.getBaseExperience(), hits);
					}
				} else {
					attacker.skills.addExperience(Skill.MAGIC, spell.getBaseExperience());
				}
			} else if(!defender.isPlayer() || !PlayerRight.isIronman(attacker)) {
				addCombatExperience(attacker, spell.getBaseExperience(), hits);
			} else {
				attacker.skills.addExperience(Skill.MAGIC, spell.getBaseExperience());
			}
		}
	}
	
	@Override
	public void attack(Player attacker, Actor defender, Hit hit) {
		if(defender.equals(attacker.getCombat().getDefender())) {
			MagicRune.remove(attacker, spell.getRunes());
		}
		
		if(attacker.isSingleCast()) {
			attacker.face(defender.getPosition());
			attacker.setSingleCast(null);
			attacker.getCombat().reset();
		}
	}
	
	@Override
	public void hit(Player attacker, Actor defender, Hit hit) {
		if(!hit.isAccurate()) {
			defender.graphic(SPLASH);
		} else {
			if(attacker.equipment.retrieve(WEAPON_SLOT).filter(item -> item.getId() == 12904).isPresent() && RandomUtils.success(0.25)) {
				defender.venom();
			}
			spell.getEnd().ifPresent(defender::graphic);
		}
	}
	
	@Override
	public void hitsplat(Player attacker, Actor defender, Hit hit) {
	}
	
	@Override
	public CombatHit[] getHits(Player attacker, Actor defender) {
		return new CombatHit[]{nextMagicHit(attacker, defender, spell.getCombatProjectile())};
	}
	
	@Override
	public int getAttackDelay(Player attacker, Actor defender, FightType fightType) {
		if(Utility.getDistance(attacker, defender) >= 8) {
			return 6;
		}
		return 5;
	}
	
	@Override
	public int getAttackDistance(Player attacker, FightType fightType) {
		return 10;
	}
	
	@Override
	public Animation getAttackAnimation(Player attacker, Actor defender) {
		FightType fightType = attacker.getCombat().getFightType();
		int animation = fightType.getAnimation();
		
		if(attacker.equipment.hasShield()) {
			Item weapon = attacker.equipment.getShield();
			animation = weapon.getAttackAnimation(fightType).orElse(animation);
		}
		
		if(attacker.equipment.hasWeapon()) {
			Item weapon = attacker.equipment.getWeapon();
			animation = weapon.getAttackAnimation(fightType).orElse(animation);
		}
		
		return new Animation(animation, UpdatePriority.HIGH);
	}
	
	/*
	 * @Override public int modifyAccuracy(Player attacker, Actor defender, int roll)
	 * { if (CombatUtil.isFullVoid(attacker)) { if
	 * (attacker.equipment.contains(11663)) { roll *= 1.45; } } return roll; }
	 */
	
	@Override
	public CombatType getCombatType() {
		return CombatType.MAGIC;
	}
	
	public CombatSpell getSpell() {
		return spell;
	}
}