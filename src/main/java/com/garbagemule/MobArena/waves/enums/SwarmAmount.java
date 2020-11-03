package com.garbagemule.MobArena.waves.enums;

public enum SwarmAmount
{
    LOW(10), MEDIUM(20), HIGH(30), PSYCHO(60);
    private int multiplier;

    SwarmAmount(int multiplier) {
        this.multiplier = multiplier;
    }

    public int getAmount(int playerCount) {
        return Math.max(1, playerCount / 2) * multiplier;
    }
}
