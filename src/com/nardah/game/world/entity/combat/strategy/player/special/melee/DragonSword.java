package com.nardah.game.world.entity.combat.strategy.player.special.melee;

import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.entity.combat.attack.FightType;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.strategy.player.PlayerMeleeStrategy;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.prayer.Prayer;
import com.nardah.net.packet.out.SendMessage;

/**
 * @author Adam_#6723 <-- Discord
 */

public class DragonSword extends PlayerMeleeStrategy {
	private static final Animation ANIMATION = new Animation(7515, UpdatePriority.HIGH);
	private static final Graphic GRAPHIC = new Graphic(1369);
	private static final DragonSword INSTANCE = new DragonSword();

	private DragonSword() {
	}

	@Override
	public void start(Player attacker, Actor defender, Hit[] hits) {
		super.start(attacker, defender, hits);
		attacker.graphic(GRAPHIC);
	}

	@Override
	public void attack(Player attacker, Actor defender, Hit h) {
		if(!defender.isPlayer())
			return;

		defender.prayer.deactivate(Prayer.PROTECT_FROM_MELEE);
		defender.getPlayer().send(new SendMessage("Your overhead prayers have been disabled!"));
		attacker.getPlayer().send(new SendMessage("You have disabled " + defender.getName() + "'s overhead prayers!"));
	}

	@Override
	public int getAttackDelay(Player attacker, Actor defender, FightType fightType) {
		return 4;
	}

	@Override
	public int modifyDamage(Player attacker, Actor defender, int damage) {
		return damage * 3 / 2;
	}

	@Override
	public Animation getAttackAnimation(Player attacker, Actor defender) {
		return ANIMATION;
	}

	public static DragonSword get() {
		return INSTANCE;
	}

}
