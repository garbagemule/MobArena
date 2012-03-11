package com.garbagemule.MobArena.waves;

import java.util.SortedSet;
import java.util.TreeSet;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.util.config.Config;
import com.garbagemule.MobArena.util.config.ConfigSection;
import com.garbagemule.MobArena.waves.enums.*;

public class WaveManager
{
    private Arena arena;
    private Config config;

    private Wave defaultWave, currentWave;
    private TreeSet<Wave> recurrentWaves, singleWaves, singleWavesInstance;
    
    private int wave, finalWave;
    
    public WaveManager(Arena arena, Config config) {
        this.arena     = arena;
        this.config    = config;
        this.wave      = 0;
        this.finalWave = 0;
        
        reloadWaves();
    }
    
    public void reload() {
        config.load();
        reloadWaves();
    }
    
    public void save() {
        config.save();
    }
    
    public TreeSet<Wave> getRecurrentWaves() {
        return recurrentWaves;
    }
    
    public void reset() {
        reloadWaves();
        wave = 0;
        singleWavesInstance = new TreeSet<Wave>(singleWaves);
    }
    
    public void reloadWaves() {
        ConfigSection rConfig = config.getConfigSection("arenas." + arena.configName() + ".waves.recurrent");
        ConfigSection sConfig = config.getConfigSection("arenas." + arena.configName() + ".waves.single");
        
        recurrentWaves = WaveParser.parseWaves(arena, rConfig, WaveBranch.RECURRENT);
        singleWaves    = WaveParser.parseWaves(arena, sConfig, WaveBranch.SINGLE);
        
        finalWave = config.getInt("arenas." + arena.configName() + ".settings.final-wave", 0);
        
        if (recurrentWaves.isEmpty()) {
            Messenger.warning(WaveError.NO_RECURRENT_WAVES.format(arena.configName()));
            
            Wave def = WaveParser.createDefaultWave();
            recurrentWaves.add(def);
        }
        
        defaultWave = recurrentWaves.first();
    }
    
    /**
     * Increment the wave number and get the next Wave to be spawned.
     * Note that this method is a mutator. 
     * @return the next Wave
     */
    public Wave next() {
        wave++;
        
        if (!singleWavesInstance.isEmpty() && singleWavesInstance.first().matches(wave)) {
            currentWave = singleWavesInstance.pollFirst();
        }
        else {
            SortedSet<Wave> matches = getMatchingRecurrentWaves(wave);
            currentWave = (matches.isEmpty() ? defaultWave : matches.last());
        }
        
        return currentWave;
    }
    
    /**
     * Get the next Wave to be spawned. This is an accessor and does not
     * advance the "counter". Note that the Wave objects, however, are
     * mutable.
     * @return the next Wave
     */
    public Wave getNext() {
        int next = wave + 1;
        
        if (!singleWavesInstance.isEmpty() && singleWavesInstance.first().matches(next)) {
            return singleWavesInstance.first();
        }
        
        SortedSet<Wave> matches = getMatchingRecurrentWaves(wave);
        return (matches.isEmpty() ? defaultWave : matches.last()); 
    }
    
    /**
     * Get the current wave that's being used.
     * Note that the current wave might not have spawned yet.
     * @return the current wave
     */
    public Wave getCurrent() {
        return currentWave;
    }
    
    /**
     * Get the current wave number.
     * @return the current wave number
     */
    public int getWaveNumber() {
        return wave;
    }
    
    /**
     * Get the final wave number.
     * @return the final wave number
     */
    public int getFinalWave() {
        return finalWave;
    }
    
    private SortedSet<Wave> getMatchingRecurrentWaves(int wave) {
        TreeSet<Wave> result = new TreeSet<Wave>(WaveUtils.getRecurrentComparator());
        for (Wave w : recurrentWaves) {
            if (w.matches(wave)) {
                result.add(w);
            }
        }
        return result;
    }
}
