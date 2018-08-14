package com.nardah.game.world.entity.combat.strategy.npc.boss;

import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.position.Position;
import com.nardah.game.world.region.RegionManager;
import com.nardah.util.RandomUtils;
import com.nardah.util.Utility;
import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.UpdatePriority;
import com.nardah.game.task.Task;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.CombatUtil;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.hit.HitIcon;
import com.nardah.game.world.entity.combat.strategy.npc.MultiStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.net.packet.out.SendMessage;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * @author Daniel
 */
public class Vetion extends MultiStrategy {
	private static Earthquake EARTHQUAKE = new Earthquake();
	private static Magic MAGIC = new Magic();
	
	private Mob pet1, pet2;
	private boolean spawnPets, secondTrans;
	
	public Vetion() {
		currentStrategy = MAGIC;
	}
	
	@Override
	public boolean withinDistance(Mob attacker, Actor defender) {
		if(!currentStrategy.withinDistance(attacker, defender)) {
			currentStrategy = MAGIC;
		}
		return currentStrategy.withinDistance(attacker, defender);
	}
	
	@Override
	public boolean canAttack(Mob attacker, Actor defender) {
		if(!currentStrategy.withinDistance(attacker, defender)) {
			currentStrategy = MAGIC;
		}
		return currentStrategy.canAttack(attacker, defender);
	}
	
	@Override
	public boolean canOtherAttack(Actor attacker, Mob defender) {
		if(pet1 != null || pet2 != null) {
			if(attacker.isPlayer()) {
				attacker.getPlayer().message("You must kill his hellhounds before dealing damage!");
			}
			return false;
		}
		return currentStrategy.canOtherAttack(attacker, defender);
	}
	
	@Override
	public void onDeath(Actor attacker, Mob defender, Hit hit) {
		if(pet1 != null)
			pet1.unregister();
		if(pet2 != null)
			pet2.unregister();
	}
	
	@Override
	public void preDeath(Actor attacker, Mob defender, Hit hit) {
		super.preDeath(attacker, defender, hit);
		
		if(secondTrans) {
			return;
		}
		
		secondTrans = true;
		spawnPets = false;
		defender.setDead(false);
		defender.heal(255);
		defender.transform(6612);
		defender.speak("Do it again!");
		defender.getCombat().attack(attacker);
		
		World.schedule(new Task(500) {
			@Override
			public void execute() {
				cancel();
				if(defender.isDead())
					return;
				defender.transform(6611);
				defender.getCombat().reset();
				secondTrans = false;
				spawnPets = false;
			}
		});
	}
	
	@Override
	public void finishIncoming(Actor attacker, Mob defender) {
		if(spawnPets || defender.getCurrentHealth() > defender.getMaximumHealth() / 2) {
			return;
		}
		
		Position[] innerBoundary = Utility.getInnerBoundaries(defender.getPosition().transform(-2, -2), defender.width() + 4, defender.length() + 4);
		defender.speak(defender.id == 6611 ? "Kill, my pets!" : "Bahh! Go, dogs!!");
		spawnPets = true;
		
		if(!secondTrans) {
			pet1 = new Mob(6613, RandomUtils.random(innerBoundary)) {
				@Override
				public void appendDeath() {
					super.appendDeath();
					pet1 = null;
				}
			};
			pet2 = new Mob(6613, RandomUtils.random(innerBoundary)) {
				@Override
				public void appendDeath() {
					super.appendDeath();
					pet2 = null;
				}
			};
		} else {
			pet1 = new Mob(6614, RandomUtils.random(innerBoundary)) {
				@Override
				public void appendDeath() {
					super.appendDeath();
					pet1 = null;
				}
			};
			pet2 = new Mob(6614, RandomUtils.random(innerBoundary)) {
				@Override
				public void appendDeath() {
					super.appendDeath();
					pet2 = null;
				}
			};
		}
		pet1.register();
		pet2.register();
		pet1.definition.setRespawnTime(-1);
		pet2.definition.setRespawnTime(-1);
		pet1.speak("GRRRRRRRRRRRR");
		pet2.speak("GRRRRRRRRRRRR");
		pet1.getCombat().attack(attacker);
		pet2.getCombat().attack(attacker);
	}
	
	@Override
	public void block(Actor attacker, Mob defender, Hit hit, CombatType combatType) {
		currentStrategy.block(attacker, defender, hit, combatType);
		defender.getCombat().attack(attacker);
	}
	
	@Override
	public void finishOutgoing(Mob attacker, Actor defender) {
		currentStrategy.finishOutgoing(attacker, defender);
		if(!NpcMeleeStrategy.get().withinDistance(attacker, defender)) {
			currentStrategy = MAGIC;
		} else if(RandomUtils.success(0.25)) {
			currentStrategy = EARTHQUAKE;
		} else {
			currentStrategy = NpcMeleeStrategy.get();
		}
	}
	
	@Override
	public int getAttackDelay(Mob attacker, Actor defender, FightType fightType) {
		return attacker.definition.getAttackDelay();
	}
	
	private static final class Earthquake extends NpcMeleeStrategy {
		private static final Animation ANIMATION = new Animation(5507, UpdatePriority.HIGH);
		
		@Override
		public void hit(Mob attacker, Actor defender, Hit hit) {
			RegionManager.forNearbyPlayer(defender, 11, player -> {
				player.send(new SendMessage("Vet'ion pummels the ground sending a shattering earthquake shockwave through you."));
				if(player.equals(defender)) {
					hit.setDamage(25 + RandomUtils.inclusive(20));
					return;
				}
				player.damage(new Hit(25 + RandomUtils.inclusive(20), HitIcon.MELEE));
			});
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return ANIMATION;
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{nextMeleeHit(attacker, defender)};
		}
	}
	
	private static final class Magic extends NpcMagicStrategy {
		private static final Animation ANIMATION = new Animation(5512, UpdatePriority.HIGH);
		
		public Magic() {
			super(CombatProjectile.getDefinition("Vet'ion"));
		}
		
		@Override
		public void start(Mob attacker, Actor defender, Hit[] hits) {
			Position[] bounds = Utility.getInnerBoundaries(defender.getPosition().transform(-1, -1), defender.width() + 2, defender.length() + 2);
			LinkedList<Position> positions = new LinkedList<>(Arrays.asList(bounds));
			for(int index = 0; index < 3; index++) {
				Position bound = index == 0 ? defender.getPosition() : RandomUtils.random(positions);
				positions.remove(bound);
				combatProjectile.getProjectile().ifPresent(projectile -> World.sendProjectile(attacker, bound, projectile));
				World.sendGraphic(new Graphic(775, 100), bound);
				World.schedule(CombatUtil.getHitDelay(attacker, defender, CombatType.MAGIC) + 2, () -> RegionManager.forNearbyPlayer(bound, 0, player -> {
					player.speak("OUCH!");
					player.damage(new Hit(RandomUtils.inclusive(45), HitIcon.MAGIC));
				}));
			}
		}
		
		@Override
		public void hit(Mob attacker, Actor defender, Hit hit) {
			hit.setAccurate(false);
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return ANIMATION;
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{nextMagicHit(attacker, defender)};
		}
	}
	
}
