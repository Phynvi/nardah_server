package io.battlerune.net.packet.in;

import io.battlerune.content.event.EventDispatcher;
import io.battlerune.content.event.impl.ItemContainerInteractionEvent;
import io.battlerune.game.event.impl.ItemContainerContextMenuEvent;
import io.battlerune.game.plugin.PluginManager;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.codec.ByteModification;
import io.battlerune.net.codec.ByteOrder;
import io.battlerune.net.packet.GamePacket;
import io.battlerune.net.packet.PacketListener;
import io.battlerune.net.packet.PacketListenerMeta;

/**
 * The {@link GamePacket}'s responsible for the options for Items inside a
 * container itemcontainer.
 * @author Daniel | Obey
 */
@PacketListenerMeta({140, 141, 145, 117, 43, 129, 135, 208})
public class ItemContainerActionPacketListener implements PacketListener {

	private static final int FIRST_ITEM_ACTION_OPCODE = 145;
	private static final int SECOND_ITEM_ACTION_OPCODE = 117;
	private static final int THIRD_ITEM_ACTION_OPCODE = 43;
	private static final int FOURTH_ITEM_ACTION_OPCODE = 129;
	private static final int FIFTH_ITEM_ACTION_OPCODE = 135;
	private static final int SIXTH_ITEM_ACTION_OPCODE = 208;
	private static final int ALL_BUT_ONE_ACTION_OPCODE = 140;
	private static final int MODIFIABLE_X_ACTION_OPCODE = 141;

	@Override
	public void handlePacket(Player player, GamePacket packet) {
		switch(packet.getOpcode()) {

			case FIRST_ITEM_ACTION_OPCODE:
				firstAction(player, packet);
				break;

			case SECOND_ITEM_ACTION_OPCODE:
				secondAction(player, packet);
				break;

			case THIRD_ITEM_ACTION_OPCODE:
				thirdAction(player, packet);
				break;

			case FOURTH_ITEM_ACTION_OPCODE:
				fourthAction(player, packet);
				break;

			case FIFTH_ITEM_ACTION_OPCODE:
				fifthAction(player, packet);
				break;

			case SIXTH_ITEM_ACTION_OPCODE:
				sixthAction(player, packet);
				break;

			case ALL_BUT_ONE_ACTION_OPCODE:
				allButOne(player, packet);
				break;

			case MODIFIABLE_X_ACTION_OPCODE:
				modifiableXAction(player, packet);
				break;
		}
	}

	/**
	 * Handles the randomevent when a player clicks on the first option of an item
	 * container itemcontainer.
	 * @param player The player clicking the option.
	 * @param packet The packet for this action.
	 */
	private void firstAction(Player player, GamePacket packet) {
		final int interfaceId = packet.readShort(ByteModification.ADD);
		final int removeSlot = packet.readShort(ByteModification.ADD);
		final int removeId = packet.readShort(ByteModification.ADD);

		if(EventDispatcher.execute(player, new ItemContainerInteractionEvent(1, interfaceId, removeSlot, removeId))) {
			return;
		}

		PluginManager.getDataBus().publish(player, new ItemContainerContextMenuEvent(1, interfaceId, removeSlot, removeId));
	}

	/**
	 * Handles the randomevent when a player clicks on the second option of an item
	 * container itemcontainer.
	 * @param player The player clicking the option.
	 * @param packet The packet for this option.
	 */
	private void secondAction(Player player, GamePacket packet) {
		final int interfaceId = packet.readShort(ByteOrder.LE, ByteModification.ADD);
		final int removeId = packet.readShort(ByteOrder.LE, ByteModification.ADD);
		final int removeSlot = packet.readShort(ByteOrder.LE);

		if(EventDispatcher.execute(player, new ItemContainerInteractionEvent(2, interfaceId, removeSlot, removeId))) {
			return;
		}

		PluginManager.getDataBus().publish(player, new ItemContainerContextMenuEvent(2, interfaceId, removeSlot, removeId));
	}

	/**
	 * Handles the randomevent when a player clicks on the third option of an item
	 * container itemcontainer.
	 * @param player The player clicking the option.
	 * @param packet The packet for this option.
	 */
	private void thirdAction(Player player, GamePacket packet) {
		final int interfaceId = packet.readShort(ByteOrder.LE);
		final int removeId = packet.readShort(ByteModification.ADD);
		final int removeSlot = packet.readShort(ByteModification.ADD);

		if(EventDispatcher.execute(player, new ItemContainerInteractionEvent(3, interfaceId, removeSlot, removeId))) {
			return;
		}

		PluginManager.getDataBus().publish(player, new ItemContainerContextMenuEvent(3, interfaceId, removeSlot, removeId));
	}

	/**
	 * Handles the randomevent when a player clicks on the fourth option of an item
	 * container itemcontainer.
	 * @param player The player clicking the option.
	 */
	private void fourthAction(Player player, GamePacket packet) {
		final int interfaceId = packet.readShort(ByteOrder.LE);
		final int removeId = packet.readShort(ByteModification.ADD);
		final int removeSlot = packet.readShort(ByteModification.ADD);

		if(EventDispatcher.execute(player, new ItemContainerInteractionEvent(4, interfaceId, removeSlot, removeId))) {
			return;
		}

		PluginManager.getDataBus().publish(player, new ItemContainerContextMenuEvent(4, interfaceId, removeSlot, removeId));
	}

	/**
	 * Handles the action when a player clicks on the fifth option of an item
	 * container itemcontainer.
	 * @param player The player clicking the option.
	 * @param packet The packet for this option.
	 */
	private void fifthAction(Player player, GamePacket packet) {
		final int removeSlot = packet.readShort(ByteOrder.LE);
		final int interfaceId = packet.readShort(ByteModification.ADD);
		final int removeId = packet.readShort(ByteOrder.LE);

		player.attributes.set("XREMOVE_SLOT", removeSlot);
		player.attributes.set("XREMOVE_INTERFACE", interfaceId);
		player.attributes.set("XREMOVE_REMOVE", removeId);

		if(EventDispatcher.execute(player, new ItemContainerInteractionEvent(5, interfaceId, removeSlot, removeId))) {
			return;
		}

		PluginManager.getDataBus().publish(player, new ItemContainerContextMenuEvent(5, interfaceId, removeSlot, removeId));
	}

	/**
	 * Handles the randomevent when a player clicks on the sixth option of an item
	 * container itemcontainer.
	 * @param player The player clicking the option.
	 * @param packet The packet for this option.
	 */
	private void sixthAction(Player player, GamePacket packet) {
		final int amount = packet.readInt();

		if(player.enterInputListener.isPresent()) {
			player.enterInputListener.get().accept(Integer.toString(amount));
			return;
		}

		final int interfaceId = player.attributes.get("XREMOVE_INTERFACE", Integer.class);
		final int removeSlot = player.attributes.get("XREMOVE_SLOT", Integer.class);
		final int removeId = player.attributes.get("XREMOVE_REMOVE", Integer.class);

		if(EventDispatcher.execute(player, new ItemContainerInteractionEvent(6, interfaceId, removeSlot, removeId))) {
			return;
		}

		PluginManager.getDataBus().publish(player, new ItemContainerContextMenuEvent(6, interfaceId, removeSlot, removeId));
	}

	/**
	 * Handles the randomevent when a player clicks on the sixth option of an item
	 * container itemcontainer.
	 * @param player The player clicking the option.
	 * @param packet The packet for this option.
	 */
	private void allButOne(Player player, GamePacket packet) {
		final int removeSlot = packet.readShort(ByteModification.ADD);
		final int interfaceId = packet.readShort();
		final int removeId = packet.readShort(ByteModification.ADD);

		PluginManager.getDataBus().publish(player, new ItemContainerContextMenuEvent(7, interfaceId, removeSlot, removeId));
	}

	/**
	 * Handles the randomevent when a player clicks on the sixth option of an item
	 * container itemcontainer.
	 * @param player The player clicking the option.
	 * @param packet The packet for this option.
	 */
	private void modifiableXAction(Player player, GamePacket packet) {
		final int removeSlot = packet.readShort(ByteModification.ADD);
		final int interfaceId = packet.readShort();
		final int removeId = packet.readShort(ByteModification.ADD);
		final int amount = packet.readInt();

		PluginManager.getDataBus().publish(player, new ItemContainerContextMenuEvent(8, interfaceId, removeSlot, removeId, amount));
	}

}
