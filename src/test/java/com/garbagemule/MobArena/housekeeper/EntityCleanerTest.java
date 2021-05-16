package com.garbagemule.MobArena.housekeeper;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.region.ArenaRegion;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class EntityCleanerTest {

    @Test
    public void defaultCleanerRemovesDefaultsOnly() {
        Entity[] defaults = new Entity[] {
            fake(EntityType.ARROW),
            fake(EntityType.BOAT),
            fake(EntityType.DROPPED_ITEM),
            fake(EntityType.EXPERIENCE_ORB),
            fake(EntityType.MINECART),
            fake(EntityType.SHULKER_BULLET),
        };
        Entity[] extras = new Entity[] {
            fake(EntityType.ARMOR_STAND),
            fake(EntityType.PIG),
        };
        Arena arena = mock(Arena.class);
        ArenaRegion region = mock(ArenaRegion.class);
        Chunk chunk = mock(Chunk.class);
        when(arena.getRegion()).thenReturn(region);
        when(region.getChunks()).thenReturn(Collections.singletonList(chunk));
        when(chunk.getEntities()).thenReturn(concat(defaults, extras));
        EntityCleaner subject = EntityCleaner.getDefault();

        subject.clean(arena);

        for (Entity entity : defaults) {
            verify(entity).remove();
        }
        for (Entity entity : extras) {
            verify(entity, never()).remove();
        }
    }

    @Test
    public void customCleanerRemovesProvidedTypesOnly() {
        Entity[] removed = new Entity[] {
            fake(EntityType.DROPPED_ITEM),
            fake(EntityType.DROPPED_ITEM),
            fake(EntityType.ARMOR_STAND),
            fake(EntityType.DROPPED_ITEM),
        };
        Entity[] retained = new Entity[] {
            fake(EntityType.MINECART),
            fake(EntityType.EXPERIENCE_ORB),
            fake(EntityType.EXPERIENCE_ORB),
            fake(EntityType.EXPERIENCE_ORB),
            fake(EntityType.ARROW),
            fake(EntityType.ARROW),
        };
        Arena arena = mock(Arena.class);
        ArenaRegion region = mock(ArenaRegion.class);
        Chunk chunk = mock(Chunk.class);
        when(arena.getRegion()).thenReturn(region);
        when(region.getChunks()).thenReturn(Collections.singletonList(chunk));
        when(chunk.getEntities()).thenReturn(concat(removed, retained));
        EntityCleaner subject = new EntityCleaner(EnumSet.of(
            EntityType.DROPPED_ITEM,
            EntityType.ARMOR_STAND
        ));

        subject.clean(arena);

        for (Entity entity : removed) {
            verify(entity).remove();
        }
        for (Entity entity : retained) {
            verify(entity, never()).remove();
        }
    }

    private static Entity fake(EntityType type) {
        Entity entity = mock(Entity.class);
        when(entity.getType()).thenReturn(type);
        return entity;
    }

    private static Entity[] concat(Entity[] a, Entity[] b) {
        Entity[] result = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

}
