package com.garbagemule.MobArena.finance;

import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class UnsupportedFinance implements Finance {

    private final Logger log;

    public UnsupportedFinance(Logger log) {
        this.log = log;
    }

    @Override
    public double getBalance(Player player) {
        log.severe("Economy operations are only supported via Vault!");
        return -1.0;
    }

    @Override
    public boolean deposit(Player player, double amount) {
        log.severe("Economy operations are only supported via Vault!");
        return false;
    }

    @Override
    public boolean withdraw(Player player, double amount) {
        log.severe("Economy operations are only supported via Vault!");
        return false;
    }

    @Override
    public String format(double amount) {
        log.severe("Economy operations are only supported via Vault!");
        return "ERROR";
    }

}
