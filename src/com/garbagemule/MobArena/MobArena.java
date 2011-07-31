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
import com.garbagemule.MobArena.util.FileUtils;
import com.garbagemule.register.payment.Method;
import com.garbagemule.register.payment.Methods;

/**
 * MobArena
 * @author garbagemule
 */
public class MobArena extends JavaPlugin
{
    private Configuration config;
    private ArenaMaster am;
    
    // Permissions stuff
    private PermissionHandler permissionHandler;
    
    // Economy stuff
    protected Methods Methods;
    protected Method  Method;
    
    // Global variables
    public static PluginDescriptionFile desc;
    public static File dir, arenaDir;
    public static final double MIN_PLAYER_DISTANCE = 256.0;
    public static final int ECONOMY_MONEY_ID = -29;

    public void onEnable()
    {        
        // Description file and data folder
        desc     = getDescription();
        dir      = getDataFolder();
        arenaDir = new File(dir, "arenas"); 
        if (!dir.exists()) dir.mkdir();
        //if (!arenaDir.exists()) arenaDir.mkdir();
        
        // Create default files and initialize config-file
        FileUtils.extractDefaults("config.yml", "announcements.properties");
        loadConfig();
        
        // Download external libraries if needed.
        FileUtils.fetchLibs(config);
        
        // Set up permissions and economy
        setupPermissions();
        setupRegister();
        
        // Set up the ArenaMaster and the announcements
        am = new ArenaMaster(this);
        am.initialize();
        MAMessages.init(this);
        
        // Register event listeners
        registerListeners();
        
        // Announce enable!
        System.out.println("[MobArena] v" + desc.getVersion() + " enabled.");
    }
    
    public void onDisable()
    {
        // TODO: Re-implement this!
        /*
        // Force all arenas to end.
        for (Arena arena : am.arenas)
            arena.forceEnd();
        am.arenaMap.clear();
        
        // Permissions & Economy
        permissionHandler = null;
        if (Methods != null && Methods.hasMethod())
        {
            Methods = null;
            System.out.println("[MobArena] Payment method was disabled. No longer accepting payments.");
        }
        */
        System.out.println("[MobArena] disabled.");
    }
    
    private void loadConfig()
    {
        File file = new File(dir, "config.yml");
        if (!file.exists())
        {
            System.out.println("[MobArena] ERROR! Config-file could not be created!");
            return;
        }
        
        config = new Configuration(file);
        config.load();
    }
    
    private void registerListeners()
    {
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
        pm.registerEvent(Event.Type.PLAYER_ANIMATION,          playerListener,   Priority.Normal,  this);
        pm.registerEvent(Event.Type.BLOCK_BREAK,               blockListener,    Priority.Highest, this);
        pm.registerEvent(Event.Type.BLOCK_PLACE,               blockListener,    Priority.Highest, this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGE,             entityListener,   Priority.High,    this); // mcMMO is "Highest"
        pm.registerEvent(Event.Type.ENTITY_DEATH,              entityListener,   Priority.Lowest,  this); // Lowest because of Tombstone
        pm.registerEvent(Event.Type.ENTITY_REGAIN_HEALTH,      entityListener,   Priority.Normal,  this);
        pm.registerEvent(Event.Type.ENTITY_EXPLODE,            entityListener,   Priority.Highest, this);
        pm.registerEvent(Event.Type.ENTITY_COMBUST,            entityListener,   Priority.Normal,  this);
        pm.registerEvent(Event.Type.ENTITY_TARGET,             entityListener,   Priority.Normal,  this);
        pm.registerEvent(Event.Type.CREATURE_SPAWN,            entityListener,   Priority.Highest, this);
        pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener,   Priority.Monitor, this);
    }
    
    // Permissions stuff
    public boolean has(Player p, String s)
    {
        //return (permissionHandler != null && permissionHandler.has(p, s));
        return (permissionHandler == null || permissionHandler.has(p, s));
    }
    
    public boolean hasDefTrue(Player p, String s)
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
    
    private void setupRegister()
    {
        Methods = new Methods();
        if (!Methods.hasMethod() && Methods.setMethod(this))
        {
            Method = Methods.getMethod();
            System.out.println("[MobArena] Payment method found (" + Method.getName() + " version: " + Method.getVersion() + ")");
        }
    }
    
    public Configuration getConfig()      { return config; }
    public ArenaMaster   getAM()          { return am; } // More convenient.
    public ArenaMaster   getArenaMaster() { return am; }
}