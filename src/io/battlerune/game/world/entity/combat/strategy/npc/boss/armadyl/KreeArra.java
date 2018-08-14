package io.battlerune.game.world.entity.combat.strategy.npc.boss.armadyl;

import io.battlerune.game.Animation;
import io.battlerune.game.UpdatePriority;
import io.battlerune.game.world.entity.combat.CombatType;
import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.hit.HitIcon;
import io.battlerune.game.world.entity.combat.projectile.CombatProjectile;
import io.battlerune.game.world.entity.combat.strategy.CombatStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.MultiStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcRangedStrategy;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.npc.Npc;

import static io.battlerune.game.world.entity.combat.CombatUtil.createStrategyArray;
import static io.battlerune.game.world.entity.combat.CombatUtil.randomStrategy;
import static io.battlerune.game.world.entity.combat.strategy.player.PlayerMagicStrategy.SPLASH;

public class KreeArra extends MultiStrategy {
	private static final Ranged RANGED = new Ranged();
	private static final Magic MAGIC = new Magic();
	private static final Melee MELEE = new Melee();
	
	private boolean melee;
	
	private static final CombatStrategy<Npc>[] NON_MELEE = createStrategyArray(MAGIC, RANGED);
	
	public KreeArra() {
		currentStrategy = MELEE;
	}
	
	@Override
	public boolean canOtherAttack(Actor attacker, Npc defender) {
		if(attacker.isPlayer() && attacker.getStrategy().getCombatType().equals(CombatType.MELEE)) {
			attacker.getPlayer().message("You can't attack Armadyl with melee!");
			return false;
		}
		return super.canOtherAttack(attacker, defender);
	}
	
	@Override
	public boolean canAttack(Npc attacker, Actor defender) {
		boolean isTarget = attacker.equals(defender.getCombat().getDefender());
		if(!melee && !isTarget) {
			currentStrategy = MELEE;
			melee = true;
		} else if(isTarget)
			melee = false;
		return super.canAttack(attacker, defender);
	}
	
	@Override
	public boolean withinDistance(Npc attacker, Actor defender) {
		boolean isTarget = attacker.equals(defender.getCombat().getDefender());
		if(!melee && !isTarget) {
			currentStrategy = MELEE;
			melee = true;
		} else if(isTarget)
			melee = false;
		return super.withinDistance(attacker, defender);
	}
	
	@Override
	public void finishOutgoing(Npc attacker, Actor defender) {
		super.finishOutgoing(attacker, defender);
		boolean isTarget = attacker.equals(defender.getCombat().getDefender());
		if(!melee && !isTarget) {
			currentStrategy = MELEE;
			melee = true;
		} else {
			currentStrategy = randomStrategy(NON_MELEE);
			if(isTarget)
				melee = false;
		}
	}
	
	@Override
	public int getAttackDelay(Npc attacker, Actor defender, FightType fightType) {
		return attacker.definition.getAttackDelay();
	}
	
	private static class Ranged extends NpcRangedStrategy {
		public Ranged() {
			super(CombatProjectile.getDefinition("Armadyl ranged"));
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			return new CombatHit[]{nextRangedHit(attacker, defender, 71)};
		}
	}
	
	private static class Magic extends NpcMagicStrategy {
		public Magic() {
			super(CombatProjectile.getDefinition("Armadyl magic"));
		}
		
		@Override
		public void hit(Npc attacker, Actor defender, Hit hit) {
			if(hit.isAccurate())
				super.hit(attacker, defender, hit);
			else
				defender.graphic(SPLASH);
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			CombatHit hit = nextRangedHit(attacker, defender, 21);
			hit.setIcon(HitIcon.MAGIC);
			return new CombatHit[]{hit};
		}
	}
	
	private static class Melee extends NpcMeleeStrategy {
		private static final Animation ANIMATION = new Animation(6981, UpdatePriority.HIGH);
		
		@Override
		public Animation getAttackAnimation(Npc attacker, Actor defender) {
			return ANIMATION;
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			return new CombatHit[]{nextMeleeHit(attacker, defender, 26)};
		}
	}
	
}
