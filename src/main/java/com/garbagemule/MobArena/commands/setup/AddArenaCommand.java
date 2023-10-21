package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.commands.Commands;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.util.Slugs;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandInfo(
    name    = "addarena",
    pattern = "(add|new)arena",
    usage   = "/ma addarena <arena>",
    desc    = "add a new arena",
    permission = "mobarena.setup.addarena"
)
public class AddArenaCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (!Commands.isPlayer(sender)) {
            am.getGlobalMessenger().tell(sender, Msg.MISC_NOT_FROM_CONSOLE);
            return true;
        }

        // Require an arena name
        if (args.length < 1) return false;

        // Unwrap the sender.
        Player p = Commands.unwrap(sender);

        String name = String.join(" ", args);
        String slug = Slugs.create(name);
        Arena arena = am.getArenaWithName(slug);
        if (arena != null) {
            am.getGlobalMessenger().tell(sender, "An arena with that name already exists.");
            return true;
        }
        am.createArenaNode(name, p.getWorld());
        am.getGlobalMessenger().tell(sender, "New arena with name '" + name + "' created!");
        return true;
    }
}
