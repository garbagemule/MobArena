package com.garbagemule.MobArena.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class SlugsTest {

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {"castle", "castle"},
            {"Castle", "castle"},
            {"CaStLe", "castle"},
            {"Castle of Kebab", "castle-of-kebab"},
            {"Area 52", "area-52"},
            {"Project: Nuclear", "project-nuclear"},
            {"Mr. Kebal Bab's Mansion", "mr-kebal-babs-mansion"},
            {"Unnamed Arena (3)", "unnamed-arena-3"},
            {"The Wolf Master", "the-wolf-master"},
            {"already-a-slug", "already-a-slug"},
        });
    }

    @Parameter
    public String input;

    @Parameter(1)
    public String expected;

    @Test
    public void test() {
        String actual = Slugs.create(input);

        assertEquals("Wrong slug for '" + input + "'",  expected, actual);
    }
}
