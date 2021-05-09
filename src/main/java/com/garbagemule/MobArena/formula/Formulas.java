package com.garbagemule.MobArena.formula;

public class Formulas {

    /**
     * Default "old" style wave growth. Equivalent to the formula:
     * <pre>{@code <initial-players> + <current-wave>}</pre>
     */
    public static final Formula DEFAULT_WAVE_GROWTH = (arena) -> {
        int players = arena.getPlayerCount();
        int wave = arena.getWaveManager().getWaveNumber();
        return players + wave;
    };

    /**
     * Default "low" swarm amount. Equivalent to the formula:
     * <pre>{@code max(1, <initial-players> / 2) * 10}</pre>
     */
    public static final Formula DEFAULT_SWARM_AMOUNT = (arena) -> {
        int players = arena.getPlayerCount();
        return Math.max(1, players / 2) * 10;
    };

    /**
     * Default "medium" boss health. Equivalent to the formula:
     * <pre>{@code (<initial-players> + 1) 20 * 15}</pre>
     */
    public static final Formula DEFAULT_BOSS_HEALTH = (arena) -> {
        int players = arena.getPlayerCount();
        return (players + 1) * 20 * 8;
    };

    private Formulas() {
        // OK BOSS
    }

}
