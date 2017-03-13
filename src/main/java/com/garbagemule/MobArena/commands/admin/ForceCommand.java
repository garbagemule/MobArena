package com.garbagemule.MobArena.commands.admin;

import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.command.CommandSender;

@CommandInfo(
    name    = "force",
    pattern = "force",
    usage   = "/ma force start|end (<arena>)",
    desc    = "force start or end an arena",
    permission = "mobarena.admin.force"
)
public class ForceCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Require at least one argument
        if (args.length < 1) return false;

        // Grab the argument, if any.
        String arg1 = (args.length > 0 ? args[0] : "");
        String arg2 = (args.length > 1 ? args[1] : "");
        
        if (arg1.equals("end")) {
            // With no arguments, end all.
            if (arg2.equals("")) {
                for (Arena arena : am.getArenas()) {
                    arena.forceEnd();
                }
                am.getGlobalMessenger().tell(sender, Msg.FORCE_END_ENDED);
                am.resetArenaMap();
                return true;
            }
            
            // Otherwise, grab the arena in question.
            Arena arena = am.getArenaWithName(arg2);
            if (arena == null) {
                am.getGlobalMessenger().tell(sender, Msg.ARENA_DOES_NOT_EXIST);
                return true;
            }
            
            if (arena.getAllPlayers().isEmpty()) {
                am.getGlobalMessenger().tell(sender, Msg.FORCE_END_EMPTY);
                return true;
            }
            
            // And end it!
            arena.forceEnd();
            am.getGlobalMessenger().tell(sender, Msg.FORCE_END_ENDED);
            return true;
        }
        
        if (arg1.equals("start")) {
            // Require argument.
            if (arg2.equals("")) return false;
            
            // Grab the arena.
            Arena arena = am.getArenaWithName(arg2);
            if (arena == null) {
                am.getGlobalMessenger().tell(sender, Msg.ARENA_DOES_NOT_EXIST);
                return true;
            }
            
            if (arena.isRunning()) {
                am.getGlobalMessenger().tell(sender, Msg.FORCE_START_RUNNING);
                return true;
            }
            
            if (arena.getReadyPlayersInLobby().isEmpty()) {
                am.getGlobalMessenger().tell(sender, Msg.FORCE_START_NOT_READY);
                return true;
            }
            
            // And start it!
            arena.forceStart();
            am.getGlobalMessenger().tell(sender, Msg.FORCE_START_STARTED);
            return true;
        }
        return false;
    }
}
