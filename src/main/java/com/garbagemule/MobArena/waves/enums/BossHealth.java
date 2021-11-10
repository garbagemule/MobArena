package com.garbagemule.MobArena.waves.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BossHealth
{
    VERYLOW(4), LOW(8), MEDIUM(15), HIGH(25), VERYHIGH(40), PSYCHO(60);
    private final int multiplier;

    public int getMax(int playerCount) {
        return (playerCount + 1) * 20 * multiplier;
    }

    public int getMultiplier() {
        return multiplier;
    }
}
