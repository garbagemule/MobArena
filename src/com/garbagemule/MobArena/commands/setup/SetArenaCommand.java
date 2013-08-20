package com.garbagemule.MobArena.commands.setup;

import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "setarena",
    pattern = "(set|select)arena",
    usage   = "/ma setarena <arena>",
    desc    = "set an arena as the selected arena",
    permission = "mobarena.setup.setarena"
)
public class SetArenaCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Require an arena name
        if (args.length != 1) return false;
        
        Arena arena = am.getArenaWithName(args[0]);
        if (arena != null) {
            am.setSelectedArena(arena);
            Messenger.tell(sender, "Currently selected arena: " + arena.configName());
        } else {
            Messenger.tell(sender, Msg.ARENA_DOES_NOT_EXIST);
        }
        return true;
    }
}
