package com.nardah.content.activity.impl.duelarena;

import com.nardah.content.activity.ActivityListener;
import com.nardah.game.world.entity.combat.CombatType;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.items.Item;
import com.nardah.net.packet.out.SendMessage;

import java.util.Optional;

/**
 * Created by Daniel on 2017-09-17.
 */
public class DuelArenaListener extends ActivityListener<DuelArenaActivity> {

	DuelArenaListener(DuelArenaActivity activity) {
		super(activity);
	}

	@Override
	public boolean canAttack(Actor attacker, Actor defender) {

		// duel arena is player vs player
		if(attacker.isNpc() || defender.isNpc()) {
			return false;
		}

		Optional<Player> result = activity.getOther(attacker.getPlayer());

		Player player = attacker.getPlayer();

		if(!result.isPresent()) {
			player.send(new SendMessage("Other player does not exist."));
			return false;
		}

		Player opponent = result.get();

		// player can only attack his opponent
		if(!defender.getPlayer().equals(opponent)) {
			player.send(new SendMessage("You cannot attack other players!"));
			return false;
		}

		if(!activity.hasDuelStarted()) {
			player.send(new SendMessage("The duel hasn't started yet!"));
			return false;
		}

		if(activity.getRules().contains(DuelRule.ONLY_FUN_WEAPONS) && !DuelUtils.hasFunWeapon(player, player.equipment.getWeapon())) {
			player.send(new SendMessage("You can only use fun weapons!"));
			return false;
		}

		if(activity.getRules().contains(DuelRule.ONLY_WHIP_DDS)) {
			if(player.equipment.hasWeapon()) {
				Item weapon = player.equipment.getWeapon();
				String name = weapon.getName().toLowerCase();

				if(!name.contains("dragon dagger") && !name.contains("abyssal whip") && !name.contains("abyssal tentacle") && !name.contains("lime whip")) {
					player.send(new SendMessage("You can only use a whip or dragon dagger!"));
					return false;
				}
			}
		}

		CombatType combatType = player.getStrategy().getCombatType();

		if(combatType == CombatType.MELEE && activity.getRules().contains(DuelRule.NO_MELEE)) {
			player.send(new SendMessage("You cannot use melee in the duel arena."));
			return false;
		}

		if(combatType == CombatType.RANGED && activity.getRules().contains(DuelRule.NO_RANGED)) {
			player.send(new SendMessage("You cannot use ranged in the duel arena."));
			return false;
		}

		if(combatType == CombatType.MAGIC && activity.getRules().contains(DuelRule.NO_MAGIC)) {
			player.send(new SendMessage("You cannot use magic in the duel arena."));
			return false;
		}

		return super.canAttack(attacker, defender);
	}
}