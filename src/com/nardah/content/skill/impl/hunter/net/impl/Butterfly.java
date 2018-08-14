package com.nardah.content.skill.impl.hunter.net.impl;

import com.nardah.content.skill.SkillRepository;

import java.util.Arrays;
import java.util.Optional;

public enum Butterfly {
	RUBY(5556, 15, 24, 10020), SAPPHIRE(5555, 25, 34, 10018), SNOWY(5554, 35, 44, 10016), BLACKWARLOCK(5553, 45, 54, 10014);

	private final int npc;
	private final int level;
	private final int experience;
	private final int item;

	Butterfly(int npc, int level, int experience, int item) {
		this.npc = npc;
		this.level = level;
		this.experience = experience;
		this.item = item;
	}

	public int getNpc() {
		return npc;
	}

	public int getLevel() {
		return level;
	}

	public int getExperience() {
		return experience;
	}

	public int getItem() {
		return item;
	}

	public static Optional<Butterfly> forId(int butterfly) {
		return Arrays.stream(values()).filter(a -> a.npc == butterfly).findAny();
	}

	public static void addList() {
		Arrays.stream(values()).forEach($it -> SkillRepository.HUNTER_SPAWNS.add($it.getNpc()));
	}
}
