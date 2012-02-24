package com.garbagemule.MobArena.waves;

import org.bukkit.entity.LivingEntity;

public class MABoss
{
    private LivingEntity entity;
    private int health, health25, maxHealth;
    private boolean dead, lowHealth;
    
    public MABoss(LivingEntity entity, int maxHealth) {
        this.entity    = entity;
        this.dead      = false;
        this.lowHealth = false;
        
        this.health   = this.maxHealth = maxHealth;
        this.health25 = maxHealth / 4;
    }
    
    public LivingEntity getEntity() {
        return entity;
    }
    
    public int getHealth() {
        return health;
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }
    
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
    
    public boolean isDead() {
        return dead;
    }
}
