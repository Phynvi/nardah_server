package io.battlerune.content.skill.impl.magic.teleport;

import io.battlerune.Config;
import io.battlerune.content.activity.Activity;
import io.battlerune.content.skill.impl.magic.Spellbook;
import io.battlerune.game.Animation;
import io.battlerune.game.Graphic;
import io.battlerune.game.UpdatePriority;
import io.battlerune.game.action.impl.TeleportAction;
import io.battlerune.game.world.entity.actor.Actor;
import io.battlerune.game.world.entity.actor.player.Player;
import io.battlerune.game.world.entity.actor.player.PlayerRight;
import io.battlerune.game.world.position.Area;
import io.battlerune.game.world.position.Position;
import io.battlerune.net.packet.out.SendMessage;

import java.util.Optional;

/**
 * Handles a player teleporting.
 * @author Daniel
 */
public class Teleportation {
	
	/**
	 * Teleports player to a position.
	 */
	public static boolean teleport(Actor actor, Position position) {
		return teleport(actor, position, 20, () -> {
			/* Empty */
		});
	}
	
	/**
	 * Teleports player to a position.
	 */
	public static boolean teleport(Actor actor, Position position, int wildernessLevel, Runnable onDestination) {
		if(actor.isNpc()) {
			teleport(actor, position, TeleportationData.MODERN, onDestination);
			return true;
		}
		
		Player player = actor.getPlayer();
		
		if(!player.interfaceManager.isClear()) {
			player.interfaceManager.close(false);
		}
		
		if(Activity.evaluate(player, it -> !it.canTeleport(player))) {
			return false;
		}
		
		if(player.wilderness > wildernessLevel && !PlayerRight.isPriviledged(player)) {
			player.send(new SendMessage("You can't teleport past " + wildernessLevel + " wilderness!"));
			return false;
		}
		
		if(player.isTeleblocked()) {
			player.message("You are currently under the affects of a teleblock spell and can not teleport!");
			return false;
		}
		
		boolean wilderness = Area.inWilderness(position);
		
		if(wilderness && player.pet != null) {
			player.dialogueFactory.sendNpcChat(player.pet.id, "I'm sorry #name,", "but I can not enter the wilderness with you!").execute();
			return false;
		}
		
		/*
		 * if (wilderness && player.playTime < 3000) { player.
		 * message("You cannot enter the wilderness until you have 30 minutes of playtime. "
		 * + Utility.getTime((3000 - player.playTime) * 3 / 5) + " minutes remaining.");
		 * return false; }
		 */
		
		TeleportationData type = TeleportationData.MODERN;
		
		if(position.equals(Config.DONATOR_ZONE) || position.equals(Config.STAFF_ZONE)) {
			type = TeleportationData.DONATOR;
		} else if(position.equals(Config.DEFAULT_POSITION) && !player.getCombat().inCombat()) {
			type = TeleportationData.HOME;
		} else if(player.spellbook == Spellbook.ANCIENT) {
			type = TeleportationData.ANCIENT;
		}
		
		teleport(player, position, type, onDestination);
		return true;
	}
	
	/**
	 * Teleports player using a certain data type.
	 */
	public static boolean teleportNoChecks(Actor actor, Position position, TeleportationData type) {
		return teleport(actor, position, type, () -> {
			/* Empty */
		});
	}
	
	/**
	 * Teleports player using a certain data type.
	 */
	public static boolean teleport(Actor actor, Position position, TeleportationData type, Runnable onDestination) {
		if(type != TeleportationData.HOME)
			actor.getCombat().reset();
		actor.action.execute(new TeleportAction(actor, position, type, onDestination), true);
		return true;
	}
	
	/**
	 * Holds all the teleportation data.
	 */
	public enum TeleportationData {
		TABLET(2, new Animation(4069), new Graphic(678, 5, false), new Animation(4731)), MODERN(3, new Animation(714), new Graphic(308, 43, true)), OBELISK(3, new Animation(1816), new Graphic(308, 43, true), new Animation(6304)), ANCIENT(3, new Animation(1979), new Graphic(392, false)), HOME(3, new Animation(714), new Graphic(308, 43, true)), LEVER(3, new Animation(714), new Graphic(308, 43, true)), DONATOR(4, new Animation(6999), new Graphic(284), new Animation(65535, UpdatePriority.VERY_HIGH)), CREVICE(2, new Animation(6301), new Graphic(571, false));
		
		private final int delay;
		private final Optional<Animation> startAnimation;
		private final Optional<Graphic> startGraphic;
		private final Optional<Animation> middleAnimation;
		private final Optional<Graphic> middleGraphic;
		private final Optional<Animation> endAnimation;
		private final Optional<Graphic> endGraphic;
		private final boolean lockMovement;
		
		TeleportationData(int delay, Animation startAnimation, Animation middleAnimation, Graphic middleGraphic) {
			this(delay, startAnimation, null, middleAnimation, middleGraphic, null, null, true);
		}
		
		TeleportationData(int delay, Animation startAnimation, Graphic startGraphic, Animation endAnimation) {
			this(delay, startAnimation, startGraphic, endAnimation, null, true);
		}
		
		TeleportationData(int delay, Animation startAnimation, Graphic startGraphic, boolean lockMovement) {
			this(delay, startAnimation, startGraphic, null, null, lockMovement);
		}
		
		TeleportationData(int delay, Animation startAnimation, Graphic startGraphic, Animation endAnimation, boolean lockMovement) {
			this(delay, startAnimation, startGraphic, endAnimation, null, lockMovement);
		}
		
		TeleportationData(int delay, Animation startAnimation, Graphic startGraphic, Animation endAnimation, Graphic endGraphic) {
			this(delay, startAnimation, startGraphic, endAnimation, endGraphic, true);
		}
		
		TeleportationData(int delay, Animation startAnimation, Graphic startGraphic) {
			this(delay, startAnimation, startGraphic, null, null, true);
		}
		
		TeleportationData(int delay, Animation startAnimation, Graphic startGraphic, Animation endAnimation, Graphic endGraphic, boolean lockMovement) {
			this(delay, startAnimation, startGraphic, null, null, endAnimation, endGraphic, lockMovement);
		}
		
		TeleportationData(int delay, Animation startAnimation, Graphic startGraphic, Animation middleAnimation, Graphic middleGraphic, Animation endAnimation, Graphic endGraphic, boolean lockMovement) {
			this.delay = delay;
			this.startAnimation = Optional.ofNullable(startAnimation);
			this.startGraphic = Optional.ofNullable(startGraphic);
			this.middleAnimation = Optional.ofNullable(middleAnimation);
			this.middleGraphic = Optional.ofNullable(middleGraphic);
			this.endAnimation = Optional.ofNullable(endAnimation);
			this.endGraphic = Optional.ofNullable(endGraphic);
			this.lockMovement = lockMovement;
		}
		
		public int getDelay() {
			return delay;
		}
		
		public Optional<Animation> getStartAnimation() {
			return startAnimation;
		}
		
		public Optional<Graphic> getStartGraphic() {
			return startGraphic;
		}
		
		public Optional<Animation> getMiddleAnimation() {
			return middleAnimation;
		}
		
		public Optional<Graphic> getMiddleGraphic() {
			return middleGraphic;
		}
		
		public Optional<Animation> getEndAnimation() {
			return endAnimation;
		}
		
		public Optional<Graphic> getEndGraphic() {
			return endGraphic;
		}
		
		public boolean lockMovement() {
			return lockMovement;
		}
	}
}
