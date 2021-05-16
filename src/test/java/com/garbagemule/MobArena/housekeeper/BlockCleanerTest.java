package com.garbagemule.MobArena.housekeeper;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class BlockCleanerTest {

    @Test
    public void defaultCleanerSetsArenaBlocksToAir() {
        Block block1 = mock(Block.class);
        Block block2 = mock(Block.class);
        Block block3 = mock(Block.class);
        Set<Block> blocks = new HashSet<>(Arrays.asList(block1, block2, block3));
        Arena arena = mock(Arena.class);
        when(arena.getBlocks()).thenReturn(blocks);
        BlockCleaner subject = BlockCleaner.getDefault();

        subject.clean(arena);

        verify(block1).setType(Material.AIR);
        verify(block2).setType(Material.AIR);
        verify(block3).setType(Material.AIR);
    }

}
