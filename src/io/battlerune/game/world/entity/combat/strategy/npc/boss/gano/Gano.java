package io.battlerune.game.world.entity.combat.strategy.npc.boss.gano;

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
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.npc.Npc;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.items.Item;
import io.battlerune.game.world.position.Position;
import io.battlerune.game.world.region.RegionManager;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.RandomUtils;
import io.battlerune.util.Utility;

import static io.battlerune.game.world.entity.combat.CombatUtil.createStrategyArray;
import static io.battlerune.game.world.entity.combat.CombatUtil.randomStrategy;

/**
 * Handles Gano Strategy
 * @author Adam Trinity
 */
public class Gano extends MultiStrategy {
	private static Magic MAGIC = new Magic();
	private static LightingRain LIGHTNING_RAIN = new LightingRain();
	private static TeleGrab TELE_GRAB = new TeleGrab();
	
	private static final CombatStrategy<Npc>[] FULL_STRATEGIES = createStrategyArray(NpcMeleeStrategy.get(), MAGIC, TELE_GRAB, LIGHTNING_RAIN);
	private static final CombatStrategy<Npc>[] MAGIC_STRATEGIES = createStrategyArray(MAGIC, MAGIC, MAGIC, TELE_GRAB, LIGHTNING_RAIN);
	
	private static final String[] SHOUTS = {"Feel the wrath of gano!", "The dark times have come for you all!"};
	
	/**
	 * Constructs a new <code>gano</code>.
	 */
	public Gano() {
		currentStrategy = MAGIC;
	}
	
	@Override
	public boolean canAttack(Npc attacker, Actor defender) {
		if(!currentStrategy.withinDistance(attacker, defender)) {
			currentStrategy = randomStrategy(MAGIC_STRATEGIES);
		}
		return currentStrategy.canAttack(attacker, defender);
	}
	
	@Override
	public void block(Actor attacker, Npc defender, Hit hit, CombatType combatType) {
		currentStrategy.block(attacker, defender, hit, combatType);
		defender.getCombat().attack(attacker);
		
		if(!defender.getCombat().isAttacking()) {
			defender.animate(new Animation(6501, UpdatePriority.VERY_HIGH));
			defender.graphic(481);
			defender.speak("ARHHHH! TIME TO SWITCH IT UP!!");
			
			RegionManager.forNearbyPlayer(attacker, 20, other -> {
				if(RandomUtils.success(.65))
					return;
				
				World.schedule(2, () -> {
					Position destination = Utility.randomElement(defender.boundaries);
					World.sendGraphic(new Graphic(481), destination);
					other.move(destination);
					other.message("gano has moved you around!");
				});
			});
		}
	}
	
	@Override
	public void finishOutgoing(Npc attacker, Actor defender) {
		currentStrategy.finishOutgoing(attacker, defender);
		if(NpcMeleeStrategy.get().withinDistance(attacker, defender)) {
			currentStrategy = randomStrategy(FULL_STRATEGIES);
		} else {
			currentStrategy = randomStrategy(MAGIC_STRATEGIES);
		}
	}
	
	@Override
	public int getAttackDelay(Npc attacker, Actor defender, FightType fightType) {
		return attacker.definition.getAttackDelay();
	}
	
	/**
	 * gano magic strategy.
	 */
	private static class Magic extends NpcMagicStrategy {
		public Magic() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public void hit(Npc attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void attack(Npc attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void start(Npc attacker, Actor defender, Hit[] hits) {
			int disarmattack = 1;
			int disaramattackrandom = Utility.random(disarmattack, 3);
			
			Projectile projectile = new Projectile(1028, 50, 80, 85, 25);
			attacker.animate(new Animation(6501, UpdatePriority.VERY_HIGH));
			RegionManager.forNearbyPlayer(attacker, 16, other -> {
				projectile.send(attacker, other);
				World.schedule(2, () -> other.damage(nextMagicHit(attacker, other, 38)));
			});
			
			if(Utility.random(0, 2) == 1)
				attacker.speak(Utility.randomElement(SHOUTS));
			if(defender.isPlayer() && disaramattackrandom == disarmattack) {
				Player player = defender.getPlayer();
				Item[] equipment = player.equipment.toNonNullArray();
				if(equipment.length == 0)
					return;
				Item disarm = Utility.randomElement(equipment);
				if(disarm == null)
					return;
				player.equipment.unEquip(disarm);
				player.send(new SendMessage("gano has removed your " + Utility.formatName(disarm.getEquipmentType().name().toLowerCase()) + "."));
				player.graphic(new Graphic(785, true, UpdatePriority.HIGH));
				
			}
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			CombatHit hit = nextMagicHit(attacker, defender, 38);
			hit.setAccurate(false);
			return new CombatHit[]{hit};
		}
		
		@Override
		public int modifyAccuracy(Npc attacker, Actor defender, int roll) {
			return roll + 50_000;
		}
	}
	
	private static class TeleGrab extends NpcMagicStrategy {
		TeleGrab() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public void hit(Npc attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void attack(Npc attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void start(Npc attacker, Actor defender, Hit[] hits) {
			attacker.animate(new Animation(6501, UpdatePriority.VERY_HIGH));
			attacker.graphic(481);
			attacker.speak("ARHHHH! TIME TO SWITCH IT UP!!");
			
			RegionManager.forNearbyPlayer(attacker, 16, other -> World.schedule(1, () -> {
				Position destination = Utility.randomElement(attacker.boundaries);
				World.sendGraphic(new Graphic(481), destination);
				other.move(destination);
				other.message("gano has moved you around!");
			}));
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			CombatHit hit = nextMagicHit(attacker, defender, 38);
			hit.setAccurate(false);
			return new CombatHit[]{hit};
		}
	}
	
	private static class LightingRain extends NpcMagicStrategy {
		LightingRain() {
			super(CombatProjectile.getDefinition("Vet'ion"));
		}
		
		@Override
		public void hit(Npc attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void attack(Npc attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void start(Npc attacker, Actor defender, Hit[] hits) {
			attacker.animate(new Animation(6501, UpdatePriority.VERY_HIGH));
			attacker.speak("YOU WILL NOW FEEL THE TRUE WRATH OF gano!");
			
			RegionManager.forNearbyPlayer(attacker, 16, other -> {
				Position position = other.getPosition();
				combatProjectile.getProjectile().ifPresent(projectile -> World.sendProjectile(attacker, position, projectile));
				
				World.schedule(2, () -> {
					World.sendGraphic(new Graphic(775), position);
					if(other.getPosition().equals(position)) {
						other.damage(new Hit(Utility.random(20, 50)));
						other.speak("OUCH!");
						other.message("gano has just electrocuted your entire body! Don't stay in one spot!");
					}
				});
			});
		}
		
		@Override
		public CombatHit[] getHits(Npc attacker, Actor defender) {
			CombatHit hit = nextMagicHit(attacker, defender, 38);
			hit.setAccurate(false);
			return new CombatHit[]{hit};
		}
	}
}
