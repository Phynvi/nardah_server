package plugin.click.item;

import com.nardah.Config;
import com.nardah.content.activity.Activity;
import com.nardah.content.skill.impl.magic.Spellbook;
import com.nardah.content.skill.impl.magic.teleport.Teleportation;
import com.nardah.content.teleport.Teleport;
import com.nardah.game.event.impl.ItemClickEvent;
import com.nardah.game.event.impl.ItemContainerContextMenuEvent;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.items.containers.equipment.Equipment;
import com.nardah.game.world.position.Area;
import com.nardah.game.world.position.Position;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.Utility;

public class AmuletOfGloryPlugin extends PluginContext {

    private static final int[] AMULETS = {1704, 1706, 1708, 1710, 1712, 11976, 11978};

    public void teleport(Player player, Item item, Position position, int index, boolean equipment) {
        index = index - 1;

        if(!player.interfaceManager.isClear()) {
            player.interfaceManager.close(false);
        }

        if(Activity.evaluate(player, it -> !it.canTeleport(player))) {
            return;
        }

        if(player.wilderness > 30 && !PlayerRight.isPriviledged(player)) {
            player.send(new SendMessage("You can't teleport past " + 30 + " wilderness!"));
            return;
        }

        if(player.isTeleblocked()) {
            player.message("You are currently under the affects of a teleblock spell and can not teleport!");
            return;
        }

        boolean wilderness = Area.inWilderness(position);

        if(wilderness && player.pet != null) {
            player.dialogueFactory.sendNpcChat(player.pet.id, "I'm sorry #name,", "but I can not enter the wilderness with you!").execute();
            return;
        }

        /*
         * if (wilderness && player.playTime < 3000) { player.
         * message("You cannot enter the wilderness until you have 30 minutes of playtime. "
         * + Utility.getTime((3000 - player.playTime) * 3 / 5) + " minutes remaining.");
         * return false; }
         */

        Teleportation.TeleportationData type = Teleportation.TeleportationData.HOME;

        Teleportation.teleportNoChecks(player, position, type);

        if (equipment) {
            player.equipment.set(Equipment.AMULET_SLOT, new Item(AMULETS[index]), true);
        } else {
            player.inventory.remove(item);
            player.inventory.add(AMULETS[index], 1);
        }
        player.message("<col=7F007F>" + (index == 0 ? "You have used your last charge."
                : "Your amulet of glory has " + Utility.convertWord(index).toLowerCase() + "charge"
                + (index == 1 ? "" : "s") + " remaining."));
    }

    private int getIndex(int item) {
        int index = -1;
        for (int amulet = 0; amulet < AMULETS.length; amulet++) {
            if (item == AMULETS[amulet]) {
                return amulet;
            }
        }
        return index;
    }

    @Override
    protected boolean thirdClickItem(Player player, ItemClickEvent event) {
        Item item = event.getItem();
        int index = getIndex(item.getId());

        if (index == -1) {
            return false;
        }

        if (index == 0) {
            player.message("Your amulet of glory has no charges left!");
            return true;
        }

        player.dialogueFactory.sendOption("Edgeville", () -> {

            if (player.wilderness > 30 && !PlayerRight.isPriviledged(player)) {
                player.message("@or2@you can't teleport above 30 wilderness");
            } else {
                teleport(player, item, new Position(3087, 3496), index, false);
            }
        }, "Karamja", () -> {
            if (player.wilderness > 30 && !PlayerRight.isPriviledged(player)) {
                player.message("@or2@you can't teleport above 30 wilderness");
            } else {
                teleport(player, item, new Position(2918, 3176), index, false);
            }
        }, "Dranyor Village", () -> {

            if (player.wilderness > 30 && !PlayerRight.isPriviledged(player)) {
                player.message("@or2@you can't teleport above 30 wilderness");
            } else {
                teleport(player, item, new Position(3105, 3251), index, false);
            }
        }, "Al-Kahrid", () -> {
            if (player.wilderness > 30 && !PlayerRight.isPriviledged(player)) {
                player.message("@or2@you can't teleport above 30 wilderness");
            } else {
                teleport(player, item, new Position(3293, 3184), index, false);
            }
        }, "Nowhere", player.interfaceManager::close).execute();
        return true;
    }

    @Override
    protected boolean secondClickItemContainer(Player player, ItemContainerContextMenuEvent event) {
        final int interfaceId = event.getInterfaceId();

        if (interfaceId != 1688) {
            return false;
        }

        final int removeId = event.getRemoveId();
        int index = getIndex(removeId);

        if (index == -1) {
            return false;
        }

        if (index == 0) {
            player.message("Your amulet of glory has no charges left!");
            return true;
        }

        Item item = player.equipment.getAmuletSlot();

        if (item == null) {
            return true;
        }

        player.dialogueFactory.sendOption("Edgeville", () -> {
            teleport(player, item, new Position(3087, 3496), index, true);
        }, "Karamja", () -> {
            teleport(player, item, new Position(2918, 3176), index, true);
        }, "Dranyor Village", () -> {
            teleport(player, item, new Position(3105, 3251), index, true);
        }, "Al-Kahrid", () -> {
            teleport(player, item, new Position(3293, 3184), index, true);
        }, "Nowhere", player.interfaceManager::close).execute();
        return true;
    }
}
