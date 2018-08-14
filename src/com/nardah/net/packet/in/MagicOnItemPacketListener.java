package com.nardah.net.packet.in;

import com.nardah.content.skill.impl.magic.spell.impl.HighAlchemy;
import com.nardah.content.skill.impl.magic.spell.impl.LowAlchemy;
import com.nardah.content.skill.impl.magic.spell.impl.SuperHeat;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.entity.actor.data.PacketType;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.codec.ByteModification;
import com.nardah.net.packet.ClientPackets;
import com.nardah.net.packet.GamePacket;
import com.nardah.net.packet.PacketListener;
import com.nardah.net.packet.PacketListenerMeta;

/**
 * The {@link GamePacket} responsible for using magic on inventory items.
 * @author Daniel
 */
@PacketListenerMeta(ClientPackets.MAGIC_ON_ITEMS)
public class MagicOnItemPacketListener implements PacketListener {

	@Override
	@SuppressWarnings("unused")
	public void handlePacket(Player player, GamePacket packet) {
		if(player.locking.locked(PacketType.USE_MAGIC))
			return;
		final int slot = packet.readShort();
		final int itemId = packet.readShort(ByteModification.ADD);
		final int childId = packet.readShort();
		final int spell = packet.readShort(ByteModification.ADD);

		if(player.positionChange) {
			return;
		}

		final Item item = player.inventory.get(slot);

		if(item == null || item.getId() != itemId) {
			return;
		}

		if(PlayerRight.isDeveloper(player) && player.debug) {
			player.message("[MagicOnItemPacket] spell=" + spell + " itemId=" + itemId + " slot=" + slot + " childId=" + childId);
		}

		switch(spell) {
			case 1155: // Lvl-1 enchant sapphire
			case 1165: // Lvl-2 enchant emerald
			case 1176: // Lvl-3 enchant ruby
			case 1180: // Lvl-4 enchant diamond
			case 1187: // Lvl-5 enchant dragonstone
			case 6003: // Lvl-6 enchant onyx
				player.spellCasting.enchantItem(itemId, spell);
				break;

			case 1162:
				player.spellCasting.cast(new LowAlchemy(), item);
				break;

			case 1178:
				player.spellCasting.cast(new HighAlchemy(), item);
				break;

			case 1173:
				player.spellCasting.cast(new SuperHeat(), item);
				break;
		}
	}
}
