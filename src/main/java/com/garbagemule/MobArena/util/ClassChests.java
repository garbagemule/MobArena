package com.garbagemule.MobArena.util;

import com.garbagemule.MobArena.ArenaClass;
import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ClassChests {

    public static boolean assignClassFromStoredClassChest(Arena arena, Player player, ArenaClass ac) {
        if (!arena.getSettings().getBoolean("use-class-chests", false)) {
            return false;
        }

        Location loc = ac.getClassChest();
        if (loc == null) {
            return false;
        }

        Block block = loc.getBlock();
        if (!(block.getState() instanceof InventoryHolder)) {
            arena.getPlugin().getLogger().warning("Class chest location for class '" + ac.getConfigName() + "' is not a chest!");
            return false;
        }

        assignClassAndGrantChestItems(arena, player, ac, block);
        return true;
    }

    public static boolean assignClassFromClassChestSearch(Arena arena, Player player, ArenaClass ac, Sign sign) {
        if (!arena.getSettings().getBoolean("use-class-chests", false)) {
            return false;
        }

        // Start the search
        BlockFace backwards = ((org.bukkit.material.Sign) sign.getData()).getFacing().getOppositeFace();
        Block blockSign   = sign.getBlock();
        Block blockBelow  = blockSign.getRelative(BlockFace.DOWN);
        Block blockBehind = blockBelow.getRelative(backwards);

        // If the block below this sign is a class sign, swap the order
        if (blockBelow.getType() == Material.WALL_SIGN || blockBelow.getType() == Material.SIGN_POST) {
            String className = ChatColor.stripColor(((Sign) blockBelow.getState()).getLine(0)).toLowerCase();
            if (arena.getClasses().containsKey(className)) {
                blockSign = blockBehind;  // Use blockSign as a temp while swapping
                blockBehind = blockBelow;
                blockBelow = blockSign;
            }
        }

        // TODO: Make number of searches configurable
        // First check the pillar below the sign
        Block block = findChestBelow(blockBelow, 6);

        // Then, if no chest was found, check the pillar behind the sign
        if (block == null) {
            block = findChestBelow(blockBehind, 6);
        }

        // If it's still null, we have no class chest
        if (block == null) {
            return false;
        }

        assignClassAndGrantChestItems(arena, player, ac, block);
        return true;
    }

    private static Block findChestBelow(Block b, int left) {
        if (left < 0) {
            return null;
        }
        if (b.getType() == Material.CHEST || b.getType() == Material.TRAPPED_CHEST) {
            return b;
        }
        return findChestBelow(b.getRelative(BlockFace.DOWN), left - 1);
    }

    private static void assignClassAndGrantChestItems(Arena arena, Player player, ArenaClass ac, Block block) {
        String classname = ac.getLowercaseName();
        InventoryHolder holder = (InventoryHolder) block.getState();
        ItemStack[] contents = holder.getInventory().getContents();

        // Guard against double-chests for now
        if (contents.length > 36) {
            ItemStack[] newContents = new ItemStack[36];
            System.arraycopy(contents, 0, newContents, 0, 36);
            contents = newContents;
        }
        arena.assignClassGiveInv(player, classname, contents);
        arena.getMessenger().tell(player, Msg.LOBBY_CLASS_PICKED, arena.getClasses().get(classname).getConfigName());

        double price = ac.getPrice();
        if (price > 0D) {
            arena.getMessenger().tell(player, Msg.LOBBY_CLASS_PRICE, arena.getPlugin().economyFormat(price));
        }
    }

}
