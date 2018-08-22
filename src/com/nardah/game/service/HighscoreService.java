package com.nardah.game.service;


import com.nardah.Config;
import com.nardah.game.world.entity.actor.player.Player;
import com.nardah.game.world.entity.actor.player.PlayerRight;
import com.nardah.game.world.entity.skill.Skill;
import com.nardah.util.log.LogManager;
import com.nardah.util.log.Logger;
import com.nardah.util.log.LoggerType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class HighscoreService {

    private static final Logger logger = LogManager.getLogger(LoggerType.DATABASE);

    private static final String CONNECTION_STRING = "jdbc:mysql://nardah.com:3306/runity_global";
    private static final String USERNAME = "runity_root";
    private static final String PASSWORD = "JNfIn3IvH5$V";

    public static void saveHighscores(Player player) {
        if (player == null || !Config.LIVE_SERVER || !Config.FORUM_INTEGRATION || PlayerRight.isPriviledged(player)) {
            return;
        }

        new Thread(() -> {

            try (Connection connection = DriverManager.getConnection(CONNECTION_STRING, USERNAME, PASSWORD);
                 PreparedStatement dsta = connection.prepareStatement("DELETE FROM highscores WHERE username = ?");
                 PreparedStatement ista = connection.prepareStatement(generateQuery())) {

                dsta.setString(1, player.getName());
                dsta.execute();

                ista.setString(1, player.getName());
                ista.setInt(2, getRank(player.right));
                ista.setInt(3, getRank(player.right));
                ista.setInt(4, (int) player.skills.getCombatLevel());
                ista.setInt(5, player.killstreak.streak);
                ista.setInt(6, player.death);
                ista.setInt(7, player.kill);


                ista.setInt(8, player.prestige.totalPrestige);

                for (int x = 0; x < Skill.SKILL_COUNT; x++) {
                    ista.setInt(9 + x, player.prestige.prestige[x]);
                }

                ista.setInt(32, player.skills.getTotalLevel());
                ista.setLong(33, player.skills.getTotalXp());

                for (int i = 0; i < Skill.SKILL_COUNT; i++) {
                    ista.setInt(34 + i, player.skills.get(i).getRoundedExperience());
                }

                ista.execute();
            } catch (SQLException ex) {
                logger.error(String.format("Failed to save highscores for player=%s", player.getName()));
                ex.printStackTrace();
            }
        }).start();
    }

    private static int getRank(PlayerRight right) {
        if (right == PlayerRight.ULTIMATE_IRONMAN)
            return 3;
        if (right == PlayerRight.HARDCORE_IRONMAN)
            return 2;
        if (right == PlayerRight.IRONMAN)
            return 1;
        return 0;
    }

    private static String generateQuery() {
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO highscores (");
//        sb.append("id, ");
        sb.append("username, ");
        sb.append("rights, ");
        sb.append("mode, ");
        sb.append("combat_level, ");
        sb.append("killstreak, ");
        sb.append("deaths, ");
        sb.append("kills, ");
        sb.append("total_prestiges, ");
        sb.append("attack_prestiges, ");
        sb.append("defence_prestiges, ");
        sb.append("strength_prestiges, ");
        sb.append("hitpoints_prestiges, ");
        sb.append("ranged_prestiges, ");
        sb.append("prayer_prestiges, ");
        sb.append("magic_prestiges, ");
        sb.append("cooking_prestiges, ");
        sb.append("woodcutting_prestiges, ");
        sb.append("fletching_prestiges, ");
        sb.append("fishing_prestiges, ");
        sb.append("firemaking_prestiges, ");
        sb.append("crafting_prestiges, ");
        sb.append("smithing_prestiges, ");
        sb.append("mining_prestiges, ");
        sb.append("herblore_prestiges, ");
        sb.append("agility_prestiges, ");
        sb.append("thieving_prestiges, ");
        sb.append("slayer_prestiges, ");
        sb.append("farming_prestiges, ");
        sb.append("runecrafting_prestiges, ");
        sb.append("hunter_prestiges, ");
        sb.append("construction_prestiges,");
        sb.append("total_level, ");
        sb.append("overall_xp, ");
        sb.append("attack_xp, ");
        sb.append("defence_xp, ");
        sb.append("strength_xp, ");
        sb.append("hitpoints_xp, ");
        sb.append("ranged_xp, ");
        sb.append("prayer_xp, ");
        sb.append("magic_xp, ");
        sb.append("cooking_xp, ");
        sb.append("woodcutting_xp, ");
        sb.append("fletching_xp, ");
        sb.append("fishing_xp, ");
        sb.append("firemaking_xp, ");
        sb.append("crafting_xp, ");
        sb.append("smithing_xp, ");
        sb.append("mining_xp, ");
        sb.append("herblore_xp, ");
        sb.append("agility_xp, ");
        sb.append("thieving_xp, ");
        sb.append("slayer_xp, ");
        sb.append("farming_xp, ");
        sb.append("runecrafting_xp, ");
        sb.append("hunter_xp, ");
        sb.append("construction_xp)");
        sb.append("VALUES (" +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                "?, ?, ?)");
        return sb.toString();
    }

    private HighscoreService() {

    }

}