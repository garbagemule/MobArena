package com.garbagemule.MobArena.util;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config
{
    private YamlConfiguration config;
    private File configFile;
    
    public Config(File configFile) {
        this.config     = new YamlConfiguration();
        this.configFile = configFile;
        
        config.options().indent(4);
    }
    
    public boolean load() {
        try {
            config.load(configFile);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean save() {
        try {
            config.save(configFile);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void setHeader(String header) {
        config.options().header(header);
    }
    
    
    
    ///////////////////////////////////////////////////////////////////////////
    //                                                                       //
    //                              GETTERS                                  //
    //                                                                       //
    ///////////////////////////////////////////////////////////////////////////
    
    public Object getProperty(String path) {
        return config.get(path);
    }
    
    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }
    
    public boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }
    
    public int getInt(String path) {
        return config.getInt(path);
    }
    
    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }
    
    public String getString(String path) {
        String result = config.getString(path); 
        return result != null ? result : null;
    }
    
    public String getString(String path, String def) {
        return config.getString(path, def);
    }
    
    public Set<String> getKeys(String path) {
        if (config.get(path) == null)
            return null;
        
        ConfigurationSection section = config.getConfigurationSection(path);
        return section.getKeys(false);
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getStringList(String path, List<String> def) {
        if (config.get(path) == null)
            return def != null ? def : new LinkedList<String>();
        
        List<?> list = config.getStringList(path);
        return (List<String>) list;
    }
    
    
    
    ///////////////////////////////////////////////////////////////////////////
    //                                                                       //
    //                             MUTATORS                                  //
    //                                                                       //
    ///////////////////////////////////////////////////////////////////////////
    
    public void set(String path, Object value) {
        config.set(path, value);
    }
    
    public void setProperty(String path, Object value) {
        this.set(path, value);
    }
    
    public void remove(String path) {
        this.set(path, null);
    }
    
    public void removeProperty(String path) {
        this.remove(path);
    }
    
    
    
    ///////////////////////////////////////////////////////////////////////////
    //                                                                       //
    //                          UTILITY METHODS                              //
    //                                                                       //
    ///////////////////////////////////////////////////////////////////////////
    
    public static Location parseLocation(World world, String coords)
    {
        String[] parts = coords.split(",");
        if (parts.length != 5)
            throw new IllegalArgumentException("Input string must contain x, y, z, yaw and pitch");
        
        Integer x   = getInteger(parts[0]);
        Integer y   = getInteger(parts[1]);
        Integer z   = getInteger(parts[2]);
        Float yaw   = getFloat(parts[3]);
        Float pitch = getFloat(parts[4]);
        
        if (x == null || y == null || z == null || yaw == null || pitch == null)
            throw new NullPointerException("Some of the parsed values are null!");
        
        return new Location(world, x, y, z, yaw, pitch);
    }
    
    private static Integer getInteger(String s)
    {
        try
        {
            return Integer.parseInt(s.trim());
        }
        catch (Exception e) {}
        
        return null;
    }
    
    private static Float getFloat(String s)
    {
        try
        {
            return Float.parseFloat(s.trim());
        }
        catch (Exception e) {}
        
        return null;
    }

}
