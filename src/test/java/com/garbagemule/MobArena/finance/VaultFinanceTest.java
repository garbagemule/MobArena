package com.garbagemule.MobArena.finance;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.junit.Before;
import org.junit.Test;

import java.util.logging.Logger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class VaultFinanceTest {

    private ServicesManager services;
    private Logger log;
    private VaultFinance subject;

    @Before
    public void setup() {
        services = mock(ServicesManager.class);
        log = mock(Logger.class);

        subject = new VaultFinance(services, log);
    }

    @Test
    public void looksUpEconomyFromServicesManager() {
        Player player = mock(Player.class);
        when(player.getName()).thenReturn("Test Subject 287");

        subject.getBalance(player);

        verify(services).getRegistration(Economy.class);
    }

    @Test
    public void logsErrorIfNoEconomyFound() {
        Player player = mock(Player.class);
        when(player.getName()).thenReturn("Test Subject 287");
        when(services.getRegistration(Economy.class)).thenReturn(null);

        subject.getBalance(player);

        verify(log).severe(anyString());
    }

    @Test
    public void initializationIsRepeatedIfEconomyIsNotFound() {
        Player player = mock(Player.class);
        when(player.getName()).thenReturn("Test Subject 287");
        when(services.getRegistration(Economy.class)).thenReturn(null);

        subject.getBalance(player);
        subject.getBalance(player);
        subject.getBalance(player);

        verify(services, times(3)).getRegistration(Economy.class);
    }

    @Test
    public void initializationIsIdempotentIfEconomyIsFound() {
        Player player = mock(Player.class);
        Economy economy = mock(Economy.class);
        RegisteredServiceProvider<Economy> provider = createProvider(economy);
        when(services.getRegistration(Economy.class)).thenReturn(provider);

        subject.getBalance(player);

        verify(services, times(1)).getRegistration(Economy.class);
    }

    @Test
    public void delegatesGetBalanceToEconomyWithGivenPlayer() {
        Player player = mock(Player.class);
        Economy economy = mock(Economy.class);
        RegisteredServiceProvider<Economy> provider = createProvider(economy);
        when(services.getRegistration(Economy.class)).thenReturn(provider);

        subject.getBalance(player);

        verify(economy).getBalance(player);
    }

    @Test
    public void delegatesDepositToEconomyWithGivenPlayerAndAmount() {
        Player player = mock(Player.class);
        double amount = 1337;
        Economy economy = mock(Economy.class);
        EconomyResponse res = new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, null);
        RegisteredServiceProvider<Economy> provider = createProvider(economy);
        when(services.getRegistration(Economy.class)).thenReturn(provider);
        when(economy.depositPlayer(any(Player.class), anyDouble())).thenReturn(res);

        subject.deposit(player, amount);

        verify(economy).depositPlayer(player, amount);
    }

    @Test
    public void delegatesWithdrawToEconomyWithGivenPlayerAndAmount() {
        Player player = mock(Player.class);
        double amount = 1337;
        Economy economy = mock(Economy.class);
        EconomyResponse res = new EconomyResponse(0, 0, EconomyResponse.ResponseType.SUCCESS, null);
        RegisteredServiceProvider<Economy> provider = createProvider(economy);
        when(services.getRegistration(Economy.class)).thenReturn(provider);
        when(economy.withdrawPlayer(any(Player.class), anyDouble())).thenReturn(res);

        subject.withdraw(player, amount);

        verify(economy).withdrawPlayer(player, amount);
    }

    @Test
    public void delegatesFormatToEconomyWithGivenAmount() {
        double amount = 1337;
        Economy economy = mock(Economy.class);
        RegisteredServiceProvider<Economy> provider = createProvider(economy);
        when(services.getRegistration(Economy.class)).thenReturn(provider);

        subject.format(amount);

        verify(economy).format(amount);
    }

    private RegisteredServiceProvider<Economy> createProvider(Economy economy) {
        return new RegisteredServiceProvider<>(
            Economy.class,
            economy,
            ServicePriority.Normal,
            mock(Plugin.class)
        );
    }

}
