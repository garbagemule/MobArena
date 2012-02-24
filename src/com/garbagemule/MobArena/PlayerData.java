package com.garbagemule.MobArena;

import java.util.Collection;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class PlayerData
{
    private Player player;
    
    private int health, food, level;
    private float exp;
    private GameMode mode  = null;
    private Location entry = null;
    private Collection<PotionEffect> potions;
    
    public PlayerData(Player player) {
        this.player  = player;
        this.mode    = player.getGameMode();
        this.entry   = player.getLocation();
        this.potions = player.getActivePotionEffects();
        
        update();
    }
    
    /**
     * Updates the information that is restored, when a player
     * dies in the arena, that is, health, food level, and
     * experience. Used when a player re-joins an arena while
     * already being a spectator.
     */
    public void update() {
        this.health = player.getHealth();
        this.food   = player.getFoodLevel();
        this.level  = player.getLevel();
        this.exp    = player.getExp();
    }
    
    /**
     * Restores health, food level, and experience as per the
     * currently stored values of this object. Used when a
     * player leaves the arena.
     */
    public void restoreData() {
        player.setFoodLevel(food);
        player.setLevel(level);
        player.setExp(exp);
    }
    
    public Player getPlayer() {
        return player;
    }
    
    public int health() {
        return health;
    }
    
    public void setHealth(int health) {
        this.health = health;
    }
    
    public int food() {
        return food;
    }
    
    public void setFood(int food) {
        this.food = food;
    }
    
    public int level() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    public float exp() {
        return exp;
    }
    
    public void setExp(int exp) {
        this.exp = exp;
    }
    
    public GameMode getMode() {
        return mode;
    }
    
    public Collection<PotionEffect> getPotionEffects() {
        return potions;
    }
    
    public void setMode(GameMode mode) {
        this.mode = mode;
    }
    
    public Location entry() {
        return entry;
    }
    
    public void setEntry(Location entry) {
        this.entry = entry;
    }
}
