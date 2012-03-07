package com.garbagemule.MobArena.util;

import java.util.Date;

public class TimeUtils
{
    /**
     * Turn the input long into a string on the form (D:)HH:MM:SS, where the
     * day-part is only added if the number of days is greater than or equal
     * to 1, i.e. a long value of 86,399,999.
     * @param ms time in milliseconds
     * @return string-representation of the input long
     */
    public static String toTime(long ms) {
        long total  = ms / 1000;
        long secs   = total % 60;
        long mins   = total % 3600 / 60;
        long hours  = total / 3600 % 24;
        long days   = total / 3600 / 24;
        String time = (days  >  0 ? days + ":"  : "") + 
                      (hours < 10 ? "0" + hours : hours) + ":" +
                      (mins  < 10 ? "0" + mins  : mins)  + ":" +
                      (secs  < 10 ? "0" + secs  : secs);
        return time;
    }
    
    /**
     * Makes a new java.util.Date with the input long and toString()s it. 
     * @param ms time in milliseconds
     * @return java.util.Date toString() of the input long
     */
    public static String toDateTime(long ms) {
        return new Date(ms).toString();
    }
    
    /**
     * Adds two string-representations of time and returns the resulting time.
     * @param t1 a time-string
     * @param t2 another time-string
     * @return the sum of the time-strings
     */
    public static String addTimes(String t1, String t2) {
        String[] parts1 = t1.split(":");
        String[] parts2 = t2.split(":");
        
        long secs1 = extractSeconds(parts1);
        long secs2 = extractSeconds(parts2);

        long mins1 = extractMinutes(parts1);
        long mins2 = extractMinutes(parts2);

        long hours1 = extractHours(parts1);
        long hours2 = extractHours(parts2);

        long days1 = extractDays(parts1);
        long days2 = extractDays(parts2);
        
        long time = (secs1 + secs2 + mins1 + mins2 + hours1 + hours2 + days1 + days2) * 1000;
        
        return toTime(time);
    }
    
    private static long extractSeconds(String[] parts) {
        int length = parts.length;
        if (length < 1) {
            return 0L;
        }
        return Long.parseLong(parts[length - 1]);
    }
    
    private static long extractMinutes(String[] parts) {
        int length = parts.length;
        if (length < 2) {
            return 0L;
        }
        return Long.parseLong(parts[length - 2]) * 60;
    }
    
    private static long extractHours(String[] parts) {
        int length = parts.length;
        if (length < 3) {
            return 0L;
        }
        return Long.parseLong(parts[length - 3]) * 3600;
    }
    
    private static long extractDays(String[] parts) {
        int length = parts.length;
        if (length < 4) {
            return 0L;
        }
        return Long.parseLong(parts[length - 4]) * 24 * 3600;
    }
}
