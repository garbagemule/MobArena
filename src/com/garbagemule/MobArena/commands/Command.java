package com.garbagemule.MobArena.commands;

import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.framework.ArenaMaster;

public interface Command
{
    /**
     * Execute the command using the given arguments.
     * If any arguments are provided, the first argument is -NOT- the
     * same as the command name, i.e. the CommandHandler must strip the
     * array of this element.
     * @param am an ArenaMaster instance
     * @param sender the sender
     * @param args array of arguments
     * @return true, if the command was successful
     */
    public boolean execute(ArenaMaster am, CommandSender sender, String... args);
}
