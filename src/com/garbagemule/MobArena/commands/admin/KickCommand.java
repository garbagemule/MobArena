package com.garbagemule.MobArena.commands.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "kick",
    pattern = "kick|kcik",
    usage   = "/ma kick <player>",
    desc    = "kick a player from an arena",
    permission = "mobarena.admin.kick"
)
public class KickCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Grab the argument, if any.
        String arg1 = (args.length > 0 ? args[0] : "");
        
        // Require an argument
        if (arg1.equals("")) {
            Messenger.tellPlayer(sender, "Usage: /ma kick <player>");
            return false;
        }
        
        Arena arena = am.getArenaWithPlayer(arg1);
        if (arena == null) {
            Messenger.tellPlayer(sender, "That player is not in an arena.");
            return false;
        }
        
        // Grab the Player object.
        Player bp = am.getPlugin().getServer().getPlayer(arg1);
        
        // Force leave.
        arena.playerLeave(bp);
        
        Messenger.tellPlayer(sender, "Player '" + arg1 + "' was kicked from arena '" + arena.configName() + "'.");
        Messenger.tellPlayer(bp, "You were kicked by " + sender.getName() + ".");
        return true;
    }
}
