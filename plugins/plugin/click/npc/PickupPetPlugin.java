package plugin.click.npc;

import io.battlerune.content.pet.PetData;
import io.battlerune.game.Animation;
import io.battlerune.game.event.impl.NpcClickEvent;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.packet.out.SendMessage;

public class PickupPetPlugin extends PluginContext {

	@Override
	protected boolean secondClickNpc(Player player, NpcClickEvent event) {
		if (!PetData.forNpc(event.getNpc().id).isPresent()) {
			return false;
		}

		if (event.getNpc().owner != null && event.getNpc().owner != player) {
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

		PetData pets = PetData.forNpc(event.getNpc().id).get();
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
