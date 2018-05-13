package com.garbagemule.MobArena;

import com.garbagemule.MobArena.commands.CommandHandler;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.listeners.MAGlobalListener;
import com.garbagemule.MobArena.listeners.MagicSpellsListener;
import com.garbagemule.MobArena.things.ThingManager;
import com.garbagemule.MobArena.util.VersionChecker;
import com.garbagemule.MobArena.util.config.ConfigUtils;
import com.garbagemule.MobArena.waves.ability.AbilityManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

/**
 * MobArena
 * @author garbagemule
 */
public class MobArena extends JavaPlugin
{
    private ArenaMaster arenaMaster;
    private CommandHandler commandHandler;
    
    // Vault
    private Economy economy;

    private File configFile;
    private FileConfiguration config;
    
    public static final double MIN_PLAYER_DISTANCE_SQUARED = 225D;
    public static Random random = new Random();

    private Messenger messenger;
    private ThingManager thingman;

    @Override
    public void onLoad() {
        thingman = new ThingManager(this);
    }

    public void onEnable() {
        // Initialize config-file
        configFile = new File(getDataFolder(), "config.yml");
        config = new YamlConfiguration();
        reloadConfig();

        // Initialize global messenger
        String prefix = config.getString("global-settings.prefix", "");
        if (prefix.isEmpty()) {
            prefix = ChatColor.GREEN + "[MobArena] ";
        }
        messenger = new Messenger(prefix);

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

        // Register event listeners
        registerListeners();

        // Announce enable!
        getLogger().info("v" + this.getDescription().getVersion() + " enabled.");

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

        getLogger().info("disabled.");
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
            getLogger().info("No config-file found, creating default...");
            saveDefaultConfig();
        }

        // Check for tab characters in config-file
        try {
            Path path = getDataFolder().toPath().resolve("config.yml");
            List<String> lines = Files.readAllLines(path);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                int index = line.indexOf('\t');
                if (index != -1) {
                    String indent = new String(new char[index]).replace('\0', ' ');
                    throw new IllegalArgumentException(
                        "Found tab in config-file on line " + (i + 1) + "! NEVER use tabs! ALWAYS use spaces!\n\n" +
                        line + "\n" +
                        indent + "^"
                    );
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("There was an error reading the config-file:\n" + e.getMessage());
        }

        // Reload the config-file
        try {
            config.load(configFile);
        } catch (IOException e) {
            throw new RuntimeException("There was an error reading the config-file:\n" + e.getMessage());
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException("\n\n>>>\n>>> There is an error in your config-file! Handle it!\n>>> Here is what snakeyaml says:\n>>>\n\n" + e.getMessage());
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
                getLogger().info("announcements.yml created.");
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
    
    private void setupVault() {
        Plugin vaultPlugin = this.getServer().getPluginManager().getPlugin("Vault");
        if (vaultPlugin == null) {
            getLogger().info("Vault was not found. Economy rewards will not work.");
            return;
        }
        
        ServicesManager manager = this.getServer().getServicesManager();
        RegisteredServiceProvider<Economy> e = manager.getRegistration(net.milkbowl.vault.economy.Economy.class);
        
        if (e != null) {
            economy = e.getProvider();
            getLogger().info("Vault found; economy rewards enabled.");
        } else {
            getLogger().warning("Vault found, but no economy plugin detected. Economy rewards will not work!");
        }
    }
    
    private void setupMagicSpells() {
        Plugin spells = this.getServer().getPluginManager().getPlugin("MagicSpells");
        if (spells == null) return;

        getLogger().info("MagicSpells found, loading config-file.");
        this.getServer().getPluginManager().registerEvents(new MagicSpellsListener(this), this);
    }
    
    private void loadAbilities() {
        File dir = new File(this.getDataFolder(), "abilities");
        if (!dir.exists()) dir.mkdir();

        AbilityManager.loadCoreAbilities();
        AbilityManager.loadCustomAbilities(dir);
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
    
    public Economy getEconomy() {
        return economy;
    }

    public Messenger getGlobalMessenger() {
        return messenger;
    }

    public ThingManager getThingManager() {
        return thingman;
    }
}