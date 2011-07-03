package com.garbagemule.MobArena;

import java.io.File;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.util.config.Configuration;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * MobArena
 * @author garbagemule
 */
public class MobArena extends JavaPlugin
{
    private Configuration config;
    private ArenaMaster am;
    
    // Permissions stuff
    protected static PermissionHandler permissionHandler;
    
    public MobArena()
    {
    }

    public void onEnable()
    {
        PluginDescriptionFile pdfFile = this.getDescription();
        
        // Config, messages and ArenaMaster initialization
        loadConfig();
        MAMessages.init(this);
        am = new ArenaMaster(this);
        am.initialize();
        
        // Permissions
        setupPermissions();
        
        // Bind the /ma, /marena, and /mobarena commands to MACommands.
        MACommands commandExecutor = new MACommands(this, am);
    	getCommand("ma").setExecutor(commandExecutor);
    	getCommand("marena").setExecutor(commandExecutor);
    	getCommand("mobarena").setExecutor(commandExecutor);
    	
        // Create event listeners.
        PluginManager pm = getServer().getPluginManager();
        PlayerListener playerListener = new MAPlayerListener(this, am);
        EntityListener entityListener = new MAEntityListener(am);
        BlockListener  blockListener  = new MABlockListener(am);
        
        // Register events.
        pm.registerEvent(Event.Type.PLAYER_INTERACT,           playerListener,   Priority.Normal,  this);
        pm.registerEvent(Event.Type.PLAYER_DROP_ITEM,          playerListener,   Priority.Normal,  this);
        pm.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY,       playerListener,   Priority.Normal,  this);
        pm.registerEvent(Event.Type.PLAYER_TELEPORT,           playerListener,   Priority.Normal,  this);
        pm.registerEvent(Event.Type.PLAYER_QUIT,               playerListener,   Priority.Normal,  this);
        pm.registerEvent(Event.Type.PLAYER_KICK,               playerListener,   Priority.Normal,  this);
        pm.registerEvent(Event.Type.PLAYER_JOIN,               playerListener,   Priority.Normal,  this);
        pm.registerEvent(Event.Type.BLOCK_BREAK,               blockListener,    Priority.Highest, this);
        pm.registerEvent(Event.Type.BLOCK_PLACE,               blockListener,    Priority.Highest, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGE,             entityListener,   Priority.Highest, this);
        pm.registerEvent(Event.Type.ENTITY_DEATH,              entityListener,   Priority.Lowest,  this); // Lowest because of Tombstone
        pm.registerEvent(Event.Type.ENTITY_REGAIN_HEALTH,      entityListener,   Priority.Normal,  this);
        pm.registerEvent(Event.Type.ENTITY_EXPLODE,            entityListener,   Priority.Highest, this);
        pm.registerEvent(Event.Type.ENTITY_COMBUST,            entityListener,   Priority.Normal,  this);
        pm.registerEvent(Event.Type.ENTITY_TARGET,             entityListener,   Priority.Normal,  this);
        pm.registerEvent(Event.Type.CREATURE_SPAWN,            entityListener,   Priority.Highest, this);
        pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener,   Priority.Monitor, this);
        
        System.out.println("[MobArena] v" + pdfFile.getVersion() + " enabled." );
    }
    
    public void onDisable()
    {        
        for (Arena arena : am.arenas)
            arena.forceEnd();
        am.arenaMap.clear();
        
        System.out.println("[MobArena] disabled.");
    }
    
    /**
     * Load the config-file and initialize the Configuration object.
     */
    private void loadConfig()
    {        
        File file = new File(this.getDataFolder(), "config.yml");
        if (!file.exists())
        {
            try
            {
                this.getDataFolder().mkdir();
                file.createNewFile();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return;
            }
        }
        // TODO: Remove in v1.0
        else
        {
            Configuration tmp = new Configuration(file);
            tmp.load();
            if (tmp.getKeys("global-settings") == null)
            {
                file.renameTo(new File(this.getDataFolder(), "config_OLD.yml"));
                file = new File(this.getDataFolder(), "config.yml");
                try
                {
                    this.getDataFolder().mkdir();
                    file.createNewFile();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    return;
                }
                
                config = new Configuration(file);
                config.load();
                fixConfig();
                config.setHeader("# MobArena Configuration-file\r\n# Please go to https://github.com/garbagemule/MobArena/wiki/Installing-MobArena for more details.");
                config.save();
            }
        }
        
        config = new Configuration(file);
        config.load();
        config.setHeader("# MobArena Configuration-file\r\n# Please go to https://github.com/garbagemule/MobArena/wiki/Installing-MobArena for more details.");
        config.save();
    }
    
    // Permissions stuff
    public static boolean has(Player p, String s)
    {
        //return (permissionHandler != null && permissionHandler.has(p, s));
        return (permissionHandler == null || permissionHandler.has(p, s));
    }
    
    public static boolean hasDefTrue(Player p, String s)
    {
        return (permissionHandler == null || permissionHandler.has(p, s));
    }
    
    private void setupPermissions()
    {
        if (permissionHandler != null)
            return;
    
        Plugin permissionsPlugin = this.getServer().getPluginManager().getPlugin("Permissions");
        if (permissionsPlugin == null) return;
        
        permissionHandler = ((Permissions) permissionsPlugin).getHandler();
    }
    
    public Configuration getConfig()      { return config; }
    public ArenaMaster   getAM()          { return am; } // More convenient.
    public ArenaMaster   getArenaMaster() { return am; }
    
    // TODO: Remove in v1.0
    private void fixConfig()
    {
        // If global-settings is sorted, don't do anything.
        if (config.getKeys("global-settings") != null)
            return;
        
        File oldFile = new File(this.getDataFolder(), "config_OLD.yml");
        if (!oldFile.exists())
            return;
        
        System.out.println("[MobArena] Config-file appears to be old. Trying to fix it...");
        
        Configuration oldConfig = new Configuration(oldFile);
        oldConfig.load();

        config.setProperty("global-settings.enabled", true);
        config.save();
        config.load();
        config.setProperty("global-settings.update-notification", true);
        config.save();
        config.load();
        config.setProperty("global-settings.repair-delay", 5);
        config.save();
        config.load();
        
        // Copy classes
        for (String s : oldConfig.getKeys("classes"))
        {
            config.setProperty("classes." + s + ".items", oldConfig.getString("classes." + s + ".items"));
            config.setProperty("classes." + s + ".armor", oldConfig.getString("classes." + s + ".armor"));
        }
        config.save();
        
        // Make the default arena node.
        config.setProperty("arenas.default.settings.enabled", true);
        config.save();
        config.load();
        config.setProperty("arenas.default.settings.world", oldConfig.getString("settings.world"));
        config.save();
        config.load();
        
        // Copy the waves and rewards to the new default arena.
        for (String s : oldConfig.getKeys("waves.default"))
            config.setProperty("arenas.default.waves.default." + s, oldConfig.getString("waves.default." + s));
        config.save();
        for (String s : oldConfig.getKeys("rewards.waves.every"))
            config.setProperty("arenas.default.rewards.waves.every." + s, oldConfig.getString("rewards.waves.every." + s));
        for (String s : oldConfig.getKeys("rewards.waves.after"))
            config.setProperty("arenas.default.rewards.waves.after." + s, oldConfig.getString("rewards.waves.after." + s));
        config.save();
        config.load();
        
        // Copy the coords.
        for (String s : oldConfig.getKeys("coords"))
        {
            if (s.equals("spawnpoints"))
                continue;
            
            StringBuffer buffy = new StringBuffer();
            buffy.append(oldConfig.getString("coords." + s + ".x"));
            buffy.append(",");
            buffy.append(oldConfig.getString("coords." + s + ".y"));
            buffy.append(",");
            buffy.append(oldConfig.getString("coords." + s + ".z"));
            buffy.append(",");
            buffy.append(oldConfig.getString("coords." + s + ".yaw"));
            buffy.append(",");
            buffy.append(oldConfig.getString("coords." + s + ".pitch"));
            
            config.setProperty("arenas.default.coords." + s, buffy.toString());
        }
        config.save();
        config.load();
        
        for (String s : oldConfig.getKeys("coords.spawnpoints"))
        {
            StringBuffer buffy = new StringBuffer();
            buffy.append(oldConfig.getString("coords.spawnpoints." + s + ".x"));
            buffy.append(",");
            buffy.append(oldConfig.getString("coords.spawnpoints." + s + ".y"));
            buffy.append(",");
            buffy.append(oldConfig.getString("coords.spawnpoints." + s + ".z"));
            buffy.append(",");
            buffy.append(oldConfig.getString("coords.spawnpoints." + s + ".yaw"));
            buffy.append(",");
            buffy.append(oldConfig.getString("coords.spawnpoints." + s + ".pitch"));
            
            config.setProperty("arenas.default.coords.spawnpoints." + s, buffy.toString());
        }
        config.save();
        config.load();
        
        System.out.println("[MobArena] Updated the config-file!");
    }
}