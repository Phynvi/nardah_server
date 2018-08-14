package com.nardah.content.activity.impl.recipefordisaster;

import com.nardah.content.achievement.AchievementHandler;
import com.nardah.content.achievement.AchievementKey;
import com.nardah.content.activity.panel.ActivityPanel;
import com.nardah.content.activity.Activity;
import com.nardah.content.activity.ActivityType;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.Actor;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.mob.MobDeath;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.items.Item;
import com.nardah.game.world.position.Area;
import com.nardah.game.world.position.Position;
import com.nardah.net.packet.out.SendEntityHintArrow;
import com.nardah.net.packet.out.SendMessage;
import com.nardah.util.RandomUtils;
import com.nardah.util.Utility;

/**
 * Handles the recipe for disaster activity.
 * @author Daniel
 * @author Michael
 */
public class RecipeForDisaster extends Activity {

	/**
	 * The player instance of this activity.
	 */
	private final Player player;

	/**
	 * The boss monster instance of this activity.
	 */
	private Mob monster;

	/**
	 * The current wave of this activity.
	 */
	private RecipeForDisasterData wave = RecipeForDisasterData.AGRITH_NA_NA;

	/**
	 * Constructs a new {@code SequencedMinigame} object.
	 */
	private RecipeForDisaster(Player player, int instance) {
		super(10, instance);
		this.player = player;
	}

	/**
	 * Handles creating a recipe for disaster activity.
	 */
	public static RecipeForDisaster create(Player player) {
		RecipeForDisaster minigame = new RecipeForDisaster(player, player.playerAssistant.instance());
		ActivityPanel.update(player, -1, "Recipe For Disaster", "Good Luck, " + player.getName() + "!", "Wave starting in 6 seconds");
		player.move(new Position(1899, 5356, 2));
		// pos[x=1899, y=5356, z=2]
		player.gameRecord.start();
		minigame.add(player);
		minigame.resetCooldown();
		return minigame;
	}

	@Override
	public void update() {
		if(wave == null) {
			ActivityPanel.update(player, 100, "Recipe For Disaster", new Item(7462), "</col>Wave: <col=FF5500>" + RecipeForDisasterData.values().length + "/" + RecipeForDisasterData.values().length, "</col>Boss: <col=FF5500>None!", "</col>Time: <col=FF5500>" + Utility.getTime(System.currentTimeMillis() - player.gameRecord.time));
			return;
		}
		int progress = (int) Utility.getPercentageAmount(wave.ordinal(), RecipeForDisasterData.values().length);
		int display = 7453 + wave.gloves;
		String npc = monster == null ? "" : monster.getName();
		ActivityPanel.update(player, progress, "Recipe For Disaster", new Item(display), "</col>Wave: <col=FF5500>" + wave.ordinal() + "/" + RecipeForDisasterData.values().length, "</col>Boss: <col=FF5500>" + npc, "</col>Time: <col=FF5500>" + Utility.getTime(System.currentTimeMillis() - player.gameRecord.time));
	}

	@Override
	protected void start() {
		if(wave == null) {
			finishCooldown();
			return;
		}
		if(player.locking.locked()) {
			return;
		}
		Position spawn = new Position(1899, 5356, 2);
		Position[] boundaries = Utility.getInnerBoundaries(spawn, 5, 5);

		Mob mob = new Mob(wave.npc, RandomUtils.random(boundaries));
		mob.owner = player;
		add(monster = mob);

		player.send(new SendEntityHintArrow(mob));
		monster.getCombat().attack(player);
		pause();
	}
	
	@Override
	public void finish() {
		cleanup();
		int reward;
		if(wave == null) {
			long time = player.gameRecord.end(ActivityType.RECIPE_FOR_DISASTER);
			reward = RecipeForDisasterData.CULINAROMANCER.gloves;
			AchievementHandler.activate(player, AchievementKey.COMPLETE_RFD);
			player.send(new SendMessage("You have completed the Recipe For Disaster activity. Final time: @red@" + Utility.getTime(time) + "</col>."));
		} else {
			reward = wave.gloves;
			player.send(new SendMessage("RIP"));
		}
		if(reward > player.glovesTier) {
			player.glovesTier = reward;
		}
		player.move(new Position(3080, 3498, 0));
		player.dialogueFactory.sendNpcChat(6526, "You have been rewarded for your efforts.", "Check my store to see your available gloves.").execute();
		remove(player);
	}

	public void clear(Player player) {
		player.move(new Position(3080, 3498, 0));
		player.dialogueFactory.sendNpcChat(6526, "You have been rewarded for your efforts.", "Check my store to see your available gloves.").execute();
		remove(player);
	}

	@Override
	public void cleanup() {
		if(monster != null) {
			player.send(new SendEntityHintArrow(monster, true));
			remove(monster);
		}
		ActivityPanel.clear(player);
	}

	@Override
	public boolean canTeleport(Player player) {
		/*if (player.getCombat().inCombat()) {
			player.send(new SendMessage("You can not do that right now!"));
			return false;
		} */
		player.locking.lock();
		cleanup();
		remove(player);
		player.send(new SendMessage("You have forfeited your current progress as you have teleproted."));
		return true;
	}

	@Override
	public void onRegionChange(Player player) {
		if(!Area.inRFD(player)) {
			finish();
		}
	}

	@Override
	public void onLogout(Player player) {
		finish();
	}

	@Override
	public void onDeath(Actor actor) {
		if(actor.isPlayer()) {
			cleanup();
			remove(actor);
			actor.move(new Position(3080, 3498, 0));
			return;
		}
		if(actor.isNpc()) {
			player.send(new SendEntityHintArrow(actor, true));
			World.schedule(new MobDeath(actor.getNpc()));
			wave = wave.getNext();
			resetCooldown();
		}
	}

	@Override
	public ActivityType getType() {
		return ActivityType.RECIPE_FOR_DISASTER;
	}
}
