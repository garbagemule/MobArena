package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;

import java.util.Arrays;
import java.util.List;

@CommandInfo(
    name    = "setwarp",
    pattern = "set(warp|point)",
    usage   = "/ma setwarp arena|lobby|spectator|exit",
    desc    = "set a warp point for an arena",
    permission = "mobarena.setup.setwarp"
)
public class SetWarpCommand implements Command
{
    private static final List<String> WARPS = Arrays.asList("arena", "lobby", "spectator", "exit");

    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (!Commands.isPlayer(sender)) {
            Messenger.tell(sender, Msg.MISC_NOT_FROM_CONSOLE);
            return false;
        }
        
        // Grab the argument, if any.
        String arg1 = (args.length > 0 ? args[0] : "");
        
        // Cast the sender.
        Player p = (Player) sender;

        // spec -> spectator
        if (arg1.equals("spec")) arg1 = "spectator";

        if (!WARPS.contains(arg1)) {
            Messenger.tell(sender, "Usage: /ma setwarp arena|lobby|spectator|exit");
            return true;
        }
        
        // Make a world check first
        Arena arena = am.getSelectedArena();
        World aw = arena.getWorld();
        World pw = p.getLocation().getWorld();
        boolean changeWorld = !arg1.equals("exit") && !aw.getName().equals(pw.getName());
        
        // Change worlds to make sure the region check doesn't fail
        if (changeWorld) arena.setWorld(pw);
        
        // Make sure the arena warp is inside the region
        if (arg1.equals("arena") && !arena.getRegion().contains(p.getLocation())) {
            if (arena.getRegion().isDefined()) {
                Messenger.tell(sender, "You must be inside the arena region!");
            } else {
                Messenger.tell(sender, "You must first set the region points p1 and p2");
            }
            
            // Restore the world reference in the arena 
            if (changeWorld) arena.setWorld(aw);
        } else {
            // Set the region point
            arena.getRegion().set(arg1, p.getLocation());
            
            // Notify the player if world changed
            if (changeWorld) {
                String msg = String.format("Changed world of arena '%s' from '%s' to '%s'", arena.configName(), aw.getName(), pw.getName());
                Messenger.tell(sender, msg);
            }
            
            // Then notify about point set
            Messenger.tell(sender, "Warp point '" + arg1 + "' was set for arena '" + am.getSelectedArena().configName() + "'");
            arena.getRegion().checkData(am.getPlugin(), sender, true, false, true, false);
        }
        return true;
    }
}
