package com.garbagemule.MobArena;

import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.util.Config;

public interface MobArenaPlugin
{
    /**
     * Get the Config object associated with this MobArena instance.
     * @return the Config object used by MobArena
     */
    public Config getMAConfig();
    
    /**
     * Get the ArenaMaster used by this instance of MobArena.
     * @return the ArenaMaster object
     */
    public ArenaMaster getArenaMaster();
    
    /**
     * Send a message to a player or the console.
     * The message is prefixed with [MobArena].
     * @param recipient the player or console to send the message to
     * @param msg the message to send
     */
    public void tell(CommandSender recipient, String msg);

    /**
     * Send a predefined announcement to a player or the console.
     * Convenience method: the above method is called with Msg.toString()
     * @param recipient the player or console to send the message to
     * @param msg the message to send
     */
    public void tell(CommandSender recipient, Msg msg);
}
