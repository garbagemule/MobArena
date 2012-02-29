package com.garbagemule.MobArena;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import com.garbagemule.MobArena.ArenaImpl;
import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.ArenaClass.ArmorType;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.util.ItemParser;
import com.garbagemule.MobArena.util.TextUtils;
import com.garbagemule.MobArena.util.config.Config;
import com.garbagemule.MobArena.util.config.ConfigSection;
import com.garbagemule.MobArena.util.config.ConfigUtils;

public class ArenaMasterImpl implements ArenaMaster
{
    private MobArena plugin;
    private Config config;

    private List<Arena> arenas;
    private Map<Player, Arena> arenaMap;
    private Arena selectedArena;

    private Map<String, ArenaClass> classes;

    private Set<String> allowedCommands;

    /**
     * Default constructor.
     */
    public ArenaMasterImpl(MobArena plugin) {
        this.plugin = plugin;
        this.config = plugin.getMAConfig();

        this.arenas = new LinkedList<Arena>();
        this.arenaMap = new HashMap<Player, Arena>();

        this.classes = new HashMap<String, ArenaClass>();

        this.allowedCommands = new HashSet<String>();
    }

    /*
     * /////////////////////////////////////////////////////////////////////////
     * // // NEW METHODS IN REFACTORING //
     * /////////////////////////////////////////////////////////////////////////
     */

    public MobArena getPlugin() {
        return plugin;
    }

    public boolean isEnabled() {
        return config.getBoolean("global-settings.enabled", true);
    }

    public void setEnabled(boolean value) {
        config.set("global-settings.enabled", value);
    }

    public boolean notifyOnUpdates() {
        return config.getBoolean("global-settings.update-notification", false);
    }

    public Arena getSelectedArena() {
        return selectedArena;
    }

    public void setSelectedArena(Arena arena) {
        selectedArena = arena;
    }

    public List<Arena> getArenas() {
        return arenas;
    }

    public Map<String, ArenaClass> getClasses() {
        return classes;
    }

    public void addPlayer(Player p, Arena arena) {
        arenaMap.put(p, arena);
    }

    public Arena removePlayer(Player p) {
        return arenaMap.remove(p);
    }

    public void resetArenaMap() {
        arenaMap.clear();
    }

    public boolean isAllowed(String command) {
        return allowedCommands.contains(command);
    }

    /*
     * /////////////////////////////////////////////////////////////////////////
     * // // Arena getters //
     * /////////////////////////////////////////////////////////////////////////
     */

    public List<Arena> getEnabledArenas() {
        List<Arena> result = new LinkedList<Arena>();
        for (Arena arena : arenas)
            if (arena.isEnabled())
                result.add(arena);
        return result;
    }

    public List<Arena> getPermittedArenas(Player p) {
        List<Arena> result = new LinkedList<Arena>();
        for (Arena arena : arenas)
            if (plugin.has(p, "mobarena.arenas." + arena.configName()))
                result.add(arena);
        return result;
    }

    public List<Arena> getEnabledAndPermittedArenas(Player p) {
        List<Arena> result = new LinkedList<Arena>();
        for (Arena arena : arenas)
            if (arena.isEnabled() && plugin.has(p, "mobarena.arenas." + arena.configName()))
                result.add(arena);
        return result;
    }

    public Arena getArenaAtLocation(Location loc) {
        for (Arena arena : arenas)
            if (arena.getRegion().contains(loc))
                return arena;
        return null;
    }

    public List<Arena> getArenasInWorld(World world) {
        List<Arena> result = new LinkedList<Arena>();
        for (Arena arena : arenas)
            if (arena.getWorld().equals(world))
                result.add(arena);
        return result;
    }

    public List<Player> getAllPlayers() {
        List<Player> result = new LinkedList<Player>();
        for (Arena arena : arenas)
            result.addAll(arena.getAllPlayers());
        return result;
    }

    public List<Player> getAllPlayersInArena(String arenaName) {
        Arena arena = getArenaWithName(arenaName);
        return (arena != null) ? new LinkedList<Player>(arena.getPlayersInArena()) : new LinkedList<Player>();
    }

    public List<Player> getAllLivingPlayers() {
        List<Player> result = new LinkedList<Player>();
        for (Arena arena : arenas)
            result.addAll(arena.getPlayersInArena());
        return result;
    }

    public List<Player> getLivingPlayersInArena(String arenaName) {
        Arena arena = getArenaWithName(arenaName);
        return (arena != null) ? new LinkedList<Player>(arena.getPlayersInArena()) : new LinkedList<Player>();
    }

    public Arena getArenaWithPlayer(Player p) {
        return arenaMap.get(p);
    }

    public Arena getArenaWithPlayer(String playerName) {
        return arenaMap.get(plugin.getServer().getPlayer(playerName));
    }

    public Arena getArenaWithSpectator(Player p) {
        for (Arena arena : arenas) {
            if (arena.getSpectators().contains(p))
                return arena;
        }
        return null;
    }

    public Arena getArenaWithMonster(Entity e) {
        for (Arena arena : arenas)
            if (arena.getMonsterManager().getMonsters().contains(e))
                return arena;
        return null;
    }

    public Arena getArenaWithPet(Entity e) {
        for (Arena arena : arenas)
            if (arena.hasPet(e))
                return arena;
        return null;
    }

    public Arena getArenaWithName(String configName) {
        return getArenaWithName(this.arenas, configName);
    }

    public Arena getArenaWithName(Collection<Arena> arenas, String configName) {
        for (Arena arena : arenas)
            if (arena.configName().equals(configName))
                return arena;
        return null;
    }

    /*
     * /////////////////////////////////////////////////////////////////////////
     * // // Initialization //
     * /////////////////////////////////////////////////////////////////////////
     */

    public void initialize() {
        config.load();
        loadSettings();
        loadClasses();
        loadArenas();
        config.save();
    }

    /**
     * Load the global settings.
     */
    public void loadSettings() {
        ConfigUtils.replaceAllNodes(plugin, config, "global-settings", "global-settings.yml");
        ConfigSection section = config.getConfigSection("global-settings");

        // Grab the commands string
        String cmds = section.getString("allowed-commands", "");

        // Split by commas
        String[] parts = cmds.split(",");

        // Add in the /ma command.
        allowedCommands.add("/ma");

        // Add in each command
        for (String part : parts) {
            allowedCommands.add(part.trim().toLowerCase());
        }
    }

    /**
     * Load all class-related stuff.
     */
    public void loadClasses() {
        Set<String> classNames = config.getKeys("classes");

        // If no classes were found, load the defaults.
        if (classNames == null || classNames.isEmpty()) {
            loadDefaultClasses();
            classNames = config.getKeys("classes");
        }

        // Establish the map.
        classes = new HashMap<String, ArenaClass>();

        // Load each individual class.
        for (String className : classNames) {
            loadClass(className.toLowerCase());
        }
    }

    /**
     * Loads the classes in res/classes.yml into the config-file.
     */
    public void loadDefaultClasses() {
        ConfigUtils.addMissingNodes(plugin, config, "classes", "classes.yml");
    }

    /**
     * Helper method for loading a single class.
     */
    private ArenaClass loadClass(String className) {
        // Grab the class section.
        ConfigSection section = config.getConfigSection("classes." + TextUtils.camelCase(className));

        // If the section doesn't exist, the class doesn't either.
        if (section == null) {
            Messenger.severe("Failed to load class '" + TextUtils.camelCase(className) + "'.");
            return null;
        }

        ArenaClass arenaClass = new ArenaClass(className);

        // Parse the items-node
        String items = section.getString("items", "");
        if (!items.equals("")) {
            List<ItemStack> stacks = ItemParser.parseItems(items);
            arenaClass.setItems(stacks);
        }

        // And the legacy armor-node
        String armor = section.getString("armor", "");
        if (!armor.equals("")) {
            List<ItemStack> stacks = ItemParser.parseItems(armor);
            arenaClass.setArmor(stacks);
        }

        // Get armor strings
        String head = section.getString("helmet", null);
        String chest = section.getString("chestplate", null);
        String legs = section.getString("leggings", null);
        String feet = section.getString("boots", null);

        // Parse to ItemStacks
        ItemStack helmet = ItemParser.parseItem(head);
        ItemStack chestplate = ItemParser.parseItem(chest);
        ItemStack leggings = ItemParser.parseItem(legs);
        ItemStack boots = ItemParser.parseItem(feet);

        // Set in ArenaClass
        arenaClass.setHelmet(helmet);
        arenaClass.setChestplate(chestplate);
        arenaClass.setLeggings(leggings);
        arenaClass.setBoots(boots);

        // Per-class permissions
        loadClassPermissions(arenaClass, section);

        // Register the permission.
        registerPermission("mobarena.classes." + className, PermissionDefault.TRUE).addParent("mobarena.classes", true);

        // Finally add the class to the classes map.
        classes.put(className, arenaClass);
        return arenaClass;
    }

    private void loadClassPermissions(ArenaClass arenaClass, ConfigSection section) {
        List<String> perms = section.getStringList("permissions", null);
        if (perms.isEmpty())
            return;

        for (String perm : perms) {
            // If the permission starts with - or ^, it must be revoked.
            boolean value = true;
            if (perm.startsWith("-") || perm.startsWith("^")) {
                perm = perm.substring(1).trim();
                value = false;
            }
            arenaClass.addPermission(perm, value);
        }
    }

    public ArenaClass createClassNode(String className, PlayerInventory inv, boolean safe) {
        String path = "classes." + TextUtils.camelCase(className);
        if (safe && config.getConfigSection(path) != null) {
            return null;
        }

        // Create the node.
        config.set(path, "");

        // Grab the section.
        ConfigSection section = config.getConfigSection(path);

        // Take the current items and armor.
        section.set("items", ItemParser.parseString(inv.getContents()));
        section.set("armor", ItemParser.parseString(inv.getArmorContents()));

        // If the helmet isn't a real helmet, set it explicitly.
        ItemStack helmet = inv.getHelmet();
        if (helmet.getTypeId() != 0 && ArmorType.getType(helmet) != ArmorType.HELMET) {
            section.set("helmet", ItemParser.parseString(helmet));
        }

        // Save changes.
        config.save();

        // Load the class
        return loadClass(className.toLowerCase());
    }

    public void removeClassNode(String className) {
        if (!classes.containsKey(className.toLowerCase()))
            throw new IllegalArgumentException("Class does not exist!");

        // Remove the class from the config-file and save it.
        config.remove("classes." + className);
        config.save();

        // Remove the class from the map.
        classes.remove(className.toLowerCase());

        unregisterPermission("mobarena.arenas." + className.toLowerCase());
    }

    public boolean addClassPermission(String className, String perm) {
        return addRemoveClassPermission(className, perm, true);
    }

    public boolean removeClassPermission(String className, String perm) {
        return addRemoveClassPermission(className, perm, false);
    }

    private boolean addRemoveClassPermission(String className, String perm, boolean add) {
        String path = "classes." + TextUtils.camelCase(className);
        if (config.getConfigSection(path) == null)
            return false;

        // Grab the class section
        ConfigSection section = config.getConfigSection(path);

        // Get any previous nodes
        List<String> nodes = section.getStringList("permissions", null);
        if (nodes.contains(perm))
            return false;

        // Add or remove.
        if (add) {
            removeContradictions(nodes, perm);
            nodes.add(perm);
        }
        else {
            nodes.remove(perm);
        }

        // Replace the set.
        section.set("permissions", nodes);
        config.save();

        // Reload the class.
        loadClass(className);
        return true;
    }

    /**
     * Removes any nodes that would contradict the permission, e.g. if the node
     * 'mobarena.use' is in the set, and the perm node is '-mobarena.use', the
     * '-mobarena.use' node is removed as to not contradict the new
     * 'mobarena.use' node.
     */
    private void removeContradictions(List<String> nodes, String perm) {
        if (perm.startsWith("^") || perm.startsWith("-")) {
            nodes.remove(perm.substring(1).trim());
        }
        else {
            nodes.remove("^" + perm);
            nodes.remove("-" + perm);
        }
    }

    /**
     * Load all arena-related stuff.
     */
    public void loadArenas() {
        Set<String> arenaNames = config.getKeys("arenas");

        // If no arenas were found, create a default node.
        if (arenaNames == null || arenaNames.isEmpty()) {
            createArenaNode("default", plugin.getServer().getWorlds().get(0));
            arenaNames = config.getKeys("arenas");
        }

        // Establish the list.
        arenas = new LinkedList<Arena>();

        for (String arenaName : arenaNames) {
            loadArena(arenaName);
        }

        selectedArena = arenas.get(0);
    }

    private Arena loadArena(String arenaName) {
        String path = "arenas." + arenaName;
        String worldName = config.getString(path + ".settings.world", "");
        World world = null;

        // If a string was found, try to fetch the world from the server.
        if (!worldName.equals("")) {
            world = plugin.getServer().getWorld(worldName);

            if (world == null) {
                Messenger.severe("The world '" + worldName + "' for arena '" + arenaName + "' does not exist!");
                return null;
            }
        }
        // Otherwise, use the default world.
        else {
            world = plugin.getServer().getWorlds().get(0);
            Messenger.warning("Could not find the world for arena '" + arenaName + "'. Using default world ('" + world.getName() + "')! Check the config-file!");
        }

        // Assert all settings nodes.
        ConfigUtils.replaceAllNodes(plugin, config, path + ".settings", "settings.yml");

        // Create an Arena with the name and world.
        Arena arena = new ArenaImpl(plugin, config, arenaName, world);

        // Register the permission
        registerPermission("mobarena.arenas." + arenaName, PermissionDefault.TRUE);

        // Finally, add it to the arena list.
        arenas.add(arena);
        return arena;
    }

    public Arena createArenaNode(String arenaName, World world) {
        String path = "arenas." + arenaName;
        if (config.getConfigSection(path) != null)
            throw new IllegalArgumentException("Arena already exists!");

        // Extract the default settings and update the world-node.
        ConfigUtils.replaceAllNodes(plugin, config, path + ".settings", "settings.yml");
        config.set(path + ".settings.world", world.getName());

        // Extract the default waves.
        ConfigUtils.replaceAllNodes(plugin, config, path + ".waves", "waves.yml");

        // Extract the default rewards.
        ConfigUtils.replaceAllNodes(plugin, config, path + ".rewards", "rewards.yml");

        // Save the changes.
        config.save();

        // Load the arena
        return loadArena(arenaName);
    }

    public void removeArenaNode(Arena arena) {
        // Remove the arena from the config-file and save it.
        config.remove("arenas." + arena.configName());
        config.save();

        // Remove the arena from the list.
        arenas.remove(arena);

        unregisterPermission("mobarena.arenas." + arena.configName());
    }

    public void reloadConfig() {
        boolean wasEnabled = isEnabled();

        // If MobArena was enabled, disable it before updating.
        if (wasEnabled) {
            setEnabled(false);
        }

        for (Arena a : arenas) {
            a.forceEnd();
        }

        config.load();
        loadSettings();
        loadClasses();
        loadArenas();

        // If MobArena was enabled, re-enable it after updating.
        if (wasEnabled) {
            setEnabled(true);
        }
    }

    public void saveConfig() {
        config.save();
    }

    private Permission registerPermission(String permString, PermissionDefault value) {
        PluginManager pm = plugin.getServer().getPluginManager();

        Permission perm = pm.getPermission(permString);
        if (perm == null) {
            perm = new Permission(permString);
            perm.setDefault(value);
            pm.addPermission(perm);
        }
        return perm;
    }

    private void unregisterPermission(String s) {
        plugin.getServer().getPluginManager().removePermission(s);
    }
}
