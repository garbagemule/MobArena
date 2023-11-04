package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.commands.Commands;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.items.SavedItemsManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CommandInfo(
    name    = "load-item",
    pattern = "load(-)?item",
    usage   = "/ma load-item <identifier>",
    desc    = "load the item saved by the given identifier into your hand",
    permission = "mobarena.setup.loaditem"
)
public class LoadItemCommand implements Command {

    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (!Commands.isPlayer(sender)) {
            am.getGlobalMessenger().tell(sender, Msg.MISC_NOT_FROM_CONSOLE);
            return true;
        }

        if (args.length != 1) {
            return false;
        }

        String key = args[0];

        SavedItemsManager items = am.getPlugin().getSavedItemsManager();
        ItemStack stack = items.getItem(key);
        if (stack == null) {
            am.getGlobalMessenger().tell(sender, "No saved item with identifier " + ChatColor.YELLOW + key + ChatColor.RESET + " found.");
            return true;
        }

        Player player = Commands.unwrap(sender);
        player.getInventory().setItemInMainHand(stack);

        am.getGlobalMessenger().tell(sender, "Saved item " + ChatColor.YELLOW + key + ChatColor.RESET + " loaded.");
        return true;
    }

    @Override
    public List<String> tab(ArenaMaster am, Player player, String... args) {
        if (args.length > 1) {
            return Collections.emptyList();
        }

        String prefix = args[0].toLowerCase();

        SavedItemsManager items = am.getPlugin().getSavedItemsManager();
        List<String> keys = items.getKeys();

        return keys.stream()
            .filter(key -> key.startsWith(prefix))
            .collect(Collectors.toList());
    }

}
