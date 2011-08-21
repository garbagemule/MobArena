package com.garbagemule.MobArena;

import java.net.URI;
import java.net.HttpURLConnection;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.server.WorldServer;

import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.config.Configuration;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.garbagemule.MobArena.MAMessages.Msg;
import com.garbagemule.MobArena.util.EntityPosition;
import com.garbagemule.MobArena.util.InventoryItem;
import com.garbagemule.MobArena.util.jnbt.ByteTag;
import com.garbagemule.MobArena.util.jnbt.CompoundTag;
import com.garbagemule.MobArena.util.jnbt.ListTag;
import com.garbagemule.MobArena.util.jnbt.NBTInputStream;
import com.garbagemule.MobArena.util.jnbt.NBTOutputStream;
import com.garbagemule.MobArena.util.jnbt.ShortTag;
import com.garbagemule.MobArena.util.jnbt.Tag;

public class MAUtils
{         
    public static final String sep = File.separator;
    // Weapons
    public static final List<Material> WEAPONS_TYPE     = new LinkedList<Material>();
    public static final List<Material> SWORDS_TYPE      = new LinkedList<Material>();
    public static final List<Material> AXES_TYPE        = new LinkedList<Material>();
    public static final List<Material> PICKAXES_TYPE    = new LinkedList<Material>();
    public static final List<Material> SPADES_TYPE      = new LinkedList<Material>();
    public static final List<Material> HOES_TYPE        = new LinkedList<Material>();
    // Armor
    public static final List<Material> ARMORS_TYPE      = new LinkedList<Material>();
    public static final List<Material> HELMETS_TYPE     = new LinkedList<Material>();
    public static final List<Material> CHESTPLATES_TYPE = new LinkedList<Material>();
    public static final List<Material> LEGGINGS_TYPE    = new LinkedList<Material>();
    public static final List<Material> BOOTS_TYPE       = new LinkedList<Material>();
    static
    {
        // Weapons
        SWORDS_TYPE.add(Material.WOOD_SWORD);
        SWORDS_TYPE.add(Material.STONE_SWORD);
        SWORDS_TYPE.add(Material.GOLD_SWORD);
        SWORDS_TYPE.add(Material.IRON_SWORD);
        SWORDS_TYPE.add(Material.DIAMOND_SWORD);
        
        AXES_TYPE.add(Material.WOOD_AXE);
        AXES_TYPE.add(Material.STONE_AXE);
        AXES_TYPE.add(Material.GOLD_AXE);
        AXES_TYPE.add(Material.IRON_AXE);
        AXES_TYPE.add(Material.DIAMOND_AXE);
        
        PICKAXES_TYPE.add(Material.WOOD_PICKAXE);
        PICKAXES_TYPE.add(Material.STONE_PICKAXE);
        PICKAXES_TYPE.add(Material.GOLD_PICKAXE);
        PICKAXES_TYPE.add(Material.IRON_PICKAXE);
        PICKAXES_TYPE.add(Material.DIAMOND_PICKAXE);
        
        SPADES_TYPE.add(Material.WOOD_SPADE);
        SPADES_TYPE.add(Material.STONE_SPADE);
        SPADES_TYPE.add(Material.GOLD_SPADE);
        SPADES_TYPE.add(Material.IRON_SPADE);
        SPADES_TYPE.add(Material.DIAMOND_SPADE);
        
        HOES_TYPE.add(Material.WOOD_HOE);
        HOES_TYPE.add(Material.STONE_HOE);
        HOES_TYPE.add(Material.GOLD_HOE);
        HOES_TYPE.add(Material.IRON_HOE);
        HOES_TYPE.add(Material.DIAMOND_HOE);

        WEAPONS_TYPE.addAll(SWORDS_TYPE);
        WEAPONS_TYPE.addAll(AXES_TYPE);
        WEAPONS_TYPE.addAll(PICKAXES_TYPE);
        WEAPONS_TYPE.addAll(SPADES_TYPE);
        WEAPONS_TYPE.addAll(HOES_TYPE);

        // Armor
        HELMETS_TYPE.add(Material.LEATHER_HELMET);
        HELMETS_TYPE.add(Material.GOLD_HELMET);
        HELMETS_TYPE.add(Material.CHAINMAIL_HELMET);
        HELMETS_TYPE.add(Material.IRON_HELMET);
        HELMETS_TYPE.add(Material.DIAMOND_HELMET);
        HELMETS_TYPE.add(Material.PUMPKIN);
        
        CHESTPLATES_TYPE.add(Material.LEATHER_CHESTPLATE);
        CHESTPLATES_TYPE.add(Material.GOLD_CHESTPLATE);
        CHESTPLATES_TYPE.add(Material.CHAINMAIL_CHESTPLATE);
        CHESTPLATES_TYPE.add(Material.IRON_CHESTPLATE);
        CHESTPLATES_TYPE.add(Material.DIAMOND_CHESTPLATE);
        
        LEGGINGS_TYPE.add(Material.LEATHER_LEGGINGS);
        LEGGINGS_TYPE.add(Material.GOLD_LEGGINGS);
        LEGGINGS_TYPE.add(Material.CHAINMAIL_LEGGINGS);
        LEGGINGS_TYPE.add(Material.IRON_LEGGINGS);
        LEGGINGS_TYPE.add(Material.DIAMOND_LEGGINGS);

        BOOTS_TYPE.add(Material.LEATHER_BOOTS);
        BOOTS_TYPE.add(Material.GOLD_BOOTS);
        BOOTS_TYPE.add(Material.CHAINMAIL_BOOTS);
        BOOTS_TYPE.add(Material.IRON_BOOTS);
        BOOTS_TYPE.add(Material.DIAMOND_BOOTS);

        ARMORS_TYPE.addAll(HELMETS_TYPE);
        ARMORS_TYPE.addAll(CHESTPLATES_TYPE);
        ARMORS_TYPE.addAll(LEGGINGS_TYPE);
        ARMORS_TYPE.addAll(BOOTS_TYPE);        
    }
    
      
    
    /* ///////////////////////////////////////////////////////////////////// //
    
            INITIALIZATION METHODS
    
    // ///////////////////////////////////////////////////////////////////// */
    
    public static Map<String,Location> getArenaContainers(Configuration config, World world, String arena)
    {
        Map<String,Location> containers = new HashMap<String,Location>();
        String arenaPath = "arenas." + arena + ".coords.containers";
        
        if (config.getKeys(arenaPath) == null)
            return containers;
        
        for (String point : config.getKeys(arenaPath))
            containers.put(point, makeLocation(world, config.getString(arenaPath + "." + point)));
        
        return containers;
    }
    
    /**
     * Grab all the spawnpoints for a specific arena.
     */
    public static Map<String,Location> getArenaSpawnpoints(Configuration config, World world, String arena)
    {
        Map<String,Location> spawnpoints = new HashMap<String,Location>();
        String arenaPath = "arenas." + arena + ".coords.spawnpoints";
        
        if (config.getKeys(arenaPath) == null)
            return spawnpoints;
        
        for (String point : config.getKeys(arenaPath))
            if (!point.matches("^(.)*boss(.)*$"))
                spawnpoints.put(point, makeLocation(world, config.getString(arenaPath + "." + point)));
        
        return spawnpoints;
    }
    
    public static Map<String,Location> getArenaBossSpawnpoints(Configuration config, World world, String arena)
    {
        Map<String,Location> spawnpoints = new HashMap<String,Location>();
        String arenaPath = "arenas." + arena + ".coords.spawnpoints";
        
        if (config.getKeys(arenaPath) == null)
            return spawnpoints;
        
        for (String point : config.getKeys(arenaPath))
            if (point.matches("^(.)*boss(.)*$"))
                spawnpoints.put(point, makeLocation(world, config.getString(arenaPath + "." + point)));
        
        return spawnpoints;
    }
    
    /**
     * Returns a map of classnames mapped to lists of ItemStacks.
     */
    public static Map<String,List<ItemStack>> getClassItems(Configuration config, String type)
    {
        Map<String,List<ItemStack>> result = new HashMap<String,List<ItemStack>>();
        
        for (String className : config.getKeys("classes"))
            result.put(className, makeItemStackList(config.getString("classes." + className + "." + type)));
        
        return result;
    }
    
    public static List<ItemStack> getEntryFee(Configuration config, String arena)
    {
        return makeItemStackList(config.getString("arenas." + arena + ".settings.entry-fee", null));
    }
    
    /**
     * Takes a comma-separated list of items in the <type>:<amount> format and
     * returns a list of ItemStacks created from that data.
     */
    public static List<ItemStack> makeItemStackList(String string)
    {
        List<ItemStack> result = new LinkedList<ItemStack>();
        if (string == null || string.isEmpty()) return result;
        
        // Trim commas and whitespace, and split items by commas
        string = string.trim();
        if (string.endsWith(","))
            string = string.substring(0, string.length()-1);
        String[] items = string.split(",");
        
        for (String item : items)
        {
            // Trim whitespace and split by colons.
            item = item.trim();
            String[] parts = item.split(":");
            
            // Grab the amount.
            int amount = 1;
            if (parts.length == 1 && parts[0].matches("\\$[0-9]+"))
                amount = Integer.parseInt(parts[0].substring(1, parts[0].length()));
            else if (parts.length == 2 && parts[1].matches("(-)?[0-9]+"))
                amount = Integer.parseInt(parts[1]);
            else if (parts.length == 3 && parts[2].matches("(-)?[0-9]+"))
                amount = Integer.parseInt(parts[2]);
            
            
            // Make the ItemStack.
            ItemStack stack = (parts.length == 3) ?
                    makeItemStack(parts[0], amount, parts[1]) :
                    makeItemStack(parts[0], amount);
            
            if (stack != null)
                result.add(stack);
        }
        return result;
    }
    
    /**
     * Generates a map of wave numbers and rewards based on the
     * type of wave ("after" or "every") and the config-file. If
     * no keys exist in the config-file, an empty map is returned.
     */    
    public static Map<Integer,List<ItemStack>> getArenaRewardMap(Configuration config, String arena, String type)
    {
        String arenaPath = "arenas." + arena + ".rewards.waves.";
        Map<Integer,List<ItemStack>> result = new HashMap<Integer,List<ItemStack>>();
        
        if (config.getKeys(arenaPath + type) == null)
        {
            if (type.equals("every"))
            {
                config.setProperty(arenaPath + "every.3", "feather, bone, stick");
                config.setProperty(arenaPath + "every.5", "dirt:4, gravel:4, stone:4");
                config.setProperty(arenaPath + "every.10", "iron_ingot:10, gold_ingot:8");
            }
            else if (type.equals("after"))
            {
                config.setProperty(arenaPath + "after.7", "minecart, storage_minecart, powered_minecart");
                config.setProperty(arenaPath + "after.13", "iron_sword, iron_pickaxe, iron_spade");
                config.setProperty(arenaPath + "after.16", "diamond_sword");
            }
        }
        
        List<String> waves = config.getKeys(arenaPath + type);
        if (waves == null) return result;
        
        for (String n : waves)
        {
            if (!n.matches("[0-9]+"))
                continue;
            
            int wave = Integer.parseInt(n);
            String rewards = config.getString(arenaPath + type + "." + n);
            
            result.put(wave, makeItemStackList(rewards));
        }
        return result;
    }
    
    public static List<String> getAllowedCommands(Configuration config)
    {
        String commands = config.getString("global-settings.allowed-commands");
        if (commands == null)
        {
            config.setProperty("global-settings.allowed-commands", "/list, /pl");
            config.save();
            commands = config.getString("global-settings.allowed-commands");
        }
        
        return stringToList(commands);
    }

    
    
    /* ///////////////////////////////////////////////////////////////////// //
    
            INVENTORY AND REWARD METHODS
    
    // ///////////////////////////////////////////////////////////////////// */
    
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
    
    public static boolean storeInventory(Player p)
    {
        // Set up the files and paths
        String invPath = "plugins" + sep + "MobArena" + sep + "inventories";
        new File(invPath).mkdir();
        File backupFile = new File(invPath + sep + p.getName() + ".inv");
        
        // If a backup file already exists, restore the inventory first
        if (backupFile.exists() && !restoreInventory(p))
            return false;
        
        // Grab the inventory contents.
        ItemStack[] items = p.getInventory().getContents();
        ItemStack[] armor = p.getInventory().getArmorContents();
        
        try
        {
            backupFile.createNewFile();
            
            InventoryItem[] inv = new InventoryItem[items.length + armor.length];
            for (int i = 0; i < items.length; i++)
                inv[i] = InventoryItem.parseItemStack(items[i]);
            for (int i = 0; i < armor.length; i++)
                inv[i + items.length] = InventoryItem.parseItemStack(armor[i]);
            
            FileOutputStream   fos = new FileOutputStream(backupFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(inv);
            oos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            MobArena.warning("Could not create backup file for " + p.getName() + ".");
            return false;
        }
        
        clearInventory(p);
        return true;
    }
    
    public static boolean restoreInventory(Player p)
    {
        // Grab the items from the MobArena .inv file
        ItemStack[] stacks = getInventoryFile(p);
        
        // If the player isn't online, hack the playerName.dat file
        if (!p.isOnline())
            return writeInventoryData(p, stacks);
        
        // Otherwise, restore the inventory directly
        ItemStack[] items = new ItemStack[stacks.length-4];
        ItemStack[] armor = new ItemStack[4];
        
        for (int i = 0; i < stacks.length - 4; i++)
            items[i] = stacks[i];
        for (int i = 0; i < 4; i++)
            armor[i] = stacks[stacks.length - 4 + i];
        
        // Restore the inventory.
        PlayerInventory inv = p.getInventory();
        inv.setArmorContents(armor);
        for (int i = 0; i < items.length; i++)
            inv.setItem(i, items[i]);
        
        return true;
    }
    
    public static ItemStack[] getInventoryFile(Player p)
    {
        String invPath = "plugins" + sep + "MobArena" + sep + "inventories";
        File backupFile = new File(invPath + sep + p.getName() + ".inv");
        
        try
        {
            // If the backup-file couldn't be found, return.
            if (!backupFile.exists())
                return null;
            
            // Grab the MAInventoryItem array from the backup-file.
            FileInputStream   fis      = new FileInputStream(backupFile);
            ObjectInputStream ois      = new ObjectInputStream(fis);
            InventoryItem[] fromFile   = (InventoryItem[]) ois.readObject();
            ois.close();

            // Delete the file
            backupFile.delete();
            
            return InventoryItem.toItemStacks(fromFile);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            MobArena.warning("Could not restore inventory for " + p.getName());
            return null;
        }
    }
    
    public static ItemStack[] readInventoryData(Player p)
    {
        // Grab the data dir <world>/players/
        File playerDir = new File(Bukkit.getServer().getWorlds().get(0).getName(), "players");
        
        try
        {
            NBTInputStream in = new NBTInputStream(new FileInputStream(new File(playerDir, p.getName() + ".dat")));
            CompoundTag tag = (CompoundTag) in.readTag();
            in.close();

            ListTag inventory = (ListTag) tag.getValue().get("Inventory");
            
            ItemStack[] stacks = new ItemStack[40];
            for (int i = 0; i < inventory.getValue().size(); i++)
            {
                CompoundTag item = (CompoundTag) inventory.getValue().get(i);
                byte count = ((ByteTag)     item.getValue().get("Count")).getValue();
                byte slot = ((ByteTag)      item.getValue().get("Slot")).getValue();
                short damage = ((ShortTag)  item.getValue().get("Damage")).getValue();
                short id = ((ShortTag)      item.getValue().get("id")).getValue();
                stacks[slot < 36 ? slot : 36 + 103-slot] = new ItemStack(id, count, damage);
            }
            return stacks;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            MobArena.warning("Could not restore inventory for " + p.getName());
            return null;
        }
    }
    
    public static boolean writeInventoryData(Player p, ItemStack[] stacks)
    {
        // Abort if stacks is null
        if (stacks == null) return false;
        
        // Grab the data dir <world>/players/
        File playerDir = new File(Bukkit.getServer().getWorlds().get(0).getName(), "players");
        
        try
        {
            NBTInputStream in = new NBTInputStream(new FileInputStream(new File(playerDir, p.getName() + ".dat")));
            CompoundTag tag = (CompoundTag) in.readTag();
            in.close();

            ArrayList<Tag> tagList = new ArrayList<Tag>();
            
            for (int i = 0; i < stacks.length; i++)
            {
                if (stacks[i] == null) continue;

                ByteTag count = new ByteTag("Count", (byte) stacks[i].getAmount());
                ByteTag slot = new ByteTag("Slot", (byte) (i < 36 ? i : 104-(stacks.length-i)));
                ShortTag damage = new ShortTag("Damage", stacks[i].getDurability());
                ShortTag id = new ShortTag("id", (short) stacks[i].getTypeId());

                HashMap<String, Tag> tagMap = new HashMap<String, Tag>();
                tagMap.put("Count", count);
                tagMap.put("Slot", slot);
                tagMap.put("Damage", damage);
                tagMap.put("id", id);

                tagList.add(new CompoundTag("", tagMap));
            }
            
            ListTag inventory = new ListTag("Inventory", CompoundTag.class, tagList);

            HashMap<String, Tag> tagCompound = new HashMap<String, Tag>(tag.getValue());
            tagCompound.put("Inventory", inventory);
            tag = new CompoundTag("Player", tagCompound);

            NBTOutputStream out = new NBTOutputStream(new FileOutputStream(new File(playerDir, p.getName() + ".dat")));
            out.writeTag(tag);
            out.close();
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            MobArena.warning("Could not restore inventory for " + p.getName());
            return false;
        }
    }
    
    /* Checks if all inventory and armor slots are empty. */
    public static boolean hasEmptyInventory(Player p)
    {
		ItemStack[] inventory = p.getInventory().getContents();
		ItemStack[] armor     = p.getInventory().getArmorContents();
        
        // For inventory, check for null
        for (ItemStack stack : inventory)
            if (stack != null) return false;
        
        // For armor, check for id 0, or AIR
        for (ItemStack stack : armor)
            if (stack.getTypeId() != 0) return false;
        
        return true;
	}
    
    /**
     * Gives the player all of the items in the list of ItemStacks.
     */
    public static void giveItems(Player p, List<ItemStack> stacks, boolean autoEquip, boolean rewards, MobArena plugin)
    {
        if (stacks == null)
            return;
        
        if (!p.isOnline())
        {
            ItemStack[] items = readInventoryData(p);
            int currentSlot = 0;
            for (ItemStack stack : stacks)
            {
                while (currentSlot < items.length && items[currentSlot] != null)
                    currentSlot++;
                
                if (currentSlot >= items.length)
                    break;
                
                items[currentSlot] = stack;
            }
            
            writeInventoryData(p, items);
            return;
        }
        
        PlayerInventory inv = p.getInventory();
        for (ItemStack stack : stacks)
        {
            if (stack == null)
                continue;
            
            // If this is money, don't add to inventory.
            if (stack.getTypeId() == MobArena.ECONOMY_MONEY_ID)
            {
                if (plugin != null && plugin.Methods.hasMethod())
                    plugin.Method.getAccount(p.getName()).add(stack.getAmount());

                continue;
            }
            
            // If these are rewards, don't tamper with them.
            if (rewards)
            {
                //inv.addItem(stack);
                giveItem(inv, stack);
                continue;
            }

            // If this is an armor piece, equip it.
            if (autoEquip && ARMORS_TYPE.contains(stack.getType()))
            {
                equipArmorPiece(stack, inv);
                continue;
            }
            
            // If this is a weapon, set its durability to "unlimited".
            if (WEAPONS_TYPE.contains(stack.getType()))
                stack.setDurability((short) -32768);

            giveItem(inv, stack);
            //inv.addItem(stack);
        }
    }
    
    public static void giveItems(Player p, List<ItemStack> stacks, boolean autoEquip, MobArena plugin)
    {
        giveItems(p, stacks, autoEquip, false, plugin);
    }
    
    public static void giveItem(PlayerInventory inv, ItemStack stack)
    {
        int id     = stack.getTypeId();
        int amount = stack.getAmount();
        
        int times     = amount / 64;
        int remainder = amount % 64;
        
        for (int i = 0; i < times; i++)
            inv.addItem(new ItemStack(id, 64));
            
        if (remainder > 0)
            inv.addItem(new ItemStack(id, remainder));
    }
    
    public static void giveRewards(Player p, List<ItemStack> stacks, MobArena plugin)
    {
        giveItems(p, stacks, false, true, plugin);
    }
    
    public static int getPetAmount(Player p)
    {
        int result = 0;
        
        for (ItemStack stack : p.getInventory().getContents())
        {
            if (stack == null || stack.getTypeId() != 352)
                continue;

            result += stack.getAmount();
        }
        
        return result;
    }
    
    /* Helper method for equipping armor pieces. */
    public static void equipArmorPiece(ItemStack stack, PlayerInventory inv)
    {
        Material type = stack.getType();
        
        if (HELMETS_TYPE.contains(type))
            inv.setHelmet(stack);
        else if (CHESTPLATES_TYPE.contains(type))
            inv.setChestplate(stack);
        else if (LEGGINGS_TYPE.contains(type))
            inv.setLeggings(stack);
        else if (BOOTS_TYPE.contains(type))
            inv.setBoots(stack);
    }
    
    /* Helper methods for making ItemStacks out of strings and ints */
    public static ItemStack makeItemStack(String name, int amount, String data)
    {
        // If this is economy money, create a dummy ItemStack.
        if (name.matches("\\$[0-9]+"))
            return new ItemStack(MobArena.ECONOMY_MONEY_ID, amount);
        
        try
        {
            byte offset = 0;
            
            Material material = (name.matches("[0-9]+")) ?
                Material.getMaterial(Integer.parseInt(name)) :
                Material.valueOf(name.toUpperCase());
            
            if (material == Material.INK_SACK)
                offset = 15;
                
            DyeColor dye = (data.matches("[0-9]+")) ?
                DyeColor.getByData((byte) Math.abs(offset - Integer.parseInt(data))) :
                DyeColor.valueOf(data.toUpperCase());
                
            //return new ItemStack(material, amount, (byte) Math.abs((offset - dye.getData())));
            return new ItemStack(material, amount, (byte) Math.abs(offset - dye.getData()));
        }
        catch (Exception e)
        {
            MobArena.warning("Could not create item \"" + name + "\". Check config.yml");
            return null;
        }
    }
    public static ItemStack makeItemStack(String name, int amount)
    {
        return makeItemStack(name, amount, "0");
    }
    
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
    
            REGION METHODS
    
    // ///////////////////////////////////////////////////////////////////// */
    
    /**
     * Check if a Location is inside two points (x1,y1,z1) (x2,y2,z2)
     */
    public static boolean inRegion(Location loc, double x1, double y1, double z1, double x2, double y2, double z2)
    {
        double x = loc.getBlockX();
        double y = loc.getBlockY();
        double z = loc.getBlockZ();
        
        return x >= x1 && x <= x2 &&
               y >= y1 && y <= y2 &&
               z >= z1 && z <= z2;
    }
    
    /**
     * Create a frame spanned by the two input coordinates.
     * @return An int arry holding x,y,z and the original type IDs of each block.
     */
    public static Set<int[]> showRegion(World world, Location p1, Location p2, int id, byte color)
    {
        Set<int[]> result = new HashSet<int[]>();

        int x1 = p1.getBlockX(); int y1 = p1.getBlockY(); int z1 = p1.getBlockZ();
        int x2 = p2.getBlockX(); int y2 = p2.getBlockY(); int z2 = p2.getBlockZ();

        int[] buffer;
        
        for (int i = x1; i <= x2; i++)
        {
            buffer = new int[] {i, y1, z1, world.getBlockTypeIdAt(i, y1, z1)};
            result.add(buffer);
            world.getBlockAt(i, y1, z1).setTypeIdAndData(id, color, false);
            
            buffer = new int[] {i, y2, z1, world.getBlockTypeIdAt(i, y2, z1)};
            result.add(buffer);
            world.getBlockAt(i, y2, z1).setTypeIdAndData(id, color, false);
            
            buffer = new int[] {i, y1, z2, world.getBlockTypeIdAt(i, y1, z2)};
            result.add(buffer);
            world.getBlockAt(i, y1, z2).setTypeIdAndData(id, color, false);
            
            buffer = new int[] {i, y2, z2, world.getBlockTypeIdAt(i, y2, z2)};
            result.add(buffer);
            world.getBlockAt(i, y2, z2).setTypeIdAndData(id, color, false);
        }
        for (int j = y1+1; j <= y2-1; j++)
        {
            buffer = new int[] {x1, j, z1, world.getBlockTypeIdAt(x1, j, z1)};
            result.add(buffer);
            world.getBlockAt(x1, j, z1).setTypeIdAndData(id, color, false);

            buffer = new int[] {x2, j, z1, world.getBlockTypeIdAt(x2, j, z1)};
            result.add(buffer);
            world.getBlockAt(x2, j, z1).setTypeIdAndData(id, color, false);
            
            buffer = new int[] {x1, j, z2, world.getBlockTypeIdAt(x1, j, z2)};
            result.add(buffer);
            world.getBlockAt(x1, j, z2).setTypeIdAndData(id, color, false);

            buffer = new int[] {x2, j, z2, world.getBlockTypeIdAt(x2, j, z2)};
            result.add(buffer);
            world.getBlockAt(x2, j, z2).setTypeIdAndData(id, color, false);
        }
        for (int k = z1+1; k <= z2-1; k++)
        {
            buffer = new int[] {x1, y1, k, world.getBlockTypeIdAt(x1, y1, k)};
            result.add(buffer);
            world.getBlockAt(x1, y1, k).setTypeIdAndData(id, color, false);

            buffer = new int[] {x2, y1, k, world.getBlockTypeIdAt(x2, y1, k)};
            result.add(buffer);
            world.getBlockAt(x2, y1, k).setTypeIdAndData(id, color, false);
            
            buffer = new int[] {x1, y2, k, world.getBlockTypeIdAt(x1, y2, k)};
            result.add(buffer);
            world.getBlockAt(x1, y2, k).setTypeIdAndData(id, color, false);

            buffer = new int[] {x2, y2, k, world.getBlockTypeIdAt(x2, y2, k)};
            result.add(buffer);
            world.getBlockAt(x2, y2, k).setTypeIdAndData(id, color, false);
        }
        
        return result;
    }
    
    public static Set<int[]> showRegion(World world, Location p1, Location p2, int id)
    {
        return showRegion(world, p1, p2, id, (byte) 0);
    }
    
    /**
     * Take all the blocks with coordinates (buffer[0], buffer[1], buffer[2]) and set
     * their type ID to buffer[3]. Used to hide regions shown by showRegion.
     */
    public static void hideRegion(int[] buffer)
    {
        
    }
    
    /**
     * Create a Location object from the config-file.
     */
    public static Location getArenaCoord(Configuration config, World world, String arena, String coord)
    {
        //config.load();
        String str = config.getString("arenas." + arena + ".coords." + coord);
        if (str == null)
            return null;
        return makeLocation(world, str);
    }
    
    /**
     * Save an arena location to the Configuration.
     */    
    public static void setArenaCoord(Configuration config, Arena arena, String coord, Location loc)
    {
        if (coord.equals("arena") || coord.equals("lobby") || coord.equals("spectator"))
            loc.setY(loc.getY() + 1);
        
        config.setProperty("arenas." + arena.configName() + ".coords." + coord, makeCoord(loc));
        config.save();
        arena.load(config);
        
        if (coord.equals("p1") || coord.equals("p2"))
            fixRegion(config, loc.getWorld(), arena);
        if (coord.equals("l1") || coord.equals("l2"))
            fixLobby(config, loc.getWorld(), arena);
    }
    
    public static boolean delArenaCoord(Configuration config, Arena arena, String coord)
    {
        if (config.getString("arenas." + arena.configName() + ".coords." + coord) == null)
            return false;
        
        config.removeProperty("arenas." + arena.configName() + ".coords." + coord);
        config.save();
        arena.load(config);
        return true;
    }
    
    public static void fixRegion(Configuration config, World world, Arena arena)
    {
        if (arena.p1 == null || arena.p2 == null)
            return;
        
        if (arena.p1.getX() > arena.p2.getX())
        {
            double tmp = arena.p1.getX();
            arena.p1.setX(arena.p2.getX());
            arena.p2.setX(tmp);
        }
        
        if (arena.p1.getZ() > arena.p2.getZ())
        {
            double tmp = arena.p1.getZ();
            arena.p1.setZ(arena.p2.getZ());
            arena.p2.setZ(tmp);
        }
        
        if (arena.p1.getY() > arena.p2.getY())
        {
            double tmp = arena.p1.getY();
            arena.p1.setY(arena.p2.getY());
            arena.p2.setY(tmp);
        }
        
        if (!arena.world.getName().equals(world.getName()))
            arena.world = world;
        
        arena.serializeConfig();
        arena.load(config);
    }
    
    private static void fixLobby(Configuration config, World world, Arena arena)
    {
        if (arena.l1 == null || arena.l2 == null)
            return;
        
        if (arena.l1.getX() > arena.l2.getX())
        {
            double tmp = arena.l1.getX();
            arena.l1.setX(arena.l2.getX());
            arena.l2.setX(tmp);
        }
        
        if (arena.l1.getZ() > arena.l2.getZ())
        {
            double tmp = arena.l1.getZ();
            arena.l1.setZ(arena.l2.getZ());
            arena.l2.setZ(tmp);
        }
        
        if (arena.l1.getY() > arena.l2.getY())
        {
            double tmp = arena.l1.getY();
            arena.l1.setY(arena.l2.getY());
            arena.l2.setY(tmp);
        }
        arena.serializeConfig();
        arena.load(config);
    }
    
    /**
     * Create a Location from the input String in the input World.
     */
    public static Location makeLocation(World world, String str, boolean extras)
    {
        String[] parts = str.split(",");
        
        double x     = Double.parseDouble(parts[0].trim());
        double y     = Double.parseDouble(parts[1].trim());
        double z     = Double.parseDouble(parts[2].trim());
        
        if (extras && parts.length > 3)
        {
            float yaw   = Float.parseFloat(parts[3].trim());
            float pitch = Float.parseFloat(parts[4].trim());
            return new Location(world, x, y, z, yaw, pitch);
        }

        return new Location(world, x, y, z);
    }
    
    public static Location makeLocation(World world, String str)
    {
        return makeLocation(world, str, true);
    }
    
    /**
     * Create a location String from the input Location.
     */
    public static String makeCoord(Location loc, boolean extras)
    {
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        
        if (extras)
        {
            float yaw   = loc.getYaw();
            float pitch = loc.getPitch();
            return x + "," + y + "," + z + "," + yaw + "," + pitch;
        }
        
        return x + "," + y + "," + z;
    }
    
    public static String makeCoord(Location loc)
    {
        return makeCoord(loc, true);
    }
    
    /**
     * Check if a location is within any arena region.
     */
    public static boolean inRegions(Location loc, Arena... arenas)
    {
        for (Arena arena : arenas)
        {
            if (arena.inRegion(loc))
                return true;
        }
        
        return false;
    }
    
    
    
    /* ///////////////////////////////////////////////////////////////////// //
    
            MISC METHODS
    
    // ///////////////////////////////////////////////////////////////////// */
    
    public static boolean tellSpoutPlayer(Player p, Msg msg, String s, Material logo)
    {
        // Grab the SpoutPlayer.
        SpoutPlayer sp = MobArena.hasSpout ? SpoutManager.getPlayer(p) : null;
        
        if (msg.hasSpoutMsg() && sp != null && sp.isSpoutCraftEnabled())
        {
            // Grab the message text.
            String text = msg.getSpout(s);
            
            // If more than 26 characters, truncate.
            if (text.length() > 26)
                text = text.substring(0, 26);
            
            // If the logo is null, use an iron sword.
            if (logo == null)
                logo = msg.getLogo();
            
            // Send the notification.
            sp.sendNotification("MobArena", text, logo, (short) 0, 2000);
            return true;
        }
        else return tellPlayer(p, msg.get(s));
    }
    
    public static boolean tellSpoutPlayer(Player p, Msg msg, Material logo)
    {
        return tellSpoutPlayer(p, msg, null, logo);
    }
    
    public static boolean tellSpoutPlayer(Player p, Msg msg, String s)
    {
        return tellSpoutPlayer(p, msg, s, null);
    }
    
    public static boolean tellSpoutPlayer(Player p, Msg msg)
    {
        return tellSpoutPlayer(p, msg, null, null);
    }
    
    public static boolean tellPlayer(CommandSender p, String msg)
    {
        // If the input sender is null or the string is empty, return.
        if (p == null || msg.equals(" "))
            return false;
        
        // Otherwise, send the message with the [MobArena] tag.
        p.sendMessage(ChatColor.GREEN + "[MobArena] " + ChatColor.WHITE + msg);
        return true;
    }
    
    public static boolean tellPlayer(CommandSender p, Msg msg, String s, boolean spout, Material logo)
    {
        if (spout && p instanceof Player)
            return tellSpoutPlayer((Player) p, msg, s, logo);
        
        return tellPlayer(p, msg.get(s));
    }
    
    public static boolean tellPlayer(CommandSender p, Msg msg, String s, Material logo)
    {
        return tellPlayer(p, msg, s, MobArena.hasSpout, logo);
    }
    
    public static boolean tellPlayer(CommandSender p, Msg msg, String s)
    {
        return tellPlayer(p, msg, s, MobArena.hasSpout, null);
    }
    
    public static boolean tellPlayer(CommandSender p, Msg msg)
    {
        return tellPlayer(p, msg, null, MobArena.hasSpout, null);
    }
    
    /**
     * Sends a message to all players in and around the arena.
     */
    public static void tellAll(Arena arena, Msg msg, String s, boolean notifyPlayers)
    {
        Set<Player> tmp = new HashSet<Player>();
        tmp.addAll(arena.arenaPlayers);
        tmp.addAll(arena.lobbyPlayers);
        tmp.addAll(arena.readyPlayers);
        tmp.addAll(arena.notifyPlayers);
        tmp.addAll(arena.specPlayers);
        if (notifyPlayers) tmp.addAll(arena.notifyPlayers);
        for (Player p : tmp)
            tellPlayer(p, msg, s);
    }
    
    public static void tellAll(Arena arena, Msg msg, String s)
    {
        tellAll(arena, msg, s, false);
    }
    
    public static void tellAll(Arena arena, Msg msg, boolean notifyPlayers)
    {
        tellAll(arena, msg, null, notifyPlayers);
    }
    
    public static void tellAll(Arena arena, Msg msg)
    {
        tellAll(arena, msg, null, false);
    }
    
    public static Player getClosestPlayer(Entity e, Arena arena)
    {
        // Set up the comparison variable and the result.
        double current = Double.POSITIVE_INFINITY;
        Player result = null;
        
        /* Iterate through the ArrayList, and update current and result every
         * time a squared distance smaller than current is found. */
        //for (Player p : arena.livePlayers)
        for (Player p : arena.arenaPlayers)
        {
            if (!arena.world.equals(p.getWorld()))
            {
                MobArena.info("Player '" + p.getName() + "' is not in the right world. Kicking...");
                p.kickPlayer("[MobArena] Cheater! (Warped out of the arena world.)");
                tellPlayer(p, "You warped out of the arena world.");
                continue;
            }
            
            double dist = p.getLocation().distanceSquared(e.getLocation());
            if (dist < current && dist < 256)
            {
                current = dist;
                result = p;
            }
        }
        return result;
    }
    
    /**
     * Convert a proper arena name to a config-file name.
     * All spaces are replaced by underscores, and the whole String is
     * lowercased.
     */
    public static String nameArenaToConfig(String name)
    {
        String tmp = name.replace(" ", "_");
        return tmp.toLowerCase();
    }
    
    /**
     * Convert a config-name to a proper spaced and capsed arena name.
     * The input String is split around all underscores, and every part
     * of the String array is properly capsed.
     */
    public static String nameConfigToArena(String name)
    {
        String[] parts = name.split("_");
        if (parts.length == 1)
            return toCamelCase(parts[0]);
        
        String separator = " ";
        StringBuffer buffy = new StringBuffer(name.length());
        for (String part : parts)
        {
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
    public static String toCamelCase(String name)
    {
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }
    
    /**
     * Turn a list into a space-separated string-representation of the list.
     */    
    public static <E> String listToString(List<E> list, boolean none, MobArena plugin)
    {
        if (list == null || list.isEmpty())
        {
            if (none)
                return Msg.MISC_NONE.get();
            else
                return "";
        }
        
        StringBuffer buffy = new StringBuffer();
        int trimLength = 0;
        
        E type = list.get(0);
        if (type instanceof Player)
        {
            for (E e : list)
            {
                buffy.append(((Player) e).getName());
                buffy.append(" ");
            }
        }
        else if (type instanceof ItemStack)
        {
            trimLength = 2;
            ItemStack stack;
            for (E e : list)
            {
                stack = (ItemStack) e;
                if (stack.getTypeId() == MobArena.ECONOMY_MONEY_ID)
                {
                    if (plugin.Methods.hasMethod())
                    {
                        buffy.append(plugin.Method.format(stack.getAmount()));
                        buffy.append(", ");
                    }
                    continue;
                }
                
                buffy.append(stack.getType().toString().toLowerCase());
                buffy.append(":");
                buffy.append(stack.getAmount());
                buffy.append(", ");
            }
        }
        else
        {
            for (E e : list)
            {
                buffy.append(e.toString());
                buffy.append(" ");
            }
        }
        return buffy.toString().substring(0, buffy.length() - trimLength);
    }
    public static <E> String listToString(List<E> list, MobArena plugin) { return listToString(list, true, plugin); }
    public static <E> String listToString(List<E> list) { return listToString(list, true, (MobArena) Bukkit.getServer().getPluginManager().getPlugin("MobArena")); }
    
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
     * Turns the current set of players into an array, and grabs a random
     * element out of it.
     */
    public static Player getRandomPlayer(Arena arena)
    {
        Random random = new Random();
        //Player[] array = (Player[]) arena.livePlayers.toArray();
        Player[] array = (Player[]) arena.arenaPlayers.toArray();
        return array[random.nextInt(array.length)];
    }
    
    /**
     * Verifies that all important variables are declared. Returns true
     * if, and only if, the warppoints, region, distribution coefficients,
     * and spawnpoints are all set up.
     */    
    public static boolean verifyData(Arena arena)
    {
        return ((arena.arenaLoc       != null) &&
                (arena.lobbyLoc       != null) &&
                (arena.spectatorLoc   != null) &&
                (arena.p1             != null) &&
                (arena.p2             != null) &&
                (arena.spawnpoints.size() > 0));
    }
    
    public static boolean verifyLobby(Arena arena)
    {
        return ((arena.l1 != null) &&
                (arena.l2 != null));
    }
    
    public static void checkData(Arena arena, CommandSender p)
    {
        if (arena.arenaLoc == null)
            tellPlayer(p, "Missing warp: arena");
        if (arena.lobbyLoc == null)
            tellPlayer(p, "Missing warp: lobby");
        if (arena.spectatorLoc == null)
            tellPlayer(p, "Missing warp: spectator");
        if (arena.p1 == null)
            tellPlayer(p, "Missing region point: p1");
        if (arena.p2 == null)
            tellPlayer(p, "Missing region point: p2");
        if (arena.spawnpoints.size() <= 0)
            tellPlayer(p, "Missing spawnpoints");
        if (arena.setup)
            tellPlayer(p, "Arena is ready to be used!");
    }

    /**
     * Checks if there is a new update of MobArena and notifies the
     * player if the boolean specified is true
     */
    public static void checkForUpdates(MobArena plugin, final Player p, boolean response)
    {
        String site = "http://forums.bukkit.org/threads/19144/";
        try
        {
            // Make a URI of the site address
            URI baseURI = new URI(site);
            
            // Open the connection and don't redirect.
            HttpURLConnection con = (HttpURLConnection) baseURI.toURL().openConnection();
            con.setConnectTimeout(5000);
            con.setInstanceFollowRedirects(false);
            
            String header = con.getHeaderField("Location");
            
            // If something's wrong with the connection...
            if (header == null)
            {
                tellPlayer(p, "Couldn't connect to the MobArena thread.");
                return;
            }
            
            // Otherwise, grab the location header to get the real URI.
            String url = new URI(con.getHeaderField("Location")).toString();
            
            // Set up the regex and matcher
            Pattern regex   = Pattern.compile("v([0-9]+-)*[0-9]+");
            Matcher matcher = regex.matcher(url);
            if (!matcher.find())
                return;

            // Split the version strings
            String[] forumVersion = matcher.group().substring(1).split("-");
            String[] thisVersion  = plugin.getDescription().getVersion().split("\\.");

            // If the current version is older than the forum version, notify.
            for (int i = 0; i < Math.min(forumVersion.length, thisVersion.length); i++)
            {
                if (Integer.parseInt(forumVersion[i]) > Integer.parseInt(thisVersion[i]))
                {
                    tellPlayer(p, "There is a new version of MobArena available!");;
                    return;
                }
            }
            
            if (response) tellPlayer(p, "Your version of MobArena is up to date!");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static void setSpawnFlags(MobArena plugin, World world, int spawnMonsters, boolean allowMonsters, boolean allowAnimals)
    {
        for (Arena arena : plugin.getAM().getArenasInWorld(world))
            if (arena.running)
                return;
        
        WorldServer ws = ((CraftWorld) world).getHandle();
        ws.spawnMonsters = spawnMonsters;
        ws.allowMonsters = allowMonsters;
        ws.allowAnimals  = allowAnimals;
    }
    
    public static String getDuration(long duration)
    {
        long seconds = duration / 1000;
        long secs = seconds % 60;
        long mins = (seconds - secs) / 60 % 60;
        long hrs  = ((seconds - secs) - (mins * 60)) / 60 / 60;
        return hrs + ":" + ((mins < 10) ? "0" + mins : mins) + ":" + ((secs < 10) ? "0" + secs : secs);
    }
    
    public static long parseDuration(String duration)
    {
        String[] elements = duration.split(":");
        long hrs  = Long.parseLong(elements[0]) * 60 * 60;
        long mins = Long.parseLong(elements[1]) * 60;
        long secs = Long.parseLong(elements[2]);
        
        return (hrs + mins + secs) * 1000;
    }
    
    /**
     * Stand back, I'm going to try science!
     */
    public static boolean doooooItHippieMonster(Location loc, int radius, String name, MobArena plugin)
    {
        // Try to restore the old patch first.
        undoItHippieMonster(name, plugin, false);
        
        // Grab the Configuration and ArenaMaster
        ArenaMaster am = plugin.getAM();
                
        // Create the arena node in the config-file.
        World world = loc.getWorld();
        Arena arena = am.createArenaNode(name, world);
        am.arenas.add(arena);
        am.selectedArena = arena;
        
        // Get the hippie bounds.
        int x1 = (int)loc.getX() - radius;
        int x2 = (int)loc.getX() + radius;
        int y1 = (int)loc.getY() - 9;
        int y2 = (int)loc.getY() - 1;
        int z1 = (int)loc.getZ() - radius;
        int z2 = (int)loc.getZ() + radius;
        
        int lx1 = x1;
        int lx2 = x1 + am.classes.size() + 3;
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
            MobArena.warning("Couldn't create backup file. Aborting auto-generate...");
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
        Iterator<String> iterator = am.classes.iterator();
        for (int i = lx1+2; i <= lx2-2; i++) // Signs
        {
            world.getBlockAt(i,ly1+1,lz2-1).setTypeIdAndData(63, (byte)0x8, false);
            Sign sign = (Sign) world.getBlockAt(i,ly1+1,lz2-1).getState();
            sign.setLine(0, (String)iterator.next());
        }
        world.getBlockAt(lx2-2,ly1+1,lz1+2).setType(Material.IRON_BLOCK);
        
        // Set up the monster points.            
        MAUtils.setArenaCoord(plugin.getConfig(), arena, "p1", new Location(world, x1, ly1, z1));
        MAUtils.setArenaCoord(plugin.getConfig(), arena, "p2", new Location(world, x2, y2+1, z2));
        MAUtils.setArenaCoord(plugin.getConfig(), arena, "arena", new Location(world, loc.getX(), y1+1, loc.getZ()));
        MAUtils.setArenaCoord(plugin.getConfig(), arena, "lobby", new Location(world, x1+2, ly1+1, z1+2));
        MAUtils.setArenaCoord(plugin.getConfig(), arena, "spectator", new Location(world, loc.getX(), y2+1, loc.getZ()));
        MAUtils.setArenaCoord(plugin.getConfig(), arena, "spawnpoints.s1", new Location(world, x1+3, y1+2, z1+3));
        MAUtils.setArenaCoord(plugin.getConfig(), arena, "spawnpoints.s2", new Location(world, x1+3, y1+2, z2-3));
        MAUtils.setArenaCoord(plugin.getConfig(), arena, "spawnpoints.s3", new Location(world, x2-3, y1+2, z1+3));
        MAUtils.setArenaCoord(plugin.getConfig(), arena, "spawnpoints.s4", new Location(world, x2-3, y1+2, z2-3));
        
        am.updateAll();
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
            if (error) MobArena.warning("Couldn't find backup file for arena '" + name + "'");
            return false;
        }
        
        World world = Bukkit.getServer().getWorld(preciousPatch.keySet().iterator().next().getWorld());
        
        for (Map.Entry<EntityPosition,Integer> entry : preciousPatch.entrySet())
        {
            world.getBlockAt(entry.getKey().getLocation(world)).setTypeId(entry.getValue());
        }
        
        Configuration config = plugin.getConfig();
        config.removeProperty("arenas." + name);
        config.save();
        
        file.delete();
        
        plugin.getAM().updateAll();
        return true;
    }
}