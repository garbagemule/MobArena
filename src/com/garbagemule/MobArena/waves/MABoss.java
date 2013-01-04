package com.garbagemule.MobArena.waves;

import org.bukkit.entity.LivingEntity;

public class MABoss
{
    private LivingEntity entity;
    private boolean dead;
    
    /**
     * Create an MABoss from the given entity with the given max health.
     * @param entity an entity
     * @param maxHealth a max health value
     */
    public MABoss(LivingEntity entity, int maxHealth) {
        entity.setMaxHealth(maxHealth);
        entity.setHealth(maxHealth);
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
    public int getHealth() {
        return entity.getHealth();
    }
    
    /**
     * Get the maximum health of this MABoss
     * @return the maximum health of the boss
     */
    public int getMaxHealth() {
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
}
