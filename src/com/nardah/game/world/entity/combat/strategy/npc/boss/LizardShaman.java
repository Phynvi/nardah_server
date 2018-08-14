package com.nardah.game.world.entity.combat.strategy.npc.boss;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.position.Position;
import com.nardah.util.Utility;
import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.UpdatePriority;
import com.nardah.game.task.Task;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.CombatUtil;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.hit.HitIcon;
import com.nardah.game.world.entity.combat.hit.Hitsplat;
import com.nardah.game.world.entity.combat.strategy.CombatStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.MultiStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import com.nardah.game.world.entity.actor.Actor;

import static com.nardah.game.world.entity.combat.CombatUtil.createStrategyArray;
import static com.nardah.game.world.entity.combat.CombatUtil.randomStrategy;

public class LizardShaman extends MultiStrategy {
	private static final MagicAttack MAGIC = new MagicAttack();
	private static final AcidAttack ACID = new AcidAttack();
	private static final MinionsAttack MINION = new MinionsAttack();
	private static final JumpAttack JUMP = new JumpAttack();
	private static final CombatStrategy<Mob>[] FULL_STRATEGIES = createStrategyArray(NpcMeleeStrategy.get(), ACID, MAGIC, MAGIC, MAGIC, MINION, JUMP);
	private static final CombatStrategy<Mob>[] MAGIC_STRATEGIES = createStrategyArray(ACID, MAGIC, MAGIC, MAGIC, MINION);
	private static final CombatStrategy<Mob>[] BASIC_STRATEGIES = createStrategyArray(ACID, MAGIC, MAGIC, MAGIC, JUMP);
	
	public LizardShaman() {
		currentStrategy = NpcMeleeStrategy.get();
	}
	
	@Override
	public boolean canAttack(Mob attacker, Actor defender) {
		if(NpcMeleeStrategy.get().canAttack(attacker, defender)) {
			currentStrategy = randomStrategy(FULL_STRATEGIES);
		} else {
			currentStrategy = randomStrategy(MAGIC_STRATEGIES);
		}
		
		boolean spawned = attacker.attributes.is("LIZARD_SPAWNED");
		
		if(currentStrategy == MINION && spawned) {
			currentStrategy = randomStrategy(BASIC_STRATEGIES);
		}
		
		return currentStrategy.canAttack(attacker, defender);
	}
	
	private static class JumpAttack extends NpcMagicStrategy {
		JumpAttack() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public void hit(Mob attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void block(Actor attacker, Mob defender, Hit hit, CombatType combatType) {
		}
		
		@Override
		public void start(Mob attacker, Actor defender, Hit[] hits) {
			Position position = defender.getPosition();
			attacker.animate(new Animation(7152, UpdatePriority.VERY_HIGH));
			
			World.schedule(new Task(1) {
				int count = 0;
				
				@Override
				protected void execute() {
					count++;
					
					if(count == 2) {
						attacker.setVisible(false);
					} else if(count == 3) {
						attacker.move(position);
					} else if(count == 4) {
						attacker.setVisible(true);
						attacker.animate(new Animation(6946, UpdatePriority.VERY_HIGH));
					} else if(count == 5) {
						CombatUtil.areaAction(defender, actor -> {
							if(actor.getPosition().equals(position)) {
								actor.damage(new Hit(Utility.random(10, 15), HitIcon.MELEE));
								actor.movement.reset();
							}
						});
						cancel();
					}
				}
				
				@Override
				protected void onCancel(boolean logout) {
					attacker.attack(defender);
				}
			});
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{};
		}
	}
	
	private static class MinionsAttack extends NpcMagicStrategy {
		MinionsAttack() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public void hit(Mob attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void block(Actor attacker, Mob defender, Hit hit, CombatType combatType) {
		}
		
		@Override
		public void start(Mob attacker, Actor defender, Hit[] hits) {
			attacker.attributes.set("LIZARD_SPAWNED", Boolean.TRUE);
			for(int index = 0; index < 3; index++) {
				Mob minion = new Mob(6768, Utility.randomElement(attacker.boundaries));
				minion.register();
				minion.follow(defender);
				
				World.schedule(8, () -> {
					World.sendGraphic(new Graphic(1295), minion.getPosition());
					
					if(defender.getPosition().isWithinDistance(minion.getPosition(), 3)) {
						defender.damage(new Hit(Utility.random(8) + 2));
					}
					
					minion.unregister();
					attacker.attributes.set("LIZARD_SPAWNED", Boolean.FALSE);
				});
			}
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{};
		}
	}
	
	private static class MagicAttack extends NpcMagicStrategy {
		MagicAttack() {
			super(CombatProjectile.getDefinition("Shaman magic"));
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return new Animation(7193, UpdatePriority.HIGH);
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit combatHit = nextMagicHit(attacker, defender, 31);
			combatHit.setAccurate(true);
			return new CombatHit[]{combatHit};
		}
	}
	
	private static class AcidAttack extends NpcMagicStrategy {
		AcidAttack() {
			super(CombatProjectile.getDefinition("Shaman acid"));
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return new Animation(7193, UpdatePriority.HIGH);
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit combatHit = nextMagicHit(attacker, defender);
			combatHit.set(Hitsplat.POISON);
			combatHit.setIcon(HitIcon.NONE);
			combatHit.setAccurate(false);
			combatHit.setDamage(Utility.random(25, 30));
			return new CombatHit[]{combatHit};
		}
	}
}
