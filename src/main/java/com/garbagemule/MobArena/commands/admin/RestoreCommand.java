package com.garbagemule.MobArena.commands.admin;

import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.util.inventory.InventoryManager;

@CommandInfo(
    name    = "restore",
    pattern = "restore",
    usage   = "/ma restore <player>",
    desc    = "restore a player's inventory",
    permission = "mobarena.admin.restore"
)
public class RestoreCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Require a player name
        if (args.length != 1) return false;
        
        if (am.getArenaWithPlayer(args[0]) != null) {
            am.getGlobalMessenger().tell(sender, "Player is currently in an arena.");
            return true;
        }
        
        if (InventoryManager.restoreFromFile(am.getPlugin(), am.getPlugin().getServer().getPlayer(args[0]))) {
            am.getGlobalMessenger().tell(sender, "Restored " + args[0] + "'s inventory!");
        } else {
            am.getGlobalMessenger().tell(sender, "Failed to restore " + args[0] + "'s inventory.");
        }
        return true;
    }
}
