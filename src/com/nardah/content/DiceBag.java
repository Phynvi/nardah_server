package com.nardah.content;

import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.position.Area;
import com.nardah.game.world.position.Position;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.Utility;

import java.util.concurrent.TimeUnit;

/**
 * Handles the operating the dicing bag.
 * @author Daniel.
 */
public class DiceBag {
	
	/**
	 * The dice animation.
	 */
	private static final Animation ANIMATION = new Animation(7219);
	
	/**
	 * The dice graphic.
	 */
	private static final Graphic GRAPHIC = new Graphic(1350);
	
	/**
	 * Handles rolling the dice bag.
	 * @param player The player rolling the dice bag.
	 * @param clan The flag if it's a clan roll.
	 */
	public static void roll(Player player, boolean clan) {
		if(player.getCombat().inCombat()) {
			player.send(new SendMessage("You can't be in combat to do this!"));
			return;
		}
		if(Area.inWilderness(player)) {
			player.send(new SendMessage("You can't be in the wilderness to do this!"));
			return;
		}
		if(clan && player.clan == null) {
			player.send(new SendMessage("You need to be in a clan chat channel to do this!"));
			return;
		}
		if(!player.diceDelay.elapsed(3, TimeUnit.SECONDS)) {
			player.send(new SendMessage("You can't do this so quickly!"));
			return;
		}
		
		int random = Utility.random(100);
		Position position = player.getPosition().transform(player.movement.lastDirection.getFaceLocation());
		
		player.animate(ANIMATION);
		World.sendGraphic(GRAPHIC, position);
		
		//        if (clan) {
		//            Clan channel = ClanRepository.get(player.clan);
		//            if (channel == null)
		//                return;
		//            ClanManager.communicate(channel, player.getName() + " has rolled <col=ff0000>" + random + "</col> on the percentile dice!");
		//            return;
		//        }
		
		player.diceDelay.reset();
		player.send(new SendMessage("@blu@You have rolled @red@" + random + "@blu@ on the percentile dice!"));
		// player.joinclan("dice");
	}
}
