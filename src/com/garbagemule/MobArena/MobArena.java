package com.garbagemule.MobArena;

import java.util.List;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

/**
 * MobArena
 *
 * @author garbagemule
 */
public class MobArena extends JavaPlugin
{
    /* Array of commands used to determine if a command belongs to MobArena
     * or Mean Admins. */
    public final String[] COMMANDS = {"join", "j", "leave", "l", "list", "who", "spectate", "spec",
                                      "ready", "notready", "enabled", "force", "config", "setwarp",
                                      "addspawn", "delspawn", "setregion", "expandregion", "protect",
                                      "undo", "dooooo", "reset"};
    public List<String> DISABLED_COMMANDS;
    
    public MobArena()
    {
    }

    public void onEnable()
    {
        PluginDescriptionFile pdfFile = this.getDescription();
        
        // Initialize convenience variables in ArenaManager.
        ArenaManager.init(this);
        DISABLED_COMMANDS = MAUtils.getDisabledCommands();
        
        // Bind the /ma and /marena commands to MACommands.
    	getCommand("ma").setExecutor(new MACommands());
    	getCommand("marena").setExecutor(new MACommands());
    	getCommand("mobarena").setExecutor(new MACommands());
    	
    	
        
        // Create event listeners.
        PluginManager pm = getServer().getPluginManager();
        PlayerListener commandListener  = new MADisabledCommands(this);
        PlayerListener lobbyListener    = new MALobbyListener(this);
        PlayerListener teleportListener = new MATeleportListener(this);
        PlayerListener discListener     = new MADisconnectListener(this);
        BlockListener  blockListener    = new MABlockListener(this);
        EntityListener deathListener    = new MADeathListener(this);
        EntityListener monsterListener  = new MAMonsterListener(this);
        // TO-DO: PlayerListener to check for kills/deaths.
        
        // Register events.
        pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, commandListener, Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT,     lobbyListener,    Priority.Normal,  this);
        pm.registerEvent(Event.Type.PLAYER_DROP_ITEM,    lobbyListener,    Priority.Normal,  this);
        pm.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, lobbyListener,    Priority.Normal,  this);
        pm.registerEvent(Event.Type.PLAYER_TELEPORT,     teleportListener, Priority.Normal,  this);
        pm.registerEvent(Event.Type.PLAYER_QUIT,         discListener,     Priority.Normal,  this);
        pm.registerEvent(Event.Type.PLAYER_KICK,         discListener,     Priority.Normal,  this);
        pm.registerEvent(Event.Type.PLAYER_JOIN,         discListener,     Priority.Normal,  this);
        pm.registerEvent(Event.Type.BLOCK_BREAK,         blockListener,    Priority.Normal,  this);
        pm.registerEvent(Event.Type.BLOCK_PLACE,         blockListener,    Priority.Normal,  this);
        pm.registerEvent(Event.Type.ENTITY_DEATH,        deathListener,    Priority.Lowest,  this);
        pm.registerEvent(Event.Type.ENTITY_EXPLODE,      monsterListener,  Priority.Normal,  this);
        pm.registerEvent(Event.Type.ENTITY_COMBUST,      monsterListener,  Priority.Normal,  this);
        pm.registerEvent(Event.Type.ENTITY_TARGET,       monsterListener,  Priority.Normal,  this);
        pm.registerEvent(Event.Type.CREATURE_SPAWN,      monsterListener,  Priority.Normal,  this);
        
        System.out.println(pdfFile.getName() + " v" + pdfFile.getVersion() + " enabled." );
    }
    
    public void onDisable()
    {
        System.out.println("WAIT! WHAT ARE YOU DOING?!");
        
        ArenaManager.forceEnd(null);
    }
}