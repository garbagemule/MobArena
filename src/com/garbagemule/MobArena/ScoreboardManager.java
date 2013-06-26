package com.garbagemule.MobArena;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.garbagemule.MobArena.framework.Arena;

public class ScoreboardManager {
    private static final String DISPLAY_NAME = ChatColor.GREEN + "Kills       " + ChatColor.AQUA + "Wave ";

    private Arena arena;
    private Scoreboard scoreboard;
    private Objective kills;
    
    /**
     * Create a new scoreboard for the given arena.
     * @param arena an arena
     */
    ScoreboardManager(Arena arena) {
        this.arena = arena;
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
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
        player.setScoreboard(scoreboard);
        kills.getScore(player).setScore(8);
    }
    
    /**
     * Remove a player from the scoreboard by setting the player's scoreboard
     * to the main server scoreboard.
     * @param player a player
     */
    void removePlayer(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    /**
     * Add a kill to the player's score. Called when a player kills a mob.
     * @param player a player
     */
    void addKill(Player player) {
        Score score = kills.getScore(player);
        score.setScore(score.getScore() + 1);
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
        arena.scheduleTask(new Runnable() {
            public void run() {
                for (Player p : arena.getPlayersInArena()) {
                    kills.getScore(p).setScore(0);
                }
            }
        }, 1);
    }
    
    private void resetKills() {
        if (kills != null) {
            kills.unregister();
        }
        kills = scoreboard.registerNewObjective("kills", "ma-kills");
        kills.setDisplaySlot(DisplaySlot.SIDEBAR);
        updateWave(0);
    }

    static class NullScoreboardManager extends ScoreboardManager {
        NullScoreboardManager(Arena arena) {
            super(arena);
        }
        void addPlayer(Player player) {}
        void removePlayer(Player player) {}
        void addKill(Player player) {}
        void updateWave(int wave) {}
        void initialize() {}
    }
}
