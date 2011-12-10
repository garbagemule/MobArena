package com.garbagemule.MobArena.commands.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;

public class EnableCommand implements MACommand
{
    @Override
    public String[] getNames() {
        return new String[] { "enable" };
    }

    @Override
    public String getPermission() {
        return "mobarena.admin.enable";
    }
    
    @Override
    public boolean execute(MobArenaPlugin plugin, Player sender, String... args) {
        return false;
    }

    @Override
    public boolean executeFromConsole(MobArenaPlugin plugin, CommandSender sender, String... args) {
        return false;
    }
}
