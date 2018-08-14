package com.nardah.content.donators;

import com.nardah.Config;
import com.nardah.content.writer.InterfaceWriter;
import com.nardah.content.writer.impl.InformationWriter;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.UpdateFlag;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.net.packet.out.SendBanner;
import com.nardah.util.Utility;
import com.nardah.game.world.entity.actor.player.Player;

/**
 * This class handles everything related to donators.
 * @author Daniel
 */
public class Donation {
	
	private final Player player;
	private int credits;
	private int spent;
	
	public Donation(Player player) {
		this.player = player;
	}
	
	public void redeem(DonatorBond bond) {
		setSpent(getSpent() + bond.moneySpent);
		setCredits(getCredits() + bond.credits);
		player.message("<col=FF0000>You have claimed your donator bond. You now have " + Utility.formatDigits(getCredits()) + " donator credits!");
		World.sendMessage("<col=CF2192>NR: <col=" + player.right.getColor() + ">" + player.getName() + " </col>has opened <col=CF2192>" + Utility.formatEnum(bond.name()) + "</col>! What a damn legend!");
		updateRank(false);
	}
	
	public void updateRank(boolean login) {
		PlayerRight rank = PlayerRight.forSpent(spent);
		
		if(rank == null) {
			return;
		}
		
		if(player.right.equals(rank)) {
			return;
		}
		
		final int groupId = PlayerRight.getForumGroupId(rank);
		
		if(Config.LIVE_SERVER || !Config.SERVER_DEBUG) {
		
		}
		/*
		 * if (groupId != -1) { try { new JdbcSession(ForumService.getConnection())
		 * .sql("UPDATE core_members SET member_group_id = ? WHERE member_id = ?")
		 * .set(groupId) .set(player.getMemberId()) .update(Outcome.VOID); } catch
		 * (SQLException e) { logger.error(String.
		 * format("error assigning donator group: player=%s right=%s", player.getName(),
		 * rank.name()), e); } } }
		 */
		
		if(login) {
			if(!PlayerRight.isIronman(player) && !PlayerRight.isManagement(player)) {
				player.right = rank;
				player.updateFlags.add(UpdateFlag.APPEARANCE);
			}
			return;
		}
		
		if(PlayerRight.isIronman(player)) {
			player.message("Since you are an iron man, your ran icon will not change.");
		} else if(!PlayerRight.isManagement(player)) {
			player.right = rank;
			player.updateFlags.add(UpdateFlag.APPEARANCE);
		}
		
		String name = rank.getName();
		player.send(new SendBanner("Rank Level Up!", "You are now " + Utility.getAOrAn(name) + " " + PlayerRight.getCrown(player) + " " + name + "!", 0x1C889E));
		player.dialogueFactory.sendStatement("You are now " + Utility.getAOrAn(name) + " " + PlayerRight.getCrown(player) + " " + name + "!").execute();
		InterfaceWriter.write(new InformationWriter(player));
	}
	
	public int getCredits() {
		return credits;
	}
	
	public int getSpent() {
		return spent;
	}
	
	public void setCredits(int credits) {
		this.credits = credits;
	}
	
	public void setSpent(int spent) {
		this.spent = spent;
	}
}
