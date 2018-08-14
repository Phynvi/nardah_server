package io.battlerune.content;

import io.battlerune.game.world.InterfaceConstants;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.items.Item;
import io.battlerune.game.world.items.containers.ItemContainer;
import io.battlerune.net.packet.out.SendItemOnInterface;
import io.battlerune.net.packet.out.SendString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.battlerune.game.world.InterfaceConstants.TOOLKIT_INTERFACE;
import static io.battlerune.util.ItemIdentifiers.*;

/**
 * @author Ethan Kyle Millard <skype:pumpklins>
 * @since Wed, June 13, 2018 @ 7:55 PM
 */
public class Toolkit extends ItemContainer {
	
	/**
	 * The {@link Player} using the {@link Toolkit}.
	 */
	private Player player;
	
	/**
	 * The place holder flag.
	 */
	public boolean placeHolder;
	
	/**
	 * The tools stored in the {@link Toolkit}.
	 */
	public static final List<Item> TOOLS = new ArrayList<>(Arrays.asList(new Item(HAMMER),
			//			new Item(SHEARS),
			//			new Item(CHISEL),
			//			new Item(TINDERBOX),
			//			new Item(KNIFE),
			new Item(SAW),
			//			new Item(PESTLE_AND_MORTAR),
			new Item(FISHING_ROD), new Item(FLY_FISHING_ROD), new Item(LOBSTER_POT), new Item(HARPOON), new Item(SMALL_FISHING_NET), new Item(BIG_FISHING_NET), new Item(RAKE), new Item(SEED_DIBBER), new Item(SPADE), new Item(SECATEURS),
			//			new Item(MAGIC_SECATEURS),
			//			new Item(GLASSBLOWING_PIPE),
			//			new Item(NEEDLE),
			new Item(BRONZE_PICKAXE),
			//			new Item(IRON_PICKAXE),
			//			new Item(MITHRIL_PICKAXE),
			//			new Item(ADAMANT_PICKAXE),
			//			new Item(RUNE_PICKAXE),
			new Item(BRONZE_AXE),
			//			new Item(IRON_AXE),
			//			new Item(MITHRIL_AXE),
			//			new Item(ADAMANT_AXE),
			//			new Item(RUNE_AXE),
			new Item(AMMO_MOULD), new Item(NECKLACE_MOULD), new Item(AMULET_MOULD), new Item(BRACELET_MOULD), new Item(RING_MOULD)));
	
	/**
	 * Constructs a {@link Toolkit} for the given {@link Player}.
	 * @param player {@link Player} player
	 */
	public Toolkit(Player player) {
		super(33, StackPolicy.ALWAYS);
		this.player = player;
		this.placeHolder = true;
	}
	
	/**
	 * Opens the {@link Toolkit}.
	 */
	public void open() {
		refresh(player, TOOLKIT_INTERFACE);
		player.attributes.set("TOOLKIT_KEY", Boolean.TRUE);
		player.interfaceManager.open(25000);
	}
	
	public void fill(int id) {
		if(contains(id)) {
			replace(id, id, true);
			return;
		}
		add(id, 1);
		refresh();
	}
	
	private void refresh() {
		refresh(player, TOOLKIT_INTERFACE);
	}
	
	@Override
	public void onRefresh() {
		player.inventory.refresh();
		player.send(new SendString("Toolkit size: " + this.size(), 25007));
		player.send(new SendItemOnInterface(InterfaceConstants.INVENTORY_STORE, player.inventory.toArray()));
	}
	
	/**
	 * Returns the {@link Player} using the {@link Toolkit}.
	 * @return {@link Player}
	 */
	public Player getPlayer() {
		return player;
	}
}