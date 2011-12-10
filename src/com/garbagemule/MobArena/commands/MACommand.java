package com.garbagemule.MobArena.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.MobArenaPlugin;

public interface MACommand
{
    /**
     * Get an array of names/aliases for this command.
     * Every command must have at least one name/alias to work.
     * @return a String[] of names/aliases
     */
    public String[] getNames();
    
    /**
     * Get the permission required to execute this command.
     * If there is no permission required, the method returns
     * the empty string.
     * @return a permission or null
     */
    public String getPermission();
    
    /**
     * Execute the implementing command with the input Player as the executor.
     * @param sender The Player executing the command
     * @param args A list of arguments
     * @return true, if the command was executed successfully, false otherwise
     */
    public boolean execute(MobArenaPlugin plugin, Player sender, String... args);
    
    /**
     * Execute the implementing command with the console as the executor.
     * @param sender The console executing the command
     * @param args A list of arguments
     * @return true, if the command was executed successfully, false otherwise
     */
    public boolean executeFromConsole(MobArenaPlugin plugin, CommandSender sender, String... args);
}
