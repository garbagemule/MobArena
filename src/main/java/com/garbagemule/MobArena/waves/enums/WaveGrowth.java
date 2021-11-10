package com.garbagemule.MobArena.waves.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum WaveGrowth
{
    OLD(0), SLOW(0.5), MEDIUM(0.65), FAST(0.8), PSYCHO(1.2);
    private final double exp;

    public int getAmount(int wave, int playerCount) {
        if (this == OLD) return wave + playerCount;

        double pc = (double) playerCount;
        double w  = (double) wave;

        double base = Math.min(Math.ceil(pc/2) + 1, 13);
        return (int) ( base * Math.pow(w, exp) );
    }
}
