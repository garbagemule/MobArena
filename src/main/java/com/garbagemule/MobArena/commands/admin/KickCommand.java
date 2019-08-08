package com.garbagemule.MobArena.commands.admin;

import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CommandInfo(
    name    = "kick",
    pattern = "kick|kcik",
    usage   = "/ma kick <player>",
    desc    = "kick a player from an arena",
    permission = "mobarena.admin.kick"
)
public class KickCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        // Require a player name
        if (args.length != 1) return false;
        
        Arena arena = am.getArenaWithPlayer(args[0]);
        if (arena == null) {
            am.getGlobalMessenger().tell(sender, "That player is not in an arena.");
            return true;
        }
        
        // Grab the Player object.
        Player bp = am.getPlugin().getServer().getPlayer(args[0]);
        
        // Force leave.
        arena.playerLeave(bp);
        am.getGlobalMessenger().tell(sender, "Player '" + args[0] + "' was kicked from arena '" + arena.configName() + "'.");
        am.getGlobalMessenger().tell(bp, "You were kicked by " + sender.getName() + ".");
        return true;
    }

    @Override
    public List<String> tab(ArenaMaster am, Player player, String... args) {
        if (args.length > 1) {
            return Collections.emptyList();
        }

        String prefix = args[0].toLowerCase();

        List<Player> players = am.getAllPlayers();

        return players.stream()
            .filter(p -> p.getDisplayName().toLowerCase().startsWith(prefix))
            .map(Player::getDisplayName)
            .collect(Collectors.toList());
    }
}
