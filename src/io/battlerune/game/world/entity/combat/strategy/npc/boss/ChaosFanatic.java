package io.battlerune.game.world.entity.combat.strategy.npc.boss;

import io.battlerune.game.Animation;
import io.battlerune.game.Graphic;
import io.battlerune.game.Projectile;
import io.battlerune.game.UpdatePriority;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.combat.CombatType;
import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.combat.hit.CombatHit;
import io.battlerune.game.world.entity.combat.hit.Hit;
import io.battlerune.game.world.entity.combat.projectile.CombatProjectile;
import io.battlerune.game.world.entity.combat.strategy.CombatStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.MultiStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import io.battlerune.game.world.entity.combat.strategy.npc.NpcRangedStrategy;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.npc.Npc;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.items.Item;
import io.battlerune.game.world.position.Position;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.Utility;

import static io.battlerune.game.world.entity.combat.CombatUtil.createStrategyArray;
import static io.battlerune.game.world.entity.combat.CombatUtil.randomStrategy;
import static io.battlerune.game.world.entity.combat.projectile.CombatProjectile.getDefinition;

/**
 * @author Daniel
 */
public class ChaosFanatic extends MultiStrategy {
	private static RainAttack RAIN = new RainAttack();
	private static RangeAttack RANGE = new RangeAttack();
	private static final CombatStrategy<Npc>[] FULL_STRATEGIES = createStrategyArray(RAIN, RANGE, NpcMeleeStrategy.get(), RANGE, NpcMeleeStrategy.get());
	private static final CombatStrategy<Npc>[] NON_MELEE = createStrategyArray(RAIN, RANGE, RANGE, RANGE, RANGE);
	
	private static final String[] MESSAGES = {"Burn!", "WEUGH!", "Develish Oxen Roll!", "All your wilderness are belong to them!", "AhehHeheuhHhahueHuUEehEahAH", "I shall call him squidgy and he shall be my squidgy!",};
	
	public ChaosFanatic() {
		currentStrategy = randomStrategy(NON_MELEE);
	}
	
	@Override
	public boolean canAttack(Npc attacker, Actor defender) {
		if(!currentStrategy.withinDistance(attacker, defender)) {
			currentStrategy = randomStrategy(NON_MELEE);
		}
		return currentStrategy.canAttack(attacker, defender);
	}
	
	@Override
	public void block(Actor attacker, Npc defender, Hit hit, CombatType combatType) {
		currentStrategy.block(attacker, defender, hit, combatType);
		defender.getCombat().attack(attacker);
	}
	
	@Override
	public void finishOutgoing(Npc attacker, Actor defender) {
		currentStrategy.finishOutgoing(attacker, defender);
		if(NpcMeleeStrategy.get().withinDistance(attacker, defender)) {
			currentStrategy = randomStrategy(FULL_STRATEGIES);
		} else {
			currentStrategy = randomStrategy(NON_MELEE);
		}
		attacker.speak(Utility.randomElement(MESSAGES));
	}
	
	@Override
	public int getAttackDelay(Npc attacker, Actor defender, FightType fightType) {
		return attacker.definition.getAttackDelay();
	}
	
	private static class RainAttack extends NpcMagicStrategy {
		public RainAttack() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public void hit(Npc attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void start(Npc attacker, Actor defender, Hit[] hits) {
			
			int disarmattack = 1;
			int disaramattackrandom = Utility.random(disarmattack, 5);
			
			attacker.animate(new Animation(1162, UpdatePriority.VERY_HIGH));
			for(int i = 0; i < 3; i++) {
				int offsetX = defender.getX() - attacker.getX();
				int offsetY = defender.getY() - attacker.getY();
				if(i == 0 || i == 2) {
					offsetX += i == 0 ? -1 : 1;
					offsetY++;
				}
				World.sendProjectile(new Projectile(551, 46, 80, 43, 31), attacker.getPosition(), -1, (byte) offsetX, (byte) offsetY);
				Position end = new Position(attacker.getX() + offsetX, attacker.getY() + offsetY, 0);
				World.schedule(3, () -> {
					World.sendGraphic(new Graphic(131, UpdatePriority.HIGH), end);
					if(defender.getPosition().equals(end)) {
						defender.damage(nextMagicHit(attacker, defender, 31));
						if(defender.isPlayer() && disaramattackrandom == disarmattack) {
							Player player = defender.getPlayer();
							Item[] equipment = player.equipment.toNonNullArray();
							if(equipment.length == 0)
								return;
							Item disarm = Utility.randomElement(equipment);
							if(disarm == null)
								return;
							player.equipment.unEquip(disarm);
							player.send(new SendMessage("Chaos fanatic has removed your " + Utility.formatName(disarm.getEquipmentType().name().toLowerCase()) + "."));
							player.graphic(new Graphic(557, true, UpdatePriority.HIGH));
						}
					}
				});
			}
		}
		
		@Override
		public void attack(Npc attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			CombatHit hit = nextMagicHit(attacker, defender, 31);
			hit.setAccurate(false);
			return new CombatHit[]{hit};
		}
		
		@Override
		public int modifyAccuracy(Npc attacker, Actor defender, int roll) {
			return roll + 50_000;
		}
	}
	
	private static class RangeAttack extends NpcRangedStrategy {
		public RangeAttack() {
			super(getDefinition("Chaos Fanatic Range"));
		}
	}
}
