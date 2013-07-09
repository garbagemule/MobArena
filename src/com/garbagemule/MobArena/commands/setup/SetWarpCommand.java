package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "setwarp",
    pattern = "set(warp|point)",
    usage   = "/ma setwarp arena|lobby|spectator",
    desc    = "set a warp point for an arena",
    permission = "mobarena.setup.setwarp"
)
public class SetWarpCommand implements Command
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
        
        if (!(arg1.equals("arena") || arg1.equals("lobby") || arg1.equals("spectator"))) {
            Messenger.tellPlayer(sender, "Usage: /ma setwarp arena|lobby|spectator");
            return true;
        }
        
        // Make a world check first
        Arena arena = am.getSelectedArena();
        World aw = arena.getWorld();
        World pw = p.getLocation().getWorld();
        boolean changeWorld = !aw.getName().equals(pw.getName());
        
        // Change worlds to make sure the region check doesn't fail
        if (changeWorld) arena.setWorld(pw);
        
        // Make sure the arena warp is inside the region
        if (arg1.equals("arena") && !arena.getRegion().contains(p.getLocation())) {
            if (arena.getRegion().isDefined()) {
                Messenger.tellPlayer(sender, "You must be inside the arena region!");
            } else {
                Messenger.tellPlayer(sender, "You must first set the region points p1 and p2");
            }
            
            // Restore the world reference in the arena 
            if (changeWorld) arena.setWorld(aw);
        } else {
            // Set the region point
            arena.getRegion().set(arg1, p.getLocation());
            
            // Notify the player if world changed
            if (changeWorld) {
                Messenger.tellPlayer(sender, "Changed world of arena '" + arena.configName() +
                        "' from '" + aw.getName() +
                        "' to '" + pw.getName() + "'");
            }
            
            // Then notify about point set
            Messenger.tellPlayer(sender, "Warp point '" + arg1 + "' was set for arena '" + am.getSelectedArena().configName() + "'");
            arena.getRegion().checkData(am.getPlugin(), sender, true, false, true, false);
        }
        return true;
    }
}
