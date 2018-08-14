package com.nardah.content.activity;

import com.nardah.content.activity.panel.Activity_Panel;
import com.nardah.Config;
import com.nardah.content.consume.FoodData;
import com.nardah.content.event.EventDispatcher;
import com.nardah.content.event.InteractionEvent;
import com.nardah.content.event.InteractionEventListener;
import com.nardah.content.event.impl.*;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.Entity;
import com.nardah.game.world.entity.combat.attack.listener.CombatListener;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.mob.MobDeath;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.items.containers.equipment.EquipmentType;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A {@code Activity} object constructs an in-game activity and sequences it
 * through the {@link #start()} and {@link #finish()} methods with a {@code
 * cooldown} set in game ticks.
 * @author Michael | Chex
 */
public abstract class Activity implements InteractionEventListener {
	
	/**
	 * The 'start' cooldown id.
	 */
	protected static final int START = 0;
	
	/**
	 * The 'finish' cooldown id.
	 */
	protected static final int FINISH = -1;
	
	/**
	 * The 'pause' cooldown id.
	 */
	protected static final int PAUSE = -2;
	
	/**
	 * The sequencing cooldown.
	 */
	private final int cooldown;
	
	/**
	 * The activity instance level.
	 */
	private int instance;
	
	/**
	 * The remaining game ticks.
	 */
	private int ticks;
	
	/**
	 * The panel for this activity.
	 */
	private Activity_Panel panel;
	
	protected void restart(int delay, Runnable runnable) {
		World.schedule(delay, runnable::run);
	}
	
	/**
	 * Constructs a new {@code SequencedMinigame} object.
	 */
	public Activity(int cooldown, int instance) {
		this.instance = instance;
		this.cooldown = cooldown;
	}
	
	public static <T extends Activity> Optional<T> search(Player player, Class<T> clazz) {
		final Activity activity = player.activity;
		
		if(activity == null) {
			return Optional.empty();
		}
		
		if(clazz.isInstance(activity)) {
			return Optional.of(clazz.cast(activity));
		}
		
		return Optional.empty();
	}
	
	public static boolean evaluate(Actor actor, Predicate<Activity> predicate) {
		return actor != null && actor.activity != null && predicate.test(actor.activity);
	}
	
	public static void forActivity(Actor actor, Consumer<Activity> consumer) {
		if(actor == null) {
			return;
		}
		
		if(actor.activity == null) {
			return;
		}
		
		consumer.accept(actor.activity);
	}
	
	public boolean canEquipItem(Player player, Item item, EquipmentType type) {
		return true;
	}
	
	public boolean canEat(Player player, FoodData foodType) {
		return true;
	}
	
	public boolean canUseSpecial(Player player) {
		return true;
	}
	
	public boolean canUsePrayer(Player player) {
		return true;
	}
	
	public boolean canDrinkPotions(Player player) {
		return true;
	}
	
	public boolean canLogout(Player player) {
		return true;
	}
	
	public boolean canSpellCast(Player player) {
		return true;
	}
	
	/**
	 * Sequences the activity.
	 */
	public void sequence() {
		update();
		
		if(isPaused()) {
			return;
		}
		
		if(ticks > 0) {
			ticks--;
		} else if(ticks == START) {
			start();
		} else if(ticks == FINISH) {
			finish();
		}
	}
	
	/**
	 * Starts the next activity stage.
	 */
	protected abstract void start();
	
	/**
	 * Finishes the activity.
	 */
	public abstract void finish();
	
	/**
	 * Cleans up the activity when finished.
	 */
	public abstract void cleanup();
	
	/**
	 * The update method.
	 */
	public void update() {
	
	}
	
	public abstract ActivityType getType();
	
	/**
	 * Called when the player logs out.
	 */
	public void onLogout(Player player) {
		remove(player);
	}
	
	/**
	 * Called when the player die
	 */
	public void onDeath(Actor actor) {
		if(actor.isNpc()) {
			World.schedule(new MobDeath(actor.getNpc()));
			return;
		}
		remove(actor);
		actor.move(Config.DEFAULT_POSITION);
		finish();
	}
	
	/**
	 * Sets the activity panel.
	 */
	public void setPanel(Activity_Panel panel) {
		this.panel = panel;
	}
	
	/**
	 * Gets an optional of the activity panel.
	 */
	public Optional<Activity_Panel> getPanel() {
		return Optional.ofNullable(panel);
	}
	
	/**
	 * Called when the player changes region.
	 */
	public void onRegionChange(Player player) {
		remove(player);
	}
	
	/**
	 * Called when the player attempts to teleport.
	 */
	public boolean canTeleport(Player player) {
		return false;
	}
	
	/**
	 * Adds a actor to the activity.
	 */
	public void add(Actor actor) {
		if(actor.isNpc() && !actor.isRegistered()) {
			actor.register();
		}
		actor.setActivity(this);
		actor.instance = instance;
		getListener().ifPresent(actor.getCombat()::addListener);
		getCombatListener().ifPresent(actor.getCombat()::addListener);
	}
	
	/**
	 * Removes all actors from the activity.
	 */
	public void removeAll(Actor... actors) {
		if(actors.length != 0)
			for(Actor actor : actors) {
				if(actor.isRegistered())
					remove(actor);
			}
	}
	
	/**
	 * Removes a actor from the activity.
	 */
	public void remove(Actor actor) {
		getListener().ifPresent(actor.getCombat()::removeListener);
		getCombatListener().ifPresent(actor.getCombat()::removeListener);
		if(actor.isNpc()) {
			actor.getNpc().unregister();
		} else {
			actor.instance = Entity.DEFAULT_INSTANCE_HEIGHT;
			actor.getPlayer().setActivity(null);
		}
	}
	
	/**
	 * Sets the pause state of the activity.
	 */
	public void setPause(boolean pause) {
		ticks = pause ? PAUSE : START;
	}
	
	/**
	 * Resets the remaining ticks to the cached cooldown ticks.
	 */
	protected final void resetCooldown() {
		cooldown(cooldown);
	}
	
	/**
	 * Applies a cooldown.
	 */
	public void cooldown(int cooldown) {
		this.ticks = cooldown;
	}
	
	/**
	 * Sets the cooldown flag to {@link #FINISH}.
	 */
	protected final void finishCooldown() {
		ticks = FINISH;
	}
	
	/**
	 * Sets the cooldown flag to {@link #PAUSE}.
	 */
	protected final void pause() {
		ticks = PAUSE;
	}
	
	/**
	 * Checks if the cooldown is paused.
	 */
	public final boolean isPaused() {
		return cooldown == PAUSE;
	}
	
	public boolean safe() {
		return true;
	}
	
	/**
	 * Gets this activity's instance level.
	 */
	public int getInstance() {
		return instance;
	}
	
	public void setInstance(int instance) {
		this.instance = instance;
	}
	
	/**
	 * Gets the current ticks.
	 */
	public int getTicks() {
		return ticks;
	}
	
	/**
	 * Gets an {@link Optional} of the {@link ActivityListener} for this activity.
	 */
	protected Optional<? extends ActivityListener<? extends Activity>> getListener() {
		return Optional.empty();
	}
	
	protected Optional<CombatListener> getCombatListener() {
		return Optional.empty();
	}
	
	protected boolean clickItem(Player player, ItemInteractionEvent event) {
		return false;
	}
	
	protected boolean clickNpc(Player player, NpcInteractionEvent event) {
		return false;
	}
	
	protected boolean clickObject(Player player, ObjectInteractionEvent event) {
		return false;
	}
	
	protected boolean clickButton(Player player, ClickButtonInteractionEvent event) {
		return false;
	}
	
	protected boolean useItem(Player player, ItemOnItemInteractionEvent event) {
		return false;
	}
	
	protected boolean pickupItem(Player player, PickupItemInteractionEvent event) {
		return false;
	}
	
	protected boolean useItem(Player player, ItemOnObjectInteractionEvent event) {
		return false;
	}
	
	protected boolean itemContainerAction(Player player, ItemContainerInteractionEvent event) {
		return false;
	}
	
	@Override
	public boolean onEvent(Player player, InteractionEvent interactionEvent) {
		final EventDispatcher dispatcher = new EventDispatcher(interactionEvent);
		dispatcher.dispatch(InteractionEvent.InteractionType.CLICK_BUTTON, e -> clickButton(player, (ClickButtonInteractionEvent) e));
		dispatcher.dispatch(InteractionEvent.InteractionType.ITEM_ON_ITEM, e -> useItem(player, (ItemOnItemInteractionEvent) e));
		dispatcher.dispatch(InteractionEvent.InteractionType.ITEM_ON_OBJECT, e -> useItem(player, (ItemOnObjectInteractionEvent) e));
		dispatcher.dispatch(InteractionEvent.InteractionType.FIRST_ITEM_CLICK, e -> clickItem(player, (FirstItemClickInteractionEvent) e));
		dispatcher.dispatch(InteractionEvent.InteractionType.SECOND_ITEM_CLICK, e -> clickItem(player, (SecondItemClickInteractionEvent) e));
		dispatcher.dispatch(InteractionEvent.InteractionType.THIRD_ITEM_CLICK, e -> clickItem(player, (ThirdItemClickInteractionEvent) e));
		//		dispatcher.dispatch(InteractionType.FOURTH_ITEM_CLICK, e -> clickItem(player, (FourthItemClick) e));
		dispatcher.dispatch(InteractionEvent.InteractionType.FIRST_CLICK_NPC, e -> clickNpc(player, (FirstNpcClick) e));
		dispatcher.dispatch(InteractionEvent.InteractionType.SECOND_CLICK_NPC, e -> clickNpc(player, (SecondNpcClick) e));
		//		dispatcher.dispatch(InteractionType.CLICK_NPC, e -> clickNpc(player, (ThirdNpcClick) e));
		//		dispatcher.dispatch(InteractionType.CLICK_NPC, e -> clickNpc(player, (FourthNpcClick) e));
		dispatcher.dispatch(InteractionEvent.InteractionType.FIRST_CLICK_OBJECT, e -> clickObject(player, (FirstObjectClick) e));
		dispatcher.dispatch(InteractionEvent.InteractionType.SECOND_CLICK_OBJECT, e -> clickObject(player, (SecondObjectClick) e));
		dispatcher.dispatch(InteractionEvent.InteractionType.THIRD_CLICK_OBJECT, e -> clickObject(player, (ThirdObjectClick) e));
		dispatcher.dispatch(InteractionEvent.InteractionType.PICKUP_ITEM, e -> pickupItem(player, (PickupItemInteractionEvent) e));
		dispatcher.dispatch(InteractionEvent.InteractionType.ITEM_CONTAINER_INTERACTION_EVENT, e -> itemContainerAction(player, (ItemContainerInteractionEvent) e));
		return interactionEvent.isHandled();
	}
	
	public ActivityDeathType deathType() {
		// TODO Auto-generated method stub
		return null;
	}
}
