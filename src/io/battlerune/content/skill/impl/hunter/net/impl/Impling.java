package io.battlerune.content.skill.impl.hunter.net.impl;

import io.battlerune.content.skill.SkillRepository;

import java.util.Arrays;
import java.util.Optional;

/**
 * Holds all the impling data.
 * @author Daniel
 */
public enum Impling {
	BABY_IMPLING(1635, 1, 25, 11238), YOUNG_IMPLING(1636, 22, 65, 11240), GOURMET_IMPLING(1637, 28, 113, 11242), EARTH_IMPLING(1638, 36, 177, 11244), ESSENCE_IMPLING(1639, 42, 255, 11246), ECLECTIC_IMPLING(1640, 50, 289, 11248), NATURE_IMPLING(1641, 58, 353, 11250), MAGPIE_IMPLING(1642, 65, 409, 11252), NINJA_IMPLING(1643, 74, 481, 11254), DRAGON_IMPLING(1644, 83, 553, 11256);

	private final int impling;
	private final int level;
	private final int experience;
	private final int reward;

	Impling(int impling, int level, int experience, int reward) {
		this.impling = impling;
		this.level = level;
		this.experience = experience;
		this.reward = reward;
	}

	public int getImpling() {
		return impling;
	}

	public int getLevel() {
		return level;
	}

	public int getExperience() {
		return experience;
	}

	public int getReward() {
		return reward;
	}

	public static Optional<Impling> forId(int impling) {
		return Arrays.stream(values()).filter(a -> a.impling == impling).findAny();
	}

	public static void addList() {
		Arrays.stream(values()).forEach($it -> SkillRepository.HUNTER_SPAWNS.add($it.getImpling()));
	}
}