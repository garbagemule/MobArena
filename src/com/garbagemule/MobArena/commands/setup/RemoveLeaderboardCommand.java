package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
    name    = "removeleaderboard",
    pattern = "(del(.)*|r(e)?m(ove)?)leaderboard",
    usage   = "/ma removeleaderboard <arena>",
    desc    = "remove the selected arena's leaderboard",
    permission = "mobarena.setup.leaderboards"
)
public class RemoveLeaderboardCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        Arena arena;
        if (args.length == 0) {
            if (am.getArenas().size() > 1) {
                Messenger.tell(sender, "There are multiple arenas.");
                return true;
            }
            arena = am.getArenas().get(0);
        } else {
            arena = am.getArenaWithName(args[0]);
            if (arena == null) {
                Messenger.tell(sender, "There is no arena named " + args[0]);
                return true;
            }
        }

        if (arena.getRegion().getLeaderboard() != null) {
            arena.getRegion().set("leaderboard", null);
            Messenger.tell(sender, "Leaderboard for " + arena.configName() + " successfully removed!");
        } else {
            Messenger.tell(sender, Msg.ARENA_LBOARD_NOT_FOUND);
        }
        return true;
    }
}