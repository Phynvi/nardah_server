package com.nardah.game.world.entity.actor.mob.drop;

import com.nardah.util.RandomUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * The class which represents a mob drop table.
 * @author <a href="http://www.rune-server.org/members/stand+up/">Stand Up</a>
 * @since 29-1-2017.
 */
public final class MobDropTable {

	/**
	 * The mob ids that share this drop table.
	 */
	public final int[] npcIds;

	/**
	 * Determines if this table has access to the rare drop table.
	 */
	private final boolean rareDropTable;

	/**
	 * The cached array of {@link MobDrop}s.
	 */
	public final MobDrop[] drops;

	private final MobDrop[] always;
	private final MobDrop[] common;
	private final MobDrop[] uncommon;
	private final MobDrop[] rare;
	private final MobDrop[] veryRare;

	/**
	 * Constructs a new {@link MobDropTable}.
	 */
	public MobDropTable(int[] npcIds, boolean rareDropTable, MobDrop[] mobDrops, MobDrop[] always, MobDrop[] common, MobDrop[] uncommon, MobDrop[] rare, MobDrop[] veryRare) {
		this.npcIds = npcIds;
		this.rareDropTable = rareDropTable;
		this.always = always;
		this.common = common;
		this.uncommon = uncommon;
		this.rare = rare;
		this.veryRare = veryRare;
		this.drops = mobDrops;
	}

	public List<MobDrop> generate() {
		LinkedList<MobDrop> items = new LinkedList<>();
		int roll = RandomUtils.inclusive(1300);

		if(veryRare.length > 0 && roll < 2) {
			items.addFirst(RandomUtils.random(veryRare));
		} else if(rare.length > 0 && roll < 45) {
			//            if (RandomUtils.success(0.40)) TODO: add rare table
			//                items.add(RandomUtils.random(RARE_TABLE));
			//            else
			items.addFirst(RandomUtils.random(rare));
		} else {
			if(common.length > 0 && roll < 850) {
				items.addFirst(RandomUtils.random(common));
			}
			if(uncommon.length > 0 && roll < 350) {
				items.addFirst(RandomUtils.random(uncommon));
			}
		}

		for(MobDrop drop : always) {
			items.addFirst(drop);
		}
		return items;
	}
}
