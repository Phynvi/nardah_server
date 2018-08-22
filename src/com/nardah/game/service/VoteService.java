package com.nardah.game.service;

import com.nardah.content.achievement.AchievementHandler;
import com.nardah.content.achievement.AchievementKey;
import com.nardah.game.world.World;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.items.Item;
import com.nardah.util.log.LogManager;
import com.nardah.util.log.Logger;
import com.nardah.util.log.LoggerType;

import java.sql.*;
import java.util.concurrent.TimeUnit;

public final class VoteService {
	
	private static final Logger logger = LogManager.getLogger(LoggerType.DATABASE);
	private static final String CONNECTION_STRING = "jdbc:mysql://nardah.com:3306/nardah_website";
	private static final String USER = "runity_root";
	private static final String PASS = "JNfIn3IvH5$V";
	private static final Item REWARD = new Item(7478, 1);
	
	public static void claimReward(Player player) {
//		if(!player.databaseRequest.elapsed(10, TimeUnit.SECONDS)) {
//			player.dialogueFactory.sendStatement("You can only check our database once every 10 seconds!").execute();
//			return;
//		}
		
		if(!player.inventory.hasCapacityFor(REWARD)) {
			player.dialogueFactory.sendStatement("Please clear up some inventory spaces before doing this!").execute();
			return;
		}
		
		boolean found = false;
		player.dialogueFactory.sendStatement("Checking request...").execute();
		player.databaseRequest.reset();
		
		try(Connection connection = DriverManager.getConnection(CONNECTION_STRING, USER, PASS); PreparedStatement sta = connection.prepareStatement("SELECT * FROM fx_votes WHERE username = ? AND claimed=0 AND callback_date IS NOT NULL", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {
			
			sta.setString(1, player.getName().replace(" ", "_"));
			
			try(ResultSet rs = sta.executeQuery()) {
				while(rs.next()) {
					String timestamp = rs.getTimestamp("callback_date").toString();
					String ipAddress = rs.getString("ip_address");
					int siteId = rs.getInt("site_id");
					
					found = true;
					player.inventory.add(REWARD);
					player.totalVotes += 1;
					logger.info(String.format("[Vote] Vote claimed by %s. (sid: %d, ip: %s, time: %s)", player.getName(), siteId, ipAddress, timestamp));
					rs.updateInt("claimed", 1); // do not delete otherwise they can reclaim!
					rs.updateRow();
				}
			}
			
			if(!found) {
				player.dialogueFactory.sendStatement("There is no vote reward for you to claim!").execute();
			} else {
				AchievementHandler.activate(player, AchievementKey.VOTE);
				player.dialogueFactory.sendStatement("Thank-you for your support, " + player.getName() + "!").execute();
				World.sendMessage("<col=CF2192>NR: <col=" + player.right.getColor() + ">" + player.getName() + " </col>has just voted! They have voted a total of <col=CF2192>" + player.totalVotes + " </col>times.");
			}
		} catch(SQLException ex) {
			logger.error(String.format("Error claiming vote for player=%s", player.getName()));
			ex.printStackTrace();
		}
	}
	
}