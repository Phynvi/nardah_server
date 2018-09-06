package plugin.click.button;

import com.nardah.Config;
import com.nardah.content.skill.impl.magic.teleport.Teleportation;
import com.nardah.content.store.Store;
import com.nardah.content.tittle.TitleManager;
import com.nardah.game.plugin.PluginContext;
import com.nardah.game.world.entity.actor.player.Player;

public class DonatorButtonPlugin extends PluginContext {

	/**
	 * @author Adam_#6723
	 */
	@Override
	protected boolean onClick(Player player, int button) {
		if (button == -8328) {
			player.donatorDeposit.confirm();
			return true;
		}
		if (button == -15115) {
			Teleportation.teleport(player, Config.DONATOR_ZONE);
			return true;
		}

		if (button == -15111) {
			TitleManager.open(player);
			player.message("Here donators can set their titles!");
		}
		if (button == -15107) {
			Store.STORES.get("Pet Store").open(player);
			player.message(
					"Donator's can purchase the pets from here! They are untradeable but have amazing benefits!");
		}

		if (button == -15099) {
			Store.STORES.get("Pk Rewards Shop 1").open(player);
			// player.message("Donator's can purchase the pets from here! They are
			// untradeable but have amazing benefits!");
		}

		if (button == -15095) {
			player.interfaceManager.open(15500);
			player.message("This feature has been disabled, alternatively you can use the command ::bank");

			/*
			 * int length = PlayerRight.isDonator(player) ? 60 : 2; if
			 * (!player.BankerPetDelay.elapsed(length, TimeUnit.MINUTES)) {
			 * player.dialogueFactory.sendNpcChat(396, "You can only do this once every " +
			 * length + " minutes!", "Time Passed: " +
			 * Utility.getTime(player.BankerPetDelay.elapsedTime())).execute(); return true;
			 * } Pets.onSpawn(player, 83, true); player.
			 * message("You have been granted a personalised Banker NPC For the next 30 Minutes!"
			 * );
			 */

		}

		if (button == -15103) {

			player.message("This feature has been disabled, alternatively you can drink from");
			player.message("the Ornate Rejuvenation Pool at home. ");

			/*
			 * if (!player.restoreDelay.elapsed(15, TimeUnit.MINUTES) &&
			 * !player.getCombat().inCombat()) { player.dialogueFactory.sendNpcChat(1152,
			 * "You can only do this once every " + 2 + " minutes!", "Time Passed: " +
			 * Utility.getTime(player.restoreDelay.elapsedTime())).execute(); return false;
			 * }
			 * 
			 * player.runEnergy = 100; player.send(new SendRunEnergy());
			 * player.skills.restoreAll(); CombatSpecial.restore(player, 100);
			 * player.dialogueFactory.sendNpcChat(1152,
			 * "Your health & special attack have been restored!").execute();
			 * player.restoreDelay.reset();
			 * 
			 * 
			 * 
			 */
		}

		return false;
	}
}
