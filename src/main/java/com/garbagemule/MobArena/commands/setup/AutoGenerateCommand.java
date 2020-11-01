package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.MAUtils;
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
    name    = "autogenerate",
    pattern = "auto(\\-)?generate",
    usage   = "/ma autogenerate <arena>",
    desc    = "autogenerate a new arena",
    permission = "mobarena.setup.autogenerate"
)
public class AutoGenerateCommand implements Command
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

        // Check if arena already exists.
        String name = String.join(" ", args);
        String slug = Slugs.create(name);
        Arena arena = am.getArenaWithName(slug);
        if (arena != null) {
            am.getGlobalMessenger().tell(sender, "An arena with that name already exists.");
            return true;
        }

        if (!MAUtils.doooooItHippieMonster(p.getLocation(), 13, name, am.getPlugin())) {
            am.getGlobalMessenger().tell(sender, "Could not auto-generate arena.");
            return true;
        }

        am.getGlobalMessenger().tell(sender, "Arena with name '" + name + "' generated.");
        return true;
    }
}
