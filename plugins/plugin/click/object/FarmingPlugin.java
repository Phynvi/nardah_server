package plugin.click.object;


import com.nardah.game.event.impl.ItemOnObjectEvent;
import com.nardah.game.event.impl.ObjectClickEvent;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.object.GameObject;
import com.nardah.net.packet.out.SendMessage;

/**
 * @author Ethan Kyle Millard <skype:pumpklins>
 * @since Sun, May 20, 2018 @ 1:48 PM
 */
public class FarmingPlugin extends PluginContext {

	@Override
	protected boolean itemOnObject(Player player, ItemOnObjectEvent event) {
		Item item = event.getUsed();
		GameObject object = event.getObject();
		if (player.getFarming().fillWateringCans(item.getId(), object))
			return true;
		if (player.getFarming().plant(item.getId(), object.getX(), object.getY()))
			return true;
		if (player.getFarming().useItemOnPlant(item.getId(), object.getX(), object.getY()))
			return true;
		switch (event.getObject().getId()) {
			case 7836:
			case 7808:
				int amt = player.inventory.getAmount(6055);
				if(amt > 0) {
					player.inventory.remove(6055, amt);
					player.send(new SendMessage("You put the weed in the compost bin."));
					player.skills.addExperience(Skill.FARMING, 20*amt);
				} else {
					player.send(new SendMessage("You do not have any weeds in your inventory."));
				}
				return true;
		}
		return false;
	}

	@Override
	protected boolean firstClickObject(Player player, ObjectClickEvent event) {
		if (player.getFarming().click(player, event.getObject().getX(), event.getObject().getY(), 1))
			return true;
		return false;
	}
}
