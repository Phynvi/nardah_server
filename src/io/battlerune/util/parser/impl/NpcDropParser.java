package io.battlerune.util.parser.impl;

import com.google.gson.JsonObject;
import io.battlerune.game.world.entity.actor.npc.drop.NpcDrop;
import io.battlerune.game.world.entity.actor.npc.drop.NpcDropChance;
import io.battlerune.game.world.entity.actor.npc.drop.NpcDropManager;
import io.battlerune.game.world.entity.actor.npc.drop.NpcDropTable;
import io.battlerune.game.world.items.ItemDefinition;
import io.battlerune.util.parser.GsonParser;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Loads npc drops on startup.
 * @author Daniel
 */
public class NpcDropParser extends GsonParser {

	public NpcDropParser() {
		super("def/npc/npc_drops", false);
	}

	@Override
	protected void parse(JsonObject data) {
		final int[] npcIds = builder.fromJson(data.get("id"), int[].class);
		final boolean rareDropTable = data.get("rare_table").getAsBoolean();
		NpcDrop[] npcDrops = builder.fromJson(data.get("drops"), NpcDrop[].class);
		List<NpcDrop> always = new LinkedList<>();
		List<NpcDrop> common = new LinkedList<>();
		List<NpcDrop> uncommon = new LinkedList<>();
		List<NpcDrop> rare = new LinkedList<>();
		List<NpcDrop> veryRare = new LinkedList<>();

		for(NpcDrop drop : npcDrops) {
			ItemDefinition definition = ItemDefinition.get(drop.item);

			if(definition == null)
				continue;

			if(!definition.isStackable() && drop.maximum > 1 && definition.isNoteable()) {
				drop.setItem(definition.getNotedId());
			}

			if(drop.item == 12073) {// elite clue
				veryRare.add(drop);
				drop.setType(NpcDropChance.VERY_RARE);
				continue;
			}

			if(drop.item == 2722) {// hard clue
				rare.add(drop);
				drop.setType(NpcDropChance.RARE);
				continue;
			}

			if(drop.item == 2801) {// medium clue
				uncommon.add(drop);
				drop.setType(NpcDropChance.UNCOMMON);
				continue;
			}

			if(drop.item == 2677) {// easy clue
				common.add(drop);
				drop.setType(NpcDropChance.COMMON);
				continue;
			}

			if(drop.type == NpcDropChance.ALWAYS) {
				always.add(drop);
			} else if(drop.type == NpcDropChance.COMMON) {
				common.add(drop);
			} else if(drop.type == NpcDropChance.UNCOMMON) {
				uncommon.add(drop);
			} else if(drop.type == NpcDropChance.RARE) {
				rare.add(drop);
			} else if(drop.type == NpcDropChance.VERY_RARE) {
				veryRare.add(drop);
			}
		}

		Arrays.sort(npcDrops);

		for(int id : npcIds) {
			NpcDropManager.NPC_DROPS.put(id, new NpcDropTable(npcIds, rareDropTable, npcDrops, always.toArray(new NpcDrop[always.size()]), common.toArray(new NpcDrop[common.size()]), uncommon.toArray(new NpcDrop[uncommon.size()]), rare.toArray(new NpcDrop[rare.size()]), veryRare.toArray(new NpcDrop[veryRare.size()])));
		}
	}
}
