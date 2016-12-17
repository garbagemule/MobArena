package com.garbagemule.MobArena.commands.user;

import com.garbagemule.MobArena.ArenaClass;
import com.garbagemule.MobArena.ClassLimitManager;
import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.commands.Commands;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.util.TextUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

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
            Messenger.tell(sender, Msg.MISC_NOT_FROM_CONSOLE);
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
            Messenger.tell(p, Msg.MISC_NO_ACCESS);
            return true;
        }

        // Grab the ArenaClass, if it exists
        String lowercase = args[0].toLowerCase();
        ArenaClass ac = am.getClasses().get(lowercase);
        if (ac == null) {
            Messenger.tell(p, Msg.LOBBY_NO_SUCH_CLASS, lowercase);
            return true;
        }

        // Check for permission.
        if (!am.getPlugin().has(p, "mobarena.classes." + lowercase) && !lowercase.equals("random")) {
            Messenger.tell(p, Msg.LOBBY_CLASS_PERMISSION);
            return true;
        }

        // Grab the old ArenaClass, if any, same => ignore
        ArenaClass oldAC = arena.getArenaPlayer(p).getArenaClass();
        if (ac.equals(oldAC)) return true;

        // If the new class is full, inform the player.
        ClassLimitManager clm = arena.getClassLimitManager();
        if (!clm.canPlayerJoinClass(ac)) {
            Messenger.tell(p, Msg.LOBBY_CLASS_FULL);
            return true;
        }

        // Check price, balance, and inform
        double price = ac.getPrice();
        if (price > 0D) {
            if (!am.getPlugin().hasEnough(p, price)) {
                Messenger.tell(p, Msg.LOBBY_CLASS_TOO_EXPENSIVE, am.getPlugin().economyFormat(price));
                return true;
            }
        }

        // Otherwise, leave the old class, and pick the new!
        clm.playerLeftClass(oldAC, p);
        clm.playerPickedClass(ac, p);

        if (!lowercase.equalsIgnoreCase("random")) {
            if (arena.getSettings().getBoolean("use-class-chests", false)) {
                Location loc = ac.getClassChest();
                if (loc != null) {
                    Block blockChest = loc.getBlock();
                    InventoryHolder holder = (InventoryHolder) blockChest.getState();
                    ItemStack[] contents = holder.getInventory().getContents();
                    // Guard against double-chests for now
                    if (contents.length > 36) {
                        ItemStack[] newContents = new ItemStack[36];
                        System.arraycopy(contents, 0, newContents, 0, 36);
                        contents = newContents;
                    }
                    arena.assignClassGiveInv(p, lowercase, contents);
                    // p.getInventory().setContents(contents); this already happens in assignClassGiveInv()
                    Messenger.tell(p, Msg.LOBBY_CLASS_PICKED, TextUtils.camelCase(lowercase));
                    if (price > 0D) {
                        Messenger.tell(p, Msg.LOBBY_CLASS_PRICE, am.getPlugin().economyFormat(price));
                    }
                    return true;
                }
                // No linked chest? Fall through to config-file
            }
            arena.assignClass(p, lowercase);
            Messenger.tell(p, Msg.LOBBY_CLASS_PICKED, TextUtils.camelCase(lowercase));
            if (price > 0D) {
                Messenger.tell(p, Msg.LOBBY_CLASS_PRICE, am.getPlugin().economyFormat(price));
            }
        } else {
            arena.addRandomPlayer(p);
            Messenger.tell(p, Msg.LOBBY_CLASS_RANDOM);
        }
        return true;
    }
}
