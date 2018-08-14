package com.nardah.content.skill.impl.slayer;

import com.nardah.content.store.StoreItem;
import com.nardah.Config;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.mob.Mob;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.game.world.items.Item;
import com.nardah.net.packet.out.SendString;
import com.nardah.util.Utility;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Handles the slayer skill.
 * @author Daniel
 */
public class Slayer {
	/**
	 * The player instance.
	 */
	private Player player;
	
	/**
	 * The partner's instance.
	 */
	public Player partner;
	
	/**
	 * The slayer points.
	 */
	private int points;
	
	/**
	 * The slayer task.
	 */
	private SlayerTask task;
	
	/**
	 * The slayer task assigned amount.
	 */
	private int assigned;
	
	/**
	 * The current slayer task amount.
	 */
	private int amount;
	
	/**
	 * The total tasks assigned.
	 */
	private int totalAssigned;
	
	/**
	 * The total tasks completed.
	 */
	private int totalCompleted;
	
	/**
	 * The total tasks cancelled.
	 */
	private int totalCancelled;
	
	/**
	 * The total points accumulated.
	 */
	private int totalPoints;
	
	/**
	 * The list of all blocked slayer tasks.
	 */
	private List<SlayerTask> blocked = new LinkedList<>();
	
	/**
	 * The Set of all unlockable slayer perks.
	 */
	private Set<SlayerUnlockable> unlocked = new HashSet<>(SlayerUnlockable.values().length);
	
	/**
	 * Constructs a new <code>Slayer<code>.
	 */
	public Slayer(Player player) {
		this.player = player;
	}
	
	/**
	 * Opens the slayer itemcontainer.
	 */
	public void open(SlayerTab tab) {
		SlayerTab.refresh(player, tab);
		player.attributes.set("SLAYER_KEY", tab);
		player.interfaceManager.open(tab.getIdentification());
	}
	
	/**
	 * Assigns a slayer task to the player.
	 */
	public void assign(TaskDifficulty difficulty) {
		if(task != null) {
			player.message("You currently have a task assigned!");
			return;
		}
		
		SlayerTask toAssign = SlayerTask.assign(player, difficulty);
		
		if(toAssign == null) {
			player.dialogueFactory.sendNpcChat(6797, "There are no tasks available for you!", "This can be because you do not have a valid", "slayer level or you haven't unlocked any tasks.");
			return;
		}
		
		int assignment = 0;
		if(toAssign.getDifficulty() == TaskDifficulty.EASY) {
			assignment = Utility.random(35, 75);
		} else if(toAssign.getDifficulty() == TaskDifficulty.MEDIUM) {
			assignment = Utility.random(45, 80);
		} else if(toAssign.getDifficulty() == TaskDifficulty.HARD) {
			assignment = Utility.random(60, 100);
		} else if(toAssign.getDifficulty() == TaskDifficulty.BOSS) {
			assignment = Utility.random(30, 50);
		}
		
		partner = null;
		totalAssigned++;
		task = toAssign;
		amount = assignment;
		SlayerTab.refresh(player, player.attributes.get("SLAYER_KEY", SlayerTab.class));
		player.message("You have been assigned " + amount + "x " + task.getName() + ".");
	}
	
	/**
	 * Cancel's the current assigned slayer task.
	 */
	public void cancel() {
		if(task == null) {
			player.message("You do not have a task to cancel.");
			return;
		}
		
		int cost = PlayerRight.isDonator(player) ? 20 : 25;
		
		if(points < cost) {
			player.message("You need " + cost + " slayer points to cancel a task.");
			return;
		}
		
		points -= cost;
		forceCancel();
	}
	
	public void forceCancel() {
		partner = null;
		task = null;
		amount = 0;
		totalCancelled++;
		player.message("Your task has been cancelled.");
		SlayerTab.refresh(player, player.attributes.get("SLAYER_KEY", SlayerTab.class));
	}
	
	/**
	 * Blocks the current assigned slayer task.
	 */
	public void block() {
		if(task == null) {
			player.message("You do not have a task to block.");
			return;
		}
		
		if(points < 100 && !PlayerRight.isDonator(player) || !PlayerRight.isDeveloper(player)) {
			player.message("You need 100 slayer points to block a task, unless you are a donator.");
			return;
		}
		
		if(blocked.size() >= 5) {
			player.message("You can only block up to 5 tasks.");
			return;
		}
		
		if(blocked.contains(task)) {
			player.message("This task is already blocked... but how did you get it again? mmm...");
			return;
		}
		blocked.add(task);
		if(!PlayerRight.isDonator(player) || !PlayerRight.isDeveloper(player)) {
			points -= 100;
		}
		task = null;
		amount = 0;
		totalCancelled++;
		SlayerTab.refresh(player, player.attributes.get("SLAYER_KEY", SlayerTab.class));
		player.message("Your task has been blocked.");
	}
	
	/**
	 * Unblocks the slayer task.
	 */
	public void unblock(int index) {
		if(!blocked.isEmpty() && index < blocked.size()) {
			SlayerTask task = blocked.get(index);
			blocked.remove(task);
			SlayerTab.refresh(player, player.attributes.get("SLAYER_KEY", SlayerTab.class));
			player.message("You have unblocked the task " + task.getName() + ".");
			return;
		}
		player.message("There is no task to unblock.");
	}
	
	/**
	 * Activates killing a slayer mob.
	 */
	@SuppressWarnings("static-access")
	public void activate(Mob mob, int killAmount, boolean partnerKilled) {
		if(task != null) {
			if(task.valid(mob.id)) {
				amount -= killAmount;
				int rewardPts = SlayerTask.getPoints(task.getDifficulty());
				if(amount <= 0) {
					task = null;
					amount = 0;
					totalCompleted++;
					if(totalCompleted % 250 == 0) {
						rewardPts *= 15;
						World.sendMessage(player.getName() + " has just completed " + totalCompleted + " tasks in a row");
						World.sendMessage("For this monumental achievement, they have received " + rewardPts + " slayer points.");
						player.message("Well done.");
					} else if(totalCompleted % 150 == 0) {
						rewardPts *= 7;
						player.message("Damn dude. 150 in a row. Here's " + rewardPts + " slayer points.");
					} else if(totalCompleted % 50 == 0) {
						rewardPts *= 5;
						player.message("Congratulations, you have completed 50 tasks in a row! You have earned " + rewardPts + " slayer points!");
					} else if(totalCompleted % 15 == 0) {
						rewardPts *= 3;
						player.message("Congratulations, you have completed 15 tasks in a row! You have earned " + rewardPts + " slayer points!");
					} else if(totalCompleted % 5 == 0) {
						rewardPts *= 2;
						player.message("Congratulations, you have completed 5 tasks in a row! You have earned " + rewardPts + " slayer points!");
					} else {
						if(PlayerRight.isDonator(player)) {
							rewardPts *= 2;
						}
						if(PlayerRight.isSuper(player)) {
							rewardPts *= 2;
						}
						if(PlayerRight.isExtreme(player)) {
							rewardPts *= 3;
						}
						if(PlayerRight.isElite(player)) {
							rewardPts *= 3;
						}
						if(PlayerRight.isKing(player)) {
							rewardPts *= 4;
						}
						if(PlayerRight.isSupreme(player)) {
							rewardPts *= 4;
						}
						
						player.message("Congratulations, you have completed your assigned task! You have earned " + rewardPts + " slayer points!");
					}
					points += rewardPts;
					if(partner != null) {
						partner.slayer.activate(mob, killAmount, true);
					}
					partner = null;
					player.skills.addExperience(Skill.SLAYER, 15_000);
				}
				if(partner != null) {
					if(!partnerKilled) {
						partner.slayer.activate(mob, killAmount, true);
						player.skills.addExperience(Skill.SLAYER, mob.getMaximumHealth() * Config.SLAYER_MODIFICATION);
						partner.skills.addExperience(Skill.SLAYER, mob.getMaximumHealth() * Config.SLAYER_MODIFICATION / 10);
						if(getAmount() != partner.slayer.getAmount()) {
							System.out.println("Correcting differentiation");
							partner.slayer.amount = getAmount();
						}
					}
				} else {
					player.skills.addExperience(Skill.SLAYER, mob.getMaximumHealth() * Config.SLAYER_MODIFICATION);
				}
			}
		}
	}
	
	/**
	 * Opens the confirm itemcontainer for purchasing a perk.
	 */
	public void confirm(int button) {
		int index = Math.abs((-18625 - button) / 6);
		if(!SlayerUnlockable.get(index).isPresent())
			return;
		SlayerUnlockable unlockable = SlayerUnlockable.get(index).get();
		player.attributes.set("SLAYER_CONFIRM_KEY", unlockable);
		open(SlayerTab.CONFIRM);
	}
	
	/**
	 * Handles purchasing a slayer perk.
	 */
	public void purchase() {
		SlayerUnlockable unlockable = player.attributes.get("SLAYER_CONFIRM_KEY", SlayerUnlockable.class);
		
		if(points < unlockable.getCost()) {
			open(SlayerTab.UNLOCK);
			player.message("You do not have enough points to purchase this!");
			return;
		}
		
		if(unlocked.contains(unlockable)) {
			open(SlayerTab.UNLOCK);
			player.message("You have already activated this perk!");
			return;
		}
		
		points -= unlockable.getCost();
		unlocked.add(unlockable);
		player.message("You have purchased the " + unlockable.getName() + " Slayer perk.");
		open(SlayerTab.UNLOCK);
	}
	
	private final static StoreItem[] STORE_ITEMS = {new StoreItem(4155, 1, 0), new StoreItem(4166, 1, 5), new StoreItem(4168, 1, 5), new StoreItem(4164, 1, 5), new StoreItem(4551, 1, 5), new StoreItem(11738, 1, 25), new StoreItem(11866, 1, 75), new StoreItem(11739, 1, 500), new StoreItem(8901, 1, 500), new StoreItem(13239, 1, 650),
			
			new StoreItem(19647, 1, 750), new StoreItem(19643, 1, 750),
			
			new StoreItem(19639, 1, 800),
			
			new StoreItem(10551, 1, 800), new StoreItem(13576, 1, 850),
			
			new StoreItem(11941, 1, 950), new StoreItem(12821, 1, 1050),
			
			new StoreItem(21225, 1, 1750)
		
	};
	
	static Item[] ITEMS = new Item[STORE_ITEMS.length];
	
	static {
		int index = 0;
		for(StoreItem storeItem : STORE_ITEMS) {
			ITEMS[index] = new Item(storeItem.getId(), storeItem.getAmount());
			index++;
		}
	}
	
	/**
	 * Handles purchasing items from the slayer shop.
	 */
	public void store(int slot, int amount, boolean value) {
		if(slot < 0 && slot > STORE_ITEMS.length) {
			return;
		}
		
		StoreItem item = STORE_ITEMS[slot];
		int cost = item.getShopValue();
		
		if(value) {
			String price = cost == 0 ? "is free!" : "costs " + Utility.formatDigits(cost) + " slayer points.";
			player.message(item.getName() + " " + price);
			return;
		}
		
		if(player.inventory.remaining() == 0) {
			player.message("You do not have enough inventory space to buy that.");
			return;
		}
		
		if(!item.isStackable()) {
			if(amount > player.inventory.remaining()) {
				amount = player.inventory.remaining();
			}
		} else {
			amount *= item.getAmount();
		}
		
		int price = cost * amount;
		
		if(getPoints() < price) {
			player.message("You do not have enough slayer points to make this purchase!");
			return;
		}
		
		item.setAmount(amount);
		points -= price;
		player.inventory.add(item);
		player.send(new SendString(Utility.formatDigits(points) + "\\nPoints", 46714));
	}
	
	/**
	 * Handles invitation Note: This function has nothing to do with the player who
	 * calls it via player.slayer.startDuoDialogue
	 **/
	public void startDuoDialogue(Player inviter, Player receiver) {
		if(PlayerRight.isIronman(inviter) || PlayerRight.isIronman(receiver)) {
			inviter.message("Sorry, ironmen cannot partake in duo slayer :c");
		}
		if(inviter.slayer.task == null) {
			inviter.message("You don't have a task!");
			return;
		}
		if(inviter.slayer.partner != null) {
			inviter.message("You already have a slayer partner!");
			return;
		}
		if(receiver.slayer.partner != null) {
			inviter.message(receiver.getName() + " already has a partner!");
			return;
		}
		inviter.dialogueFactory.sendStatement("How do you want to share your slayer task with " + receiver.getName() + "?").sendOption("Share Task", () -> {
			sendDuoInvitation(inviter, receiver, true);
		}, "Double-up Task", () -> {
			sendDuoInvitation(inviter, receiver, false);
		}).execute();
		inviter.message("Sending invitation to " + receiver.getName() + "...");
	}
	
	private void sendDuoInvitation(Player inviter, Player receiver, boolean shared) {
		receiver.dialogueFactory.sendStatement(inviter.getName() + " has offered to share his task with you.");
		receiver.dialogueFactory.sendOption("Details", () -> {
			receiver.slayer.detailsOnDuo(inviter, receiver, shared);
		}, "Ignore", () -> {
			inviter.dialogueFactory.sendStatement(receiver.getName() + " has rejected your offer as a slayer partner.").execute();
		}).execute();
	}
	
	/**
	 * Handles "Details" on duo slayer
	 **/
	private void detailsOnDuo(Player inviter, Player receiver, boolean shared) {
		
		player.dialogueFactory.sendStatement("If accepted, together with " + inviter.getName() + " you would have", "to slay " + (shared ? inviter.slayer.getAmount() : 2 * inviter.slayer.getAmount()) + " " + inviter.slayer.getTask().getName() + ".").sendOption("Accept", () -> {
			setUpDuo(inviter, receiver, shared);
		}, "Decline", () -> {
			player.message("You have declined the duo slayer offering of" + inviter.getName());
			inviter.message(player.getName() + " has declined your duo slayer offering.");
		});
	}
	
	private void setUpDuo(Player inviter, Player receiver, boolean shared) {
		player.message("You have accepted the duo slayer offering of " + inviter.getName());
		inviter.message(player.getName() + " has accepted your duo slayer offering.");
		inviter.slayer.partner = receiver;
		receiver.slayer.partner = inviter;
		inviter.slayer.setAmount(shared ? inviter.slayer.getAmount() : inviter.slayer.getAmount() * 2);
		receiver.slayer.setAmount(inviter.slayer.getAmount());
		
		receiver.slayer.task = inviter.slayer.task;
		// receiver.slayer.refreshSlayerTab();
	}
	
	private void refreshSlayerTab() {
		SlayerTab.refresh(player, player.attributes.get("SLAYER_KEY", SlayerTab.class));
	}
	
	public int getPoints() {
		return points;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
	
	public SlayerTask getTask() {
		return task;
	}
	
	public void setTask(SlayerTask task) {
		this.task = task;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public int getAssigned() {
		return assigned;
	}
	
	public void setAssigned(int assigned) {
		this.assigned = assigned;
	}
	
	public int getTotalAssigned() {
		return totalAssigned;
	}
	
	public void setTotalAssigned(int totalAssigned) {
		this.totalAssigned = totalAssigned;
	}
	
	public int getTotalCompleted() {
		return totalCompleted;
	}
	
	public void setTotalCompleted(int totalCompleted) {
		this.totalCompleted = totalCompleted;
	}
	
	public int getTotalCancelled() {
		return totalCancelled;
	}
	
	public void setTotalCancelled(int totalCancelled) {
		this.totalCancelled = totalCancelled;
	}
	
	public int getTotalPoints() {
		return totalPoints;
	}
	
	public void setTotalPoints(int totalPoints) {
		this.totalPoints = totalPoints;
	}
	
	public List<SlayerTask> getBlocked() {
		return blocked;
	}
	
	public void setBlocked(List<SlayerTask> blocked) {
		this.blocked = blocked;
	}
	
	public Set<SlayerUnlockable> getUnlocked() {
		return unlocked;
	}
	
	public void setUnlocked(Set<SlayerUnlockable> unlocked) {
		this.unlocked = unlocked;
	}
}
