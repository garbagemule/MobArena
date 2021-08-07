package com.garbagemule.MobArena.things;

import com.garbagemule.MobArena.MobArena;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.mockito.Mockito.*;

public class PermissionThingParserTest {

    private PermissionThingParser subject;

    @Before
    public void setup() {
        MobArena plugin = mock(MobArena.class);
        subject = new PermissionThingParser(plugin);
    }

    @Test
    public void noPrefixNoPerms() {
        String input = "mobarena.use.join";

        PermissionThing result = subject.parse(input);

        assertThat(result, is(nullValue()));
    }

    @Test
    public void grant() {
        String perm = "mobarena.use.leave";
        String input = "perm:" + perm;

        PermissionThing result = subject.parse(input);

        assertThat(result.getPermission(), equalTo(perm));
        assertThat(result.getValue(), equalTo(true));
    }

    @Test
    public void denyMinus() {
        String perm = "mobarena.setup.addarena";
        String input = "perm:-" + perm;

        PermissionThing result = subject.parse(input);

        assertThat(result.getPermission(), equalTo(perm));
        assertThat(result.getValue(), equalTo(false));
    }

    @Test
    public void denyCaret() {
        String perm = "mobarena.use.join";
        String input = "perm:^" + perm;

        PermissionThing result = subject.parse(input);

        assertThat(result.getPermission(), equalTo(perm));
        assertThat(result.getValue(), equalTo(false));
    }

}
