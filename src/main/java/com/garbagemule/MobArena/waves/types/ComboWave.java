package com.garbagemule.MobArena.waves.types;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.AbstractWave;
import com.garbagemule.MobArena.waves.MACreature;
import com.garbagemule.MobArena.waves.Wave;
import com.garbagemule.MobArena.waves.WaveUtils;
import com.garbagemule.MobArena.waves.enums.WaveType;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComboWave extends AbstractWave {

    private final Wave waveA;
    private final Wave waveB;

    public ComboWave(Wave waveA, Wave waveB) {
        this.waveA = waveA;
        this.waveB = waveB;
        this.setType(WaveType.COMBO);
    }

    public Wave getWaveA() {
        return waveA;
    }

    public Wave getWaveB() {
        return waveB;
    }

    @Override
    public Map<MACreature, Integer> getMonstersToSpawn(int wave, int playerCount, Arena arena) {
        Map<MACreature, Integer> monstersA = this.waveA.getMonstersToSpawn(wave, playerCount, arena);
        Map<MACreature, Integer> monstersB = this.waveB.getMonstersToSpawn(wave, playerCount, arena);

        Map<MACreature, Integer> monstersCombined = new HashMap<>();
        monstersCombined.putAll(monstersA);
        monstersCombined.putAll(monstersB);

        return monstersCombined;
    }

    public List<Location> getSpawnpoints(Arena arena) {
        List<Location> spawnpointsA = waveA.getSpawnpoints(arena);
        List<Location> spawnpointsB = waveB.getSpawnpoints(arena);

        List<Location> spawnpointsCombined = super.getSpawnpoints(arena);
        spawnpointsCombined.addAll(spawnpointsA);
        spawnpointsCombined.addAll(spawnpointsB);

        return WaveUtils.getValidSpawnpoints(arena, spawnpointsCombined, arena.getPlayersInArena());
    }

    @Override
    public Wave copy() {
        return new ComboWave(getWaveA(), getWaveB());
    }
}
