package com.garbagemule.MobArena.commands;

import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.framework.ArenaMaster;

public interface Command
{
    /**
     * Execute the command using the given arguments.
     * <p>
     * If the method returns false, the command handler will print the usage
     * message (usage + description) for the command, and as such, if the
     * execution was successful in any way, the method should return true.
     * Note that "successful in any way" means if the execution managed to
     * complete in the sense that it itself prints a message to the sender,
     * or otherwise executed properly, not if the intent of the command
     * wasn't fulfilled. Typically, this means that false is only returned
     * if the command was executed with a set of arguments that did not
     * match the usage message.
     *
     * @param am an ArenaMaster instance
     * @param sender the sender
     * @param args array of arguments
     * @return true, if the command succeeded in any way, false if the
     * command handler should print the usage message to the sender
     */
    public boolean execute(ArenaMaster am, CommandSender sender, String... args);
}
