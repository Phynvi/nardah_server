package com.nardah.game.world.entity.combat.strategy.npc.boss;

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
import com.nardah.game.world.entity.combat.strategy.npc.NpcRangedStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.prayer.Prayer;
import com.nardah.net.packet.out.SendMessage;

import static com.nardah.game.world.entity.combat.CombatUtil.createStrategyArray;
import static com.nardah.game.world.entity.combat.CombatUtil.randomStrategy;

/**
 * Handles Giant Roc's strategy
 * @author Adam
 */
public class GiantRoc extends MultiStrategy {
	private static Magic MAGIC = new Magic();
	private static Range RANGE = new Range();
	private static LightingRain LIGHTNING_RAIN = new LightingRain();
	private static TeleGrab TELE_GRAB = new TeleGrab();
	
	private static final CombatStrategy<Mob>[] FULL_STRATEGIES = createStrategyArray(NpcMeleeStrategy.get(), MAGIC, TELE_GRAB, LIGHTNING_RAIN);
	private static final CombatStrategy<Mob>[] MAGIC_STRATEGIES = createStrategyArray(MAGIC, MAGIC, MAGIC, TELE_GRAB, LIGHTNING_RAIN);
	private static final CombatStrategy<Mob>[] NON_MELEE = createStrategyArray(MAGIC, RANGE, MAGIC, MAGIC, MAGIC, TELE_GRAB, LIGHTNING_RAIN);
	
	private static final String[] SHOUTS = {"The Cold Winds are Rising!", "Darkness and death marches upon gilenor!"};
	
	/**
	 * Constructs a new <code>Giant Roc</code>.
	 */
	public GiantRoc() {
		currentStrategy = MAGIC;
		currentStrategy = RANGE;
	}
	
	@Override
	public boolean canAttack(Mob attacker, Actor defender) {
		if(!currentStrategy.withinDistance(attacker, defender)) {
			currentStrategy = randomStrategy(MAGIC_STRATEGIES);
			currentStrategy = randomStrategy(NON_MELEE);
		}
		return currentStrategy.canAttack(attacker, defender);
	}
	
	@Override
	public void block(Actor attacker, Mob defender, Hit hit, CombatType combatType) {
		currentStrategy.block(attacker, defender, hit, combatType);
		defender.getCombat().attack(attacker);
		
		if(!defender.getCombat().isAttacking()) {
			defender.animate(new Animation(5023, UpdatePriority.VERY_HIGH));
			defender.graphic(1196);
			defender.graphic(481);
			defender.speak("Night King, Lend me your powers for i am your faithful servant!");
			defender.prayer.deactivate(Prayer.PROTECT_FROM_MAGIC, Prayer.PROTECT_FROM_MELEE, Prayer.PROTECT_FROM_RANGE);
			defender.getPlayer().send(new SendMessage("Your overhead prayers have been disabled!"));
			
			RegionManager.forNearbyPlayer(attacker, 20, other -> {
				if(RandomUtils.success(.65))
					return;
				
				World.schedule(2, () -> {
					Position destination = Utility.randomElement(defender.boundaries);
					World.sendGraphic(new Graphic(481), destination);
					other.move(destination);
					other.message("Giant Roc has moved you around!");
					
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
	
	private static class Range extends NpcRangedStrategy {
		
		public Range() {
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
			Projectile projectile = new Projectile(1242, 50, 80, 85, 25);
			attacker.animate(new Animation(5023, UpdatePriority.VERY_HIGH));
			
			CombatHit hit = nextRangedHit(attacker, defender, 21);
			RegionManager.forNearbyPlayer(attacker, 16, other -> {
				projectile.send(attacker, other);
				World.schedule(2, () -> other.damage(nextMagicHit(attacker, other, 38)));
			});
			
			if(Utility.random(0, 3) == 2)
				attacker.speak(Utility.randomElement(SHOUTS));
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit hit = nextRangedHit(attacker, defender, 38);
			hit.setAccurate(false);
			return new CombatHit[]{hit};
		}
		
		@Override
		public int modifyAccuracy(Mob attacker, Actor defender, int roll) {
			return roll + 50_000;
		}
		
	}
	
	/**
	 * Skotizo's magic strategy.
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
			Projectile projectile = new Projectile(1198, 50, 80, 85, 25);
			attacker.animate(new Animation(5023, UpdatePriority.VERY_HIGH));
			RegionManager.forNearbyPlayer(attacker, 16, other -> {
				projectile.send(attacker, other);
				World.schedule(2, () -> other.damage(nextMagicHit(attacker, other, 38)));
			});
			
			if(Utility.random(0, 2) == 1)
				attacker.speak(Utility.randomElement(SHOUTS));
			defender.prayer.deactivate(Prayer.PROTECT_FROM_MAGIC, Prayer.PROTECT_FROM_MELEE, Prayer.PROTECT_FROM_RANGE);
			defender.getPlayer().send(new SendMessage("Your overhead prayers have been disabled!"));
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
			attacker.animate(new Animation(5023, UpdatePriority.VERY_HIGH));
			attacker.graphic(481);
			attacker.speak("ARHHHH! TIME TO SWITCH IT UP!!");
			
			RegionManager.forNearbyPlayer(attacker, 16, other -> World.schedule(1, () -> {
				Position destination = Utility.randomElement(attacker.boundaries);
				World.sendGraphic(new Graphic(481), destination);
				other.move(destination);
				other.message("Giant Roc has moved you around!");
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
			attacker.animate(new Animation(5023, UpdatePriority.VERY_HIGH));
			attacker.speak("YOU WILL NOW FEEL THE TRUE WRATH OF Giant Roc!");
			
			RegionManager.forNearbyPlayer(attacker, 16, other -> {
				Position position = other.getPosition();
				combatProjectile.getProjectile().ifPresent(projectile -> World.sendProjectile(attacker, position, projectile));
				
				World.schedule(2, () -> {
					World.sendGraphic(new Graphic(775), position);
					if(other.getPosition().equals(position)) {
						other.damage(new Hit(Utility.random(20, 50)));
						other.speak("OUCH!");
						other.message("Giant Roc has just electrocuted your entire body! Don't stay in one spot!");
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
