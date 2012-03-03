package com.garbagemule.MobArena;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import org.bukkit.block.Sign;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.garbagemule.MobArena.MAUtils;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.region.ArenaRegion;
import com.garbagemule.MobArena.util.EntityPosition;
import com.garbagemule.MobArena.util.ItemParser;
import com.garbagemule.MobArena.util.TextUtils;
import com.garbagemule.MobArena.util.config.Config;
import com.garbagemule.MobArena.util.config.ConfigUtils;

public class MAUtils
{         
    public static final String sep = File.separator;
    
      
    
    /* ///////////////////////////////////////////////////////////////////// //
    
            INITIALIZATION METHODS
    
    // ///////////////////////////////////////////////////////////////////// */
    
    /**
     * Generates a map of wave numbers and rewards based on the
     * type of wave ("after" or "every") and the config-file. If
     * no keys exist in the config-file, an empty map is returned.
     */    
    public static Map<Integer,List<ItemStack>> getArenaRewardMap(MobArena plugin, Config config, String arena, String type)
    {
        //String arenaPath = "arenas." + arena + ".rewards.waves.";
        String typePath = ConfigUtils.waveRewardList(arena, type);
        Map<Integer,List<ItemStack>> result = new HashMap<Integer,List<ItemStack>>();
        
        if (config.getKeys(typePath) == null)
        {
            if (type.equals("every"))
            {
                config.set(typePath + ".3", "feather, bone, stick");
                config.set(typePath + ".5", "dirt:4, gravel:4, stone:4");
                config.set(typePath + ".10", "iron_ingot:10, gold_ingot:8");
            }
            else if (type.equals("after"))
            {
                config.set(typePath + ".7", "minecart, storage_minecart, powered_minecart");
                config.set(typePath + ".13", "iron_sword, iron_pickaxe, iron_spade");
                config.set(typePath + ".16", "diamond_sword");
            }
        }
        
        //Set<String> waves = config.getKeys(arenaPath + type);
        Set<String> waves = config.getKeys(typePath);
        if (waves == null) return result;
        
        for (String n : waves)
        {
            if (!n.matches("[0-9]+"))
                continue;
            
            int wave = Integer.parseInt(n);
            String path = ConfigUtils.waveReward(arena, type, wave);
            String rewards = config.getString(path);
            
            result.put(wave, ItemParser.parseItems(rewards));
        }
        return result;
    }

    
    
    /* ///////////////////////////////////////////////////////////////////// //
    
            INVENTORY AND REWARD METHODS
    
    // ///////////////////////////////////////////////////////////////////// */
    
    /* Helper method for grabbing a random reward */
    public static ItemStack getRandomReward(List<ItemStack> rewards)
    {
        if (rewards.isEmpty())
            return null;
        
        Random ran = new Random();
        return rewards.get(ran.nextInt(rewards.size()));
    }
    
    
    
    /* ///////////////////////////////////////////////////////////////////// //
    
            PET CLASS METHODS
    
    // ///////////////////////////////////////////////////////////////////// */
    
    /**
     * Makes all nearby wolves sit if their owner is the given player.
     */
    public static void sitPets(Player p)
    {
        if (p == null)
            return;
        
        List<Entity> entities = p.getNearbyEntities(80, 40, 80);
        for (Entity e : entities)
        {
            if (!(e instanceof Wolf))
                continue;
            
            Wolf w = (Wolf) e;            
            if (w.isTamed() && w.getOwner() != null && w.getOwner().equals(p))
                w.setSitting(true);
        }
    }
    
    
    
    /* ///////////////////////////////////////////////////////////////////// //
    
            MISC METHODS
    
    // ///////////////////////////////////////////////////////////////////// */
    
    
    public static Player getClosestPlayer(MobArena plugin, Entity e, Arena arena) {
        // Set up the comparison variable and the result.
        double current = Double.POSITIVE_INFINITY;
        Player result = null;
        
        /* Iterate through the ArrayList, and update current and result every
         * time a squared distance smaller than current is found. */
        //for (Player p : arena.livePlayers)
        for (Player p : arena.getPlayersInArena()) {
            if (!arena.getWorld().equals(p.getWorld())) {
                Messenger.info("Player '" + p.getName() + "' is not in the right world. Kicking...");
                p.kickPlayer("[MobArena] Cheater! (Warped out of the arena world.)");
                Messenger.tellPlayer(p, "You warped out of the arena world.");
                continue;
            }
            
            double dist = distanceSquared(plugin, p, e.getLocation());
            if (dist < current && dist < 256D) {
                current = dist;
                result = p;
            }
        }
        return result;
    }
    
    public static double distanceSquared(MobArena plugin, Player p, Location l) {
        try {
            return p.getLocation().distanceSquared(l);
        }
        catch (Exception e) {
            p.kickPlayer("Banned for life! No, but stop trying to cheat in MobArena!");
            if (plugin != null) {
                Messenger.warning(p.getName() + " tried to cheat in MobArena and has been kicked.");
            }
            return Double.MAX_VALUE;
        }
    }
    
    /**
     * Convert a config-name to a proper spaced and capsed arena name.
     * The input String is split around all underscores, and every part
     * of the String array is properly capsed.
     */
    public static String nameConfigToArena(String name)
    {
        String[] parts = name.split("_");
        if (parts.length == 1) {
            return toCamelCase(parts[0]);
        }
        
        String separator = " ";
        StringBuffer buffy = new StringBuffer(name.length());
        for (String part : parts) {
            buffy.append(toCamelCase(part));
            buffy.append(separator);
        }
        buffy.replace(buffy.length()-1, buffy.length(), "");
        
        return buffy.toString();
    }
    
    /**
     * Returns the input String with a capital first letter, and all the
     * other letters become lower case. 
     */
    public static String toCamelCase(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
    
    /**
     * Turn a list into a space-separated string-representation of the list.
     */    
    public static <E> String listToString(Collection<E> list, boolean none, MobArena plugin)
    {
        if (list == null || list.isEmpty()) {
            return (none ? Msg.MISC_NONE.toString() : "");
        }
        
        StringBuffer buffy = new StringBuffer();
        int trimLength = 0;
        
        E type = list.iterator().next();
        if (type instanceof Player) {
            for (E e : list) {
                buffy.append(((Player) e).getName());
                buffy.append(" ");
            }
        }
        else if (type instanceof ItemStack) {
            trimLength = 2;
            ItemStack stack;
            for (E e : list) {
                stack = (ItemStack) e;
                if (stack.getTypeId() == MobArena.ECONOMY_MONEY_ID) {
                    String formatted = plugin.economyFormat(stack.getAmount());
                    if (formatted != null) {
                        buffy.append(formatted);
                        buffy.append(", ");
                    }
                    else {
                        Messenger.warning("Tried to do some money stuff, but no economy plugin was detected!");
                        return buffy.toString();
                    }
                    continue;
                }
                
                buffy.append(stack.getType().toString().toLowerCase());
                buffy.append(":");
                buffy.append(stack.getAmount());
                buffy.append(", ");
            }
        }
        else {
            for (E e : list) {
                buffy.append(e.toString());
                buffy.append(" ");
            }
        }
        return buffy.toString().substring(0, buffy.length() - trimLength);
    }
    public static <E> String listToString(Collection<E> list, JavaPlugin plugin) { return listToString(list, true, (MobArena) plugin); }
    
    /**
     * Returns a String-list version of a comma-separated list.
     */
    public static List<String> stringToList(String list)
    {
        List<String> result = new LinkedList<String>();
        if (list == null) return result;
        
        String[] parts = list.trim().split(",");
        
        for (String part : parts)
            result.add(part.trim());
        
        return result;
    }
    
    /**
     * Stand back, I'm going to try science!
     */
    public static boolean doooooItHippieMonster(Location loc, int radius, String name, MobArena plugin)
    {
        // Try to restore the old patch first.
        undoItHippieMonster(name, plugin, false);
        
        // Grab the Configuration and ArenaMaster
        ArenaMaster am = plugin.getArenaMaster();
                
        // Create the arena node in the config-file.
        World world = loc.getWorld();
        Arena arena = am.createArenaNode(name, world);
        am.setSelectedArena(arena);
        
        // Get the hippie bounds.
        int x1 = (int)loc.getX() - radius;
        int x2 = (int)loc.getX() + radius;
        int y1 = (int)loc.getY() - 9;
        int y2 = (int)loc.getY() - 1;
        int z1 = (int)loc.getZ() - radius;
        int z2 = (int)loc.getZ() + radius;
        
        int lx1 = x1;
        int lx2 = x1 + am.getClasses().size() + 3;
        int ly1 = y1-6;
        int ly2 = y1-2;
        int lz1 = z1;
        int lz2 = z1 + 6;
        
        // Save the precious patch
        HashMap<EntityPosition,Integer> preciousPatch = new HashMap<EntityPosition,Integer>();
        Location lo;
        int id;
        for (int i = x1; i <= x2; i++)
        {
            for (int j = ly1; j <= y2; j++)
            {
                for (int k = z1; k <= z2; k++)
                {
                    lo = world.getBlockAt(i,j,k).getLocation();
                    id = world.getBlockAt(i,j,k).getTypeId();
                    preciousPatch.put(new EntityPosition(lo),id);
                }
            }
        }
        try
        {
            new File("plugins" + sep + "MobArena" + sep + "agbackup").mkdir();
            FileOutputStream fos = new FileOutputStream("plugins" + sep + "MobArena" + sep + "agbackup" + sep + name + ".tmp");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(preciousPatch);
            oos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Messenger.warning("Couldn't create backup file. Aborting auto-generate...");
            return false;
        }
        
        // Build some monster walls.
        for (int i = x1; i <= x2; i++)
        {
            for (int j = y1; j <= y2; j++)
            {
                world.getBlockAt(i,j,z1).setTypeId(24);
                world.getBlockAt(i,j,z2).setTypeId(24);
            }
        }
        for (int k = z1; k <= z2; k++)
        {
            for (int j = y1; j <= y2; j++)
            {
                world.getBlockAt(x1,j,k).setTypeId(24);
                world.getBlockAt(x2,j,k).setTypeId(24);
            }
        }
        
        // Add some hippie light.
        for (int i = x1; i <= x2; i++)
        {
            world.getBlockAt(i,y1+2,z1).setTypeId(89);
            world.getBlockAt(i,y1+2,z2).setTypeId(89);
        }
        for (int k = z1; k <= z2; k++)
        {
            world.getBlockAt(x1,y1+2,k).setTypeId(89);
            world.getBlockAt(x2,y1+2,k).setTypeId(89);
        }
        
        // Build a monster floor, and some Obsidian foundation.
        for (int i = x1; i <= x2; i++)
        {
            for (int k = z1; k <= z2; k++)
            {
                world.getBlockAt(i,y1,k).setTypeId(24);
                world.getBlockAt(i,y1-1,k).setTypeId(49);
            }
        }
        
        // Make a hippie roof.
        for (int i = x1; i <= x2; i++)
        {
            for (int k = z1; k <= z2; k++)
                world.getBlockAt(i,y2,k).setTypeId(20);
        }
        
        // Monster bulldoze
        for (int i = x1+1; i < x2; i++)
            for (int j = y1+1; j < y2; j++)
                for (int k = z1+1; k < z2; k++)
                    world.getBlockAt(i,j,k).setTypeId(0);
        
        // Build a hippie lobby
        for (int i = lx1; i <= lx2; i++) // Walls
        {
            for (int j = ly1; j <= ly2; j++)
            {
                world.getBlockAt(i,j,lz1).setTypeId(24);
                world.getBlockAt(i,j,lz2).setTypeId(24);
            }
        }
        for (int k = lz1; k <= lz2; k++) // Walls
        {
            for (int j = ly1; j <= ly2; j++)
            {
                world.getBlockAt(lx1,j,k).setTypeId(24);
                world.getBlockAt(lx2,j,k).setTypeId(24);
            }
        }
        for (int k = lz1; k <= lz2; k++) // Lights
        {
            world.getBlockAt(lx1,ly1+2,k).setTypeId(89);
            world.getBlockAt(lx2,ly1+2,k).setTypeId(89);
            world.getBlockAt(lx1,ly1+3,k).setTypeId(89);
            world.getBlockAt(lx2,ly1+3,k).setTypeId(89);
        }
        for (int i = lx1; i <= lx2; i++) // Floor
        {
            for (int k = lz1; k <= lz2; k++)
                world.getBlockAt(i,ly1,k).setTypeId(24);
        }
        for (int i = x1+1; i < lx2; i++) // Bulldoze
            for (int j = ly1+1; j <= ly2; j++)
                for (int k = lz1+1; k < lz2; k++)
                    world.getBlockAt(i,j,k).setTypeId(0);
        
        // Place the hippie signs
        //Iterator<String> iterator = am.getClasses().iterator();
        Iterator<String> iterator = am.getClasses().keySet().iterator();
        for (int i = lx1+2; i <= lx2-2; i++) // Signs
        {
            world.getBlockAt(i,ly1+1,lz2-1).setTypeIdAndData(63, (byte)0x8, false);
            Sign sign = (Sign) world.getBlockAt(i,ly1+1,lz2-1).getState();
            sign.setLine(0, TextUtils.camelCase((String)iterator.next()));
            sign.update();
        }
        world.getBlockAt(lx2-2,ly1+1,lz1+2).setType(Material.IRON_BLOCK);
        
        // Set up the monster points. 
        ArenaRegion region = arena.getRegion();
        region.set("p1", new Location(world, x1, ly1, z1));
        region.set("p2", new Location(world, x2, y2+1, z2));
        
        region.set("arena", new Location(world, loc.getX(), y1+1, loc.getZ()));
        region.set("lobby", new Location(world, x1+2, ly1+1, z1+2));
        region.set("spectator", new Location(world, loc.getX(), y2+1, loc.getZ()));
        
        region.addSpawn("s1", new Location(world, x1+3, y1+2, z1+3));
        region.addSpawn("s2", new Location(world, x1+3, y1+2, z2-3));
        region.addSpawn("s3", new Location(world, x2-3, y1+2, z1+3));
        region.addSpawn("s4", new Location(world, x2-3, y1+2, z2-3));
        region.save();
        
        am.reloadConfig();
        return true;
    }
    
    /**
     * This fixes everything!
     */
    @SuppressWarnings("unchecked")
    public static boolean undoItHippieMonster(String name, MobArena plugin, boolean error)
    {
        File file = new File("plugins" + sep + "MobArena" + sep + "agbackup" + sep + name + ".tmp");
        HashMap<EntityPosition,Integer> preciousPatch;
        try
        {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
            preciousPatch = (HashMap<EntityPosition,Integer>) ois.readObject();
            ois.close();
        }
        catch (Exception e)
        {
            if (error) Messenger.warning("Couldn't find backup file for arena '" + name + "'");
            return false;
        }
        
        World world = plugin.getServer().getWorld(preciousPatch.keySet().iterator().next().getWorld());
        
        for (Map.Entry<EntityPosition,Integer> entry : preciousPatch.entrySet())
        {
            world.getBlockAt(entry.getKey().getLocation(world)).setTypeId(entry.getValue());
        }
        
        Config config = plugin.getMAConfig();
        config.remove("arenas." + name);
        config.save();
        
        file.delete();
        
        plugin.getArenaMaster().reloadConfig();
        return true;
    }
}