package com.nardah.content.activity.impl.battlerealm;

import com.google.common.collect.ImmutableList;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.position.Area;
import com.nardah.game.world.position.impl.SquareArea;

import static com.nardah.content.activity.impl.battlerealm.Constants.*;

public class BattleRealmCallers {
	public static ImmutableList<SquareArea> getAreas() {
		return ImmutableList.of(GAME_AREA, LOBBY_AREA, PRE_LOBBY);
	}

	public static boolean canFight(Player attacker, Actor defender) {
		try {
			return (attacker.battleRealmNode.team != defender.getPlayer().battleRealmNode.team) && Area.inBattleRealm(attacker) && Area.inBattleRealm(defender);
		} catch(Exception e) {
			return false;
		}

	}

	public static boolean cantFight(Player attacker, Actor defender) {
		try {
			return (attacker.battleRealmNode.team == defender.getPlayer().battleRealmNode.team) && Area.inBattleRealm(attacker) && Area.inBattleRealm(defender);
		} catch(Exception e) {
			return false;
		}
	}
}
