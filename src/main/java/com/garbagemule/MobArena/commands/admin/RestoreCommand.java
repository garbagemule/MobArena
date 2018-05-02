package com.garbagemule.MobArena.commands.admin;

import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.util.inventory.InventoryManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        
        Player player = am.getPlugin().getServer().getPlayer(args[0]);
        if (player == null) {
            am.getGlobalMessenger().tell(sender, "Player not found.");
            return true;
        }
        if (am.getArenaWithPlayer(player) != null) {
            am.getGlobalMessenger().tell(sender, "Player is currently in an arena.");
            return true;
        }
        
        if (InventoryManager.restoreFromFile(am.getPlugin(), player)) {
            am.getGlobalMessenger().tell(sender, "Restored " + args[0] + "'s inventory!");
        } else {
            am.getGlobalMessenger().tell(sender, "Failed to restore " + args[0] + "'s inventory.");
        }
        return true;
    }
}
