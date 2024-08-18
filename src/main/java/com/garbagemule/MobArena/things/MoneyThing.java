package com.garbagemule.MobArena.things;

import com.garbagemule.MobArena.finance.Finance;
import org.bukkit.entity.Player;

public class MoneyThing implements Thing {

    private final Finance finance;
    private final double amount;

    public MoneyThing(Finance finance, double amount) {
        this.finance = finance;
        this.amount = amount;
    }

    @Override
    public boolean giveTo(Player player) {
        return finance.deposit(player, amount);
    }

    @Override
    public boolean takeFrom(Player player) {
        return finance.withdraw(player, amount);
    }

    @Override
    public boolean heldBy(Player player) {
        return finance.getBalance(player) >= amount;
    }

    @Override
    public String toString() {
        return finance.format(amount);
    }

}
