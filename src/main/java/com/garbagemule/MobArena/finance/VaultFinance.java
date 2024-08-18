package com.garbagemule.MobArena.finance;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicesManager;

import java.util.logging.Logger;

public class VaultFinance implements Finance {

    private final ServicesManager services;
    private final Logger log;

    private Economy economy;

    public VaultFinance(ServicesManager services, Logger log) {
        this.services = services;
        this.log = log;
    }

    @Override
    public double getBalance(Player player) {
        try {
            return getEconomy().getBalance(player);
        } catch (IllegalStateException e) {
            log.severe("Failed to check balance of player " + player.getName() + " because: " + e.getMessage());
            return 0;
        }
    }

    @Override
    public boolean deposit(Player player, double amount) {
        try {
            EconomyResponse res = getEconomy().depositPlayer(player, amount);
            return res.type == EconomyResponse.ResponseType.SUCCESS;
        } catch (IllegalStateException e) {
            log.severe("Failed to give " + amount + " economy money to player " + player.getName() + " because: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean withdraw(Player player, double amount) {
        try {
            EconomyResponse res = getEconomy().withdrawPlayer(player, amount);
            return res.type == EconomyResponse.ResponseType.SUCCESS;
        } catch (IllegalStateException e) {
            log.severe("Failed to take " + amount + " economy money from player " + player.getName() + " because: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String format(double amount) {
        try {
            return getEconomy().format(amount);
        } catch (IllegalStateException e) {
            log.severe("Failed to format " + amount + " as economy money because: " + e.getMessage());
            return "ERROR";
        }
    }

    private Economy getEconomy() {
        if (economy != null) {
            return economy;
        }

        RegisteredServiceProvider<Economy> provider = services.getRegistration(Economy.class);
        if (provider == null) {
            throw new IllegalStateException("No Vault economy provider found!");
        }

        return (economy = provider.getProvider());
    }

}
