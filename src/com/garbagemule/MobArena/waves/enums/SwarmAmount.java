package com.garbagemule.MobArena.waves.enums;

import com.garbagemule.MobArena.waves.WaveUtils;

public enum SwarmAmount
{
    LOW(10), MEDIUM(20), HIGH(30), PSYCHO(60);
    private int multiplier;
    
    private SwarmAmount(int multiplier) {
        this.multiplier = multiplier;
    }
    
    public int getAmount(int playerCount) {
        return Math.max(1, playerCount / 2) * multiplier;
    }
    
    public static SwarmAmount fromString(String string) {
        return WaveUtils.getEnumFromString(SwarmAmount.class, string, LOW);
    }
}