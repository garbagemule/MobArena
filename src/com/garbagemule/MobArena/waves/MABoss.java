package com.garbagemule.MobArena.waves;

import org.bukkit.entity.LivingEntity;

public class MABoss
{
    private LivingEntity entity;
    private int health, health25, maxHealth;
    private boolean dead, lowHealth;
    
    /**
     * Create an MABoss from the given entity with the given max health.
     * @param entity an entity
     * @param maxHealth a max health value
     */
    public MABoss(LivingEntity entity, int maxHealth) {
        this.entity    = entity;
        this.dead      = false;
        this.lowHealth = false;
        
        this.health   = this.maxHealth = maxHealth;
        this.health25 = maxHealth / 4;
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
        return health;
    }
    
    /**
     * Get the maximum health of this MABoss
     * @return the maximum health of the boss
     */
    public int getMaxHealth() {
        return maxHealth;
    }
    
    /**
     * Set the health of this boss as a percentage between 1 and 100.
     * @param percentage an integer percentage
     */
    public void setHealth(int percentage) {
        if (percentage < 1) {
            percentage = 1;
        }
        else if (percentage > 100) {
            percentage = 100;
        }
        
        health = maxHealth * percentage / 100;
    }
    
    /**
     * Heal the boss for the given amount. Useful for "siphon life"-like abilities.
     * @param amount the health amount
     */
    public void heal(int amount) {
        health = Math.min(maxHealth, health + amount);
    }
    
    /**
     * Damage the boss for the given amount. Used internally by MobArena.
     * @param amount the amount.
     */
    public void damage(int amount) {
        health -= amount;
        
        if (health <= health25 && !lowHealth) {
            lowHealth = true;
            //System.out.println("Boss is at 25%!");
        }
        
        if (health <= 0) {
            dead = true;
        }
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
