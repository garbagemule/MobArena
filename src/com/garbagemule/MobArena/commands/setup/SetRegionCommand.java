package com.garbagemule.MobArena.commands.setup;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.*;

public class SetRegionCommand implements MACommand
{
    @Override
    public String[] getNames() {
        return new String[] { "setregion" };
    }

    @Override
    public String getPermission() {
        return "mobarena.setup.setregion";
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
