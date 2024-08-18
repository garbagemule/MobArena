package com.garbagemule.MobArena.finance;

import org.bukkit.entity.Player;

public interface Finance {

    double getBalance(Player player);

    boolean deposit(Player player, double amount);

    boolean withdraw(Player player, double amount);

    String format(double amount);

}
