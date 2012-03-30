package com.garbagemule.MobArena.waves.enums;

import com.garbagemule.MobArena.waves.WaveUtils;

public enum BossHealth
{
    VERYLOW(4), LOW(8), MEDIUM(15), HIGH(25), VERYHIGH(40), PSYCHO(60);
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