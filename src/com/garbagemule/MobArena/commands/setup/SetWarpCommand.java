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
    usage   = "/ma setwarp arena|lobby|spectator|exit",
    desc    = "set a warp point for an arena",
    permission = "mobarena.setup.setwarp"
)
public class SetWarpCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (!Commands.isPlayer(sender)) {
            Messenger.tell(sender, Msg.MISC_NOT_FROM_CONSOLE);
            return true;
        }

        // Require a point name
        if (args.length != 1) return false;
        
        // Cast the sender.
        Player p = (Player) sender;

        // spec -> spectator
        if (args[0].equals("spec")) args[0] = "spectator";

        // Check that the point is valid
        if (!args[0].matches("arena|lobby|spectator|exit")) {
            Messenger.tell(sender, "There's no warp called '" + args[0] + "'.");
            return true;
        }
        
        // Make a world check first
        Arena arena = am.getSelectedArena();
        World aw = arena.getWorld();
        World pw = p.getLocation().getWorld();
        boolean changeWorld = !args[0].equals("exit") && !aw.getName().equals(pw.getName());
        
        // Change worlds to make sure the region check doesn't fail
        if (changeWorld) arena.setWorld(pw);
        
        // Make sure the arena warp is inside the region
        if (args[0].equals("arena") && !arena.getRegion().contains(p.getLocation())) {
            if (arena.getRegion().isDefined()) {
                Messenger.tell(sender, "You must be inside the arena region!");
            } else {
                Messenger.tell(sender, "You must first set the region points p1 and p2");
            }
            
            // Restore the world reference in the arena 
            if (changeWorld) arena.setWorld(aw);
        } else {
            // Set the region point
            arena.getRegion().set(args[0], p.getLocation());
            
            // Notify the player if world changed
            if (changeWorld) {
                String msg = String.format("Changed world of arena '%s' from '%s' to '%s'", arena.configName(), aw.getName(), pw.getName());
                Messenger.tell(sender, msg);
            }
            
            // Then notify about point set
            Messenger.tell(sender, "Warp point '" + args[0] + "' was set for arena '" + am.getSelectedArena().configName() + "'");
            arena.getRegion().checkData(am.getPlugin(), sender, true, false, true, false);
        }
        return true;
    }
}
