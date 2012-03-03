package com.garbagemule.MobArena.commands.user;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "spec",
    pattern = "s|spec.*",
    usage   = "/ma spec (<arena>)",
    desc    = "spec an arena",
    permission = "mobarena.use.spec"
)
public class SpecCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (!Commands.isPlayer(sender)) {
            Messenger.tellPlayer(sender, Msg.MISC_NOT_FROM_CONSOLE);
            return false;
        }
        
        // Cast the sender, grab the argument, if any.
        Player p    = (Player) sender;
        String arg1 = (args.length > 0 ? args[0] : null);
        
        // Run some rough sanity checks, and grab the arena to spec.
        Arena arena = Commands.getArenaToJoinOrSpec(am, p, arg1);
        if (arena == null) {
            return false;
        }
        
        // Per-arena sanity checks
        if (!arena.canSpec(p)) {
            return false;
        }
        
        // Spec the arena!
        arena.playerSpec(p, p.getLocation());
        return true;
    }
}
