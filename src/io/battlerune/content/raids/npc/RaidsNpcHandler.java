package io.battlerune.content.raids.npc;

import io.battlerune.content.raids.npc.impl.LizardShaman;

import java.util.HashMap;
import java.util.Map;

public class RaidsNpcHandler {

	public static Map<RaidsNpcData, RaidsNpc> npcs = new HashMap<>();

	public static void load() {

		npcs.put(RaidsNpcData.LIZARD_SHAMAN, new LizardShaman());
	}
}
