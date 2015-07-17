package com.garbagemule.MobArena.waves.enums;

import com.garbagemule.MobArena.waves.Wave;

public enum WaveBranch
{
    SINGLE {
        @Override
        public boolean matches(int wave, Wave w) {
            return (w.getFirstWave() == wave);
        }
    },
    
    RECURRENT {
        @Override
        public boolean matches(int wave, Wave w) {
            if (wave < w.getFirstWave()) {
                return false;
            }
            return ((wave - w.getFirstWave()) % w.getFrequency() == 0);
        }
    };
    
    public abstract boolean matches(int wave, Wave w);
}