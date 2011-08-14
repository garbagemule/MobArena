package com.garbagemule.MobArena.repairable;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;

public class RepairableBlock implements Repairable
{
    private BlockState state;
    private World world;
    private int id, x, y, z;
    private Material type;
    private byte data;
    
    public RepairableBlock(BlockState state)
    {
        this.state = state;
        
        world = state.getWorld();
        
        x = state.getX();
        y = state.getY();
        z = state.getZ();
        
        id   = state.getTypeId();
        type = state.getType();
        data = state.getRawData();
    }
    
    /**
     * Repairs the block by setting the type and data
     */
    public void repair()
    {
        getWorld().getBlockAt(x,y,z).setTypeIdAndData(id, data, false);
    }

    public BlockState getState()
    {
        return state;
    }
    
    public World getWorld()
    {
        return world;
    }
    
    public void setWorld(World world)
    {
        this.world = world;
    }
    
    public Material getType()
    {
        return type;
    }
    
    public void setType(Material type)
    {
        this.type = type;
    }
    
    public int getId()
    {
        return id;
    }
    
    public void setId(int id)
    {
        this.id = id;
    }
    
    public byte getData()
    {
        return data;
    }
    
    public void setData(byte data)
    {
        this.data = data;
    }
    
    public int getX()
    {
        return x;
    }
    
    public void setX(int x)
    {
        this.x = x;
    }
    
    public int getY()
    {
        return y;
    }
    
    public void setY(int y)
    {
        this.y = y;
    }
    
    public int getZ()
    {
        return z;
    }
    
    public void setZ(int z)
    {
        this.z = z;
    }
}
