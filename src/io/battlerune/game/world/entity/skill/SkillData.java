package io.battlerune.game.world.entity.skill;

import io.battlerune.util.Utility;

/**
 * The enumerated type whose elements represent data for the skills.
 * @author lare96 <http://github.com/lare96>
 */
public enum SkillData {
	ATTACK(Skill.ATTACK, 6248, 6249, 6247, true), DEFENCE(Skill.DEFENCE, 6254, 6255, 6253, true), STRENGTH(Skill.STRENGTH, 6207, 6208, 6206, true), HITPOINTS(Skill.HITPOINTS, 6217, 6218, 6216, true), RANGED(Skill.RANGED, 5453, 6114, 4443, true), PRAYER(Skill.PRAYER, 6243, 6244, 6242, true), MAGIC(Skill.MAGIC, 6212, 6213, 6211, true), COOKING(Skill.COOKING, 6227, 6228, 6226, false), WOODCUTTING(Skill.WOODCUTTING, 4273, 4274, 4272, false), FLETCHING(Skill.FLETCHING, 6232, 6233, 6231, false), FISHING(Skill.FISHING, 6259, 6260, 6258, false), FIREMAKING(Skill.FIREMAKING, 4283, 4284, 4282, false), CRAFTING(Skill.CRAFTING, 6264, 6265, 6263, false), SMITHING(Skill.SMITHING, 6222, 6223, 6221, false), MINING(Skill.MINING, 4417, 4438, 4416, false), HERBLORE(Skill.HERBLORE, 6238, 6239, 6237, false), AGILITY(Skill.AGILITY, 4278, 4279, 4277, false), THIEVING(Skill.THIEVING, 4263, 4264, 4261, false), SLAYER(Skill.SLAYER, 12123, 12124, 12122, false), FARMING(Skill.FARMING, 4889, 4890, 4887, false), RUNECRAFTING(Skill.RUNECRAFTING, 4268, 4269, 4267, false), CONSTRUCTION(Skill.CONSTRUCTION, 4268, 4269, 4267, false), HUNTER(Skill.HUNTER, 4268, 4269, 4267, false);
	
	/**
	 * The identification for this skill in the skills array.
	 */
	private final int id;
	
	/**
	 * The first line that level up text will be printed on.
	 */
	private final int firstLine;
	
	/**
	 * The second line that level up text will be printed on.
	 */
	private final int secondLine;
	
	/**
	 * The chatbox itemcontainer displayed on level up.
	 */
	private final int chatbox;
	
	/**
	 * The state of the skill being related to combat.
	 */
	private final boolean combatSkill;
	
	/**
	 * Creates a new {@code SkillData}.
	 * @param id the identification for this skill in the skills array.
	 * @param firstLine the first line that level up text will be printed on.
	 * @param secondLine the second line that level up text will be printed on.
	 * @param chatbox the chatbox itemcontainer displayed on level up.
	 * @param combatSkill the state of the skill being related to combat.
	 */
	SkillData(int id, int firstLine, int secondLine, int chatbox, boolean combatSkill) {
		this.id = id;
		this.firstLine = firstLine;
		this.secondLine = secondLine;
		this.chatbox = chatbox;
		this.combatSkill = combatSkill;
	}
	
	/**
	 * Gets the identification for this skill in the skills array.
	 * @return the identification for this skill.
	 */
	public final int getId() {
		return id;
	}
	
	/**
	 * Gets the first line that level up text will be printed on.
	 * @return the first line.
	 */
	public final int getFirstLine() {
		return firstLine;
	}
	
	/**
	 * Gets the second line that level up text will be printed on.
	 * @return the second line.
	 */
	public final int getSecondLine() {
		return secondLine;
	}
	
	/**
	 * Gets the chatbox itemcontainer displayed on level up.
	 * @return the chatbox itemcontainer.
	 */
	public final int getChatbox() {
		return chatbox;
	}
	
	/**
	 * Gets if the skill is combat related.
	 * @return the state.
	 */
	public final boolean isCombatSkill() {
		return combatSkill;
	}
	
	@Override
	public final String toString() {
		return Utility.capitalizeSentence(name().toLowerCase().replace("_", " "));
	}
}
