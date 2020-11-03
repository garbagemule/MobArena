package com.garbagemule.MobArena.waves.types;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.things.Thing;
import com.garbagemule.MobArena.waves.AbstractWave;
import com.garbagemule.MobArena.waves.MACreature;
import com.garbagemule.MobArena.waves.Wave;
import com.garbagemule.MobArena.waves.enums.WaveType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpgradeWave extends AbstractWave
{
    private Map<String,List<Thing>> upgrades;

    public UpgradeWave(Map<String,List<Thing>> upgrades) {
        this.upgrades = upgrades;
        this.setType(WaveType.UPGRADE);
    }

    @Override
    public Map<MACreature,Integer> getMonstersToSpawn(int wave, int playerCount, Arena arena) {
        return new HashMap<>();
    }

    public void grantItems(Player p, String slug) {
        List<Thing> list = upgrades.get(slug);
        if (list == null) return;

        list.forEach(thing -> thing.giveTo(p));
    }

    public Wave copy() {
        Map<String,List<Thing>> upgrades = new HashMap<>();
        for (Map.Entry<String,List<Thing>> entry : this.upgrades.entrySet()) {
            upgrades.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        UpgradeWave result = new UpgradeWave(upgrades);

        // From AbstractWave
        result.setAmountMultiplier(getAmountMultiplier());
        result.setHealthMultiplier(getHealthMultiplier());
        result.setName(getName());
        result.setSpawnpoints(getSpawnpoints());
        result.setEffects(getEffects());
        return result;
    }
}
