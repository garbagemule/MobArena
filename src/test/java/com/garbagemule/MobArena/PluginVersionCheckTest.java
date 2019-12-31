package com.garbagemule.MobArena;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class PluginVersionCheckTest {

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            // Patch: local < remote
            { "1.1.1", "1.1.2", true },
            // Patch: local > remote
            { "1.1.2", "1.1.1", false },
            // Patch: equal
            { "1.1.2", "1.1.2", false },

            // Minor: local < remote
            { "1.1.1", "1.2.1", true },
            { "1.1.2", "1.2.1", true },
            // Minor: local > remote
            { "1.2.1", "1.1.1", false },
            { "1.2.1", "1.1.2", false },
            // Minor: equal
            { "1.2.1", "1.2.1", false },
            { "1.2.2", "1.2.2", false },

            // Major: local < remote
            { "1.1.1", "2.1.1", true },
            { "1.1.2", "2.1.1", true },
            { "1.2.1", "2.1.1", true },
            { "1.2.2", "2.1.1", true },
            // Major: local > remote
            { "2.1.1", "1.1.1", false },
            { "2.1.1", "1.1.2", false },
            { "2.1.1", "1.2.1", false },
            { "2.1.1", "1.2.2", false },
            // Major: equal
            { "2.1.1", "2.1.1", false },
            { "2.2.1", "2.2.1", false },
            { "2.2.2", "2.2.2", false },

            // Incomplete: local < remote
            { "1",     "1.1.1", true },
            { "1.1",   "1.1.1", true },
            { "1.1.1", "2"    , true },
            { "1.1.1", "1.2"  , true },
            // Incomplete: local > remote
            { "1.1.1", "1",     false },
            { "1.1.1", "1.1",   false },
            { "2"    , "1.1.1", false },
            { "1.2"  , "1.1.1", false },

            // Snapshot: local < remote
            { "1.1.1-SNAPSHOT", "1.1.2", true },
            { "1.1.1-SNAPSHOT", "1.2.1", true },
            { "1.1.1-SNAPSHOT", "2.1.1", true },
            // Snapshot: local > remote
            { "1.1.2-SNAPSHOT", "1.1.1", false },
            { "1.2.1-SNAPSHOT", "1.1.1", false },
            { "2.1.1-SNAPSHOT", "1.1.1", false },
            // Snapshot: equal
            { "1.1.2-SNAPSHOT", "1.1.2", true },
            { "1.2.1-SNAPSHOT", "1.2.1", true },
            { "2.1.1-SNAPSHOT", "2.1.1", true },
        });
    }

    @Parameterized.Parameter
    public String local;

    @Parameterized.Parameter(1)
    public String remote;

    @Parameterized.Parameter(2)
    public boolean expected;

    @Test
    public void test() {
        boolean actual = PluginVersionCheck.lessThan(local, remote);

        assertEquals("Expected " + local + " < " + remote + "?",  expected, actual);
    }

}
