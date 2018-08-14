package plugin.click.npc;

import com.nardah.content.pet.PetData;
import com.nardah.game.Animation;
import com.nardah.game.event.impl.NpcClickEvent;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.out.SendMessage;

public class PickupPetPlugin extends PluginContext {

	@Override
	protected boolean secondClickNpc(Player player, NpcClickEvent event) {
		if (!PetData.forNpc(event.getMob().id).isPresent()) {
			return false;
		}

		if (event.getMob().owner != null && event.getMob().owner != player) {
			player.send(new SendMessage("This is not your pet!"));
			return true;
		}

		if (player.pet == null) {
			return true;
		}

		if (player.inventory.remaining() == 0) {
			player.send(new SendMessage("You need at least 1 free inventory space to do this."));
			return true;
		}

		PetData pets = PetData.forNpc(event.getMob().id).get();
		player.interact(player.pet);
		player.animate(new Animation(827));
		World.schedule(1, () -> {
			player.pet.unregister();
			player.pet = null;
			player.inventory.add(pets.getItem(), 1);
		});
		return true;
	}

}
