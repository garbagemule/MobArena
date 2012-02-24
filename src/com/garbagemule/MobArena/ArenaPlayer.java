package com.garbagemule.MobArena;

import org.bukkit.entity.Player;

import com.garbagemule.MobArena.ArenaPlayerStatistics;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.framework.Arena;

public class ArenaPlayer
{
    private Player player;

    private ArenaClass arenaClass;
    private ArenaPlayerStatistics stats;
    private boolean isDead;

    //private List<ItemStack> rewards;
    //private List<Block> blocks;

    public ArenaPlayer(Player player, Arena arena, MobArena plugin) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public ArenaClass getArenaClass() {
        return arenaClass;
    }

    public void setArenaClass(ArenaClass arenaClass) {
        this.arenaClass = arenaClass;
    }

    /**
     * Check if the player is "dead", i.e. died or not.
     * @return true, if the player is either a spectator or played and died, false otherwise
     */
    public boolean isDead() {
        return isDead;
    }

    /**
     * Set the player's death status.
     * @param value true, if the player is dead, false otherwise
     */
    public void setDead(boolean value) {
        isDead = value;
    }

    public void resetStats() {
        if (stats != null) {
            stats.reset();
            return;
        }
        stats = new ArenaPlayerStatistics(this);
    }

    public ArenaPlayerStatistics getStats() {
        return stats;
    }
}
