package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.ArenaClass;
import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.commands.Commands;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.util.config.ConfigUtils;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static com.garbagemule.MobArena.util.config.ConfigUtils.setLocation;

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
            Messenger.tell(sender, Msg.MISC_NOT_FROM_CONSOLE);
            return false;
        }

        if (args.length != 1) {
            Messenger.tell(sender, "Usage: /ma classchest <class>");
            return true;
        }

        ArenaClass ac = am.getClasses().get(args[0].toLowerCase());
        if (ac == null) {
            Messenger.tell(sender, "Class not found.");
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
                Messenger.tell(sender, "You must look at a chest.");
                return true;
        }

        setLocation(am.getPlugin().getConfig(), "classes." + ac.getConfigName() + ".classchest", b.getLocation());
        am.saveConfig();
        Messenger.tell(sender, "Class chest updated for class " + ac.getConfigName());
        am.loadClasses();
        return true;
    }
}
