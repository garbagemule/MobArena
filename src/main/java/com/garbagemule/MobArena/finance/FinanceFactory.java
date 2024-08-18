package com.garbagemule.MobArena.finance;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicesManager;

import java.util.logging.Logger;

public class FinanceFactory {

    FinanceFactory() {
        // OK BOSS
    }

    public static Finance create(Server server, Logger log) {
        Plugin plugin = server.getPluginManager().getPlugin("Vault");
        if (plugin == null) {
            return new UnsupportedFinance(log);
        }

        ServicesManager services = server.getServicesManager();
        return new VaultFinance(services, log);
    }

}
