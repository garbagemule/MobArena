package com.garbagemule.MobArena.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import com.garbagemule.MobArena.framework.Arena;

public class RegionSerializer
{
    //private UUID worldUID;
    private int  x1, y1, z1, x2, y2, z2;
    private long width, height, length;
    
    private int[][][]  blocks;
    private byte[][][] data;
    
    //private transient World world;
    
    public RegionSerializer(World world, Location p1, Location p2) {
        //this.worldUID = world.getUID();

        this.x1 = p1.getBlockX();
        this.y1 = p1.getBlockY();
        this.z1 = p1.getBlockZ();

        this.x2 = p2.getBlockX();
        this.y2 = p2.getBlockY();
        this.z2 = p2.getBlockZ();

        this.width  = (x2 - x1) + 1;
        this.height = (y2 - y1) + 1;
        this.length = (z2 - z1) + 1;

        int w = (int) width;
        int h = (int) height;
        int l = (int) length;
        
        this.blocks = new int[w][h][l];
        this.data   = new byte[w][h][l];
    }
    
    public void serialize(Arena arena) {
        Serializer s = new Serializer(arena);
        s.start();
    }
    
    public void deserialize(Arena arena) {
        Deserializer d = new Deserializer(arena);
        d.start();
    }
    
    private class Serializer implements Runnable
    {
        private Arena arena;
        private long total;
        
        public Serializer(Arena arena) {
            this.arena = arena;
        }
        
        public void start() {
            // Disable the arena while serializing.
            arena.setEnabled(false);
            
            // Start serializing!
            total = 0;
            arena.scheduleTask(this, 1);
        }
        
        @Override
        public void run() {
            int y = (int) (total / (width*length));
            int z = (int) ((total % (width*length)) / width);
            int x = (int) ((total % (width*length)) % width);
            
            long max = width*height*length;
            int amount = (int) Math.min(20, (max - total));
            
            for (int i = 0; i < amount; i++) {
                Block b = arena.getWorld().getBlockAt(x,y,z);
                blocks[x][y][z] = b.getTypeId();
                data[x][y][z]   = b.getData();

                x = (int) ((x+1) % width);
                y = (int) ((y+1) % height);
                z = (int) ((z+1) % length);
            }
            
            total += amount;
            
            if (total == max) {
                arena.setEnabled(true);
                return;
            }
            
            arena.scheduleTask(this, 1);
        }
    }
    
    private class Deserializer implements Runnable
    {
        private Arena arena;
        private long total;
        
        public Deserializer(Arena arena) {
            this.arena = arena;
        }
        
        public void start() {
            // Disable the arena while serializing.
            arena.setEnabled(false);
            
            // Start serializing!
            total = 0;
            arena.scheduleTask(this, 1);
        }
        
        @Override
        public void run() {
            int y = (int) (total / (width*length));
            int z = (int) ((total % (width*length)) / width);
            int x = (int) ((total % (width*length)) % width);
            
            long max = width*height*length;
            int amount = (int) Math.min(20, (max - total));
            
            for (int i = 0; i < 20; i++) {
                Block b = arena.getWorld().getBlockAt(x,y,z);
                b.setTypeIdAndData(blocks[x][y][z], data[x][y][z], false);
            }
            
            total += amount;
            
            if (total == max) {
                arena.setEnabled(true);
                return;
            }
            
            arena.scheduleTask(this, 1);
        }
    }
}
