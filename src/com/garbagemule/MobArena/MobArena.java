package com.garbagemule.MobArena;

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
    public MobArena()
    {
    }

    public void onEnable()
    {
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println(pdfFile.getName() + " v" + pdfFile.getVersion() + " enabled." );
        
        // Initialize convenience variables in ArenaManager.
        ArenaManager.init(this);
        
        // Bind the /ma and /marena commands to MACommands.
    	getCommand("ma").setExecutor(new MACommands());
    	getCommand("marena").setExecutor(new MACommands());
        
        // Create event listeners.
        PluginManager pm = getServer().getPluginManager();
        PlayerListener signListener     = new MASignListener(this);
        PlayerListener dropListener     = new MADropListener(this);
        PlayerListener readyListener    = new MAReadyListener(this);
        PlayerListener teleportListener = new MATeleportListener(this);
        BlockListener  blockListener    = new MABlockListener(this);
        EntityListener creeperListener  = new MACreeperListener(this);
        EntityListener damageListener   = new MADamageListener(this);
        // TO-DO: PlayerListener to check for player logout during battle.
        // TO-DO: PlayerListener to check for kills/deaths.
        
        // Register events.
        pm.registerEvent(Event.Type.PLAYER_INTERACT,  signListener,     Priority.Normal,  this);
        pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, dropListener,     Priority.Normal,  this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT,  readyListener,    Priority.Normal,  this);
        pm.registerEvent(Event.Type.PLAYER_TELEPORT,  teleportListener, Priority.Normal,  this);
        pm.registerEvent(Event.Type.BLOCK_BREAK,      blockListener,    Priority.Normal,  this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGE,     blockListener,    Priority.Normal,  this);
        pm.registerEvent(Event.Type.BLOCK_PLACE,      blockListener,    Priority.Normal,  this);
        pm.registerEvent(Event.Type.ENTITY_EXPLODE,   creeperListener,  Priority.Normal,  this);
        pm.registerEvent(Event.Type.ENTITY_DAMAGE,    damageListener,   Priority.Normal,  this);
        pm.registerEvent(Event.Type.ENTITY_COMBUST,    creeperListener,   Priority.Normal,  this);
        System.out.println(pdfFile.getName() + " v" + pdfFile.getVersion() + " initialized." );
    }
    
    public void onDisable()
    {
        System.out.println("WAIT! WHAT ARE YOU DOING?!");
    }
}