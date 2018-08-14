package com.nardah.game.world.entity.combat.strategy.npc.boss;

import com.nardah.content.activity.impl.cerberus.CerberusActivity;
import com.nardah.game.world.entity.combat.projectile.CombatProjectile;
import com.nardah.game.world.position.Position;
import com.nardah.util.RandomGen;
import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.Projectile;
import com.nardah.game.UpdatePriority;
import com.nardah.game.task.Task;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.combat.hit.CombatHit;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.strategy.CombatStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.MultiStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMagicStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcMeleeStrategy;
import com.nardah.game.world.entity.combat.strategy.npc.NpcRangedStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.prayer.Prayer;

import java.util.HashSet;

import static com.nardah.game.world.entity.combat.CombatUtil.createStrategyArray;
import static com.nardah.game.world.entity.combat.CombatUtil.randomStrategy;

public class Cerberus extends MultiStrategy {
	
	private static final RangedAttack RANGED = new RangedAttack();
	private static final MagicAttack MAGIC = new MagicAttack();
	private static final CombatStrategy<Mob>[] STRATEGIES = createStrategyArray(RANGED, MAGIC, NpcMeleeStrategy.get());
	
	public final HashSet<Position> lavaPoolPositions = new HashSet<>();
	
	public Cerberus() {
		currentStrategy = MAGIC;
	}
	
	@Override
	public boolean canAttack(Mob attacker, Actor defender) {
		if(!currentStrategy.withinDistance(attacker, defender)) {
			currentStrategy = randomStrategy(STRATEGIES);
		}
		if(attacker.isDead()) {
			return false;
		}
		return currentStrategy.canAttack(attacker, defender);
	}
	
	@Override
	public void block(Actor attacker, Mob defender, Hit hit, CombatType combatType) {
		currentStrategy.block(attacker, defender, hit, combatType);
		defender.getCombat().attack(attacker);
	}
	
	@Override
	public void hit(Mob attacker, Actor defender, Hit hit) {
		super.hit(attacker, defender, hit);
		
		if(!defender.isPlayer())
			return;
		Player player = defender.getPlayer();
		
		if(currentStrategy.getCombatType().equals(CombatType.MELEE) && player.prayer.isActive(Prayer.PROTECT_FROM_MELEE)) {
			hit.setDamage(0);
		} else if(currentStrategy.getCombatType().equals(CombatType.RANGED) && player.prayer.isActive(Prayer.PROTECT_FROM_RANGE)) {
			hit.setDamage(0);
		} else if(currentStrategy.getCombatType().equals(CombatType.MAGIC) && player.prayer.isActive(Prayer.PROTECT_FROM_MAGIC)) {
			hit.setDamage(0);
		}
		
		if(attacker.getCurrentHealth() < 200 && new RandomGen().inclusive(10) == 0) {
			executeSpecial(attacker, defender.getPlayer(), CerberusSpecial.LAVA);
		}
		if(new RandomGen().inclusive(10) == 0) {
			boolean ghostsPresent = !((CerberusActivity) attacker.activity).ghosts.isEmpty();
			if(!ghostsPresent) {
				executeSpecial(attacker, defender.getPlayer(), CerberusSpecial.GHOSTS);
			}
		}
		
	}
	
	@Override
	public void finishOutgoing(Mob attacker, Actor defender) {
		currentStrategy.finishOutgoing(attacker, defender);
		if(NpcMeleeStrategy.get().withinDistance(attacker, defender)) {
			currentStrategy = randomStrategy(STRATEGIES);
		} else {
			currentStrategy = randomStrategy(STRATEGIES);
		}
	}
	
	@Override
	public int modifyAccuracy(Mob attacker, Actor defender, int roll) {
		return roll;
	}
	
	private static class RangedAttack extends NpcRangedStrategy {
		private static final Animation ANIMATION = new Animation(4490, UpdatePriority.HIGH);
		
		RangedAttack() {
			super(CombatProjectile.getDefinition("Cerb Range"));
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			return new CombatHit[]{nextRangedHit(attacker, defender, 23, 4, 0)};
		}
		
		@Override
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return ANIMATION;
		}
	}
	
	private static class MagicAttack extends NpcMagicStrategy {
		private static final Animation ANIMATION = new Animation(4490, UpdatePriority.HIGH);
		
		public MagicAttack() {
			super(CombatProjectile.getDefinition("Cerb Magic"));
		}
		
		@Override
		public CombatHit[] getHits(Mob attacker, Actor defender) {
			CombatHit hit = nextMagicHit(attacker, defender, 23, 4, 0);
			return new CombatHit[]{hit};
		}
		
		public Animation getAttackAnimation(Mob attacker, Actor defender) {
			return ANIMATION;
		}
	}
	
	private void executeSpecial(Mob cerberus, Player target, CerberusSpecial special) {
		cerberus.speak(special.forceChat);
		if(special == CerberusSpecial.LAVA) {
			cerberus.animate(new Animation(4494, UpdatePriority.VERY_HIGH));
			for(int i = 0; i < 3; i++) {
				int offsetX = target.getX() - cerberus.getX();
				int offsetY = target.getY() - cerberus.getY();
				if(i == 0 || i == 2) {
					offsetX += i == 0 ? -1 : 1;
					offsetY++;
				}
				Position pool = new Position(cerberus.getX() + offsetX, cerberus.getY() + offsetY, target.playerAssistant.instance());
				lavaPoolPositions.add(pool);
				World.sendGraphic(new Graphic(1246, UpdatePriority.HIGH), pool);
				World.schedule(new Task(9) {
					protected void execute() {
						lavaPoolPositions.clear();
					}
					
					protected void onLoop() {
						if(target.getPosition().equals(pool)) {
							target.damage(new Hit(15));
						}
						if(lavaPoolPositions.isEmpty()) {
							this.cancel();
						}
					}
				});
			}
		} else {
			Mob meleeGhost = new Mob(5869, new Position(1239, 1256, target.playerAssistant.instance()));
			Mob mageGhost = new Mob(5868, new Position(1240, 1256, target.playerAssistant.instance()));
			Mob rangeGhost = new Mob(5867, new Position(1241, 1256, target.playerAssistant.instance()));
			meleeGhost.register();
			mageGhost.register();
			rangeGhost.register();
			target.activity.add(meleeGhost);
			target.activity.add(mageGhost);
			target.activity.add(rangeGhost);
			World.schedule(2, () -> {
				if(!cerberus.isDead()) {
					new Projectile(1248, 52, 125, 43, 31).send(meleeGhost, target);
					World.schedule(3, () -> {
						if(!target.prayer.isActive(Prayer.PROTECT_FROM_MELEE)) {
							target.damage(new Hit(30));
						}
					});
					World.schedule(2, () -> meleeGhost.movement.walk(new Position(1239, 1265)));
				}
			});
			World.schedule(6, () -> {
				if(!cerberus.isDead()) {
					new Projectile(100, 52, 125, 43, 31).send(mageGhost, target);
					World.schedule(3, () -> {
						if(!target.prayer.isActive(Prayer.PROTECT_FROM_MAGIC)) {
							target.damage(new Hit(30));
						}
					});
					World.schedule(2, () -> mageGhost.movement.walk(new Position(1240, 1265)));
				}
			});
			World.schedule(10, () -> {
				if(!cerberus.isDead()) {
					new Projectile(24, 52, 125, 43, 31).send(rangeGhost, target);
					World.schedule(3, () -> {
						if(!target.prayer.isActive(Prayer.PROTECT_FROM_RANGE)) {
							target.damage(new Hit(30));
						}
					});
					World.schedule(2, () -> rangeGhost.movement.walk(new Position(1241, 1265)));
				}
			});
			World.schedule(15, () -> {
				meleeGhost.unregister();
				mageGhost.unregister();
				rangeGhost.unregister();
			});
		}
	}
	
	private static enum CerberusSpecial {
		LAVA("Grrrrrrrrrrrrrr"), GHOSTS("Aaarrroooooooo");
		
		private final String forceChat;
		
		CerberusSpecial(String forceChat) {
			this.forceChat = forceChat;
		}
	}
}