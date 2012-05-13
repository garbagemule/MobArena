package com.garbagemule.MobArena.commands.setup;

import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.ArenaMaster;

@CommandInfo(
        name    = "removeleaderboard",
        pattern = "(del(.)*|r(e)?m(ove)?)leaderboard",
        usage   = "/ma removeleaderboard <arenaname>",
        desc    = "remove the selected arena's leaderboard",
        permission = "mobarena.setup.leaderboards"
    )
public class RemoveLeaderboardCommand implements Command{

    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Grab the argument, if any.
        String arg1 = (args.length > 0 ? args[0] : "");
        
        // If no argument, use the currently selected arena
        if (arg1.equals("")) {
            if (am.getSelectedArena().getRegion().getLeaderboard() != null) {
                am.getSelectedArena().getRegion().set("leaderboard", null);
                Messenger.tellPlayer(sender, "Leaderboard for " + am.getSelectedArena().arenaName() + " successfully removed!");
                return true;
            }
            else {
                Messenger.tellPlayer(sender, Msg.ARENA_LBOARD_NOT_FOUND);
            }
        }
        else {
            if (am.getArenaWithName(arg1) != null) {
                if (am.getSelectedArena().getRegion().getLeaderboard() != null) {
                    am.getArenaWithName(arg1).getRegion().set("leaderboard", null);
                    Messenger.tellPlayer(sender, "Leaderboard for " + am.getArenaWithName(arg1).arenaName() + " successfully removed!");
                    return true;
                }
                else {
                    Messenger.tellPlayer(sender, Msg.ARENA_LBOARD_NOT_FOUND);
                }
            }
            else {
                Messenger.tellPlayer(sender, "Usage: /ma removeleaderboard <arenaname>");
                return false;
            }
        }
        
        return false;
    }

}