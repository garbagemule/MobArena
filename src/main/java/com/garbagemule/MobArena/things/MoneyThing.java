package com.garbagemule.MobArena.things;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.economy.EconomyResponse.ResponseType;
import org.bukkit.entity.Player;

public class MoneyThing implements Thing {
    private Economy economy;
    private double amount;

    public MoneyThing(Economy economy, double amount) {
        this.economy = economy;
        this.amount = amount;
    }

    @Override
    public boolean giveTo(Player player) {
        if (economy == null) {
            return false;
        }
        EconomyResponse result = economy.depositPlayer(player, amount);
        return result.type == ResponseType.SUCCESS;
    }

    @Override
    public boolean takeFrom(Player player) {
        if (economy == null) {
            return false;
        }
        EconomyResponse result = economy.withdrawPlayer(player, amount);
        return result.type == ResponseType.SUCCESS;
    }

    @Override
    public boolean heldBy(Player player) {
        if (economy == null) {
            return false;
        }
        return economy.getBalance(player) >= amount;
    }

    @Override
    public String toString() {
        if (economy == null) {
            return "$" + amount;
        }
        return economy.format(amount);
    }
}
