package com.nardah.util;

import com.nardah.Nardah;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.net.codec.login.LoginResponse;
import com.nardah.util.database.query.command.SQLCommand;
import com.nardah.util.database.query.command.impl.InsertCommand;
import com.nardah.util.database.query.command.impl.SelectCommand;
import com.nardah.util.database.query.command.impl.UpdateCommand;
import com.nardah.util.database.query.options.impl.SelectionRangeOption;
import com.nardah.util.database.query.options.impl.TableColumnValueOption;
import com.nardah.util.database.query.options.impl.WhereConditionOption;

import java.sql.ResultSet;
import java.time.Instant;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Represents a class for additional functionality of I/O of an account
 * 
 * @author Telopya <telopya@frostblades.org>
 *
 */
public class AccountUtility {

	/**
	 * The logger of this class
	 */
	private final static Logger logger = Logger.getLogger(AccountUtility.class.getName());

	/**
	 * The name of the table where the accounts are stored
	 */
	public static final String GLOBAL_ACCOUNTS_TABLE = "global_accounts";

	/**
	 * The query to check if the player is banned from logging in
	 */
	public final static String OFFENCE_QUERY = "SELECT id FROM global_offences WHERE ((target = ? AND type = 'NORMAL_BAN') OR (ip_address = ? AND type = 'IP_BAN') OR (uuid = ? AND type = 'UUID_BAN')) AND active = 1";

	/**
	 * Verifies the player by account name and password
	 * 
	 * @param name
	 *            the account name of the player
	 * @param password
	 *            the password of the player
	 * @return if player is verified
	 */
	public static boolean verify(String name, String password) {

		try {
			if (name.isEmpty() || name.length() > 12 || name.length() < 3) {
				return false;
			} else {

				final SQLCommand command = new SelectCommand(GLOBAL_ACCOUNTS_TABLE);
				command.addOption(new SelectionRangeOption("password_salt", "password"));
				command.addOption(new WhereConditionOption("account_name", name));
				command.addOption(new WhereConditionOption("activated", 1));


				return Nardah.getDatabase().execute(command, rs -> {

                    if (rs.next()) {
                        return password.equals(rs.getString("password"));
                    }

                    return false;

                });

			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
		}

		return false;

	}

	public static boolean create(String name, String password) {

		try {

			if (name.isEmpty() || name.length() > 12 || name.length() < 3 || password.isEmpty()) {
				return false;
			} else {

				if (exists(name)) {
					return false;
				}

				name = Utility.capitalizeFully(name);
				final String salt = Utility.createRandomString(12);
				final SQLCommand command = new InsertCommand(GLOBAL_ACCOUNTS_TABLE);
				command.addOption(new TableColumnValueOption("account_name", name));
				command.addOption(new TableColumnValueOption("character_name", name));
				command.addOption(new TableColumnValueOption("password_salt", "default_salt"));
				command.addOption(new TableColumnValueOption("password", password));
				command.addOption(new TableColumnValueOption("signup_address", "0.0.0.0"));
				command.addOption(new TableColumnValueOption("ban_duration", "0"));
				command.addOption(new TableColumnValueOption("ban_start", "0"));
				return Nardah.getDatabase().execute(command) > 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
		}

		return false;

	}

	/**
	 *
	 * @param player
	 *            the account name
	 * 
	 * @return the player's details
	 */
	public static LoginResponse load(final Player player) {

		try {

			SQLCommand command = new SelectCommand(GLOBAL_ACCOUNTS_TABLE);
			command.addOption(new SelectionRangeOption("character_name", "previous_name", "authority_rank", "total_credits", "credits"));
			command.addOption(new WhereConditionOption("account_name", player.getUsername()));
			command.addOption(new WhereConditionOption("activated", 1));


			return Nardah.getDatabase().execute(command, rs -> {

                if (rs.next()) {
                    player.setUsername(rs.getString("character_name"));
//                    definition.setPreviousName(rs.getString("previous_name"));
                    player.right = (PlayerRight.getRankByName(rs.getString("authority_rank")));
//                    player.setTotalCredits(rs.getInt("total_credits"));
//                    definition.setCredits(rs.getInt("credits"));
                    return LoginResponse.NORMAL;
                } else {
                    return null;
                }

            });

		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
		}

		return null;

	}

	/**
	 * Checks if the account is registered in the MySQL database
	 * 
	 * @param name
	 *            the account name of the player
	 * @return if account is registered
	 */
	public static boolean exists(String name) {

		try {

			SQLCommand command = new SelectCommand(GLOBAL_ACCOUNTS_TABLE);
			command.addOption(new WhereConditionOption("account_name", name));
			command.addOption(new WhereConditionOption("activated", 1));


			return Nardah.getDatabase().execute(command, rs -> rs.next());

		} catch (Exception e) {
			logger.warning(e.getMessage());
		}

		return false;

	}


	/**
	 * Stores details of {@link Player} into the MySQL database
	 * 
	 * @param player
	 *            the player
	 * @return true if player was stored
	 */
	public static boolean store(Player player) {

		try {

			final SQLCommand command = new UpdateCommand(GLOBAL_ACCOUNTS_TABLE);

//			if (!player.getDefinition().isMaster()) {
//				command.addOption(new TableColumnValueOption("last_seen_date", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date())));
//				command.addOption(new TableColumnValueOption("last_seen_address", player.getIPAddress()));
//				command.addOption(new TableColumnValueOption("last_seen_uuid", player.getUUID()));
//			}

			command.addOption(new TableColumnValueOption("authority_rank", player.right.name()));
//			command.addOption(new TableColumnValueOption("total_credits", player.getDefinition().getTotalCredits()));
//			command.addOption(new TableColumnValueOption("credits", player.getDefinition().getCredits()));
			command.addOption(new WhereConditionOption("account_name", player.getUsername()));

			return Nardah.getDatabase().execute(command) > 0;

		} catch (Exception e) {
			logger.warning(e.getMessage());
		}

		return false;

	}

	public static <T extends Object> T getDefinitionByName(String name, String definition, Class<T> c) {

		try {

			SQLCommand command = new SelectCommand(GLOBAL_ACCOUNTS_TABLE);
			command.addOption(new SelectionRangeOption(definition));
			command.addOption(new WhereConditionOption("account_name", name));
			command.addOption(new WhereConditionOption("activated", 1));

			return Nardah.getDatabase().execute(command, rs -> {

                if (rs.next()) {
                    return c.cast(rs.getObject(definition));
                }

                return null;
            });

		} catch (Exception e) {
			e.printStackTrace();
			logger.warning(e.getMessage());
		}

		return null;

	}

	public static boolean isBanned(String name) {

			SQLCommand command = new SelectCommand(GLOBAL_ACCOUNTS_TABLE);
			command.addOption(new WhereConditionOption("account_name", name));

		return Nardah.getDatabase().execute(command, rs -> {
			if (rs.next()) {
				long duration = rs.getLong("ban_duration");
				long start = rs.getLong("ban_start");
				if(duration < 0) { // perm ban
					return true;
				} else if (start > 0 && System.currentTimeMillis() - start < duration) {
					return true;
				}
				return false;
			}
			return null;
		});
	}

}
