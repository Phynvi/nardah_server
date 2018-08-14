package com.nardah.content.skill.impl.magic.spell.impl;

import com.nardah.content.activity.randomevent.RandomEventHandler;
import com.nardah.Config;
import com.nardah.content.skill.impl.magic.Magic;
import com.nardah.content.skill.impl.magic.Spellbook;
import com.nardah.content.skill.impl.magic.spell.Spell;
import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.game.world.items.Item;
import com.nardah.net.packet.out.SendMessage;

public class BonesToBananas implements Spell {

	@Override
	public String getName() {
		return "Bones to bananas";
	}

	@Override
	public Item[] getRunes() {
		return new Item[]{new Item(557, 2), new Item(555, 2), new Item(561, 1)};
	}

	@Override
	public int getLevel() {
		return 15;
	}

	@Override
	public void execute(Player player, Item item) {
		if(player.spellbook != Spellbook.MODERN)
			return;

		int bone = 0;

		for(final int bones : Magic.BONES) {
			if(player.inventory.contains(bones)) {
				bone = bones;
				break;
			}
		}

		if(bone == 0) {
			player.send(new SendMessage("You have no bones to do this!"));
			return;
		}

		final int amount = player.inventory.computeAmountForId(bone);

		player.inventory.remove(bone, amount);
		player.inventory.add(new Item(1963, amount), -1, true);
		player.inventory.removeAll(getRunes());
		player.animate(new Animation(722));
		player.graphic(new Graphic(141, true));
		player.skills.addExperience(Skill.MAGIC, 25 * Config.MAGIC_MODIFICATION);
		player.send(new SendMessage("You have converted " + amount + " bones to bananas."));
		RandomEventHandler.trigger(player);
	}
}
