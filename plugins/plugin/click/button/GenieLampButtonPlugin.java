package plugin.click.button;

import java.util.concurrent.TimeUnit;

import com.nardah.content.GenieLamp;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.game.world.items.Item;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.Utility;

public class GenieLampButtonPlugin extends PluginContext {

	@Override
	protected boolean onClick(Player player, int button) {
		if (!GenieLamp.forButton(button).isPresent()) {
			return false;
		}

		if (!player.buttonDelay.elapsed(599, TimeUnit.MILLISECONDS)) {
			return true;
		}

		player.buttonDelay.reset();

		if (!player.inventory.contains(2528)) {
			return true;
		}

		if (!player.interfaceManager.isInterfaceOpen(2808)) {
			player.interfaceManager.close();
			return true;
		}

		GenieLamp genie = GenieLamp.forButton(button).get();
		int base = 750;
		double modified = (base * player.skills.getMaxLevel(genie.getSkill())) / 1.5;
		double experience = modified + Utility.random(3000);
		int random = Utility.random(0, 10);

		player.dialogueFactory.sendOption("Confirm <col=255>" + Skill.getName(genie.getSkill()) + "</col>?", () -> {
			player.interfaceManager.close();
			player.inventory.remove(new Item(2528));
			player.skills.addExperience(genie.getSkill(), experience);
			player.send(new SendMessage(
					"You rub on the lamp... and were given experience in " + Skill.getName(genie.getSkill()) + "."));
			player.setskillingPoints(player.getskillingPoints() + 2);
			player.message("<img=14>You now have @red@" + player.getskillingPoints() + " Skilling Points!");
			if (random == 1) {
				player.message("<img=14>You have been lucky enough to win a Prize! @or2@There is a 1/10 Chance");
				player.message("of winning one!");
				player.inventory.add(6199, 1);
				World.sendMessage("<col=CF2192>NR: </col>Random Mystery Box Won by " + player.getName() + "!");

			} else {
				player.message("<img=14>You have been unfortunate in this occasion, and did not win the mystery box.");
			}
		}, "Nevermind", () -> player.dialogueFactory.clear());
		player.dialogueFactory.execute();
		return true;
	}
}
