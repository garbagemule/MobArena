package com.garbagemule.MobArena.commands.admin;

import org.bukkit.command.CommandSender;

import com.garbagemule.MobArena.*;
import com.garbagemule.MobArena.commands.*;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;

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
                Messenger.tell(sender, Msg.FORCE_END_ENDED);
                am.resetArenaMap();
                return true;
            }
            
            // Otherwise, grab the arena in question.
            Arena arena = am.getArenaWithName(arg2);
            if (arena == null) {
                Messenger.tell(sender, Msg.ARENA_DOES_NOT_EXIST);
                return true;
            }
            
            if (arena.getAllPlayers().isEmpty()) {
                Messenger.tell(sender, Msg.FORCE_END_EMPTY);
                return true;
            }
            
            // And end it!
            arena.forceEnd();
            Messenger.tell(sender, Msg.FORCE_END_ENDED);
            return true;
        }
        
        if (arg1.equals("start")) {
            // Require argument.
            if (arg2.equals("")) return false;
            
            // Grab the arena.
            Arena arena = am.getArenaWithName(arg2);
            if (arena == null) {
                Messenger.tell(sender, Msg.ARENA_DOES_NOT_EXIST);
                return true;
            }
            
            if (arena.isRunning()) {
                Messenger.tell(sender, Msg.FORCE_START_RUNNING);
                return true;
            }
            
            if (arena.getReadyPlayersInLobby().isEmpty()) {
                Messenger.tell(sender, Msg.FORCE_START_NOT_READY);
                return true;
            }
            
            // And start it!
            arena.forceStart();
            Messenger.tell(sender, Msg.FORCE_START_STARTED);
            return true;
        }
        return false;
    }
}
