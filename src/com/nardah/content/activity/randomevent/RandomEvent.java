package com.nardah.content.activity.randomevent;

import com.nardah.content.activity.Activity;
import com.nardah.content.activity.ActivityType;
import com.nardah.game.Animation;
import com.nardah.game.Graphic;
import com.nardah.game.world.entity.combat.hit.Hit;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.player.Player;

/**
 * The random event handler.
 * @author Daniel.
 */
public abstract class RandomEvent extends Activity {

	/**
	 * The player instance.
	 */
	public Player player;

	/**
	 * The event mob.
	 */
	protected Mob eventMob;

	/**
	 * Th message count.
	 */
	private int count;

	/**
	 * Flag if the event is angered.
	 */
	protected boolean angered;

	/**
	 * Constructs a new <code>RandomEvent</code>.
	 */
	public RandomEvent(Player player, int cooldown) {
		super(cooldown, player.instance);
		this.player = player;
		this.count = 0;
	}

	/**
	 * The event mob identification.
	 */
	protected abstract int eventNpcIdentification();

	/**
	 * The event mob shout messages.
	 */
	protected abstract String[] eventNpcShout();

	@Override
	protected void start() {
		if(count >= eventNpcShout().length) {
			player.damage(new Hit(5));
			finish();
			return;
		}
		if(eventMob == null) {
			eventMob = new Mob(eventNpcIdentification(), player.getPosition());
			add(eventMob);
			eventMob.interact(player);
			eventMob.follow(player);
			eventMob.graphic(new Graphic(86, true));
			eventMob.owner = player;
			cooldown(2);
			return;
		}
		eventMob.animate(new Animation(863));
		eventMob.speak(eventNpcShout()[count].replace("%name", player.getName()));
		eventMob.follow(player);
		count++;
		if(count >= eventNpcShout().length) {
			angered = true;
			eventMob.graphic(new Graphic(86, true));
			eventMob.animate(new Animation(864));
			cooldown(2);
		} else {
			resetCooldown();
		}
	}

	@Override
	public void finish() {
		cleanup();
		remove(player);
	}

	@Override
	public void onRegionChange(Player player) {
		if(eventMob != null && eventMob.getPosition().isWithinDistance(player.getPosition(), 15)) {
			finishCooldown();
		}
	}

	@Override
	public void onLogout(Player player) {
		finish();
	}

	@Override
	public void cleanup() {
		remove(eventMob);
	}

	@Override
	public boolean safe() {
		return false;
	}

	@Override
	public ActivityType getType() {
		return ActivityType.RANDOM_EVENT;
	}
}
