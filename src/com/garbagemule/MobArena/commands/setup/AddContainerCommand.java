package com.garbagemule.MobArena.commands.setup;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "addcontainer",
    pattern = "add(chest|container)",
    usage   = "/ma addcontainer <point name>",
    desc    = "add a new container for the selected arena",
    permission = "mobarena.setup.containers"
)
public class AddContainerCommand implements Command
{
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
        
        if (!arg1.matches("^[a-zA-Z][a-zA-Z0-9]*$")) {
            Messenger.tell(sender, "Usage: /ma addcontainer <point name>");
            return false;
        }
        
        if (!(p.getTargetBlock(null, 50).getState() instanceof InventoryHolder)) {
            Messenger.tell(sender, "You must look at container.");
            return false;
        }
        
        am.getSelectedArena().getRegion().addChest(arg1, p.getTargetBlock(null, 50).getLocation());
        Messenger.tell(sender, "Container '" + arg1 + "' added for arena \"" + am.getSelectedArena().configName() + "\"");
        return true;
    }
}
