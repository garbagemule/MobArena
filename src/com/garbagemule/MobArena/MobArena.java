package com.garbagemule.MobArena;

import java.io.*;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.garbagemule.MobArena.commands.CommandHandler;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.listeners.MAGlobalListener;
import com.garbagemule.MobArena.listeners.MagicSpellsListener;
import com.garbagemule.MobArena.metrics.Metrics;
import com.garbagemule.MobArena.util.VersionChecker;
import com.garbagemule.MobArena.util.config.ConfigUtils;
import com.garbagemule.MobArena.util.inventory.InventoryManager;
import com.garbagemule.MobArena.waves.ability.AbilityManager;

/**
 * MobArena
 * @author garbagemule
 */
public class MobArena extends JavaPlugin
{
    private ArenaMaster arenaMaster;
    private CommandHandler commandHandler;
    
    // Inventories from disconnects
    private Set<String> inventoriesToRestore;
    
    // Vault
    private Economy economy;

    private File configFile;
    private FileConfiguration config;
    
    public static final double MIN_PLAYER_DISTANCE_SQUARED = 225D;
    public static final int ECONOMY_MONEY_ID = -29;
    public static Random random = new Random();

    public void onEnable() {
        // Initialize config-file
        configFile = new File(getDataFolder(), "config.yml");
        config = new YamlConfiguration();
        reloadConfig();

        // Set the header and save
        getConfig().options().header(getHeader());
        saveConfig();

        // Initialize announcements-file
        loadAnnouncementsFile();

        // Load boss abilities
        loadAbilities();

        // Set up soft dependencies
        setupVault();
        setupMagicSpells();

        // Set up the ArenaMaster
        arenaMaster = new ArenaMasterImpl(this);
        arenaMaster.initialize();

        // Register any inventories to restore.
        registerInventories();

        // Register event listeners
        registerListeners();

        // Go go Metrics
        startMetrics();

        // Announce enable!
        Messenger.info("v" + this.getDescription().getVersion() + " enabled.");

        // Check for updates
        if (getConfig().getBoolean("global-settings.update-notification", false)) {
            VersionChecker.checkForUpdates(this, null);
        }
    }
    
    public void onDisable() {
        // Force all arenas to end.
        if (arenaMaster == null) return;
        for (Arena arena : arenaMaster.getArenas()) {
            arena.forceEnd();
        }
        arenaMaster.resetArenaMap();
        VersionChecker.shutdown();
        
        Messenger.info("disabled.");
    }

    public File getPluginFile() {
        return getFile();
    }

    @Override
    public FileConfiguration getConfig() {
        return config;
    }

    @Override
    public void reloadConfig() {
        // Check if the config-file exists
        if (!configFile.exists()) {
            Messenger.info("No config-file found, creating default...");
            saveDefaultConfig();
        }

        // Check for tab characters in config-file
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(new File(getDataFolder(), "config.yml")));
            int row = 0;
            String line;
            while ((line = in.readLine()) != null) {
                row++;
                if (line.indexOf('\t') != -1) {
                    StringBuilder buffy = new StringBuilder();
                    buffy.append("Found tab in config-file on line ").append(row).append(".");
                    buffy.append('\n').append("NEVER use tabs! ALWAYS use spaces!");
                    buffy.append('\n').append(line);
                    buffy.append('\n');
                    for (int i = 0; i < line.indexOf('\t'); i++) {
                        buffy.append(' ');
                    }
                    buffy.append('^');
                    throw new IllegalArgumentException(buffy.toString());
                }
            }

            // Actually reload the config-file
            config.load(configFile);
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException("\n\n>>>\n>>> There is an error in your config-file! Handle it!\n>>> Here is what snakeyaml says:\n>>>\n\n" + e.getMessage());
        } catch (FileNotFoundException e) {
            throw new IllegalStateException("Config-file could not be created for some reason! <o>");
        } catch (IOException e) {
            // Error reading the file, just re-throw
            Messenger.severe("There was an error reading the config-file:\n" + e.getMessage());
        } finally {
            // Java 6 <3
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // Swallow
                }
            }
        }
    }

    @Override
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadAnnouncementsFile() {
        // Create if missing
        File file = new File(getDataFolder(), "announcements.yml");
        try {
            if (file.createNewFile()) {
                Messenger.info("announcements.yml created.");
                YamlConfiguration yaml = Msg.toYaml();
                yaml.save(file);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Otherwise, load the announcements from the file
        try {
            YamlConfiguration yaml = new YamlConfiguration();
            yaml.load(file);
            ConfigUtils.addMissingRemoveObsolete(file, Msg.toYaml(), yaml);
            Msg.load(yaml);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void registerListeners() {
        // Bind the /ma, /mobarena commands to MACommands.
        commandHandler = new CommandHandler(this);
        getCommand("ma").setExecutor(commandHandler);
        getCommand("mobarena").setExecutor(commandHandler);
        
        PluginManager pm = this.getServer().getPluginManager();
        pm.registerEvents(new MAGlobalListener(this, arenaMaster), this);
    }
    
    // Permissions stuff
    public boolean has(Player p, String s) {
        return p.isOp() || p.hasPermission(s);
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
            Messenger.info("Vault found; economy rewards enabled.");
        } else {
            Messenger.warning("Vault found, but no economy plugin detected. Economy rewards will not work!");
        }
    }
    
    private void setupMagicSpells() {
        Plugin spells = this.getServer().getPluginManager().getPlugin("MagicSpells");
        if (spells == null) return;

        Messenger.info("MagicSpells found, loading config-file.");
        this.getServer().getPluginManager().registerEvents(new MagicSpellsListener(this), this);
    }
    
    private void loadAbilities() {
        File dir = new File(this.getDataFolder(), "abilities");
        if (!dir.exists()) dir.mkdir();

        AbilityManager.loadCoreAbilities();
        AbilityManager.loadCustomAbilities(dir);
    }
    
    private void startMetrics() {
        try {
            Metrics m = new Metrics(this);
            m.start();
        } catch (Exception e) {
            Messenger.warning("y u disable stats :(");
        }
    }
    
    public ArenaMaster getArenaMaster() {
        return arenaMaster;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }
    
    private String getHeader() {
        String sep = System.getProperty("line.separator");
        return "MobArena v" + this.getDescription().getVersion() + " - Config-file" + sep + 
               "Read the Wiki for details on how to set up this file: http://goo.gl/F5TTc" + sep +
               "Note: You -must- use spaces instead of tabs!";
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

    public boolean giveMoney(Player p, ItemStack item) {
        if (economy != null) {
            EconomyResponse result = economy.depositPlayer(p.getName(), getAmount(item));
            return (result.type == ResponseType.SUCCESS);
        }
        return false;
    }

    public boolean takeMoney(Player p, ItemStack item) {
        return takeMoney(p, getAmount(item));
    }

    public boolean takeMoney(Player p, double amount) {
        if (economy != null) {
            EconomyResponse result = economy.withdrawPlayer(p.getName(), amount);
            return (result.type == ResponseType.SUCCESS);
        }
        return false;
    }

    public boolean hasEnough(Player p, ItemStack item) {
        return hasEnough(p, getAmount(item));
    }

    public boolean hasEnough(Player p, double amount) {
        return economy == null || (economy.getBalance(p.getName()) >= amount);
    }
    
    public String economyFormat(ItemStack item) {
        return economyFormat(getAmount(item));
    }

    public String economyFormat(double amount) {
        return economy == null ? null : economy.format(amount);
    }

    private double getAmount(ItemStack item) {
        double major = item.getAmount();
        double minor = item.getDurability() / 100D;
        return major + minor;
    }
}