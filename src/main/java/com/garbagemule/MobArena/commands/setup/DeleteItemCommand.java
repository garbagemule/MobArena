package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.items.SavedItemsManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CommandInfo(
    name    = "delete-item",
    pattern = "delete(-)?item",
    usage   = "/ma delete-item <identifier>",
    desc    = "delete the item with the given identifier",
    permission = "mobarena.setup.deleteitem"
)
public class DeleteItemCommand implements Command {

    @Override
    public boolean execute(ArenaMaster am, CommandSender sender, String... args) {
        if (args.length != 1) {
            return false;
        }

        String key = args[0];

        SavedItemsManager items = am.getPlugin().getSavedItemsManager();
        try {
            items.deleteItem(key);
        } catch (Exception e) {
            am.getGlobalMessenger().tell(sender, "Couldn't delete " + ChatColor.YELLOW + key + ChatColor.RESET + ", because: " + ChatColor.RED + e.getMessage());
            return true;
        }

        am.getGlobalMessenger().tell(sender, "Saved item " + ChatColor.YELLOW + key + ChatColor.RESET + " deleted.");
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
