package com.garbagemule.MobArena.signs;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;

import org.bukkit.Location;
import org.bukkit.World;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class ArenaSignTest {

    @Test
    public void serialize() {
        World world = mock(World.class);
        Location location = new Location(world, 1, 2, 3);
        String templateId = "a good template";
        String arenaId = "cool arena";
        String type = "join";
        ArenaSign sign = new ArenaSign(location, templateId, arenaId, type);

        Map<String, Object> result = sign.serialize();

        assertThat(result.get("location"), equalTo(location));
        assertThat(result.get("templateId"), equalTo(templateId));
        assertThat(result.get("arenaId"), equalTo(arenaId));
        assertThat(result.get("type"), equalTo(type));
    }

    @Test
    public void deserialize() {
        World world = mock(World.class);
        Location location = new Location(world, 1, 2, 3);
        String templateId = "a good template";
        String arenaId = "cool arena";
        String type = "join";
        Map<String, Object> map = new HashMap<>();
        map.put("location", location);
        map.put("templateId", templateId);
        map.put("arenaId", arenaId);
        map.put("type", type);

        ArenaSign result = ArenaSign.deserialize(map);

        assertThat(result.location, is(equalTo(location)));
        assertThat(result.templateId, is(equalTo(templateId)));
        assertThat(result.arenaId, is(equalTo(arenaId)));
        assertThat(result.type, is(equalTo(type)));
    }

}
