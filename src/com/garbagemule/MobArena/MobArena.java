package com.garbagemule.MobArena;

import java.io.File;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.garbagemule.MobArena.ArenaMasterImpl;
import com.garbagemule.MobArena.MAMessages;
import com.garbagemule.MobArena.commands.CommandHandler;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.health.HealthStrategy;
import com.garbagemule.MobArena.health.HealthStrategyHeroes;
import com.garbagemule.MobArena.health.HealthStrategyStandard;
import com.garbagemule.MobArena.listeners.MAGlobalListener;
import com.garbagemule.MobArena.listeners.MagicSpellsListener;
import com.garbagemule.MobArena.listeners.SpoutScreenListener;
import com.garbagemule.MobArena.metrics.Metrics;
import com.garbagemule.MobArena.util.FileUtils;
import com.garbagemule.MobArena.util.config.Config;
import com.garbagemule.MobArena.util.config.ConfigUtils;
import com.garbagemule.MobArena.util.inventory.InventoryManager;
import com.garbagemule.MobArena.waves.ability.AbilityManager;

/**
 * MobArena
 * @author garbagemule
 */
public class MobArena extends JavaPlugin
{
    private Config config;
    private ArenaMaster arenaMaster;
    
    // Inventories from disconnects
    private Set<String> inventoriesToRestore;
    
    // Heroes
    private boolean hasHeroes;
    private HealthStrategy healthStrategy;
    
    // Vault
    private Economy economy;
    
    // Spout stuff
    public static boolean hasSpout;
    
    public static final double MIN_PLAYER_DISTANCE_SQUARED = 225D;
    public static final int ECONOMY_MONEY_ID = -29;
    public static Random random = new Random();

    public void onEnable() {
        // Create default files and initialize config-file
        FileUtils.extractResource(this.getDataFolder(), "config.yml");
        loadConfigFile();
        
        // Load boss abilities
        loadAbilities();
        
        // Set up soft dependencies
        setupVault();
        setupHeroes();
        setupSpout();
        setupMagicSpells();
        setupStrategies();
        
        // Set up the ArenaMaster
        arenaMaster = new ArenaMasterImpl(this);
        arenaMaster.initialize();
        
        // Register any inventories to restore.
        registerInventories();
        
        // Make sure all the announcements are configured.
        MAMessages.init(this);
        
        // Register event listeners
        registerListeners();
        
        // Go go Metrics
        startMetrics();
        
        // Announce enable!
        Messenger.info("v" + this.getDescription().getVersion() + " enabled.");
    }
    
    public void onDisable() {
        // Disable Spout features.
        hasSpout = false;
        
        // Force all arenas to end.
        if (arenaMaster == null) return;
        for (Arena arena : arenaMaster.getArenas()) {
            arena.forceEnd();
        }
        arenaMaster.resetArenaMap();
        
        Messenger.info("disabled.");
    }
    
    private void loadConfigFile() {
        File dir  = this.getDataFolder();
        if (!dir.exists()) dir.mkdir();
        
        File file = new File(this.getDataFolder(), "config.yml");
        config = new Config(file);
        if (!config.load()) {
            this.getServer().getPluginManager().disablePlugin(this);
            throw new IllegalStateException("The config-file could not be loaded! Read further up to find the actual bug!");
        }
        
        updateSettings(config);
        config.setHeader(getHeader());
        config.save();
    }
    
    private void registerListeners() {
        // Bind the /ma, /mobarena commands to MACommands.
        CommandHandler handler = new CommandHandler(this);
        getCommand("ma").setExecutor(handler);
        getCommand("mobarena").setExecutor(handler);
        
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new MAGlobalListener(this, arenaMaster), this);
        
        if (hasSpout) {
            pm.registerEvents(new SpoutScreenListener(this), this);
        }
    }
    
    // Permissions stuff
    public boolean has(Player p, String s) {
        return p.hasPermission(s);
    }
    
    public boolean has(CommandSender sender, String s) {
        if (sender instanceof ConsoleCommandSender) {
            return true;
        }
        return has((Player) sender, s);
    }
    
    private void setupVault() {
        Plugin vaultPlugin = this.getServer().getPluginManager().getPlugin("Vault");
        if (vaultPlugin == null) {
            Messenger.warning("Vault was not found. Economy rewards will not work!");
            return;
        }
        
        ServicesManager manager = this.getServer().getServicesManager();
        RegisteredServiceProvider<Economy> e = manager.getRegistration(net.milkbowl.vault.economy.Economy.class);
        
        if (e != null) {
            economy = e.getProvider();
        } else {
            Messenger.warning("Vault found, but no economy plugin detected. Economy rewards will not work!");
        }
    }
    
    private void setupHeroes() {
        Plugin heroesPlugin = this.getServer().getPluginManager().getPlugin("Heroes");
        if (heroesPlugin == null) return;
        
        hasHeroes = true;
    }
    
    private void setupSpout() {
        Plugin spoutPlugin = this.getServer().getPluginManager().getPlugin("Spout");
        if (spoutPlugin == null) return;
        
        hasSpout = true;
    }
    
    private void setupMagicSpells() {
        Plugin spells = this.getServer().getPluginManager().getPlugin("MagicSpells");
        if (spells == null) return;
        
        this.getServer().getPluginManager().registerEvents(new MagicSpellsListener(this), this);
    }
    
    private void setupStrategies() {
        healthStrategy = (hasHeroes ? new HealthStrategyHeroes() : new HealthStrategyStandard());
    }
    
    private void loadAbilities() {
        File dir = new File(this.getDataFolder(), "abilities");
        if (!dir.exists()) dir.mkdir();
        
        AbilityManager.loadAbilities(dir);
    }
    
    private void startMetrics() {
        try {
            Metrics m = new Metrics(this);
            m.start();
        } catch (Exception e) {
            Messenger.warning("y u disable stats :(");
        }
    }
    
    public HealthStrategy getHealthStrategy() {
        return healthStrategy;
    }
    
    public Config getMAConfig() {
        return config;
    }
    
    public ArenaMaster getArenaMaster() {
        return arenaMaster;
    }
    
    private void updateSettings(Config config) {
        Set<String> arenas = config.getKeys("arenas");
        if (arenas == null) return;
        
        for (String arena : arenas) {
            String path = "arenas." + arena + ".settings";
            ConfigUtils.replaceAllNodes(this, config, path, "settings.yml");
        }
    }
    
    private String getHeader() {
        String sep = System.getProperty("line.separator");
        return "MobArena v" + this.getDescription().getVersion() + " - Config-file" + sep + 
               "Read the Wiki for details on how to set up this file: http://goo.gl/F5TTc" + sep +
               "Note: You -must- use spaces instead of tabs!\r";
    }
    
    private void registerInventories() {
        this.inventoriesToRestore = new HashSet<String>();
        
        File dir = new File(getDataFolder(), "inventories");
        if (!dir.exists()) {
            dir.mkdir();
            return;
        }
        
        for (File f : dir.listFiles()) {
            if (f.getName().endsWith(".inv")) {
                inventoriesToRestore.add(f.getName().substring(0, f.getName().indexOf(".")));
            }
        }
    }

    public void restoreInventory(Player p) {
        if (!inventoriesToRestore.contains(p.getName())) {
            return;
        }
        
        if (InventoryManager.restoreFromFile(this, p)) {
            inventoriesToRestore.remove(p.getName());
        }
    }

    public boolean giveMoney(Player p, int amount) {
        if (economy != null) {
            EconomyResponse result = economy.depositPlayer(p.getName(), amount);
            return (result.type == ResponseType.SUCCESS);
        }
        return false;
    }
    
    public boolean takeMoney(Player p, int amount) {
        if (economy != null) {
            EconomyResponse result = economy.withdrawPlayer(p.getName(), amount);
            return (result.type == ResponseType.SUCCESS);
        }
        return false;
    }

    public boolean hasEnough(Player p, double amount) {
        if (economy != null) {
            return (economy.getBalance(p.getName()) >= amount);
        }
        return true;
    }
    
    public String economyFormat(double amount) {
        if (economy != null) {
            return economy.format(amount);
        }
        return null;
    }
}