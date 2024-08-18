package com.garbagemule.MobArena.finance;

import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.junit.Test;

import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FinanceFactoryTest {

    @Test
    public void looksUpVaultPluginFromPluginManager() {
        Server server = mock(Server.class);
        Logger log = mock(Logger.class);
        PluginManager plugins = mock(PluginManager.class);
        when(server.getPluginManager()).thenReturn(plugins);

        FinanceFactory.create(server, log);

        verify(plugins).getPlugin("Vault");
    }

    @Test
    public void createsUnsupportedFinanceIfVaultNotPresent() {
        Server server = mock(Server.class);
        Logger log = mock(Logger.class);
        PluginManager plugins = mock(PluginManager.class);
        when(server.getPluginManager()).thenReturn(plugins);
        when(plugins.getPlugin(any())).thenReturn(null);

        Finance result = FinanceFactory.create(server, log);

        assertThat(result, instanceOf(UnsupportedFinance.class));
    }

    @Test
    public void createsVaultFinanceIfVaultIsPresent() {
        Server server = mock(Server.class);
        Logger log = mock(Logger.class);
        Plugin vault = mock(Plugin.class);
        PluginManager plugins = mock(PluginManager.class);
        when(server.getPluginManager()).thenReturn(plugins);
        when(plugins.getPlugin(any())).thenReturn(vault);

        Finance result = FinanceFactory.create(server, log);

        assertThat(result, instanceOf(VaultFinance.class));
    }

}
