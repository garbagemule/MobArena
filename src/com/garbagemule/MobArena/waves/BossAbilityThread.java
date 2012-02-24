package com.garbagemule.MobArena.waves;

import java.util.List;
import java.util.Set;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.types.BossWave;

public class BossAbilityThread implements Runnable
{
    private BossWave wave;
    private List<BossAbility> abilities;
    private Arena arena;
    private int counter;
    
    public BossAbilityThread(BossWave wave, List<BossAbility> abilities, Arena arena) {
        this.wave      = wave;
        this.abilities = abilities;
        this.arena     = arena;
        this.counter   = 0;
    }
    
    @Override
    public void run() {
        // If we have no abilities, we can't execute any, so just quit.
        if (abilities.isEmpty()) {
            return;
        }
        
        // If the arena isn't running or has no players, quit.
        if (!arena.isRunning() || arena.getPlayersInArena().isEmpty()) {
            return;
        }
        
        // If all bosses are dead, quit.
        Set<MABoss> bosses = wave.getMABosses();
        if (bosses.isEmpty()) {
            return;
        }
        
        // Get the next ability in the list.
        BossAbility ability = abilities.get(counter++ % abilities.size());
        
        // And make each boss in this boss wave use it!
        for (MABoss boss : bosses) {
            ability.run(arena, boss.getEntity());
        }
        
        // Schedule for another run!
        wave.scheduleTask(arena, this, wave.getAbilityInterval());
    }
}
