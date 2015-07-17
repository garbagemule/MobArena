package com.garbagemule.MobArena.waves;

import com.garbagemule.MobArena.Messenger;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MABoss
{
    private LivingEntity entity;
    private boolean dead;
    private ItemStack reward;
    private List<ItemStack> drops;
    
    /**
     * Create an MABoss from the given entity with the given max health.
     * @param entity an entity
     * @param maxHealth a max health value
     */
    public MABoss(LivingEntity entity, double maxHealth) {
        try {
            entity.setMaxHealth(maxHealth);
            entity.setHealth(maxHealth);
        } catch (IllegalArgumentException ex) {
            // Spigot... *facepalm*
            Messenger.severe("Can't set health to " + maxHealth + ", using default health. If you are running Spigot, set 'maxHealth' higher in your Spigot settings.");
            Messenger.severe(ex.getLocalizedMessage());
        }
        this.entity    = entity;
        this.dead      = false;
    }
    
    /**
     * Get the LivingEntity associated with this MABoss
     * @return a LivingEntity
     */
    public LivingEntity getEntity() {
        return entity;
    }
    
    /**
     * Get the current health of this MABoss
     * @return the current health of the boss
     */
    public double getHealth() {
        return entity.getHealth();
    }
    
    /**
     * Get the maximum health of this MABoss
     * @return the maximum health of the boss
     */
    public double getMaxHealth() {
        return entity.getMaxHealth();
    }
    
    /**
     * Check if the boss is dead.
     * A boss is dead if it has been damaged such that its health is below 0.
     * @return true, if the boss is dead, false otherwise
     */
    public boolean isDead() {
        return dead;
    }
    
    /**
     * Set the death status of a boss.
     * This is used by the ArenaListener to force kill bosses that die due to
     * unhandled damage events (Bukkit issues).
     * @param dead death status
     */
    public void setDead(boolean dead) {
        this.dead = dead;
    }
    
    public void setReward(ItemStack reward) {
        this.reward = reward;
    }

    public ItemStack getReward() {
        return reward;
    }

    public void setDrops(List<ItemStack> drops) {
        this.drops = drops;
    }

    public List<ItemStack> getDrops() {
        return drops;
    }
}
