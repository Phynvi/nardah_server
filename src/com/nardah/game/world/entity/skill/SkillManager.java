package com.nardah.game.world.entity.skill;

import com.google.common.primitives.Doubles;
import com.nardah.content.WellOfGoodwill;
import com.nardah.content.achievement.AchievementHandler;
import com.nardah.content.achievement.AchievementKey;
import com.nardah.content.event.InteractionEvent;
import com.nardah.content.skill.impl.cooking.Cooking;
import com.nardah.content.skill.impl.crafting.Crafting;
import com.nardah.content.skill.impl.firemaking.Firemaking;
import com.nardah.content.skill.impl.fishing.Fishing;
import com.nardah.content.skill.impl.fletching.Fletching;
import com.nardah.content.skill.impl.herblore.Herblore;
import com.nardah.content.skill.impl.hunter.Hunter;
import com.nardah.content.skill.impl.mining.Mining;
import com.nardah.content.skill.impl.prayer.BoneSacrifice;
import com.nardah.content.skill.impl.runecrafting.Runecraft;
import com.nardah.content.skill.impl.smithing.Smelting;
import com.nardah.content.skill.impl.smithing.Smithing;
import com.nardah.content.skill.impl.thieving.Thieving;
import com.nardah.content.skill.impl.woodcutting.Woodcutting;
import com.nardah.net.packet.out.*;
import com.nardah.util.Utility;
import com.nardah.Config;
import com.nardah.game.Graphic;
import com.nardah.game.UpdatePriority;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.EntityType;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.UpdateFlag;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.prayer.Prayer;
import io.battlerune.net.packet.out.*;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Manages all skills related to an actor.
 * @author Michael | Chex
 * @Edited by Adam / Adameternal123
 */
public class SkillManager {
	
	/**
	 * The actor to manage for.
	 */
	private final Actor actor;
	
	/**
	 * The experience counter.
	 */
	public double experienceCounter;
	
	/**
	 * An array of skills that belong to an actor.
	 */
	private Skill[] skills;
	
	/**
	 * The actor's combat level.
	 */
	private double combatLevel;
	
	/**
	 * Constructs a new {@code SkillManager} object.
	 */
	public SkillManager(Actor actor) {
		this.actor = actor;
		this.skills = new Skill[actor.isPlayer() ? Skill.SKILL_COUNT : 7];
		for(int index = 0; index < skills.length; index++) {
			boolean hitpoints = actor.isPlayer() && index == 3;
			skills[index] = hitpoints ? new Skill(index, 10, 1154) : new Skill(index, 1, 0);
		}
		if(actor.isPlayer()) {
			skills[Skill.HUNTER] = new Hunter(1, 0);
			skills[Skill.COOKING] = new Cooking(1, 0);
			skills[Skill.HERBLORE] = new Herblore(1, 0);
			skills[Skill.CRAFTING] = new Crafting(1, 0);
			skills[Skill.THIEVING] = new Thieving(1, 0);
			skills[Skill.FLETCHING] = new Fletching(1, 0);
			skills[Skill.PRAYER] = new BoneSacrifice(1, 0);
			skills[Skill.FIREMAKING] = new Firemaking(1, 0);
			skills[Skill.RUNECRAFTING] = new Runecraft(1, 0);
			skills[Skill.MINING] = new Mining(1, 0);
			skills[Skill.WOODCUTTING] = new Woodcutting(1, 0);
			skills[Skill.SMITHING] = new Smithing(1, 0);
			skills[Skill.FISHING] = new Fishing(1, 0);
		}
	}
	
	/**
	 * Calculates the combat level of an actor.
	 */
	public static double calculateCombat(int attack, int defence, int strength, int hp, int prayer, int ranged, int magic) {
		final double base_calculation = .25 * (defence + hp + Math.floor(prayer / 2));
		final double melee_calculation = .325 * (attack + strength);
		final double range_calculation = .325 * (Math.floor(ranged / 2) + ranged);
		final double magic_calculation = .325 * (Math.floor(magic / 2) + magic);
		return Math.floor(base_calculation + Doubles.max(melee_calculation, range_calculation, magic_calculation));
	}
	
	/**
	 * Gets the skill for an id.
	 */
	public Skill get(int id) {
		if(id < 0 || id >= skills.length) {
			throw new IllegalArgumentException("The skill id is out of bounds! id=" + id);
		}
		return skills[id];
	}
	
	/**
	 * Gets the level of a skill.
	 */
	public int getLevel(int id) {
		return get(id).getLevel();
	}
	
	/**
	 * Gets the highest possible level of a skill.
	 */
	public int getMaxLevel(int id) {
		return get(id).getMaxLevel();
	}
	
	/**
	 * Sets the level of a skill.
	 */
	public void setLevel(int id, int level) {
		get(id).setLevel(level);
		refresh(id);
	}
	
	/**
	 * Sets the max level of a skill.
	 */
	public void setMaxLevel(int id, int level) {
		setExperience(id, Skill.getExperienceForLevel(level));
		if(actor.isPlayer() && id <= Skill.MAGIC && !actor.getPlayer().quickPrayers.getEnabled().isEmpty()) {
			List<Prayer> deactivate = new LinkedList<>();
			for(Prayer prayer : actor.getPlayer().quickPrayers.getEnabled())
				if(!prayer.canToggle(actor.getPlayer()))
					deactivate.add(prayer);
			if(!deactivate.isEmpty())
				actor.getPlayer().quickPrayers.deactivate(deactivate.toArray(new Prayer[deactivate.size()]));
		}
	}
	
	/**
	 * Sets the experience for a skill.
	 */
	public void setExperience(int id, double experience) {
		Skill skill = get(id);
		int level = Skill.getLevelForExperience(experience);
		skill.setLevel(level);
		skill.setMaxLevel(level);
		skill.setExperience(experience);
		refresh(id);
	}
	
	/**
	 * Sets the experience for a mob skill.
	 */
	public void setNpcMaxLevel(int id, int level) {
		Skill skill = get(id);
		skill.setLevel(level);
		skill.setMaxLevel(level);
	}
	
	/**
	 * Sets the level of a skill.
	 */
	public void modifyLevel(Function<Integer, Integer> modification, int id, int lowerBound, int upperBound) {
		Skill skill = get(id);
		skill.modifyLevel(modification, lowerBound, upperBound);
	}
	
	/**
	 * Handles regressing the skills.
	 */
	public void regress(int skill) {
		Skill s = get(skill);
		if(s.getLevel() > s.getMaxLevel()) {
			
			s.modifyLevel(level -> level - 1, 0, s.getLevel());
			refresh(skill);
		} else if(s.getLevel() < s.getMaxLevel()) {
			s.modifyLevel(level -> level + 1, 0, s.getMaxLevel());
			refresh(skill);
		}
	}
	
	/**
	 * Sets the level of a skill.
	 */
	public void modifyLevel(Function<Integer, Integer> modification, int id) {
		Skill skill = get(id);
		skill.modifyLevel(modification);
	}
	
	/**
	 * Gets the total level of the actor.
	 */
	public int getTotalLevel() {
		int total = 0;
		for(Skill skill : skills) {
			total += skill.getMaxLevel();
		}
		return total;
	}
	
	public long getTotalXp() {
		long totalXp = 0;
		for(Skill skill : skills) {
			totalXp += skill.getExperience();
		}
		return totalXp;
	}
	
	/**
	 * Refreshes all the skills for the actor.
	 */
	public void refresh() {
		for(final Skill skill : skills) {
			refresh(skill.getSkill());
		}
	}
	
	/**
	 * Restores a specific skill.
	 */
	public void restore(int id) {
		Skill skill = get(id);
		skill.setLevel(skill.getMaxLevel());
		refresh(id);
	}
	
	/**
	 * Restores all the skills.
	 */
	public void restoreAll() {
		IntStream.range(0, skills.length).forEach(this::restore);
	}
	
	/**
	 * Handles a player logging in.
	 */
	public void login() {
		Smelting.clearInterfaces(actor.getPlayer());
		refresh();
		setCombatLevel();
	}
	
	/**
	 * Handles mastering all skills. Only players should access this.
	 */
	public void master() {
		for(int index = 0; index < Skill.SKILL_COUNT; index++) {
			setMaxLevel(index, 99);
			if(index < 7) {
				actor.getPlayer().achievedSkills[index] = 99;
				actor.getPlayer().achievedExp[index] = 13034431;
			}
			
		}
		actor.getPlayer().send(new SendMessage("You have successfully mastered all skills."));
		setCombatLevel();
		actor.updateFlags.add(UpdateFlag.APPEARANCE);
	}
	
	/**
	 * Refreshes a skill to a player's client if this class's actor is a player.
	 */
	public void refresh(int id) {
		if(actor.isPlayer()) {
			Skill skill = get(id);
			actor.getPlayer().send(new SendSkill(skill));
		}
	}
	
	/**
	 * Checks if the actor is maxed in all skills.
	 */
	public boolean isMaxed() {
		int maxCount = Skill.SKILL_COUNT;
		int count = 0;
		for(int index = 0; index < maxCount; index++) {
			if(getMaxLevel(index) >= 99) {
				count++;
			} else {
				if(index == Skill.HUNTER)
					count++;
				if(index == Skill.FARMING)
					count++;
				if(index == Skill.CONSTRUCTION)
					count++;
			}
		}
		return count == maxCount;
	}
	
	/**
	 * The interactionEvent listener.
	 */
	public boolean onEvent(InteractionEvent interactionEvent) {
		if(actor.is(EntityType.PLAYER)) {
			Player player = (Player) actor;
			boolean success = false;
			for(final Skill skill : skills) {
				success |= skill.onEvent(player, interactionEvent);
			}
			return success;
		}
		return false;
	}
	
	/**
	 * Adds experience to a given skill. The skill level will increase if the
	 * experience causes the skill to level up.
	 */
	public void addExperience(int id, double experience) {
		addExperience(id, experience, true);
	}
	
	/**
	 * Adds experience to a given skill. The skill level will increase if the
	 * experience causes the skill to level up.
	 */
	public void addExperience(int id, double experience, boolean levelUp) {
		addExperience(id, experience, levelUp, true);
	}
	
	/**
	 * Adds experience to a given skill. The skill level will increase if the
	 * experience causes the skill to level up.
	 */
	public void addExperience(int id, double experience, boolean levelUp, boolean counter) {
		if(!actor.isPlayer() || actor.getPlayer().settings.lockExperience)
			return;
		Player player = (Player) actor;
		Skill skill = get(id);
		double old = skill.getExperience();
		double modified_experience;
		
		if(Config.DOUBLE_EXPERIENCE) {
			modified_experience = experience * 2;
		} else {
			modified_experience = experience * (WellOfGoodwill.isActive() ? 2 : 1);
		}
		/*
		 * if(player.equipment.contains(20366) && Area.inWilderness(player) ||
		 * Area.inWilderness(player) || Area.inWildernessResource(player) ||
		 * Area.inWildernessCourse(player)) { modified_experience = experience * 3;
		 * player.
		 * message("You are now recieving 3x the normal EXP Rate due to your amulet!");
		 * }
		 */
		if(Config.X4_EXPERIENCE) {
			modified_experience = experience * 4;
		}
		// modified_experience *= player.expRate;
		
		int maxLevel = Skill.getLevelForExperience(old);
		SkillData skillData = SkillData.values()[id];
		boolean combatSkill = skillData.isCombatSkill();
		
		if(counter) {
			experienceCounter += modified_experience;
		}
		
		player.send(new SendExpCounter(skill.getSkill(), (int) modified_experience, counter));
		
		if(combatSkill && skill.getMaxLevel() < player.achievedSkills[id]) {
			return;
		}
		
		int newMax = Skill.getLevelForExperience(skill.addExperience(modified_experience));
		updateSkill(player, maxLevel, newMax, skill, levelUp);
		
		if(skill.getExperience() >= 200_000_000) {
			AchievementHandler.activate(player, AchievementKey.EXPERIENCE_MASTERY, 1);
		}
		
		if(combatSkill) {
			player.achievedExp[id] += modified_experience;
			if(maxLevel < 99 && newMax != maxLevel) {
				player.achievedSkills[id] = skill.getMaxLevel();
			}
			updateCombat();
			actor.updateFlags.add(UpdateFlag.APPEARANCE);
		} else {
			//            ClanManager.addExperience(player, experience);
		}
		
		player.send(new SendSkill(skill));
	}
	
	/**
	 * Handles updating a skill.
	 */
	private void updateSkill(Player player, int maxLevel, int newMax, Skill skill, boolean levelUp) {
		if(maxLevel < 99 && newMax != maxLevel) {
			skill.setMaxLevel(newMax);
			skill.modifyLevel(level -> (newMax - maxLevel) + level);
			if(levelUp) {
				// adam should disable levling up stopping skills anaomly
				// player.action.clearNonWalkableActions(); // causes flax task to stop upon
				// leveling up
				showLevelUpInterface(player, skill);
				if(newMax == 99)
					World.sendMessage("<col=7B44B3>Runity: <col=" + player.right.getColor() + ">" + player.getName() + " </col>has just reached level 99 in <col=7B44B3>" + Skill.getName(skill.getSkill()) + "</col>!");
			} else {
				player.send(new SendMessage("Congratulations, you have reached " + Utility.getAOrAn(Skill.getName(skill.getSkill())) + " " + Skill.getName(skill.getSkill()) + " level of " + skill.getMaxLevel() + "."));
			}
			if(skill.getMaxLevel() == 99)
				AchievementHandler.activate(player, AchievementKey.SKILL_MASTERY, 1);
		}
	}
	
	private void showLevelUpInterface(Player player, Skill skill) {
		SkillData skillData = SkillData.values()[skill.getSkill()];
		String line1 = "Congratulations! You've just advanced " + Utility.getAOrAn(skillData.toString()) + " " + skillData + " level!";
		String line2 = "You have reached level " + skill.getMaxLevel() + "!";
		player.send(new SendString(line1, skillData.getFirstLine()));
		player.send(new SendString(line2, skillData.getSecondLine()));
		player.send(new SendChatBoxInterface(skillData.getChatbox()));
		player.send(new SendMessage("Congratulations, you just advanced " + Utility.getAOrAn(skillData.toString()) + " " + skillData + " level."));
		player.graphic(new Graphic(199, UpdatePriority.VERY_HIGH));
		player.dialogueFactory.setActive(true);
	}
	
	/**
	 * Handles updating the combat level.
	 */
	private void updateCombat() {
		int oldLevel = (int) getCombatLevel();
		setCombatLevel();
		int newLevel = (int) getCombatLevel();
		if(newLevel != oldLevel)
			actor.getPlayer().send(new SendMessage("You've reached a combat level of " + newLevel + "."));
	}
	
	/**
	 * Sends all skills to this player's client. If the actor created with this object
	 * is not a player, no code will be executed.
	 */
	public void sendSkills() {
		if(actor.is(EntityType.PLAYER)) {
			Player player = (Player) actor;
			for(Skill skill : skills) {
				player.send(new SendSkill(skill));
			}
		}
	}
	
	public void resetSkilling() {
		boolean doingSkill = false;
		for(Skill skill : skills) {
			if(skill.isDoingSkill()) {
				doingSkill = true;
				skill.setDoingSkill(false);
			}
		}
		if(doingSkill) {
			actor.resetAnimation();
		}
	}
	
	/**
	 * Calculates the combat level of an actor.
	 */
	public double calculateCombat() {
		return calculateCombat(getMaxLevel(Skill.ATTACK), getMaxLevel(Skill.DEFENCE), getMaxLevel(Skill.STRENGTH), getMaxLevel(Skill.HITPOINTS), getMaxLevel(Skill.PRAYER), getMaxLevel(Skill.RANGED), getMaxLevel(Skill.MAGIC));
	}
	
	/**
	 * Gets the experience counter value.
	 */
	public int getExpCounter() {
		return (int) Math.floor(experienceCounter);
	}
	
	/**
	 * Gets the actor's combat level.
	 */
	public double getCombatLevel() {
		return combatLevel;
	}
	
	/**
	 * Sets the actor's combat level.
	 */
	public void setCombatLevel() {
		this.combatLevel = calculateCombat();
	}
	
	/**
	 * Gets the skills of the actor.
	 */
	public Skill[] getSkills() {
		return skills;
	}
	
	/**
	 * Sets the skills of the actor.
	 */
	public void setSkills(Skill[] skills) {
		this.skills = skills;
		if(actor.isPlayer()) {
			this.skills[Skill.HUNTER] = new Hunter(skills[Skill.HUNTER].getLevel(), skills[Skill.HUNTER].getExperience());
			this.skills[Skill.PRAYER] = new BoneSacrifice(skills[Skill.PRAYER].getLevel(), skills[Skill.PRAYER].getExperience());
			this.skills[Skill.COOKING] = new Cooking(skills[Skill.COOKING].getLevel(), skills[Skill.COOKING].getExperience());
			this.skills[Skill.CRAFTING] = new Crafting(skills[Skill.CRAFTING].getLevel(), skills[Skill.CRAFTING].getExperience());
			this.skills[Skill.THIEVING] = new Thieving(skills[Skill.THIEVING].getLevel(), skills[Skill.THIEVING].getExperience());
			this.skills[Skill.HERBLORE] = new Herblore(skills[Skill.HERBLORE].getLevel(), skills[Skill.HERBLORE].getExperience());
			this.skills[Skill.FLETCHING] = new Fletching(skills[Skill.FLETCHING].getLevel(), skills[Skill.FLETCHING].getExperience());
			this.skills[Skill.FIREMAKING] = new Firemaking(skills[Skill.FIREMAKING].getLevel(), skills[Skill.FIREMAKING].getExperience());
			this.skills[Skill.RUNECRAFTING] = new Runecraft(skills[Skill.RUNECRAFTING].getLevel(), skills[Skill.RUNECRAFTING].getExperience());
			this.skills[Skill.MINING] = new Mining(skills[Skill.MINING].getLevel(), skills[Skill.MINING].getExperience());
			this.skills[Skill.SMITHING] = new Smithing(skills[Skill.SMITHING].getLevel(), skills[Skill.SMITHING].getExperience());
			this.skills[Skill.WOODCUTTING] = new Woodcutting(skills[Skill.WOODCUTTING].getLevel(), skills[Skill.WOODCUTTING].getExperience());
			this.skills[Skill.FISHING] = new Fishing(skills[Skill.FISHING].getLevel(), skills[Skill.FISHING].getExperience());
		}
	}
	
	@Override
	public String toString() {
		return super.toString();
	}
	
}
