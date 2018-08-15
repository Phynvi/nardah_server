package com.nardah.content;

import com.nardah.action.impl.ButtonAction;
import com.nardah.game.world.entity.actor.UpdateFlag;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.game.world.position.Area;
import com.nardah.net.packet.out.SendInputAmount;
import com.nardah.net.packet.out.SendMessage;

import java.util.Arrays;
import java.util.Optional;

/**
 * Handles setting a combat skill.
 * @author Daniel
 */
public class SkillSet {
	
	public enum SkillData {
		ATTACK(8654, Skill.ATTACK), STRENGTH(8657, Skill.STRENGTH), DEFENCE(8660, Skill.DEFENCE), RANGED(8663, Skill.RANGED), PRAYER(8666, Skill.PRAYER), MAGIC(8669, Skill.MAGIC), HITPOINTS(8655, Skill.HITPOINTS);
		
		private final int button;
		public final int skill;
		
		SkillData(int button, int skill) {
			this.button = button;
			this.skill = skill;
		}
		
		public static Optional<SkillData> forButton(int button) {
			return Arrays.stream(values()).filter(skillData -> skillData.button == button).findFirst();
		}
	}
	
	public static void init() {
		for(SkillData data : SkillData.values()) {
			ButtonAction click = new ButtonAction() {
				@Override
				public boolean click(Player player, int button) {
					if (Area.inWilderness(player)) {
						player.send(new SendMessage("You can not do this whilst in the wilderness."));
						return true;
					}
					if (player.getCombat().inCombat()) {
						player.send(new SendMessage("You can not do this whilst in combat."));
						return true;
					}
					if (!player.equipment.isEmpty()) {
						player.message("Please remove all your equipment before doing this!");
						return true;
					}
					if (!PlayerRight.isDonator(player)) {
						player.message("You have to be a Donator to be able to set your Melee combat stats!");
						return true;
					}
					// if(player.right.isDeveloper(player)) {
					player.dialogueFactory.sendOption("Set desired level",
							() -> player.dialogueFactory.onAction(() -> SkillSet.set(player, data)), "Set regular level",
							() -> player.dialogueFactory.onAction(() -> {
								player.skills.setMaxLevel(data.skill, player.achievedSkills[data.skill]);
								player.skills.setExperience(data.skill, player.achievedExp[data.skill]);
								player.skills.refresh(data.skill);
								player.skills.setCombatLevel();
								player.updateFlags.add(UpdateFlag.APPEARANCE);
								player.dialogueFactory.sendStatement(
										"You have successfully set your <col=255>" + Skill.getName(data.skill) + "</col> level",
										"back to <col=255>" + player.skills.getMaxLevel(data.skill) + "</col>.",
										"You will gain experience now (unless manually toggled off).").execute();
							}), "How does this work?", () -> {
							}).execute();
					// }
					return true;
				}
			};
			click.register(data.button);
		}
	}
	
	public static void set(Player player, SkillData data) {
		int max = player.achievedSkills[data.skill];
		player.send(new SendInputAmount("Enter your desired <col=255>" + Skill.getName(data.skill) + "</col> level (1-<col=255>" + max + "</col>)", 2, input -> {
			int level = Integer.parseInt(input);
			if(player.skills.getLevel(data.skill) != player.skills.getMaxLevel(data.skill)) {
				player.dialogueFactory.sendStatement("Your level has to be fully restored before changing it.").execute();
				return;
			}
			if(level > max) {
				player.dialogueFactory.sendStatement("You have not unlocked that level yet.", "You must manually achieve it first!").execute();
				return;
			}
			player.skills.setMaxLevel(data.skill, level);
			player.skills.setCombatLevel();
			player.updateFlags.add(UpdateFlag.APPEARANCE);
			player.dialogueFactory.sendStatement("You have successfully set your <col=255>" + Skill.getName(data.skill) + "</col> level", "to <col=255>" + level + "</col>.", "You will not earn any experience unless you revert back to your", "original level. However, the experience counter will still display.").execute();
		}));
	}
}
