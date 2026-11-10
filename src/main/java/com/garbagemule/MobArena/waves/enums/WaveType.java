package com.garbagemule.MobArena.waves.enums;

import com.garbagemule.MobArena.Msg;
import com.garbagemule.MobArena.framework.Arena;

public enum WaveType
{
    DEFAULT {
        @Override
        public void announce(Arena arena, int wave) {
            arena.announce(Msg.WAVE_DEFAULT, "" + wave);
        }
    },

    SPECIAL {
        @Override
        public void announce(Arena arena, int wave) {
            arena.announce(Msg.WAVE_SPECIAL, "" + wave);
        }
    },

    SWARM {
        @Override
        public void announce(Arena arena, int wave) {
            arena.announce(Msg.WAVE_SWARM, "" + wave);
        }
    },

    SUPPLY {
        @Override
        public void announce(Arena arena, int wave) {
            arena.announce(Msg.WAVE_SUPPLY, "" + wave);
        }
    },

    BOSS {
        @Override
        public void announce(Arena arena, int wave) {
            arena.announce(Msg.WAVE_BOSS, "" + wave);
        }
    },

    UPGRADE {
        @Override
        public void announce(Arena arena, int wave) {
            arena.announce(Msg.WAVE_UPGRADE, "" + wave);
        }
    },

    COMBO {
        @Override
        public void announce(Arena arena, int wave) {
            // announce waveA in MASpawnThread.handleWave(Wave, int, boolean)
        }
    };

    public abstract void announce(Arena arena, int wave);

    public static WaveType fromString(String string) {
        try {
            return WaveType.valueOf(string.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
