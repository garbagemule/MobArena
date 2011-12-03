package com.garbagemule.MobArena.util;

import java.io.Serializable;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * NOTE: I (garbagemule) DID NOT WRITE THIS CLASS (notice the author below)
 * @author creadri
 */
@SuppressWarnings("serial")
public class EntityPosition implements Serializable{
    private double x;
    private double y;
    private double z;
    private String world;
    private float yaw;
    private float pitch;

    public EntityPosition(double x, double y, double z, String world, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
        this.yaw = yaw;
        this.pitch = pitch;
    }
    
    public EntityPosition(Location location) {
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.world = location.getWorld().getName();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
    }
    
    public Location getLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }
}