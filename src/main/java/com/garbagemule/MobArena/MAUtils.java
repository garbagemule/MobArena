package com.garbagemule.MobArena;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.region.ArenaRegion;
import com.garbagemule.MobArena.things.InvalidThingInputString;
import com.garbagemule.MobArena.things.ThingPicker;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.Rotatable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MAUtils
{
    /* ///////////////////////////////////////////////////////////////////// //

            INITIALIZATION METHODS

    // ///////////////////////////////////////////////////////////////////// */

    /**
     * Generates a map of wave numbers and rewards based on the
     * type of wave ("after" or "every") and the config-file. If
     * no keys exist in the config-file, an empty map is returned.
     */
    public static Map<Integer, ThingPicker> getArenaRewardMap(MobArena plugin, ConfigurationSection config, String arena, String type)
    {
        //String arenaPath = "arenas." + arena + ".rewards.waves.";
        Map<Integer, ThingPicker> result = new HashMap<>();

        String typePath = "rewards.waves." + type;
        if (!config.contains(typePath)) return result;

        //Set<String> waves = config.getKeys(arenaPath + type);
        Set<String> waves = config.getConfigurationSection(typePath).getKeys(false);
        if (waves == null) return result;

        for (String n : waves)
        {
            if (!n.matches("[0-9]+"))
                continue;

            int wave = Integer.parseInt(n);
            String path = typePath + "." + wave;
            String rewards = config.getString(path);

            try {
                String wrapped = "random(" + rewards + ")";
                ThingPicker picker = plugin.getThingPickerManager().parse(wrapped);
                result.put(wave, picker);
            } catch (InvalidThingInputString e) {
                throw new ConfigError("Failed to parse reward for wave " + wave + " in the '" + type + "' branch of arena " + arena + ": " + e.getInput());
            }
        }
        return result;
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
        List<Player> players = new ArrayList<>(arena.getPlayersInArena());
        for (Player p : players) {
            if (!arena.getWorld().equals(p.getWorld())) {
                plugin.getLogger().info("Player '" + p.getName() + "' is not in the right world. Kicking...");
                p.kickPlayer("[MobArena] Cheater! (Warped out of the arena world.)");
                arena.getMessenger().tell(p, "You warped out of the arena world.");
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
                plugin.getLogger().warning(p.getName() + " tried to cheat in MobArena and has been kicked.");
            }
            return Double.MAX_VALUE;
        }
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
     * Stand back, I'm going to try science!
     */
    public static boolean doooooItHippieMonster(Location loc, int radius, String name, MobArena plugin)
    {
        // Grab the Configuration and ArenaMaster
        ArenaMaster am = plugin.getArenaMaster();

        // Create the arena node in the config-file.
        World world = loc.getWorld();
        Arena arena = am.createArenaNode(name, world);

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

        // Build some monster walls.
        for (int i = x1; i <= x2; i++)
        {
            for (int j = y1; j <= y2; j++)
            {
                world.getBlockAt(i,j,z1).setType(Material.SANDSTONE);
                world.getBlockAt(i,j,z2).setType(Material.SANDSTONE);
            }
        }
        for (int k = z1; k <= z2; k++)
        {
            for (int j = y1; j <= y2; j++)
            {
                world.getBlockAt(x1,j,k).setType(Material.SANDSTONE);
                world.getBlockAt(x2,j,k).setType(Material.SANDSTONE);
            }
        }

        // Add some hippie light.
        for (int i = x1; i <= x2; i++)
        {
            world.getBlockAt(i,y1+2,z1).setType(Material.GLOWSTONE);
            world.getBlockAt(i,y1+2,z2).setType(Material.GLOWSTONE);
        }
        for (int k = z1; k <= z2; k++)
        {
            world.getBlockAt(x1,y1+2,k).setType(Material.GLOWSTONE);
            world.getBlockAt(x2,y1+2,k).setType(Material.GLOWSTONE);
        }

        // Build a monster floor, and some Obsidian foundation.
        for (int i = x1; i <= x2; i++)
        {
            for (int k = z1; k <= z2; k++)
            {
                world.getBlockAt(i,y1,k).setType(Material.SANDSTONE);
                world.getBlockAt(i,y1-1,k).setType(Material.OBSIDIAN);
            }
        }

        // Make a hippie roof.
        for (int i = x1; i <= x2; i++)
        {
            for (int k = z1; k <= z2; k++)
                world.getBlockAt(i,y2,k).setType(Material.GLASS);
        }

        // Monster bulldoze
        for (int i = x1+1; i < x2; i++)
            for (int j = y1+1; j < y2; j++)
                for (int k = z1+1; k < z2; k++)
                    world.getBlockAt(i,j,k).setType(Material.AIR);

        // Build a hippie lobby
        for (int i = lx1; i <= lx2; i++) // Walls
        {
            for (int j = ly1; j <= ly2; j++)
            {
                world.getBlockAt(i,j,lz1).setType(Material.SANDSTONE);
                world.getBlockAt(i,j,lz2).setType(Material.SANDSTONE);
            }
        }
        for (int k = lz1; k <= lz2; k++) // Walls
        {
            for (int j = ly1; j <= ly2; j++)
            {
                world.getBlockAt(lx1,j,k).setType(Material.SANDSTONE);
                world.getBlockAt(lx2,j,k).setType(Material.SANDSTONE);
            }
        }
        for (int k = lz1; k <= lz2; k++) // Lights
        {
            world.getBlockAt(lx1,ly1+2,k).setType(Material.GLOWSTONE);
            world.getBlockAt(lx2,ly1+2,k).setType(Material.GLOWSTONE);
            world.getBlockAt(lx1,ly1+3,k).setType(Material.GLOWSTONE);
            world.getBlockAt(lx2,ly1+3,k).setType(Material.GLOWSTONE);
        }
        for (int i = lx1; i <= lx2; i++) // Floor
        {
            for (int k = lz1; k <= lz2; k++)
                world.getBlockAt(i,ly1,k).setType(Material.SANDSTONE);
        }
        for (int i = x1+1; i < lx2; i++) // Bulldoze
            for (int j = ly1+1; j <= ly2; j++)
                for (int k = lz1+1; k < lz2; k++)
                    world.getBlockAt(i,j,k).setType(Material.AIR);

        // Place the hippie signs
        //Iterator<String> iterator = am.getClasses().iterator();
        Iterator<ArenaClass> iterator = am.getClasses().values().iterator();
        Rotatable signData = (Rotatable) Material.SIGN.createBlockData();
        signData.setRotation(BlockFace.NORTH);
        for (int i = lx1+2; i <= lx2-2; i++) // Signs
        {
            world.getBlockAt(i,ly1+1,lz2-1).setBlockData(signData);
            Sign sign = (Sign) world.getBlockAt(i,ly1+1,lz2-1).getState();
            sign.setLine(0, iterator.next().getConfigName());
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
}
