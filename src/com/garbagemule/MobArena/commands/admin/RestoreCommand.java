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
        // Grab the argument, if any.
        String arg1 = (args.length > 0 ? args[0] : "");
        
        // Require an argument
        if (arg1.equals("")) {
            Messenger.tellPlayer(sender, "Usage: /ma restore <player>");
            return false;
        }
        
        if (am.getArenaWithPlayer(arg1) != null) {
            Messenger.tellPlayer(sender, "Player is currently in an arena.");
            return false;
        }
        
        if (InventoryManager.restoreFromFile(am.getPlugin(), am.getPlugin().getServer().getPlayer(arg1))) {
            Messenger.tellPlayer(sender, "Restored " + arg1 + "'s inventory!");
        } else {
            Messenger.tellPlayer(sender, "Failed to restore " + arg1 + "'s inventory.");
        }
        
        return true;
    }
}
