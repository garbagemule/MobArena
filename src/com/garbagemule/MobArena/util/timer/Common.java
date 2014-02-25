package com.garbagemule.MobArena.util.timer;

public class Common {
    /**
     * Convert seconds to ticks.
     *
     * @param seconds value to convert
     * @return the value converted to ticks
     */
    public static long toTicks(int seconds) {
        return seconds * 20l;
    }

    /**
     * Convert ticks to seconds.
     *
     * @param ticks value to convert
     * @return the value converted to seconds
     */
    public static int toSeconds(long ticks) {
        return (int) (ticks / 20l);
    }
}
