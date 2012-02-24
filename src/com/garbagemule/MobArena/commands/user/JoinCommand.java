package com.garbagemule.MobArena.commands.user;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "join",
    pattern = "j|jo.*|j.*n",
    usage   = "/ma join (<arena>)",
    desc    = "join an arena",
    permission = "mobarena.use.join"
)
public class JoinCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (!Commands.isPlayer(sender)) {
            Messenger.tellPlayer(sender, Msg.MISC_NOT_FROM_CONSOLE);
            return false;
        }
        
        // Grab the argument, if any.
        String arg1 = (args.length > 0 ? args[0] : "");
        
        // Cast the sender.
        Player p = (Player) sender;
        
        List<Arena> arenas = am.getEnabledArenas();
        if (!am.isEnabled() || arenas.size() < 1) {
            Messenger.tellPlayer(p, Msg.JOIN_NOT_ENABLED);
            return true;
        }
        
        // Grab the arena to join
        Arena arena = arenas.size() == 1 ? arenas.get(0) : am.getArenaWithName(arg1);
        
        // Run a couple of basic sanity checks
        if (!Commands.sanityChecks(p, am, arena, arg1, arenas)) {
            return true;
        }
        
        // Run a bunch of per-arena sanity checks
        if (!arena.canJoin(p)) {
            return false;
        }
        
        // If player is in a boat/minecart, eject!
        if (p.isInsideVehicle())
            p.leaveVehicle();
        
        // If player is in a bed, unbed!
        if (p.isSleeping()) {
            p.kickPlayer("Banned for life... Nah, just don't join from a bed ;)");
            return false;
        }
        
        // Join the arena!
        if (!arena.playerJoin(p, p.getLocation())) {
            return false;
        }
        
        return true;
    }
}
