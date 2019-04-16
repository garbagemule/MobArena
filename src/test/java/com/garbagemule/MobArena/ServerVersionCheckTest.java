package com.garbagemule.MobArena;

import org.bukkit.Server;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class ServerVersionCheckTest {

    public Server server;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setup() {
        server = mock(Server.class);
    }

    @Test
    public void oneFourteen() {
        win("git-Spigot-cafebae-dedbeef (MC: 1.14)");
    }

    @Test
    public void oneThirteen() {
        win("git-Spigot-f09662d-be557e6 (MC: 1.13.2)");
    }

    private void win(String version) {
        when(server.getVersion()).thenReturn(version);
        ServerVersionCheck.check(server);
    }

    @Test
    public void oneTwelve() {
        fail("git-Spigot-79a30d7-acbc348 (MC: 1.12.2)");
    }

    @Test
    public void oneEleven() {
        fail("git-Spigot-3fb9445-6e3cec8 (MC: 1.11.2)");
    }

    @Test
    public void oneTen() {
        fail("git-Spigot-de459a2-51263e9 (MC: 1.10.2)");
    }

    @Test
    public void oneNine() {
        fail("git-Spigot-c6871e2-0cd0397 (MC: 1.9.4)");
    }

    @Test
    public void oneEight() {
        fail("git-Spigot-21fe707-e1ebe52 (MC: 1.8.8)");
    }

    private void fail(String version) {
        when(server.getVersion()).thenReturn(version);
        exception.expect(IllegalStateException.class);
        ServerVersionCheck.check(server);
    }

}
