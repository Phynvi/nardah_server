package com.nardah.game.world.entity.combat.strategy.npc.boss.arena;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.position.Position;
import com.nardah.game.world.region.RegionManager;
import com.nardah.util.RandomUtils;
import com.nardah.util.Utility;
import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.Projectile;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.strategy.CombatStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.MultiStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.items.Item;
import com.nardah.net.packet.out.SendMessage;

import static com.nardah.game.world.entity.combat.CombatUtil.createStrategyArray;
import static com.nardah.game.world.entity.combat.CombatUtil.randomStrategy;

/**
 * Handles Skotizo's stategy.
 * @author Adam Trinity
 */
public class Arena extends MultiStrategy {
	private static Magic MAGIC = new Magic();
	private static LightingRain LIGHTNING_RAIN = new LightingRain();
	private static TeleGrab TELE_GRAB = new TeleGrab();
	
	private static final CombatStrategy<Mob>[] FULL_STRATEGIES = createStrategyArray(NpcMeleeStrategy.get(), MAGIC, TELE_GRAB, LIGHTNING_RAIN);
	private static final CombatStrategy<Mob>[] MAGIC_STRATEGIES = createStrategyArray(MAGIC, MAGIC, MAGIC, TELE_GRAB, LIGHTNING_RAIN);
	
	private static final String[] SHOUTS = {"Feel the wrath of Glod!", "The dark times have come for you all!"};
	
	/**
	 * Constructs a new <code>Arena</code>.
	 */
	public Arena() {
		currentStrategy = MAGIC;
	}
	
	@Override
	public boolean canAttack(Mob attacker, Actor defender) {
		if(!currentStrategy.withinDistance(attacker, defender)) {
			currentStrategy = randomStrategy(MAGIC_STRATEGIES);
		}
		return currentStrategy.canAttack(attacker, defender);
	}
	
	@Override
	public void block(Actor attacker, Mob defender, Hit hit, CombatType combatType) {
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
					other.message("Glod has moved you around!");
				});
			});
		}
	}
	
	@Override
	public void finishOutgoing(Mob attacker, Actor defender) {
		currentStrategy.finishOutgoing(attacker, defender);
		if(NpcMeleeStrategy.get().withinDistance(attacker, defender)) {
			currentStrategy = randomStrategy(FULL_STRATEGIES);
		} else {
			currentStrategy = randomStrategy(MAGIC_STRATEGIES);
		}
	}
	
	@Override
	public int getAttackDelay(Mob attacker, Actor defender, FightType fightType) {
		return attacker.definition.getAttackDelay();
	}
	
	/**
	 * Arena magic strategy.
	 */
	private static class Magic extends NpcMagicStrategy {
		public Magic() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public void hit(Mob attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void attack(Mob attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void start(Mob attacker, Actor defender, Hit[] hits) {
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
				player.send(new SendMessage("Glod has removed your " + Utility.formatName(disarm.getEquipmentType().name().toLowerCase()) + "."));
				player.graphic(new Graphic(785, true, UpdatePriority.HIGH));
				
			}
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit hit = nextMagicHit(attacker, defender, 38);
			hit.setAccurate(false);
			return new CombatHit[]{hit};
		}
		
		@Override
		public int modifyAccuracy(Mob attacker, Actor defender, int roll) {
			return roll + 50_000;
		}
	}
	
	private static class TeleGrab extends NpcMagicStrategy {
		TeleGrab() {
			super(CombatProjectile.getDefinition("EMPTY"));
		}
		
		@Override
		public void hit(Mob attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void attack(Mob attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void start(Mob attacker, Actor defender, Hit[] hits) {
			attacker.animate(new Animation(6501, UpdatePriority.VERY_HIGH));
			attacker.graphic(481);
			attacker.speak("ARHHHH! TIME TO SWITCH IT UP!!");
			
			RegionManager.forNearbyPlayer(attacker, 16, other -> World.schedule(1, () -> {
				Position destination = Utility.randomElement(attacker.boundaries);
				World.sendGraphic(new Graphic(481), destination);
				other.move(destination);
				other.message("Glod has moved you around!");
			}));
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
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
		public void hit(Mob attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void attack(Mob attacker, Actor defender, Hit hit) {
		}
		
		@Override
		public void start(Mob attacker, Actor defender, Hit[] hits) {
			attacker.animate(new Animation(6501, UpdatePriority.VERY_HIGH));
			attacker.speak("YOU WILL NOW FEEL THE TRUE WRATH OF Arena!");
			
			RegionManager.forNearbyPlayer(attacker, 16, other -> {
				Position position = other.getPosition();
				combatProjectile.getProjectile().ifPresent(projectile -> World.sendProjectile(attacker, position, projectile));
				
				World.schedule(2, () -> {
					World.sendGraphic(new Graphic(775), position);
					if(other.getPosition().equals(position)) {
						other.damage(new Hit(Utility.random(20, 50)));
						other.speak("OUCH!");
						other.message("Glod has just electrocuted your entire body! Don't stay in one spot!");
					}
				});
			});
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit hit = nextMagicHit(attacker, defender, 38);
			hit.setAccurate(false);
			return new CombatHit[]{hit};
		}
	}
}
