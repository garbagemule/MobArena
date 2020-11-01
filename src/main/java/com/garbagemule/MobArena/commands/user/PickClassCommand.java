package com.garbagemule.MobArena.commands.user;

import com.garbagemule.MobArena.ArenaClass;
import com.garbagemule.MobArena.ClassLimitManager;
import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.commands.Commands;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.things.Thing;
import com.garbagemule.MobArena.util.ClassChests;
import com.garbagemule.MobArena.util.Slugs;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CommandInfo(
    name    = "class",
    pattern = "(pick)?class",
    usage   = "/ma class <class>",
    desc    = "pick a class",
    permission = "mobarena.use.class"
)
public class PickClassCommand implements Command
{
    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (!Commands.isPlayer(sender)) {
            am.getGlobalMessenger().tell(sender, Msg.MISC_NOT_FROM_CONSOLE);
            return true;
        }

        // Require a class name
        if (args.length != 1) return false;

        // Unwrap the sender
        Player p = Commands.unwrap(sender);

        // Make sure the player is in an arena
        Arena arena = am.getArenaWithPlayer(p);
        if (arena == null) return true;

        // Make sure the player is in the lobby
        if (!arena.inLobby(p)) {
            arena.getMessenger().tell(p, Msg.MISC_NO_ACCESS);
            return true;
        }

        // Grab the ArenaClass, if it exists
        String slug = Slugs.create(args[0]);
        ArenaClass ac = am.getClasses().get(slug);
        if (ac == null) {
            arena.getMessenger().tell(p, Msg.LOBBY_NO_SUCH_CLASS, slug);
            return true;
        }

        // Check for permission.
        if (!ac.hasPermission(p) && !slug.equals("random")) {
            arena.getMessenger().tell(p, Msg.LOBBY_CLASS_PERMISSION);
            return true;
        }

        // Grab the old ArenaClass, if any, same => ignore
        ArenaClass oldAC = arena.getArenaPlayer(p).getArenaClass();
        if (ac.equals(oldAC)) return true;

        // If the new class is full, inform the player.
        ClassLimitManager clm = arena.getClassLimitManager();
        if (!clm.canPlayerJoinClass(ac)) {
            arena.getMessenger().tell(p, Msg.LOBBY_CLASS_FULL);
            return true;
        }

        // Check price, balance, and inform
        Thing price = ac.getPrice();
        if (price != null) {
            if (!price.heldBy(p)) {
                arena.getMessenger().tell(p, Msg.LOBBY_CLASS_TOO_EXPENSIVE, price.toString());
                return true;
            }
        }

        // Otherwise, leave the old class, and pick the new!
        clm.playerLeftClass(oldAC, p);
        clm.playerPickedClass(ac, p);

        if (!slug.equalsIgnoreCase("random")) {
            if (arena.getSettings().getBoolean("use-class-chests", false)) {
                if (ClassChests.assignClassFromStoredClassChest(arena, p, ac)) {
                    return true;
                }
                // No linked chest? Fall through to config-file
            }
            arena.assignClass(p, slug);
            arena.getMessenger().tell(p, Msg.LOBBY_CLASS_PICKED, arena.getClasses().get(slug).getConfigName());
            if (price != null) {
                arena.getMessenger().tell(p, Msg.LOBBY_CLASS_PRICE, price.toString());
            }
        } else {
            arena.addRandomPlayer(p);
            arena.getMessenger().tell(p, Msg.LOBBY_CLASS_RANDOM);
        }
        return true;
    }

    @Override
    public List<String> tab(ArenaMaster am, Player player, String... args) {
        if (args.length > 1) {
            return Collections.emptyList();
        }

        String prefix = args[0].toLowerCase();

        Collection<ArenaClass> classes = am.getClasses().values();

        return classes.stream()
            .filter(cls -> cls.getSlug().startsWith(prefix))
            .filter(cls -> cls.hasPermission(player))
            .map(ArenaClass::getSlug)
            .collect(Collectors.toList());
    }
}
