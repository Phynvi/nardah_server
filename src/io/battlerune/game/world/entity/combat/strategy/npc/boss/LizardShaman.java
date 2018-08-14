package io.battlerune.game.world.entity.combat.strategy.npc.boss;

import io.battlerune.game.Animation;
import io.battlerune.game.Graphic;
import io.battlerune.game.UpdatePriority;
import io.battlerune.game.task.Task;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.combat.CombatType;
import io.battlerune.game.world.entity.combat.CombatUtil;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.hit.HitIcon;
import io.battlerune.game.world.entity.combat.hit.Hitsplat;
import io.battlerune.game.world.entity.combat.projectile.CombatProjectile;
import io.battlerune.game.world.entity.combat.strategy.CombatStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.MultiStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.npc.Npc;
import io.battlerune.game.world.position.Position;
import io.battlerune.util.Utility;

import static io.battlerune.game.world.entity.combat.CombatUtil.createStrategyArray;
import static io.battlerune.game.world.entity.combat.CombatUtil.randomStrategy;

public class LizardShaman extends MultiStrategy {
	private static final MagicAttack MAGIC = new MagicAttack();
	private static final AcidAttack ACID = new AcidAttack();
	private static final MinionsAttack MINION = new MinionsAttack();
	private static final JumpAttack JUMP = new JumpAttack();
	private static final CombatStrategy<Npc>[] FULL_STRATEGIES = createStrategyArray(NpcMeleeStrategy.get(), ACID, MAGIC, MAGIC, MAGIC, MINION, JUMP);
	private static final CombatStrategy<Npc>[] MAGIC_STRATEGIES = createStrategyArray(ACID, MAGIC, MAGIC, MAGIC, MINION);
	private static final CombatStrategy<Npc>[] BASIC_STRATEGIES = createStrategyArray(ACID, MAGIC, MAGIC, MAGIC, JUMP);
	
	public LizardShaman() {
		currentStrategy = NpcMeleeStrategy.get();
	}
	
	@Override
	public boolean canAttack(Npc attacker, Actor defender) {
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
		public void hit(Npc attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void block(Actor attacker, Npc defender, Hit hit, CombatType combatType) {
		}
		
		@Override
		public void start(Npc attacker, Actor defender, Hit[] hits) {
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
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			return new CombatHit[]{};
		}
	}
	
	private static class MinionsAttack extends NpcMagicStrategy {
		MinionsAttack() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public void hit(Npc attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void block(Actor attacker, Npc defender, Hit hit, CombatType combatType) {
		}
		
		@Override
		public void start(Npc attacker, Actor defender, Hit[] hits) {
			attacker.attributes.set("LIZARD_SPAWNED", Boolean.TRUE);
			for(int index = 0; index < 3; index++) {
				Npc minion = new Npc(6768, Utility.randomElement(attacker.boundaries));
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
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			return new CombatHit[]{};
		}
	}
	
	private static class MagicAttack extends NpcMagicStrategy {
		MagicAttack() {
			super(CombatProjectile.getDefinition("Shaman magic"));
		}
		
		@Override
		public Animation getAttackAnimation(Npc attacker, Actor defender) {
			return new Animation(7193, UpdatePriority.HIGH);
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
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
		public Animation getAttackAnimation(Npc attacker, Actor defender) {
			return new Animation(7193, UpdatePriority.HIGH);
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			CombatHit combatHit = nextMagicHit(attacker, defender);
			combatHit.set(Hitsplat.POISON);
			combatHit.setIcon(HitIcon.NONE);
			combatHit.setAccurate(false);
			combatHit.setDamage(Utility.random(25, 30));
			return new CombatHit[]{combatHit};
		}
	}
}
