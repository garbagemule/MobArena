package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.ArenaClass;
import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.commands.Commands;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
    name    = "classchest",
    pattern = "classchest",
    usage   = "/ma classchest <class>",
    desc    = "link a chest to a class",
    permission = "mobarena.setup.classchest"
)
public class ClassChestCommand implements Command {
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (!Commands.isPlayer(sender)) {
            Messenger.tellPlayer(sender, Msg.MISC_NOT_FROM_CONSOLE);
            return false;
        }

        if (args.length != 1) {
            Messenger.tellPlayer(sender, "Usage: /ma classchest <class>");
            return true;
        }

        ArenaClass ac = am.getClasses().get(args[0].toLowerCase());
        if (ac == null) {
            Messenger.tellPlayer(sender, "Class not found.");
            return true;
        }

        Player p = (Player) sender;
        Block b = p.getTargetBlock(null, 10);

        switch (b.getType()) {
            case CHEST:
            case LOCKED_CHEST:
            case ENDER_CHEST:
            case TRAPPED_CHEST:
                break;
            default:
                Messenger.tellPlayer(sender, "You must look at a chest.");
                return true;
        }

        am.getPlugin().getConfig().set("classes." + ac.getConfigName() + ".classchest", b.getLocation());
        am.saveConfig();
        Messenger.tellPlayer(sender, "Class chest updated for class " + ac.getConfigName());
        am.loadClasses();
        return true;
    }
}
