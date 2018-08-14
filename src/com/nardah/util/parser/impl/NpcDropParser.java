package com.nardah.util.parser.impl;

import com.google.gson.JsonObject;
import com.nardah.game.world.entity.actor.mob.drop.MobDrop;
import com.nardah.game.world.entity.actor.mob.drop.MobDropChance;
import com.nardah.game.world.entity.actor.mob.drop.MobDropManager;
import com.nardah.game.world.entity.actor.mob.drop.MobDropTable;
import com.nardah.game.world.items.ItemDefinition;
import com.nardah.util.parser.GsonParser;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Loads mob drops on startup.
 * @author Daniel
 */
public class NpcDropParser extends GsonParser {

	public NpcDropParser() {
		super("def/mob/npc_drops", false);
	}

	@Override
	protected void parse(JsonObject data) {
		final int[] npcIds = builder.fromJson(data.get("id"), int[].class);
		final boolean rareDropTable = data.get("rare_table").getAsBoolean();
		MobDrop[] mobDrops = builder.fromJson(data.get("drops"), MobDrop[].class);
		List<MobDrop> always = new LinkedList<>();
		List<MobDrop> common = new LinkedList<>();
		List<MobDrop> uncommon = new LinkedList<>();
		List<MobDrop> rare = new LinkedList<>();
		List<MobDrop> veryRare = new LinkedList<>();

		for(MobDrop drop : mobDrops) {
			ItemDefinition definition = ItemDefinition.get(drop.item);

			if(definition == null)
				continue;

			if(!definition.isStackable() && drop.maximum > 1 && definition.isNoteable()) {
				drop.setItem(definition.getNotedId());
			}

			if(drop.item == 12073) {// elite clue
				veryRare.add(drop);
				drop.setType(MobDropChance.VERY_RARE);
				continue;
			}

			if(drop.item == 2722) {// hard clue
				rare.add(drop);
				drop.setType(MobDropChance.RARE);
				continue;
			}

			if(drop.item == 2801) {// medium clue
				uncommon.add(drop);
				drop.setType(MobDropChance.UNCOMMON);
				continue;
			}

			if(drop.item == 2677) {// easy clue
				common.add(drop);
				drop.setType(MobDropChance.COMMON);
				continue;
			}

			if(drop.type == MobDropChance.ALWAYS) {
				always.add(drop);
			} else if(drop.type == MobDropChance.COMMON) {
				common.add(drop);
			} else if(drop.type == MobDropChance.UNCOMMON) {
				uncommon.add(drop);
			} else if(drop.type == MobDropChance.RARE) {
				rare.add(drop);
			} else if(drop.type == MobDropChance.VERY_RARE) {
				veryRare.add(drop);
			}
		}

		Arrays.sort(mobDrops);

		for(int id : npcIds) {
			MobDropManager.NPC_DROPS.put(id, new MobDropTable(npcIds, rareDropTable, mobDrops, always.toArray(new MobDrop[always.size()]), common.toArray(new MobDrop[common.size()]), uncommon.toArray(new MobDrop[uncommon.size()]), rare.toArray(new MobDrop[rare.size()]), veryRare.toArray(new MobDrop[veryRare.size()])));
		}
	}
}
