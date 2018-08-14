package io.battlerune.net.packet.in;

import io.battlerune.content.achievement.AchievementHandler;
import io.battlerune.content.activity.Activity;
import io.battlerune.game.world.InterfaceConstants;
import io.battlerune.game.world.entity.mob.data.PacketType;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.mob.player.PlayerRight;
import io.battlerune.game.world.items.Item;
import io.battlerune.game.world.items.containers.equipment.EquipmentType;
import io.battlerune.net.codec.ByteModification;
import io.battlerune.net.packet.GamePacket;
import io.battlerune.net.packet.PacketListener;
import io.battlerune.net.packet.PacketListenerMeta;
import io.battlerune.net.packet.out.SendMessage;
import io.battlerune.util.MessageColor;

/**
 * The {@code GamePacket} responsible for wielding items.
 * @author Daniel | Obey
 */
@PacketListenerMeta(41)
public class WieldItemPacketListener implements PacketListener {

	/**
	 * Array of all the max cape identification.
	 */
	private static final int[] MAX_CAPE_AND_HOOD = {13280, 13337, 13333, 13335, 13329, 20760, 13331, 21285, 13281, 13330, 13332, 13334, 13336, 13338, 20764, 21282};

	@Override
	public void handlePacket(Player player, GamePacket packet) {
		if(player.locking.locked(PacketType.WIELD_ITEM))
			return;

		final int wearId = packet.readShort();
		final int wearSlot = packet.readShort(ByteModification.ADD);
		final int interfaceId = packet.readShort(ByteModification.ADD);

		switch(interfaceId) {

			case InterfaceConstants.INVENTORY_INTERFACE:
				final Item item = player.inventory.get(wearSlot);

				if(item == null || item.getId() != wearId) {
					return;
				}

				if(!item.isEquipable()) {
					return;
				}

				if(Activity.evaluate(player, it -> !it.canEquipItem(player, item, item.getEquipmentType()))) {
					return;
				}

				if(!player.interfaceManager.isClear() && !player.interfaceManager.isInterfaceOpen(15106)) {
					player.interfaceManager.close(false);
				}

				if(player.right.equals(PlayerRight.OWNER) && player.debug) {
					player.send(new SendMessage("[WearItem] - [id= " + wearId + "] [slot= " + wearSlot + "] [itemcontainer " + interfaceId + "]", MessageColor.DEVELOPER));
				}

				if(item.getEquipmentType() == EquipmentType.NOT_WIELDABLE) {
					player.send(new SendMessage("This item cannot be worn."));
					return;
				}

				for(int maxCape : MAX_CAPE_AND_HOOD) {
					if(item.getId() == maxCape && !player.skills.isMaxed()) {
						player.message("You can not wield this item until you have 99 in all your skills!");
						return;
					}
				}

				if(item.getId() == 13069 || item.getId() == 13070) {
					if(!AchievementHandler.completedAll(player)) {
						player.send(new SendMessage("You need to have completed all the achievements to wear this."));
						return;
					}
				}

				player.equipment.equip(wearSlot);
		}
	}
}
