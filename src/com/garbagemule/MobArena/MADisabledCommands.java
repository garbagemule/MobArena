package com.garbagemule.MobArena;

import java.util.Arrays;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Handles the disabled commands.
 */
public class MADisabledCommands extends PlayerListener
{
    private MobArena plugin;
    
    public MADisabledCommands(MobArena instance)
    {
        plugin = instance;
    }
    
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        Player p = event.getPlayer();
        
        if (!ArenaManager.playerSet.contains(p))
            return;
        
        if (!Arrays.asList(plugin.DISABLED_COMMANDS).contains(event.getMessage().substring(1)))
            return;
        
        event.setCancelled(true);
        ArenaManager.tellPlayer(p, "You can't use that command in the arena!");
    }
}