package com.garbagemule.MobArena;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;

public class ScoreboardManager {
    private static final String DISPLAY_NAME = ChatColor.GREEN + "Kills       " + ChatColor.AQUA + "Wave ";

    private Arena arena;
    private Scoreboard scoreboard;
    private Objective kills;

    private Map<Player, Scoreboard> scoreboards;

    /**
     * Create a new scoreboard for the given arena.
     * @param arena an arena
     */
    ScoreboardManager(Arena arena) {
        this.arena = arena;
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        scoreboards = new HashMap<>();
    }

    /**
     * Add a player to the scoreboard by setting the player's scoreboard
     * and giving him an initial to-be-reset non-zero score.
     * @param player a player
     */
    void addPlayer(Player player) {
        /* Set the player's scoreboard and give them an initial non-zero
         * score. This is necessary due to either Minecraft or Bukkit
         * not wanting to show non-zero scores initially. */
        scoreboards.put(player, player.getScoreboard());
        player.setScoreboard(scoreboard);
        kills.getScore(player.getName()).setScore(8);
    }

    /**
     * Remove a player from the scoreboard by setting the player's scoreboard
     * to the main server scoreboard.
     * @param player a player
     */
    void removePlayer(Player player) {
        try {
            Scoreboard scoreboard = scoreboards.remove(player);
            if (scoreboard != null) {
                player.setScoreboard(scoreboard);
            } else {
                player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            }
        } catch (IllegalStateException e) {
            // Happens if the player is logging out, just swallow it
        }
    }

    /**
     * Add a kill to the player's score. Called when a player kills a mob.
     * @param player a player
     */
    void addKill(Player player) {
        Score score = kills.getScore(player.getName());
        score.setScore(score.getScore() + 1);
    }

    /**
     * Signal a player death.
     * @param player a player
     */
    void death(Player player) {
        if (kills == null) {
            return;
        }

        String name = ChatColor.GRAY + player.getName();
        if (name.length() > 16) {
            name = name.substring(0, 15);
        }

        Score score = kills.getScore(player.getName());
        if (score == null) {
            return;
        }

        int value = score.getScore();
        scoreboard.resetScores(player.getName());

        /* In case the player has no kills, they will not show up on the
         * scoreboard unless they are first given a different score.
         * If zero kills, the score is set to 8 (which looks a bit like
         * 0), and then in the next tick, it's set to 0. Otherwise, the
         * score is just set to its current value.
         */
        final Score fake = kills.getScore(name);
        if (value == 0) {
            fake.setScore(8);
            arena.scheduleTask(() -> fake.setScore(0), 1);
        } else {
            fake.setScore(value);
        }
    }

    /**
     * Update the scoreboard to display the given wave number.
     * @param wave a wave number
     */
    void updateWave(int wave) {
        kills.setDisplayName(DISPLAY_NAME + wave);
    }

    /**
     * Initialize the scoreboard by resetting the kills objective and
     * setting all player scores to 0.
     */
    void initialize() {
        /* Initialization involves first unregistering the kill counter if
         * it was already registered, and then setting it back up.
         * It is necessary to delay the reset of the player scores, and the
         * reset is necessary because of non-zero crappiness. */
        resetKills();
        arena.scheduleTask(this::resetPlayerScores, 1);
    }

    private void resetKills() {
        if (kills != null) {
            kills.unregister();
        }
        kills = scoreboard.registerNewObjective("kills", "ma-kills", "Kills");
        kills.setDisplaySlot(DisplaySlot.SIDEBAR);
        updateWave(0);
    }

    private void resetPlayerScores() {
        arena.getPlayersInArena().stream()
            .map(Player::getName)
            .map(kills::getScore)
            .forEach(score -> score.setScore(0));
    }

    static class NullScoreboardManager extends ScoreboardManager {
        NullScoreboardManager(Arena arena) {
            super(arena);
        }
        void addPlayer(Player player) {}
        void removePlayer(Player player) {}
        void addKill(Player player) {}
        void death(Player player) {}
        void updateWave(int wave) {}
        void initialize() {}
    }
}
