package com.garbagemule.MobArena.util.config;

import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import com.garbagemule.MobArena.util.config.Config;

public class ConfigSection
{
    private Config config;
    private String path;
    
    public ConfigSection(Config config, String path) {
        this.config = config;
        this.path   = (path.endsWith(".") ? path : path + ".");
    }
    
    public Config getParent() {
        return config;
    }
    
    public ConfigSection getConfigSection(String path) {
        return new ConfigSection(config, this.path + path);
    }
    
    public Object get(String node) {
        return config.get(path + node);
    }
    
    public int getInt(String node) {
        return config.getInt(path + node);
    }
    
    public int getInt(String node, int def) {
        return config.getInt(path + node, def);
    }
    
    public double getDouble(String node) {
        return config.getDouble(path + node);
    }
    
    public double getDouble(String node, double def) {
        return config.getDouble(path + node, def);
    }
    
    public boolean getBoolean(String node) {
        return config.getBoolean(path + node);
    }
    
    public boolean getBoolean(String node, boolean def) {
        return config.getBoolean(path + node, def);
    }
    
    public String getString(String node) {
        return config.getString(path + node);
    }
    
    public String getString(String node, String def) {
        return config.getString(path + node, def);
    }
    
    public Location getLocation(String node, World world) {
        return config.getLocation(path + node, world);
    }
    
    public Location getLocation(String node, World world, Location def) {
        return config.getLocation(path + node, world, def);
    }
    
    public ItemStack getItemStack(String path) {
        return config.getItemStack(path);
    }
    
    public ItemStack getItemStack(String path, ItemStack def) {
        return config.getItemStack(path, def);
    }
    
    public Set<String> getKeys() {
        return config.getKeys(path);
    }
    
    public Set<String> getKeys(String node) {
        return config.getKeys(path + node);
    }
    
    public List<String> getStringList(String node, List<String> def) {
        return config.getStringList(path + node, def);
    }
    
    public void set(String node, Object value) {
        config.set(path + node, value);
    }
}
