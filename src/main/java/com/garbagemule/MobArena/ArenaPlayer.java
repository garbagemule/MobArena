package com.garbagemule.MobArena;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import java.util.ArrayList;
import java.util.List;

public class ArenaPlayer
{
    private Player player;

    private ArenaClass arenaClass;
    private ArenaPlayerStatistics stats;
    private boolean isDead;

    private List<Projectile> projectiles;

    //private List<ItemStack> rewards;
    //private List<Block> blocks;

    public ArenaPlayer(Player player, Arena arena, MobArena plugin) {
        this.player = player;
        projectiles = new ArrayList<>();
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

    public List<Projectile> getProjectiles() {
        return projectiles;
    }
}
