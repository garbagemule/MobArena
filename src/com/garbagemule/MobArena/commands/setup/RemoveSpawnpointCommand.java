package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "delspawn",
    pattern = "(del(.)*|r(e)?m(ove)?)spawn(point)?",
    usage   = "/ma delspawn <arena> <point>",
    desc    = "delete a spawnpoint",
    permission = "mobarena.setup.spawnpoints"
)
public class RemoveSpawnpointCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (args.length < 1) return false;

        Arena arena;
        String point;
        if (args.length == 1) {
            if (am.getArenas().size() > 1) {
                Messenger.tell(sender, "There are multiple arenas.");
                return true;
            }
            arena = am.getArenas().get(0);
            point = args[0];
        } else {
            arena = am.getArenaWithName(args[0]);
            if (arena == null) {
                Messenger.tell(sender, "There is no arena named " + args[0]);
                return true;
            }
            point = args[1];
        }

        if (arena.getRegion().removeSpawn(point)) {
            Messenger.tell(sender, "Spawnpoint " + point + " removed for arena '" + arena.configName() + "'");
        } else {
            Messenger.tell(sender, "Could not find the spawnpoint " + point + " for the arena '" + arena.configName() + "'");
        }
        return true;
    }
}
