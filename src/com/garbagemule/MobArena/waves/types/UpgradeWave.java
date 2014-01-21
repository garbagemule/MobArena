package com.garbagemule.MobArena.waves.types;

import java.util.*;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.garbagemule.MobArena.ArenaClass.ArmorType;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.AbstractWave;
import com.garbagemule.MobArena.waves.MACreature;
import com.garbagemule.MobArena.waves.enums.WaveType;

public class UpgradeWave extends AbstractWave
{
    private Map<String,List<Upgrade>> upgrades;
    private boolean giveAll;

    public UpgradeWave(Map<String,List<Upgrade>> upgrades) {
        this.upgrades = upgrades;
        this.setType(WaveType.UPGRADE);
    }
    
    @Override
    public Map<MACreature,Integer> getMonstersToSpawn(int wave, int playerCount, Arena arena) {
        return new HashMap<MACreature,Integer>();
    }

    public void grantItems(Arena arena, Player p, String className) {
        List<Upgrade> list = upgrades.get(className);
        if (list == null) return;

        if (giveAll) {
            for (Upgrade upgrade : list) {
                upgrade.upgrade(arena, p);
            }
        } else {
            int index = new Random().nextInt(list.size());
            list.get(index).upgrade(arena, p);
        }
    }
    
    public void setGiveAll(boolean giveAll) {
        this.giveAll = giveAll;
    }

    /**
     * Represents an upgrade for an upgrade wave
     */
    public static interface Upgrade {
        public void upgrade(Arena arena, Player p);
    }

    /**
     * Armor upgrades
     * Replace the item in the specific slot
     */
    public static class ArmorUpgrade implements Upgrade {
        private ItemStack item;
        private ArmorType type;

        public ArmorUpgrade(ItemStack item) {
            this.item = item;
            this.type = ArmorType.getType(item);
        }

        @Override
        public void upgrade(Arena arena, Player p) {
            if (item == null || type == null) return;

            switch (type) {
                case HELMET:     p.getInventory().setHelmet(item);     break;
                case CHESTPLATE: p.getInventory().setChestplate(item); break;
                case LEGGINGS:   p.getInventory().setLeggings(item);   break;
                case BOOTS:      p.getInventory().setBoots(item);      break;
            }
        }
    }

    /**
     * Weapon upgrades
     * Replace the first item that matches the ID on the quickbar
     */
    public static class WeaponUpgrade implements Upgrade {
        private ItemStack item;

        public WeaponUpgrade(ItemStack item) {
            this.item = item;
        }

        @Override
        public void upgrade(Arena arena, Player p) {
            if (item == null) return;

            ItemStack[] items = p.getInventory().getContents();
            int firstEmpty = -1;

            // Find a matching ID and upgrade it
            for (int i = 0; i < 9; i++) {
                // Save the first null index
                if (items[i] == null) {
                    if (firstEmpty < 0) firstEmpty = i;
                    continue;
                }
                // If we find an ID, upgrade and quit
                if (items[i].getTypeId() == item.getTypeId()) {
                    items[i] = item;
                    p.getInventory().setContents(items);
                    return;
                }
            }

            // If nothing was found, just give them a new weapon
            if (firstEmpty > 0) {
                items[firstEmpty] = item;
                p.getInventory().setContents(items);
            } else {
                p.getInventory().addItem(item);
            }
        }
    }

    /**
     * Generic upgrades
     * Add the item to the player's inventory
     */
    public static class GenericUpgrade implements Upgrade {
        private ItemStack item;

        public GenericUpgrade(ItemStack item) {
            this.item = item;
        }

        @Override
        public void upgrade(Arena arena, Player p) {
            if (item == null) return;

            p.getInventory().addItem(item);
        }
    }

    /**
     * Permission upgrades
     * Set the given permission
     */
    public static class PermissionUpgrade implements Upgrade {
        private String perm;
        private boolean value;

        public PermissionUpgrade(String perm) {
            if (perm.startsWith("-") || perm.startsWith("^")) {
                perm = perm.substring(1).trim();
                value = false;
            } else {
                value = true;
            }
            this.perm = perm;
        }

        @Override
        public void upgrade(Arena arena, Player p) {
            if (perm == null) return;

            arena.addPermission(p, perm, value);
        }
    }
}
