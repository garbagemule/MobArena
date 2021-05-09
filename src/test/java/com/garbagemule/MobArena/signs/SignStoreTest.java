package com.garbagemule.MobArena.signs;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class SignStoreTest {

    SignStore subject;

    @Before
    public void setup() {
        subject = new SignStore();
    }

    @Test
    public void findByLocationReturnsNullIfSignDoesNotExist() {
        Location location = mock(Location.class);

        ArenaSign result = subject.findByLocation(location);

        assertThat(result, nullValue());
    }

    @Test
    public void findByLocationReturnsSignIfItExists() {
        Location location = mock(Location.class);
        ArenaSign sign = new ArenaSign(location, "cool-sign", "castle", "join");

        subject.add(sign);
        ArenaSign result = subject.findByLocation(location);

        assertThat(result, equalTo(sign));
    }

    @Test
    public void findByArenaIdReturnsEmptyListIfNoSignsMatch() {
        Location location1 = mock(Location.class);
        Location location2 = mock(Location.class);
        ArenaSign sign1 = new ArenaSign(location1, "cool-sign", "castle", "join");
        ArenaSign sign2 = new ArenaSign(location2, "lame-sign", "island", "leave");

        subject.add(sign1);
        subject.add(sign2);
        List<ArenaSign> result = subject.findByArenaId("jungle");

        assertThat(result.isEmpty(), equalTo(true));
    }

    @Test
    public void findByArenaIdReturnsOnlyMatchingSigns() {
        Location location1 = mock(Location.class);
        Location location2 = mock(Location.class);
        Location location3 = mock(Location.class);
        ArenaSign sign1 = new ArenaSign(location1, "cool-sign", "castle", "join");
        ArenaSign sign2 = new ArenaSign(location2, "lame-sign", "island", "leave");
        ArenaSign sign3 = new ArenaSign(location3, "very-sign", "jungle", "info");

        subject.add(sign1);
        subject.add(sign2);
        subject.add(sign3);
        List<ArenaSign> result = subject.findByArenaId("island");

        assertThat(result.size(), equalTo(1));
        assertThat(result, hasItem(sign2));
    }

    @Test
    public void removeReturnsNullIfSignDoesNotExist() {
        Location location = mock(Location.class);
        ArenaSign sign = new ArenaSign(location, "cool-sign", "castle", "join");

        ArenaSign result = subject.removeByLocation(location);

        assertThat(result, nullValue());

        subject.add(sign);
        assertThat(subject.removeByLocation(location), equalTo(sign));

        assertThat(subject.removeByLocation(location), nullValue());
    }

    @Test
    public void removeReturnsSignIfItExists() {
        Location location = mock(Location.class);
        ArenaSign sign = new ArenaSign(location, "cool-sign", "castle", "join");

        subject.add(sign);
        ArenaSign result = subject.removeByLocation(location);

        assertThat(result, equalTo(sign));
    }

    @Test
    public void removeReturnsNullIfSignWasRemoved() {
        Location location = mock(Location.class);
        ArenaSign sign = new ArenaSign(location, "cool-sign", "castle", "join");

        subject.add(sign);
        subject.removeByLocation(location);
        ArenaSign result = subject.removeByLocation(location);

        assertThat(result, nullValue());
    }

    @Test
    public void removeByWorldReturnsOnlyMatchingSigns() {
        World world1 = mock(World.class);
        World world2 = mock(World.class);
        Location location1 = mock(Location.class);
        Location location2 = mock(Location.class);
        when(location1.getWorld()).thenReturn(world1);
        when(location2.getWorld()).thenReturn(world2);
        ArenaSign sign1 = new ArenaSign(location1, "lame-sign", "jungle", "info");
        ArenaSign sign2 = new ArenaSign(location2, "cool-sign", "castle", "join");

        subject.add(sign1);
        subject.add(sign2);
        List<ArenaSign> result = subject.removeByWorld(world2);

        assertThat(result, not(hasItem(sign1)));
        assertThat(result, hasItem(sign2));
    }

    @Test
    public void removeByWorldReturnsEmptyListIfSignsWereRemoved() {
        World world1 = mock(World.class);
        World world2 = mock(World.class);
        Location location1 = mock(Location.class);
        Location location2 = mock(Location.class);
        when(location1.getWorld()).thenReturn(world1);
        when(location2.getWorld()).thenReturn(world2);
        ArenaSign sign1 = new ArenaSign(location1, "lame-sign", "jungle", "info");
        ArenaSign sign2 = new ArenaSign(location2, "cool-sign", "castle", "join");

        subject.add(sign1);
        subject.add(sign2);
        subject.removeByWorld(world2);
        List<ArenaSign> result = subject.removeByWorld(world2);

        assertThat(result.isEmpty(), equalTo(true));
    }

}
