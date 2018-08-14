package io.battlerune.game.world.entity.combat.effect.impl;

import io.battlerune.game.world.entity.combat.effect.AntifireDetails;
import io.battlerune.game.world.entity.combat.effect.CombatEffect;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.packet.out.SendMessage;

/**
 * The class which is responsible for the effect when you drink an anti-fire
 * potion.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 */
public final class CombatAntifireEffect extends CombatEffect {

	/**
	 * The type of antifire this player has drunk.
	 */
	private final AntifireDetails.AntifireType type;

	/**
	 * Constructs a new {@link CombatAntifireEffect}.
	 * @param type {@link #type}.
	 */
	public CombatAntifireEffect(AntifireDetails.AntifireType type) {
		super(1);
		this.type = type;
	}

	@Override
	public boolean apply(Actor actor) {
		if(!actor.isPlayer()) {
			return false;
		}

		Player player = actor.getPlayer();

		if(player.getAntifireDetails().isPresent()) {
			player.setAntifireDetail(new AntifireDetails(type));
			return false;
		}
		player.setAntifireDetail(new AntifireDetails(type));
		return true;
	}

	@Override
	public boolean removeOn(Actor actor) {
		if(actor.isPlayer()) {
			Player player = actor.getPlayer();

			return !player.getAntifireDetails().isPresent();

		}
		return true;
	}

	@Override
	public void process(Actor actor) {
		if(actor.isPlayer() && actor.getPlayer().getAntifireDetails().isPresent()) {
			Player player = actor.getPlayer();
			AntifireDetails detail = player.getAntifireDetails().get();
			int count = detail.getAntifireDelay().decrementAndGet();
			if(count == 30) {
				player.send(new SendMessage("@red@Your resistance to dragon fire is about to wear off!"));
			}
			if(count < 1) {
				player.setAntifireDetail(null);
				player.send(new SendMessage("Your resistance to dragon fire has worn off!"));
			}
		}
	}

	@Override
	public boolean onLogin(Actor actor) {
		return actor.isPlayer() && actor.getPlayer().getAntifireDetails().isPresent();
	}

}