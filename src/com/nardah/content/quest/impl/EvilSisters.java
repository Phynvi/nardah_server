package com.nardah.content.quest.impl;

import com.nardah.content.dialogue.Expression;
import com.nardah.content.dialogue.DialogueFactory;
import com.nardah.content.event.impl.NpcInteractionEvent;
import com.nardah.content.quest.Quest;
import com.nardah.content.quest.QuestManager;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.strategy.npc.boss.skotizo.SkotizoUtility.SpawnData;
import com.nardah.game.world.entity.actor.Direction;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.game.world.items.Item;
import com.nardah.net.packet.out.SendString;

/**
 * Handles the cook's assistant quest.
 * @author adameternal123
 */
public class EvilSisters extends Quest {

	@Override
	public String name() {
		return "Evil Sisters";
	}

	@Override
	public String created() {
		return "2018/03/27";
	}

	@Override
	public int questPoint() {
		return 2;
	}

	@Override
	public int index() {
		return QuestManager.EVIL_SISTERS;
	}

	@Override
	public void update(Player player) {
		clean(player);
		switch(getStage(player)) {
			case 0:
				player.send(new SendString("To start this quest please speak to Scared Skavid.", 37116));
				player.send(new SendString("Brother to the evil sisters. Located at varrock.", 37117));

				break;
			case 1:
				player.send(new SendString("I have talked with Scared Skavid and he explained what", 37112));
				player.send(new SendString("must be done to save Nardah and lift the curse!", 37113));
				player.send(new SendString("This will open the gateway to the evil sisters lair!", 37114));
				break;
			case 2:
				player.send(new SendString("I have given Scared Skavid the golden goblet, and he has opened the portal.", 37115));
				break;
			case 3:
				player.send(new SendString("I should speak to Scared Skavid, I think I'm ready to fight his evil sisters", 37115));
				player.send(new SendString("It would be ideal to prepare by bringing food and gear.", 37116));
				player.send(new SendString("I am going to slay all three of the ugly whores.", 37117));
				break;
			case 4:
				player.send(new SendString("QUEST COMPLETED", 37111));
				player.send(new SendString("<col=000080>You have completed this quest!", 37112));
				player.send(new SendString("<col=000080>You were awarded:", 37113));
				player.send(new SendString("1 quest point", 37115));
				player.send(new SendString("Anubis Cape & 100K Experience in Str, Atk & Defence.", 37116));
				player.send(new SendString("10 Million gold coins", 37117));
				break;
		}
	}

	@Override
	public void sendDialogues(DialogueFactory factory) {
		Player player = factory.getPlayer();
		switch(getStage(player)) {
			case 0:
				factory.sendPlayerChat("Hello?....Are you ok?").sendNpcChat(4374, Expression.ALMOST_CRYING, "oh #name, my sisters have done a terrible thing!", "They have cursed me, with a very strong magical spell", "I know what they've done to me can't be undone but you must stop THEM!!", "they are wicked and are a threat to everyone on NR!", "Help me?!?!").sendOption("Sure", () -> {
					setStage(player, 1);
					player.interfaceManager.close();
					player.dialogueFactory.sendDialogue(this);
				}, "No, I'm too busy right now", () -> {
					factory.sendPlayerChat("No, I'm too busy right now").sendNpcChat(4374, Expression.ALMOST_CRYING, "*sigh*", "you're a little wimp. can't take on three little childish girls *smh*");
				}).execute();
				break;
			case 1:
				Item[] needed = {new Item(11210)};
				if(player.inventory.containsAny(needed) && !player.inventory.containsAll(needed)) {
					factory.sendPlayerChat("So what do you need me to do?").sendNpcChat(4374, Expression.HAPPY, "I have been able to practise the magical arts, and I need one more ingrediant", " a Golden goblet and then i will be able to lift their magical barrier thus allowing you can to them!").sendPlayerChat("Alright, I will be right back.").execute();
					player.message("You can find this golden goblet at", "@red@ Varrock Palace", "Make sure to search for it!");

					return;
				}
				if(player.inventory.containsAll(needed)) {
					setStage(player, 2);
					player.inventory.removeAll(needed);
					player.dialogueFactory.sendStatement("You have handed over all your items.").execute();
					return;
				}
				factory.sendPlayerChat("So what do you need me to do?").sendNpcChat(4374, Expression.HAPPY, "I need the following items:", "1x Shield of Argoth, 1x Staff of Desctruction, 1x Divine Amulet and 1x Ring of Psychosis.").sendPlayerChat("Alright, I will get you these items.").execute();
				break;
			case 2:
				factory.sendPlayerChat("So what happens now, when do i bend your sisters over the table?").sendNpcChat(4374, Expression.HAPPY, "You wouldn't be able to handle Them, anyways i need you to burn them all! Burn Them all!!!").sendPlayerChat("Alright, I'm ready for this! My father trained me for this day!!.").execute();
				setStage(player, 3);
				TrioSisters = true;
				anubisSpawn();
				player.message("<col=8714E6> the trio sisters have shown themselves!", "<col=8714E6> do ::trio to enter their lair and slay them!");
				break;

			case 3:
				factory.sendNpcChat(4374, Expression.HAPPY, "Thank you so much #name!", "Please take this as a reward").onAction(() -> {

					complete(player);

				}).execute();

				break;
			default:
				factory.sendStatement("You have completed the quest", "the curse has been lifted, the whore sisters do not lurk these lands no more.").execute();
				break;
		}
	}

	public static boolean TrioSisters;
	public static boolean TrioSistersded = true;

	/**
	 * Constructs an instanced zone, for the minigame, and once completed quest ==
	 * completed.
	 **/

	static Mob anubisSpawn() {
		if(TrioSisters = true) {
			SpawnData spawn = SpawnData.generate();
			Mob anubis = new Mob(7286, spawn.position, 10, Direction.NORTH);
			anubis.register();
			anubis.definition.setRespawnTime(-1);
			anubis.definition.setAggressive(true);
			anubis.speak("Darkness is here to penetrate your souls!");
			return anubis;

		} else {
			return null;
		}

	}

	static Mob rubyspawn() {
		if(TrioSisters = true) {
			SpawnData spawn = SpawnData.generate();
			Mob rubyspawn = new Mob(7286, spawn.position, 10, Direction.NORTH);
			rubyspawn.register();
			rubyspawn.definition.setRespawnTime(-1);
			rubyspawn.definition.setAggressive(true);
			rubyspawn.speak("Darkness is here to penetrate your souls!");
			return rubyspawn;

		} else {
			return null;
		}

	}

	static Mob sistersenki() {
		if(TrioSisters = true) {
			SpawnData spawn = SpawnData.generate();
			Mob sistersenki = new Mob(7286, spawn.position, 10, Direction.NORTH);
			sistersenki.register();
			sistersenki.definition.setRespawnTime(-1);
			sistersenki.definition.setAggressive(true);
			sistersenki.speak("Darkness is here to penetrate your souls!");
			return sistersenki;

		} else {
			return null;
		}

	}

	@Override
	public void reward(Player player) {
		player.skills.addExperience(Skill.COOKING, 50000, false);
		player.inventory.add(new Item(20527, 100_000), -1, true);
		player.send(new SendString("50,000 cooking experience", 12150));
		player.send(new SendString("100,000 Royale tokens", 12151));
		player.send(new SendString("1 Quest point", 12152));
		player.send(new SendString("", 12153));
		player.send(new SendString("", 12154));
		player.send(new SendString("", 12155));
	}

	@Override
	protected boolean clickNpc(Player player, NpcInteractionEvent event) {
		if(event.getOpcode() == 0 && event.getMob().id == 4374) {
			player.dialogueFactory.sendDialogue(this);
			return true;
		}
		return false;
	}
}
