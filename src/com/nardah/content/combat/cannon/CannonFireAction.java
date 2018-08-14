package com.nardah.content.combat.cannon;

import com.nardah.game.task.Task;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.combat.hit.HitIcon;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.net.packet.out.SendMessage;

public final class CannonFireAction extends Task {
	private final Player player;
	private final Cannon cannon;

	public CannonFireAction(Player player, Cannon cannon) {
		super(true, 2);
		this.player = player;
		this.cannon = cannon;
	}

	@Override
	public void execute() {
		if(cannon.getAmmunition() == 0) {
			player.send(new SendMessage("Your cannon has run out of ammunition!"));
			cannon.setFiring(false);
			cancel();
			return;
		}

		switch(cannon.getRotation()) {
			case CannonManager.Rotation.NORTH:
				cannon.setRotation(CannonManager.Rotation.NORTH_EAST);
				break;
			case CannonManager.Rotation.NORTH_EAST:
				cannon.setRotation(CannonManager.Rotation.EAST);
				break;
			case CannonManager.Rotation.EAST:
				cannon.setRotation(CannonManager.Rotation.SOUTH_EAST);
				break;
			case CannonManager.Rotation.SOUTH_EAST:
				cannon.setRotation(CannonManager.Rotation.SOUTH);
				break;
			case CannonManager.Rotation.SOUTH:
				cannon.setRotation(CannonManager.Rotation.SOUTH_WEST);
				break;
			case CannonManager.Rotation.SOUTH_WEST:
				cannon.setRotation(CannonManager.Rotation.WEST);
				break;
			case CannonManager.Rotation.WEST:
				cannon.setRotation(CannonManager.Rotation.NORTH_WEST);
				break;
			case CannonManager.Rotation.NORTH_WEST:
				cannon.setRotation(CannonManager.Rotation.NORTH);
				break;
		}

		CannonManager.rotate(cannon);

		Mob[] mobs = CannonManager.getNpc(cannon);

		if(mobs != null) {
			for(Mob i : mobs) {
				if(i != null) {
					int lockon = i.getIndex() + 1;
					byte offsetX = (byte) ((i.getPosition().getY() - i.getPosition().getY()) * -1);
					byte offsetY = (byte) ((i.getPosition().getX() - i.getPosition().getX()) * -1);
					World.sendProjectile(CannonManager.getCannonFire(), cannon.getPosition(), lockon, offsetX, offsetY);

					Hit hit = new Hit(3, HitIcon.CANON);

					i.damage(hit);
					cannon.setAmmunition(cannon.getAmmunition() - 1);
				}
			}
		}
	}
}
