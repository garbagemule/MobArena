package com.garbagemule.MobArena.housekeeper;

import com.garbagemule.MobArena.MonsterManager;
import com.garbagemule.MobArena.framework.Arena;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class MonsterCleanerTest {

    @Test
    public void defaultCleanerClearsMonsterManager() {
        Arena arena = mock(Arena.class);
        MonsterManager monsters = mock(MonsterManager.class);
        when(arena.getMonsterManager()).thenReturn(monsters);
        MonsterCleaner subject = MonsterCleaner.getDefault();

        subject.clean(arena);

        verify(monsters).clear();
    }

}
