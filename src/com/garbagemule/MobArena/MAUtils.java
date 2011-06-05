package com.garbagemule.MobArena;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import org.bukkit.block.Sign;
import org.bukkit.block.BlockFace;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.config.Configuration;

public class MAUtils
{                                                  
    public static final List<Integer>  SWORDS_ID   = new LinkedList<Integer>();
    public static final List<Material> SWORDS_TYPE = new LinkedList<Material>();
    static
    {
        SWORDS_TYPE.add(Material.WOOD_SWORD);
        SWORDS_TYPE.add(Material.STONE_SWORD);
        SWORDS_TYPE.add(Material.GOLD_SWORD);
        SWORDS_TYPE.add(Material.IRON_SWORD);
        SWORDS_TYPE.add(Material.DIAMOND_SWORD);
    }

    
    /* ///////////////////////////////////////////////////////////////////// //
    
            INVENTORY AND REWARD METHODS
    
    // ///////////////////////////////////////////////////////////////////// */

    /* Clears the players inventory and armor slots. */
    public static PlayerInventory clearInventory(Player p)
    {
        PlayerInventory inv = p.getInventory();
        inv.clear();
        inv.setHelmet(null);
        inv.setChestplate(null);
        inv.setLeggings(null);
        inv.setBoots(null);
        return inv;
    }
    
    /* Checks if all inventory and armor slots are empty. */
    public static boolean hasEmptyInventory(Player player)
    {
		ItemStack[] inventory = player.getInventory().getContents();
		ItemStack[] armor     = player.getInventory().getArmorContents();
        
        // For inventory, check for null
        for (ItemStack stack : inventory)
            if (stack != null) return false;
        
        // For armor, check for id 0, or AIR
        for (ItemStack stack : armor)
            if (stack.getTypeId() != 0) return false;
        
        return true;
	}
    
    /* Gives all the items in the input string(s) to the player */
    public static void giveItems(boolean reward, Player p, String... strings)
    {
        // Variables used.
        ItemStack stack, current;
        int id, amount;
        
        PlayerInventory inv;
        
        if (reward)
            inv = p.getInventory();
        else
            inv = clearInventory(p);
        
        for (String s : strings)
        {
            /* Trim the list, remove possible trailing commas, split by
             * commas, and start the item loop. */
            s = s.trim();
            if (s.endsWith(","))
                s = s.substring(0, s.length()-1);
            String[] items = s.split(",");
            
            // For every item in the list
            for (String i : items)
            {
                /* Take into account possible amount, and if there is
                 * one, set the amount variable to that amount, else 1. */
                i = i.trim();
                String[] item = i.split(":");
                if (item.length == 2 && item[1].matches("[0-9]+"))
                    amount = Integer.parseInt(item[1]);
                else
                    amount = 1;
                
                // Create ItemStack with appropriate constructor.
                if (item[0].matches("[0-9]+"))
                {
                    id = Integer.parseInt(item[0]);
                    stack = new ItemStack(id, amount);
                    if (!reward && SWORDS_ID.contains(id))
                        stack.setDurability((short)-3276);
                }
                else
                {
                    stack = makeItemStack(item[0], amount);
                    if (stack == null) continue;
                    if (!reward && SWORDS_ID.contains(stack.getTypeId()))
                        stack.setDurability((short)-3276);
                }
                
                inv.addItem(stack);
            }
        }
    }
    
    /* Used for giving items "normally". */
    public static void giveItems(Player p, String... strings)
    {
        giveItems(false, p, strings);
    }
    
    /* Helper method for grabbing a random reward */
    public static String getRandomReward(String rewardlist)
    {
        Random ran = new Random();
        
        String[] rewards = rewardlist.split(",");
        String item = rewards[ran.nextInt(rewards.length)];
        return item.trim();
    }
    
    /* Helper method for making an ItemStack out of a string */
    private static ItemStack makeItemStack(String s, int amount)
    {
        Material mat;
        try
        {
            mat = Material.valueOf(s.toUpperCase());
            return new ItemStack(mat, amount);
        }
        catch (Exception e)
        {
            System.out.println("[MobArena] ERROR! Could not create item " + s + ". Check config.yml");
            return null;
        }
    }
    
    
    
    /* ///////////////////////////////////////////////////////////////////// //
    
            INITIALIZATION METHODS
    
    // ///////////////////////////////////////////////////////////////////// */
    
    /**
     * Replaces all tabs with 4 spaces in config.yml
     
    public static void fixConfigFile()
    {
        new File("plugins/MobArena").mkdir();
        File configFile = new File("plugins/MobArena/config.yml");
        
        try
        {
            if(!configFile.exists())
            {
                configFile.createNewFile();
            }
            else
            {
                // Create an inputstream from the file
                FileInputStream fis = new FileInputStream(configFile);
                
                // Read the file into a byte array, and make a String out of it.
                byte[] bytes = new byte[(int)configFile.length()];
                fis.read(bytes);
                String input = new String(bytes);
                
                // Close the stream.
                fis.close();
                
                // Replace all tabs with 4 spaces.
                String output = "";
                for (char c : input.toCharArray())
                {
                    if (c == '\t')
                        output += "    ";
                    else
                        output += c;
                }
                
                // Create an outputstream from the file.
                FileOutputStream fos = new FileOutputStream(configFile);
                
                // Write all the bytes to it.
                for (byte b : output.getBytes())
                    fos.write(b);
                
                // Close the stream.
                fos.close();
            }
        }
        catch(Exception e)
        {
            System.out.println("[MobArena] ERROR: Config file could not be created.");
        }
    }
    */
    
    /**
     * Creates a Configuration object from the config.yml file.
     */
    public static Configuration getConfig()
    {
        new File("plugins/MobArena").mkdir();
        File configFile = new File("plugins/MobArena/config.yml");
        
        try
        {
            if(!configFile.exists())
            {
                configFile.createNewFile();
            }
        }
        catch(Exception e)
        {
            System.out.println("[MobArena] ERROR: Config file could not be created.");
            return null;
        }
        
        return new Configuration(configFile);
    }
    
    public static List<String> getDisabledCommands()
    {
        Configuration c = ArenaManager.config;
        c.load();
        
        String commands = c.getString("disabledcommands", "kill");
        c.setProperty("disabledcommands", commands);
        c.save();
        
        List<String> result = new LinkedList<String>();
        for (String s : commands.split(","))
        {
            System.out.println(s.trim());
            result.add(s.trim());
        }
        
        return result;
    }
    
    /**
     * Grabs the world from the config-file, or the "default" world
     * from the list of worlds in the server object.
     */
    public static World getWorld()
    {
        Configuration c = ArenaManager.config;
        c.load();
        
        String world = c.getString("world", ArenaManager.server.getWorlds().get(0).getName());
        c.setProperty("world", world);
        
        c.save();
        return ArenaManager.server.getWorld(world);
    }
    
    /**
     * Grabs the list of classes from the config-file. If no list is
     * found, generate a set of default classes.
     */
    public static List<String> getClasses()
    {
        Configuration c = ArenaManager.config;
        c.load();
        
        if (c.getKeys("classes") == null)
        {
            c.setProperty("classes.Archer.items", "wood_sword, bow, arrow:128, grilled_pork");
            c.setProperty("classes.Archer.armor", "298,299,300,301");
            c.setProperty("classes.Knight.items", "diamond_sword, grilled_pork");
            c.setProperty("classes.Knight.armor", "306,307,308,309");
            c.setProperty("classes.Tank.items",   "iron_sword, grilled_pork:2");
            c.setProperty("classes.Tank.armor",   "310,311,312,313");
            c.setProperty("classes.Oddjob.items", "stone_sword, flint_and_steel, netherrack:2, wood_pickaxe, wood_door, fishing_rod, apple, grilled_pork:3");
            c.setProperty("classes.Oddjob.armor", "298,299,300,301");
            c.setProperty("classes.Chef.items",   "stone_sword, bread:6, grilled_pork:4, mushroom_soup, cake:3, cookie:12");
            c.setProperty("classes.Chef.armor",   "314,315,316,317");
            
            c.save();
        }
        
        return c.getKeys("classes");
    }
    
    /**
     * Generates a map of class names and class items based on the
     * type of items ("items" or "armor") and the config-file.
     * Will explode if the classes aren't well-defined.
     */
    public static Map<String,String> getClassItems(String type)
    {
        Configuration c = ArenaManager.config;
        c.load();
        
        Map<String,String> result = new HashMap<String,String>();
        
        // Assuming well-defined classes.
        List<String> classes = c.getKeys("classes");
        for (String s : classes)
        {
            result.put(s, c.getString("classes." + s + "." + type, null));
        }
        
        return result;
    }
    
    /**
     * Generates a map of wave numbers and rewards based on the
     * type of wave ("after" or "every") and the config-file. If
     * no keys exist in the config-file, an empty map is returned.
     */
    public static Map<Integer,String> getWaveMap(String type)
    {
        Configuration c = ArenaManager.config;
        c.load();
        
        // Set up variables and resulting map.
        Map<Integer,String> result = new HashMap<Integer,String>();
        int wave;
        String rewards;
        
        /* Check if the keys exist in the config-file, if not, set some. */
        if (c.getKeys("rewards.waves." + type) == null)
        {
            if (type.equals("every"))
            {
                c.setProperty("rewards.waves.every.3", "feather, bone, stick");
                c.setProperty("rewards.waves.every.5", "dirt:4, gravel:4, stone:4");
                c.setProperty("rewards.waves.every.10", "iron_ingot:10, gold_ingot:8");
            }
            else if (type.equals("after"))
            {
                c.setProperty("rewards.waves.after.7", "minecart, storage_minecart, powered_minecart");
                c.setProperty("rewards.waves.after.13", "iron_sword, iron_pickaxe, iron_spade");
                c.setProperty("rewards.waves.after.16", "diamon_sword");
            }
            
            c.save();
        }
        List<String> waves = c.getKeys("rewards.waves." + type);
        
        // Put all the rewards in the map.
        for (String n : waves)
        {
            if (!n.matches("[0-9]+"))
                continue;
            
            wave = Integer.parseInt(n);
            rewards = c.getString("rewards.waves." + type + "." + n);
            
            result.put(wave,rewards);
        }
        
        // And return the resulting map.
        return result;
    }
    
    /**
     * Grabs all the spawnpoints from the config-file. IF no points
     * are found, an empty list is returned.
     */
    public static List<Location> getSpawnPoints()
    {
        Configuration c = ArenaManager.config;
        c.load();
        
        List<String> spawnpoints = c.getKeys("coords.spawnpoints");
        if (spawnpoints == null)
            return new LinkedList<Location>();
        
        List<Location> result = new LinkedList<Location>();
        for (String s : spawnpoints)
        {
            Location loc = getCoords("spawnpoints." + s);
            
            if (loc != null)
                result.add(loc);
        }
        
        return result;
    }
    
    /**
     * Grabs the distribution coefficients from the config-file. If
     * no coefficients are found, defaults (10) are added.
     */
    public static int getDistribution(String monster)
    {
        return getDistribution(monster, "default");
    }
    
    public static int getDistribution(String monster, String type)
    {
        Configuration c = ArenaManager.config;
        c.load();
        
        if (c.getInt("waves." + type + "." + monster, -1) == -1)
        {
            int dist = 10;
            if (monster.equals("giants") || monster.equals("ghasts") || monster.equals("slimes"))
                dist = 0;
            
            c.setProperty("waves." + type + "." + monster, dist);
            c.save();
        }
        
        return c.getInt("waves." + type + "." + monster, 0);
    }
    
    
    /* ///////////////////////////////////////////////////////////////////// //
    
            REGION AND SETUP METHODS
    
    // ///////////////////////////////////////////////////////////////////// */
    
    /**
     * Checks if the Location object is within the arena region.
     */
    public static boolean inRegion(Location loc)
    {
        Location p1 = ArenaManager.p1;
        Location p2 = ArenaManager.p2;
        
        // Return false if the location is outside of the region.
        if ((loc.getX() < p1.getX()) || (loc.getX() > p2.getX()))
            return false;
            
        if ((loc.getZ() < p1.getZ()) || (loc.getZ() > p2.getZ()))
            return false;
            
        if ((loc.getY() < p1.getY()) || (loc.getY() > p2.getY()))
            return false;
            
        return true;
    }
    
    /**
     * Grabs coordinate information from the config-file.
     */
    public static Location getCoords(String name)
    {
        Configuration c = ArenaManager.config;
        c.load();
        
        // Return null if coords aren't in the config file.
        if (c.getKeys("coords." + name) == null)
            return null;
        
        double x    = c.getDouble("coords." + name + ".x", 0);
        double y    = c.getDouble("coords." + name + ".y", 0);
        double z    = c.getDouble("coords." + name + ".z", 0);
        
        return new Location(ArenaManager.world, x, y, z);
    }
    
    /**
     * Writes coordinate information to the config-file.
     */
    public static void setCoords(String name, Location loc)
    {
        Configuration c = ArenaManager.config;
        c.load();
        
        c.setProperty("coords." + name + ".world", loc.getWorld().getName());
        c.setProperty("coords." + name + ".x",     loc.getX());
        c.setProperty("coords." + name + ".y",     loc.getY());
        c.setProperty("coords." + name + ".z",     loc.getZ());
        c.setProperty("coords." + name + ".yaw",   loc.getYaw());
        c.setProperty("coords." + name + ".pitch", loc.getPitch());
        
        c.save();
        ArenaManager.updateVariables();
    }
    
    /**
     * Removes coordinate information from the config-file.
     */    
    public static void delCoords(String name)
    {
        Configuration c = ArenaManager.config;
        c.load();
        
        c.removeProperty(name);
        
        c.save();
        ArenaManager.updateVariables();
    }
    
    /**
     * Maintains the invariant that p1's coordinates are of lower
     * values than their respective counter-parts of p2. Makes the
     * inRegion()-method much faster/easier.
     */
    public static void fixCoords()
    {
        Location p1 = getCoords("p1");
        Location p2 = getCoords("p2");
        double tmp;
        
        if (p1 == null || p2 == null)
            return;
            
        if (p1.getX() > p2.getX())
        {
            tmp = p1.getX();
            p1.setX(p2.getX());
            p2.setX(tmp);
        }
        
        if (p1.getY() > p2.getY())
        {
            tmp = p1.getY();
            p1.setY(p2.getY());
            p2.setY(tmp);
        }
        
        if (p1.getZ() > p2.getZ())
        {
            tmp = p1.getZ();
            p1.setZ(p2.getZ());
            p2.setZ(tmp);
        }
        
        setCoords("p1", p1);
        setCoords("p2", p2);
    }
    
    /**
     * Expands the arena region either upwards, downwards, or
     * outwards (meaning on both the X and Z axes).
     */
    public static void expandRegion(String direction, int i)
    {
        Location p1 = ArenaManager.p1;
        Location p2 = ArenaManager.p2;
        
        if (direction.equals("up"))
            p2.setY(p2.getY() + i);
        else if (direction.equals("down"))
            p1.setY(p1.getY() - i);
        else if (direction.equals("out"))
        {
            p1.setX(p1.getX() - i);
            p1.setZ(p1.getZ() - i);
            p2.setX(p2.getX() + i);
            p2.setZ(p2.getZ() + i);
        }
        
        setCoords("p1", p1);
        setCoords("p2", p2);
        fixCoords();
    }
    
    public static String spawnList()
    {
        Configuration c = ArenaManager.config;
        c.load();
        
        String result = "";
        if (c.getKeys("coords.spawnpoints") == null)
            return result;
        
        for (String s : c.getKeys("coords.spawnpoints"))
            result += s + " ";
        
        return result;
    }
    
    
    
    /* ///////////////////////////////////////////////////////////////////// //
    
            MISC METHODS
    
    // ///////////////////////////////////////////////////////////////////// */
    
    /**
     * Verifies that all important variables are declared. Returns true
     * if, and only if, the warppoints, region, distribution coefficients,
     * classes and spawnpoints are all set up.
     */
    public static boolean verifyData()
    {
        return ((ArenaManager.arenaLoc     != null) &&
                (ArenaManager.lobbyLoc     != null) &&
                (ArenaManager.spectatorLoc != null) &&
                (ArenaManager.p1           != null) &&
                (ArenaManager.p2           != null) &&
                (ArenaManager.dZombies     != -1)   &&
                (ArenaManager.dSkeletons   != -1)   &&
                (ArenaManager.dSpiders     != -1)   &&
                (ArenaManager.dCreepers    != -1)   &&
                (ArenaManager.classes.size() > 0)   &&
                (ArenaManager.spawnpoints.size() > 0));
    }   
    
    /**
     * Notifies the player if MobArena is set up and ready to be used.
     */
    public static void notifyIfSetup(Player p)
    {
        if (verifyData())
        {
            ArenaManager.tellPlayer(p, "MobArena is set up and ready to roll!");
        }
    }
    
    /**
     * Turns the current set of players into an array, and grabs a random
     * element out of it.
     */
    public static Player getRandomPlayer()
    {
        Random random = new Random();
        Object[] array = ArenaManager.playerSet.toArray();
        return (Player) array[random.nextInt(array.length)];
    }
    
    /**
     * Stand back, I'm going to try science!
     */
    public static void DoooooItHippieMonster(Location loc, int radius)
    {
        // Get the hippie bounds.
        int x1 = (int)loc.getX() - radius;
        int x2 = (int)loc.getX() + radius;
        int y1 = (int)loc.getY() - 9;
        int y2 = (int)loc.getY() - 1;
        int z1 = (int)loc.getZ() - radius;
        int z2 = (int)loc.getZ() + radius;
        
        int lx1 = x1;
        int lx2 = x1 + ArenaManager.classes.size() + 3;
        int ly1 = y1-5;
        int ly2 = y1-1;
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
                    lo = ArenaManager.world.getBlockAt(i,j,k).getLocation();
                    id = ArenaManager.world.getBlockAt(i,j,k).getTypeId();
                    preciousPatch.put(new EntityPosition(lo),id);
                }
            }
        }
        try
        {
            FileOutputStream fos = new FileOutputStream("plugins/MobArena/precious.tmp");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(preciousPatch);
            oos.close();
        }
        catch (Exception e)
        {
            System.out.println("Couldn't create backup file. Aborting...");
            e.printStackTrace();
            return;
        }
        
        // Build some monster walls.
        for (int i = x1; i <= x2; i++)
        {
            for (int j = y1; j <= y2; j++)
            {
                ArenaManager.world.getBlockAt(i,j,z1).setTypeId(24);
                ArenaManager.world.getBlockAt(i,j,z2).setTypeId(24);
            }
        }
        for (int k = z1; k <= z2; k++)
        {
            for (int j = y1; j <= y2; j++)
            {
                ArenaManager.world.getBlockAt(x1,j,k).setTypeId(24);
                ArenaManager.world.getBlockAt(x2,j,k).setTypeId(24);
            }
        }
        
        // Add some hippie light.
        for (int i = x1; i <= x2; i++)
        {
            ArenaManager.world.getBlockAt(i,y1+2,z1).setTypeId(89);
            ArenaManager.world.getBlockAt(i,y1+2,z2).setTypeId(89);
        }
        for (int k = z1; k <= z2; k++)
        {
            ArenaManager.world.getBlockAt(x1,y1+2,k).setTypeId(89);
            ArenaManager.world.getBlockAt(x2,y1+2,k).setTypeId(89);
        }
        
        // Build a monster floor.
        for (int i = x1; i <= x2; i++)
        {
            for (int k = z1; k <= z2; k++)
                ArenaManager.world.getBlockAt(i,y1,k).setTypeId(24);
        }
        
        // Make a hippie roof.
        for (int i = x1; i <= x2; i++)
        {
            for (int k = z1; k <= z2; k++)
                ArenaManager.world.getBlockAt(i,y2,k).setTypeId(20);
        }
        
        // Monster bulldoze
        for (int i = x1+1; i < x2; i++)
            for (int j = y1+1; j < y2; j++)
                for (int k = z1+1; k < z2; k++)
                    ArenaManager.world.getBlockAt(i,j,k).setTypeId(0);
        
        // Build a hippie lobby
        for (int i = lx1; i <= lx2; i++) // Walls
        {
            for (int j = ly1; j <= ly2; j++)
            {
                ArenaManager.world.getBlockAt(i,j,lz1).setTypeId(24);
                ArenaManager.world.getBlockAt(i,j,lz2).setTypeId(24);
            }
        }
        for (int k = lz1; k <= lz2; k++) // Walls
        {
            for (int j = ly1; j <= ly2; j++)
            {
                ArenaManager.world.getBlockAt(lx1,j,k).setTypeId(24);
                ArenaManager.world.getBlockAt(lx2,j,k).setTypeId(24);
            }
        }
        for (int k = lz1; k <= lz2; k++) // Lights
        {
            ArenaManager.world.getBlockAt(lx1,ly1+2,k).setTypeId(89);
            ArenaManager.world.getBlockAt(lx2,ly1+2,k).setTypeId(89);
            ArenaManager.world.getBlockAt(lx1,ly1+3,k).setTypeId(89);
            ArenaManager.world.getBlockAt(lx2,ly1+3,k).setTypeId(89);
        }
        for (int i = lx1; i <= lx2; i++) // Floor
        {
            for (int k = lz1; k <= lz2; k++)
                ArenaManager.world.getBlockAt(i,ly1,k).setTypeId(24);
        }
        for (int i = x1+1; i < lx2; i++) // Bulldoze
            for (int j = ly1+1; j <= ly2; j++)
                for (int k = lz1+1; k < lz2; k++)
                    ArenaManager.world.getBlockAt(i,j,k).setTypeId(0);
        
        // Place the hippie signs
        java.util.Iterator iterator = ArenaManager.classes.iterator();
        for (int i = lx1+2; i <= lx2-2; i++) // Signs
        {
            ArenaManager.world.getBlockAt(i,ly1+1,lz2-1).setTypeIdAndData(63, (byte)0x8, false);
            Sign sign = (Sign) ArenaManager.world.getBlockAt(i,ly1+1,lz2-1).getState();
            sign.setLine(0, (String)iterator.next());
        }
        ArenaManager.world.getBlockAt(lx2-2,ly1+1,lz1+2).setType(Material.IRON_BLOCK);
        
        // Set up the monster points.
        setCoords("arena", new Location(ArenaManager.world, loc.getX(), y1+1, loc.getZ()));
        setCoords("lobby", new Location(ArenaManager.world, x1+2, y1-3, z1+2));
        setCoords("spectator", new Location(ArenaManager.world, loc.getX(), y2+1, loc.getZ()));
        setCoords("p1", new Location(ArenaManager.world, x1, y1-4, z1));
        setCoords("p2", new Location(ArenaManager.world, x2, y2+1, z2));
        setCoords("spawnpoints.s1", new Location(ArenaManager.world, x1+3, y1+2, z1+3));
        setCoords("spawnpoints.s2", new Location(ArenaManager.world, x1+3, y1+2, z2-3));
        setCoords("spawnpoints.s3", new Location(ArenaManager.world, x2-3, y1+2, z1+3));
        setCoords("spawnpoints.s4", new Location(ArenaManager.world, x2-3, y1+2, z2-3));
    }
    
    /**
     * This fixes everything!
     */
    @SuppressWarnings("unchecked")
    public static void UnDoooooItHippieMonster()
    {
        HashMap<EntityPosition,Integer> preciousPatch;
        try
        {
            FileInputStream fis = new FileInputStream("plugins/MobArena/precious.tmp");
            ObjectInputStream ois = new ObjectInputStream(fis);
            preciousPatch = (HashMap) ois.readObject();
            ois.close();
        }
        catch (Exception e)
        {
            System.out.println("Couldn't find backup file...");
            return;
        }
        
        for (EntityPosition ep : preciousPatch.keySet())
        {
            ArenaManager.world.getBlockAt(ep.getLocation(ArenaManager.world)).setTypeId(preciousPatch.get(ep));
        }
        
        delCoords("coords");
    }
}