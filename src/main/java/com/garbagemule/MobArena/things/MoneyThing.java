package com.garbagemule.MobArena.things;

import com.garbagemule.MobArena.MobArena;
import org.bukkit.entity.Player;

public class MoneyThing implements Thing {
    private MobArena plugin;
    private double amount;

    public MoneyThing(MobArena plugin, double amount) {
        this.plugin = plugin;
        this.amount = amount;
    }

    @Override
    public boolean giveTo(Player player) {
        return plugin.giveMoney(player, amount);
    }

    @Override
    public boolean takeFrom(Player player) {
        return plugin.takeMoney(player, amount);
    }

    @Override
    public boolean heldBy(Player player) {
        return plugin.hasEnough(player, amount);
    }

    @Override
    public String toString() {
        return plugin.economyFormat(amount);
    }
}
