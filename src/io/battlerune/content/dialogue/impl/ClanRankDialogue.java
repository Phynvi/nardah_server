package io.battlerune.content.dialogue.impl;

import io.battlerune.content.clanchannel.ClanMember;
import io.battlerune.content.clanchannel.ClanRank;
import io.battlerune.content.clanchannel.channel.ClanChannel;
import io.battlerune.content.dialogue.Dialogue;
import io.battlerune.content.dialogue.DialogueFactory;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.net.packet.out.SendMessage;

public class ClanRankDialogue extends Dialogue {

	@Override
	public void sendDialogues(DialogueFactory factory) {
		Player player = factory.getPlayer();
		ClanChannel channel = player.clanChannel;
		if(channel == null)
			return;
		ClanMember playerMember = channel.getMember(player.getName()).orElse(null);
		if(playerMember == null || !channel.canManage(playerMember)) {
			player.send(new SendMessage("You do not have the required rank to do this."));
			return;
		}
		ClanMember member = player.attributes.get("CLAN_RANK_MEMBER", ClanMember.class);
		if(member == null)
			return;
		factory.sendOption("Recruit", () -> factory.onAction(() -> {
			setRank(player, channel, member, ClanRank.RECRUIT);
		}), "Corporal", () -> factory.onAction(() -> {
			setRank(player, channel, member, ClanRank.CORPORAL);
		}), "Sergeant", () -> factory.onAction(() -> {
			setRank(player, channel, member, ClanRank.SERGEANT);
		}), "Lieutenant", () -> factory.onAction(() -> {
			setRank(player, channel, member, ClanRank.LIEUTENANT);
		}), "Next", () -> {
			factory.onAction(() -> {
				factory.sendOption("Captain", () -> factory.onAction(() -> {
					setRank(player, player.clanChannel, member, ClanRank.CAPTAIN);
				}), "General", () -> factory.onAction(() -> {
					setRank(player, player.clanChannel, member, ClanRank.GENERAL);
				}), "Nevermind", () -> factory.onAction(factory::clear));
			});
		}).execute();
	}

	private void setRank(Player player, ClanChannel channel, ClanMember member, ClanRank rank) {
		channel.setRank(member, rank);
		player.dialogueFactory.sendStatement("You have promoted " + member.name + " to " + rank.getString() + "  " + rank.name).execute();
		player.interfaceManager.close();
	}

}
