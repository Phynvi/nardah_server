package com.nardah.net.packet.in;

import com.nardah.content.DropDisplay;
import com.nardah.content.DropSimulator;
import com.nardah.content.ProfileViewer;
import com.nardah.content.famehall.FameHandler;
import com.nardah.content.staff.StaffPanel;
import com.nardah.content.store.impl.PersonalStore;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.MessageColor;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.GamePacket;
import com.nardah.net.packet.PacketListener;
import com.nardah.net.packet.PacketListenerMeta;

@PacketListenerMeta(142)
public class InputFieldPacketListener implements PacketListener {

	@Override
	public void handlePacket(Player player, GamePacket packet) {
		final int component = packet.readInt();
		final String context = packet.getRS2String();

		if(component < 0) {
			return;
		}
		if(PlayerRight.isDeveloper(player) && player.debug) {
			player.send(new SendMessage("[InputField] - Text: " + context + " Component: " + component, MessageColor.DEVELOPER));
		}

		switch(component) {

			/* Clan chat */
			case 42102:
				player.forClan(clan -> {
					if(clan.canManage(clan.getMember(player.getName()).orElse(null))) {
						clan.setName(player, context);
					}
				});
				break;
			case 42104:
				player.forClan(clan -> {
					if(clan.canManage(clan.getMember(player.getName()).orElse(null))) {
						clan.setTag(player, context);
					}
				});
				break;
			case 42106:
				player.forClan(clan -> {
					if(clan.canManage(clan.getMember(player.getName()).orElse(null))) {
						clan.setSlogan(player, context);
						player.message("The new clan slogan is: " + context + ".");
					}
				});
				break;

			case 42108: {
				player.forClan(clan -> {
					if(clan.canManage(clan.getMember(player.getName()).orElse(null))) {
						clan.getManagement().password = context;
						if(context.isEmpty()) {
							player.message("Your clan will no longer use a password.");
						} else {
							player.message("The new clan password is: " + context + ".");
						}
					}
				});
				break;
			}

			/* Personal Store */
			case 38307:
				PersonalStore.changeName(player, context, false);
				break;
			case 38309:
				PersonalStore.changeName(player, context, true);
				break;

			/* Drop simulator */
			case 26810:
				DropSimulator.drawList(player, context);
				break;

			/* Price checker */
			case 48508:
				player.priceChecker.searchItem(context);
				break;

			/* Hall of fame */
			case 58506:
				FameHandler.search(player, context);
				break;

			/* Preset */
			case 57021:
				player.presetManager.name(context);
				break;

			/* Drop display */
			case 54506:
				DropDisplay.search(player, context, DropDisplay.DropType.ITEM);
				break;
			case 54507:
				DropDisplay.search(player, context, DropDisplay.DropType.NPC);
				break;

			/* Staff panel */
			case 36706:
				StaffPanel.search(player, context);
				break;

			/* Friend's Profile view */
			case 353:
				if(World.search(context).isPresent()) {
					ProfileViewer.open(player, World.search(context).get());
					return;
				}

				player.send(new SendMessage("You can not view " + context + "'s profile as they are currently offline."));
				break;

			/* Friend's manage */
			case 354:
				if(PlayerRight.isManagement(player)) {
					if(World.search(context).isPresent()) {
						StaffPanel.search(player, context);
						return;
					}

					player.send(new SendMessage("You can not manage " + context + " as they are currently offline."));
				}
				break;
		}
	}
}
