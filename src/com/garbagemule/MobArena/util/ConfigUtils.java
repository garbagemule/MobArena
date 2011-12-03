package com.garbagemule.MobArena.util;

import org.bukkit.Location;
import org.bukkit.World;

public class ConfigUtils
{
    public static Location parseLocation(World world, String coords)
    {
        String[] parts = coords.split(",");
        if (parts.length != 5)
            throw new IllegalArgumentException("Input string must contain x, y, z, yaw and pitch");
        
        Integer x   = getInt(parts[0]);
        Integer y   = getInt(parts[1]);
        Integer z   = getInt(parts[2]);
        Float yaw   = getFloat(parts[3]);
        Float pitch = getFloat(parts[4]);
        
        if (x == null || y == null || z == null || yaw == null || pitch == null)
            throw new NullPointerException("Some of the parsed values are null!");
        
        return new Location(world, x, y, z, yaw, pitch);
    }
    
    private static Integer getInt(String s)
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
