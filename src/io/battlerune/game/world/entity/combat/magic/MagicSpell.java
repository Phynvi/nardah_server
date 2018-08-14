package io.battlerune.game.world.entity.combat.magic;

import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.skill.Skill;
import io.battlerune.game.world.items.Item;
import io.battlerune.net.packet.out.SendMessage;

import java.util.Optional;

/**
 * Represents a combat spell. Created by mmaks on 8/24/2017.
 */
public abstract class MagicSpell {
	
	public final int level;
	
	final double baseExperience;
	
	public final RequiredRune[] runes;
	
	MagicSpell(int level, double baseExperience, RequiredRune... runes) {
		this.level = level;
		this.baseExperience = baseExperience;
		this.runes = runes;
	}
	
	public Optional<Item[]> equipmentRequired() {
		return Optional.empty();
	}
	
	boolean canCast(Actor attacker, Optional<Actor> defender) {
		if(attacker.isNpc()) {
			return true;
		}
		
		Player player = attacker.getPlayer();
		
		if(player.skills.getLevel(Skill.MAGIC) < level) {
			player.send(new SendMessage("You need a Magic level of " + level + " to cast this spell."));
			player.getCombat().reset();
			return false;
		}
		
		if(equipmentRequired().isPresent() && !player.equipment.containsAll(equipmentRequired().get())) {
			player.send(new SendMessage("You do not have the required equipment to cast this spell."));
			player.getCombat().reset();
			return false;
		}
		
		return MagicRune.hasRunes(player, runes);
	}
	
}
