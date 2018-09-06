package com.nardah.content.skill.impl.prayer;

import com.nardah.content.activity.randomevent.RandomEventHandler;
import com.nardah.content.dialogue.ChatBoxItemDialogue;
import com.nardah.Config;
import com.nardah.content.event.impl.ItemInteractionEvent;
import com.nardah.content.event.impl.ItemOnObjectInteractionEvent;
import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.action.Action;
import com.nardah.game.action.impl.BuryBoneAction;
import com.nardah.game.action.policy.WalkablePolicy;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.items.ItemDefinition;
import com.nardah.game.world.object.GameObject;
import com.nardah.game.world.position.Position;
import com.nardah.net.packet.out.SendInputAmount;
import com.nardah.net.packet.out.SendMessage;

/**
 * Handles sacrificing bones to an altar.
 * @author Daniel
 */
public class BoneSacrifice extends Skill {

	public BoneSacrifice(int level, double experience) {
		super(Skill.PRAYER, level, experience);
	}

	@Override
	protected double modifier() {
		return Config.PRAYER_MODIFICATION;
	}

	@Override
	protected boolean clickItem(Player player, ItemInteractionEvent event) {
		if(event.getOpcode() != 0)
			return false;
		Item item = event.getItem();
		int slot = event.getSlot();
		if(!BoneData.forId(item.getId()).isPresent())
			return false;
		BoneData bone = BoneData.forId(item.getId()).get();
		new BuryBoneAction(player, bone, slot).start();
		return true;
	}

	@Override
	protected boolean useItem(Player player, ItemOnObjectInteractionEvent event) {
		Item item = event.getItem();
		GameObject object = event.getObject();
		if(object.getId() != 409 && object.getId() != 13193)
			return false;
		if(!BoneData.forId(item.getId()).isPresent())
			return false;
		BoneData bone = BoneData.forId(item.getId()).get();

		if(player.inventory.computeAmountForId(bone.getId()) == 0) {
			player.action.execute(sacrifice(player, object.getPosition(), bone, 1), true);
		} else {
			ChatBoxItemDialogue.sendInterface(player, 1746, bone.getId(), 0, -5, 170);
			player.chatBoxItemDialogue = new ChatBoxItemDialogue(player) {
				@Override
				public void firstOption(Player player) {
					player.action.execute(sacrifice(player, object.getPosition(), bone, 1), true);
				}

				@Override
				public void secondOption(Player player) {
					player.action.execute(sacrifice(player, object.getPosition(), bone, 5), true);
				}

				@Override
				public void thirdOption(Player player) {
					player.send(new SendInputAmount("Enter the amount of bones you would like to sacrifice", 10, input -> player.action.execute(sacrifice(player, object.getPosition(), bone, Integer.parseInt(input)), true)));
				}

				@Override
				public void fourthOption(Player player) {
					player.action.execute(sacrifice(player, object.getPosition(), bone, 28), true);
				}
			};
		}
		return true;
	}

	private Action<Player> sacrifice(Player player, Position position, BoneData bone, int amount) {
		return new Action<Player>(player, 4, true) {
			int ticks = 0;

			@Override
			public void execute() {
				if(!player.inventory.contains(bone.getId())) {
					cancel();
					return;
				}
				Graphic graphic = new Graphic(624, true);
				World.sendGraphic(graphic, position);
				player.animate(new Animation(645, 5));
				player.inventory.remove(bone.getId(), 1);
				player.skills.addExperience(Skill.PRAYER, bone.getExperience() * (modifier() * 1.8));
				player.send(new SendMessage("You sacrifice the " + ItemDefinition.get(bone.getId()).getName() + " ."));
				RandomEventHandler.trigger(player);
				if(++ticks == amount) {
					cancel();
					return;
				}
			}

			@Override
			public String getName() {
				return "Bone sacrifice";
			}

			@Override
			public boolean prioritized() {
				return false;
			}

			@Override
			public WalkablePolicy getWalkablePolicy() {
				return WalkablePolicy.NON_WALKABLE;
			}
		};
	}

}
