package io.battlerune.game.world.entity.combat.strategy.player.custom;

import io.battlerune.game.world.entity.combat.attack.FightType;
import io.battlerune.game.world.entity.combat.strategy.player.PlayerRangedStrategy;
import io.battlerune.game.world.entity.mob.Mob;
import io.battlerune.game.world.entity.mob.npc.NpcAssistant;
import io.battlerune.game.world.entity.mob.player.Player;

public class DragonHunterCrossbowStrategy extends PlayerRangedStrategy {
	private static final DragonHunterCrossbowStrategy INSTANCE = new DragonHunterCrossbowStrategy();

	@Override
	public int modifyAccuracy(Player attacker, Mob defender, int roll) {
		return increase10percent(defender, roll);
	}

	@Override
	public int modifyDamage(Player attacker, Mob defender, int roll) {
		return increase10percent(defender, roll);
	}

	private static int increase10percent(Mob defender, int roll) {
		if(NpcAssistant.isDragon(defender.id))
			return roll * 11 / 10;
		return roll;
	}

	@Override
	public int getAttackDelay(Player attacker, Mob defender, FightType fightType) {
		if(attacker.equipment.contains(10519) || attacker.equipment.contains(10518)) {
			return 2;
		} else {
			return 3;

		}
	}

	public static DragonHunterCrossbowStrategy get() {
		return INSTANCE;
	}

}
