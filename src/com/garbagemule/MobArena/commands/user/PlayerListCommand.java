package com.garbagemule.MobArena.commands.user;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.*;

public class PlayerListCommand implements MACommand
{
    @Override
    public String[] getNames() {
        return new String[] { "who" , "players" };
    }

    @Override
    public String getPermission() {
        return "mobarena.use.playerlist";
    }

    @Override
    public boolean execute(MobArenaPlugin plugin, Player sender, String... args) {
        return true;
    }

    @Override
    public boolean executeFromConsole(MobArenaPlugin plugin, CommandSender sender, String... args) {
        return false;
    }
}
