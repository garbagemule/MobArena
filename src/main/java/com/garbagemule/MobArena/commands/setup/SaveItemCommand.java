package com.garbagemule.MobArena.commands.setup;

import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.commands.Command;
import com.garbagemule.MobArena.commands.CommandInfo;
import com.garbagemule.MobArena.commands.Commands;
import com.garbagemule.MobArena.framework.ArenaMaster;
import com.garbagemule.MobArena.items.SavedItemsManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@CommandInfo(
    name    = "save-item",
    pattern = "save(-)?item",
    usage   = "/ma save-item <identifier>",
    desc    = "save the currently held item for use in the config-file",
    permission = "mobarena.setup.saveitem"
)
public class SaveItemCommand implements Command {

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
        if (!key.matches("^[\\p{IsAlphabetic}\\d_]+$")) {
            am.getGlobalMessenger().tell(sender, "The identifier must contain only letters, numbers, and underscores");
            return true;
        }

        Player player = Commands.unwrap(sender);
        ItemStack stack = player.getInventory().getItemInMainHand();
        if (stack.getType() == Material.AIR) {
            am.getGlobalMessenger().tell(sender, "You must be holding an item.");
            return true;
        }

        SavedItemsManager items = am.getPlugin().getSavedItemsManager();
        try {
            items.saveItem(key, stack);
        } catch (Exception e) {
            am.getGlobalMessenger().tell(sender, "Couldn't save " + ChatColor.YELLOW + key + ChatColor.RESET + ", because: " + ChatColor.RED + e.getMessage());
            return true;
        }

        am.getGlobalMessenger().tell(sender, "Item saved as " + ChatColor.YELLOW + key + ChatColor.RESET + ".");
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
