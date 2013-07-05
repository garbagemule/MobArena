package com.garbagemule.MobArena.region;

public enum RegionPoint {
    P1,
    P2,
    L1,
    L2,
    ARENA,
    LOBBY,
    SPECTATOR,
    LEADERBOARD;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
