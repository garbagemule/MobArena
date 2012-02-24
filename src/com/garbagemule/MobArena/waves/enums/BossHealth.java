package com.garbagemule.MobArena.waves.enums;

import com.garbagemule.MobArena.waves.WaveUtils;

public enum BossHealth
{
    LOW(8), MEDIUM(15), HIGH(25), PSYCHO(40);
    private int multiplier;
    
    private BossHealth(int multiplier) {
        this.multiplier = multiplier;
    }
    
    public int getMax(int playerCount) {
        return (playerCount + 1) * 20 * multiplier;
    }
    
    public static BossHealth fromString(String string) {
        return WaveUtils.getEnumFromString(BossHealth.class, string, MEDIUM);
    }
}