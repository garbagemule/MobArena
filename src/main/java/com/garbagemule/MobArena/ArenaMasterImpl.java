package com.garbagemule.MobArena;

import static com.garbagemule.MobArena.util.config.ConfigUtils.makeSection;
import static com.garbagemule.MobArena.util.config.ConfigUtils.parseLocation;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.things.InvalidThingInputString;
import com.garbagemule.MobArena.things.Thing;
import com.garbagemule.MobArena.util.JoinInterruptTimer;
import com.garbagemule.MobArena.util.config.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ArenaMasterImpl implements ArenaMaster
{
    private MobArena plugin;

    private List<Arena> arenas;
    private Map<Player, Arena> arenaMap;

    private Map<String, ArenaClass> classes;

    private Set<String> allowedCommands;
    private SpawnsPets spawnsPets;

    private boolean enabled;

    private JoinInterruptTimer joinInterruptTimer;

    /**
     * Default constructor.
     */
    public ArenaMasterImpl(MobArena plugin) {
        this.plugin = plugin;

        this.arenas = new ArrayList<>();
        this.arenaMap = new HashMap<>();

        this.classes = new HashMap<>();

        this.allowedCommands = new HashSet<>();
        this.spawnsPets = new SpawnsPets();

        this.joinInterruptTimer = new JoinInterruptTimer();
    }

    /*
     * /////////////////////////////////////////////////////////////////////////
     * // // NEW METHODS IN REFACTORING //
     * /////////////////////////////////////////////////////////////////////////
     */

    public MobArena getPlugin() {
        return plugin;
    }

    @Override
    public Messenger getGlobalMessenger() {
        return plugin.getGlobalMessenger();
    }

    public boolean isEnabled() {
        return plugin.getLastFailureCause() == null && enabled;
    }

    public void setEnabled(boolean value) {
        enabled = value;
        FileConfiguration config = plugin.getConfig();
        if (config != null) {
            config.set("global-settings.enabled", enabled);
            plugin.saveConfig();
        }
    }

    public boolean notifyOnUpdates() {
        FileConfiguration config = plugin.getConfig();
        return config != null && config.getBoolean("global-settings.update-notification", false);
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

    public JoinInterruptTimer getJoinInterruptTimer() {
        return joinInterruptTimer;
    }

    /*
     * /////////////////////////////////////////////////////////////////////////
     * // // Arena getters //
     * /////////////////////////////////////////////////////////////////////////
     */

    public List<Arena> getEnabledArenas() {
        return getEnabledArenas(arenas);
    }

    public List<Arena> getEnabledArenas(List<Arena> arenas) {
        List<Arena> result = new ArrayList<>(arenas.size());
        for (Arena arena : arenas)
            if (arena.isEnabled()) 
                result.add(arena);
        return result;
    }

    public List<Arena> getPermittedArenas(Player p) {
        List<Arena> result = new ArrayList<>(arenas.size());
        for (Arena arena : arenas)
            if (arena.hasPermission(p))
                result.add(arena);
        return result;
    }

    public List<Arena> getEnabledAndPermittedArenas(Player p) {
        List<Arena> result = new ArrayList<>(arenas.size());
        for (Arena arena : arenas)
            if (arena.isEnabled() && arena.hasPermission(p))
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
        List<Arena> result = new ArrayList<>(arenas.size());
        for (Arena arena : arenas)
            if (arena.getWorld().equals(world))
                result.add(arena);
        return result;
    }

    public List<Player> getAllPlayers() {
        List<Player> result = new ArrayList<>(arenas.size());
        for (Arena arena : arenas)
            result.addAll(arena.getAllPlayers());
        return result;
    }

    public List<Player> getAllPlayersInArena(String arenaName) {
        Arena arena = getArenaWithName(arenaName);
        return (arena != null) ? new ArrayList<>(arena.getPlayersInArena()) : new ArrayList<>();
    }

    public List<Player> getAllLivingPlayers() {
        List<Player> result = new ArrayList<>();
        for (Arena arena : arenas)
            result.addAll(arena.getPlayersInArena());
        return result;
    }

    public List<Player> getLivingPlayersInArena(String arenaName) {
        Arena arena = getArenaWithName(arenaName);
        return (arena != null) ? new ArrayList<>(arena.getPlayersInArena()) : new ArrayList<>();
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
            if (arena.configName().equalsIgnoreCase(configName))
                return arena;
        return null;
    }

    /*
     * /////////////////////////////////////////////////////////////////////////
     * // // Initialization //
     * /////////////////////////////////////////////////////////////////////////
     */

    public void initialize() {
        loadSettings();
        loadClasses();
        loadArenas();
    }

    /**
     * Load the global settings.
     */
    public void loadSettings() {
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("global-settings");
        ConfigUtils.addIfEmpty(plugin, "global-settings.yml", section);

        enabled = section.getBoolean("enabled", true);

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

        loadPetItems(section);
    }

    private void loadPetItems(ConfigurationSection settings) {
        spawnsPets.clear();

        ConfigurationSection items = settings.getConfigurationSection("pet-items");
        if (items == null) {
            return;
        }

        for (String key : items.getKeys(false)) {
            EntityType entity;
            try {
                entity = EntityType.valueOf(key.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ConfigError("Failed to parse entity type for pet item: " + key);
            }

            if (!entity.isAlive()) {
                throw new ConfigError("Invalid entity type for pet item: " + key);
            }

            Material material;
            try {
                material = Material.getMaterial(items.getString(key, "").toUpperCase());
            } catch (Exception e) {
                throw new ConfigError("Failed to parse material type for pet item: " + key);
            }

            if (material == null) {
                throw new ConfigError("Failed to parse material type for pet item: " + key);
            }

            spawnsPets.register(material, entity);
        }
    }

    /**
     * Load all class-related stuff.
     */
    public void loadClasses() {
        ConfigurationSection section = makeSection(plugin.getConfig(), "classes");
        ConfigUtils.addIfEmpty(plugin, "classes.yml", section);

        // Establish the map.
        classes = new HashMap<>();
        Set<String> classNames = section.getKeys(false);

        // Load each individual class.
        for (String className : classNames) {
            loadClass(className);
        }

        // Add a class for "my items"
        loadClass("My Items");
    }

    /**
     * Helper method for loading a single class.
     */
    private ArenaClass loadClass(String classname) {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection("classes." + classname);
        String lowercase = classname.toLowerCase().replace(" ", "");

        // If the section doesn't exist, the class doesn't either.
        if (section == null) {
            // We may not have a class entry for My Items, but that's fine
            if (classname.equals("My Items")) {
                ArenaClass myItems = new ArenaClass.MyItems(null, false, false, this);
                classes.put(lowercase, myItems);
                return myItems;
            }
            plugin.getLogger().severe("Failed to load class '" + classname + "'.");
            return null;
        }

        // Check if weapons and armor for this class should be unbreakable
        boolean weps = section.getBoolean("unbreakable-weapons", true);
        boolean arms = section.getBoolean("unbreakable-armor", true);

        // Grab the class price, if any
        Thing price = null;
        String priceString = section.getString("price", null);
        if (priceString != null) {
            try {
                price = plugin.getThingManager().parse(priceString);
            } catch (InvalidThingInputString e) {
                throw new ConfigError("Failed to parse price of class " + classname + ": " + e.getInput());
            }
        }

        // Create an ArenaClass with the config-file name.
        ArenaClass arenaClass = classname.equals("My Items")
            ? new ArenaClass.MyItems(price, weps, arms, this)
            : new ArenaClass(classname, price, weps, arms);

        // Load items
        loadClassItems(section, arenaClass);

        // Load armor
        loadClassArmor(section, arenaClass);

        // Load potion effects
        loadClassPotionEffects(section, arenaClass);

        // Per-class permissions
        loadClassPermissions(arenaClass, section);
        loadClassLobbyPermissions(arenaClass, section);

        // Check for class chests
        try {
            Location cc = parseLocation(section, "classchest", null);
            arenaClass.setClassChest(cc);
        } catch (IllegalArgumentException e) {
            throw new ConfigError("Failed to parse classchest location for class " + classname + " because: " + e.getMessage());
        }

        // Load pet name
        String petName = section.getString("pet-name", "<display-name>'s pet");
        arenaClass.setPetName(petName);

        // Finally add the class to the classes map.
        classes.put(lowercase, arenaClass);
        return arenaClass;
    }

    private void loadClassItems(ConfigurationSection section, ArenaClass arenaClass) {
        List<String> items = section.getStringList("items");
        if (items == null || items.isEmpty()) {
            String value = section.getString("items", null);
            if (value == null || value.isEmpty()) {
                return;
            }
            items = Arrays.asList(value.split(","));
        }

        try {
            List<Thing> things = items.stream()
                .map(String::trim)
                .map(plugin.getThingManager()::parse)
                .collect(Collectors.toList());
            arenaClass.setItems(things);
        } catch (InvalidThingInputString e) {
            throw new ConfigError("Failed to parse item for class " + arenaClass.getConfigName() + ": " + e.getInput());
        }
    }

    private void loadClassArmor(ConfigurationSection section, ArenaClass arenaClass) {
        // Legacy armor node
        loadClassArmorLegacyNode(section, arenaClass);

        // Specific armor pieces
        String name = arenaClass.getConfigName();
        loadClassArmorPiece(section, "helmet",     name, arenaClass::setHelmet);
        loadClassArmorPiece(section, "chestplate", name, arenaClass::setChestplate);
        loadClassArmorPiece(section, "leggings",   name, arenaClass::setLeggings);
        loadClassArmorPiece(section, "boots",      name, arenaClass::setBoots);
        loadClassArmorPiece(section, "offhand",    name, arenaClass::setOffHand);
    }

    private void loadClassArmorLegacyNode(ConfigurationSection section, ArenaClass arenaClass) {
        List<String> armor = section.getStringList("armor");
        if (armor == null || armor.isEmpty()) {
            String value = section.getString("armor", null);
            if (value == null || value.isEmpty()) {
                return;
            }
            armor = Arrays.asList(value.split(","));
        }

        try {
            // Prepend "armor:" for the armor thing parser
            List<Thing> things = armor.stream()
                .map(String::trim)
                .map(s -> plugin.getThingManager().parse("armor", s))
                .collect(Collectors.toList());
            arenaClass.setArmor(things);
        } catch (InvalidThingInputString e) {
            throw new ConfigError("Failed to parse armor for class " + arenaClass.getConfigName() + ": " + e.getInput());
        }
    }

    private void loadClassArmorPiece(ConfigurationSection section, String slot, String name, Consumer<Thing> setter) {
        String value = section.getString(slot, null);
        if (value == null) {
            return;
        }
        try {
            // Prepend the slot name for the item parser
            Thing thing = plugin.getThingManager().parse(slot, value);
            setter.accept(thing);
        } catch (InvalidThingInputString e) {
            throw new ConfigError("Failed to parse " + slot + " slot for class " + name + ": " + e.getInput());
        }
    }

    private void loadClassPotionEffects(ConfigurationSection section, ArenaClass arenaClass) {
        List<String> effects = section.getStringList("effects");
        if (effects == null || effects.isEmpty()) {
            String value = section.getString("effects", null);
            if (value == null || value.isEmpty()) {
                return;
            }
            effects = Arrays.asList(value.split(","));
        }

        try {
            // Prepend "effect:" for the potion effect thing parser
            List<Thing> things = effects.stream()
                .map(String::trim)
                .map(s -> plugin.getThingManager().parse("effect", s))
                .collect(Collectors.toList());

            arenaClass.setEffects(things);
        } catch (InvalidThingInputString e) {
            throw new ConfigError("Failed to parse potion effect of class " + arenaClass.getConfigName() + ": " + e.getInput());
        }
    }

    private void loadClassPermissions(ArenaClass arenaClass, ConfigurationSection section) {
        try {
            section.getStringList("permissions").stream()
                .map(s -> plugin.getThingManager().parse("perm", s))
                .forEach(arenaClass::addPermission);
        } catch (InvalidThingInputString e) {
            throw new ConfigError("Failed to parse permission of class " + arenaClass.getConfigName() + ": " + e.getInput());
        }
    }

    private void loadClassLobbyPermissions(ArenaClass arenaClass, ConfigurationSection section) {
        try {
            section.getStringList("lobby-permissions").stream()
                .map(s -> plugin.getThingManager().parse("perm", s))
                .forEach(arenaClass::addLobbyPermission);
        } catch (InvalidThingInputString e) {
            throw new ConfigError("Failed to parse lobby-permission of class " + arenaClass.getConfigName() + ": " + e.getInput());
        }
    }

    /**
     * Load all arena-related stuff.
     */
    public void loadArenas() {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection section = makeSection(config, "arenas");
        Set<String> arenanames = section.getKeys(false);

        // If no arenas were found, create a default node.
        if (arenanames == null || arenanames.isEmpty()) {
            createArenaNode(section, "default", plugin.getServer().getWorlds().get(0), false);
        }

        arenas = new ArrayList<>();
        for (World w : Bukkit.getServer().getWorlds()) {
            loadArenasInWorld(w.getName());
        }
    }

    public void loadArenasInWorld(String worldName) {
        FileConfiguration config = plugin.getConfig();
        Set<String> arenaNames = config.getConfigurationSection("arenas").getKeys(false);
        if (arenaNames == null || arenaNames.isEmpty()) {
            return;
        }

        List<Arena> arenas = new ArrayList<>();
        for (String arenaName : arenaNames) {
            Arena arena = getArenaWithName(arenaName);
            if (arena != null) continue;

            String arenaWorld = config.getString("arenas." + arenaName + ".settings.world", "");
            if (!arenaWorld.equals(worldName)) continue;

            Arena loaded = loadArena(arenaName);
            if (loaded != null) {
                arenas.add(loaded);
            }
        }

        reportOverlappingRegions(arenas);
    }

    private void reportOverlappingRegions(List<Arena> arenas) {
        // If we iterate the upper/lower triangular matrix of the cartesian
        // product of the arena list, we avoid not only duplicate reports like
        // "a vs. b and b vs. a", but also "self comparisons" (j = i + 1).
        for (int i = 0; i < arenas.size(); i++) {
            Arena a = arenas.get(i);
            for (int j = i + 1; j < arenas.size(); j++) {
                Arena b = arenas.get(j);
                if (a.getRegion().intersects(b.getRegion())) {
                    plugin.getLogger().warning(String.format(
                        "Regions of arenas '%s' and '%s' overlap!",
                        a.configName(),
                        b.configName()
                    ));
                }
            }
        }
    }

    public void unloadArenasInWorld(String worldName) {
        FileConfiguration config = plugin.getConfig();
        Set<String> arenaNames = config.getConfigurationSection("arenas").getKeys(false);
        if (arenaNames == null || arenaNames.isEmpty()) {
            return;
        }
        for (String arenaName : arenaNames) {
            Arena arena = getArenaWithName(arenaName);
            if (arena == null) continue;

            String arenaWorld = arena.getWorld().getName();
            if (!arenaWorld.equals(worldName)) continue;

            arena.forceEnd();
            arenas.remove(arena);
        }
    }

    // Load an already existing arena node
    private Arena loadArena(String arenaname) {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection section  = makeSection(config, "arenas." + arenaname);
        ConfigurationSection settings = makeSection(section, "settings");
        String worldName = settings.getString("world", "");
        World world;

        if (!worldName.equals("")) {
            world = plugin.getServer().getWorld(worldName);
            if (world == null) {
                plugin.getLogger().warning("World '" + worldName + "' for arena '" + arenaname + "' was not found...");
                return null;
            }
        } else {
            world = plugin.getServer().getWorlds().get(0);
            plugin.getLogger().warning("Could not find the world for arena '" + arenaname + "'. Using default world ('" + world.getName() + "')! Check the config-file!");
        }

        ConfigUtils.addMissingRemoveObsolete(plugin, "settings.yml", settings);
        ConfigUtils.addIfEmpty(plugin, "waves.yml", makeSection(section, "waves"));

        Arena arena = new ArenaImpl(plugin, section, arenaname, world);
        arenas.add(arena);
        plugin.getLogger().info("Loaded arena '" + arenaname + "'");
        return arena;
    }

    @Override
    public boolean reloadArena(String name) {
        Arena arena = getArenaWithName(name);
        if (arena == null) return false;

        arena.forceEnd();
        arenas.remove(arena);

        plugin.reloadConfig();

        loadArena(name);
        return true;
    }

    // Create and load a new arena node
    @Override
    public Arena createArenaNode(String arenaName, World world) {
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection section = makeSection(config, "arenas");
        return createArenaNode(section, arenaName, world, true);
    }

    // Create a new arena node, and (optionally) load it
    private Arena createArenaNode(ConfigurationSection arenas, String arenaName, World world, boolean load) {
        if (arenas.contains(arenaName)) {
            throw new IllegalArgumentException("Arena already exists!");
        }
        ConfigurationSection section = makeSection(arenas, arenaName);

        // Add missing settings and remove obsolete ones
        ConfigUtils.addMissingRemoveObsolete(plugin, "settings.yml", makeSection(section, "settings"));
        section.set("settings.world", world.getName());
        ConfigUtils.addIfEmpty(plugin, "waves.yml",   makeSection(section, "waves"));
        ConfigUtils.addIfEmpty(plugin, "rewards.yml", makeSection(section, "rewards"));
        plugin.saveConfig();

        // Load the arena
        return (load ? loadArena(arenaName) : null);
    }

    public void removeArenaNode(Arena arena) {
        arenas.remove(arena);

        FileConfiguration config = plugin.getConfig();
        config.set("arenas." + arena.configName(), null);
        plugin.saveConfig();
    }

    public SpawnsPets getSpawnsPets() {
        return spawnsPets;
    }

    public void reloadConfig() {
        plugin.reload();
    }

    public void saveConfig() {
        plugin.saveConfig();
    }
}
