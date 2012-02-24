package com.garbagemule.MobArena.util;

public class TextUtils
{
    /**
     * Add character padding on the right side of a String.
     * @param s String to add padding to
     * @param length Total amount of characters in the returned String
     * @param pad The padding character
     * @return A padded String with the input length
     */
    public static String padRight(String s, int length, char pad)
    {
        StringBuffer buffy = new StringBuffer();
        buffy.append(s);
        for (int i = s.length(); i < length; i++)
            buffy.append(pad);
        return buffy.toString();
    }
    public static String padRight(String s, int length) { return padRight(s, length, ' '); }
    public static String padRight(int s, int length) { return padRight(Integer.toString(s), length, ' '); }
    public static String padRight(double s, int length) { return padRight(Double.toString(s), length, ' '); }

    /**
     * Add character padding on the left side of a String.
     * @param s String to add padding to
     * @param length Total amount of characters in the returned String
     * @param pad The padding character
     * @return A padded String with the input length
     */
    public static String padLeft(String s, int length, char pad)
    {
        StringBuffer buffy = new StringBuffer();
        for (int i = 0; i < length - s.length(); i++)
            buffy.append(pad);
        buffy.append(s);
        return buffy.toString();
    }
    public static String padLeft(String s, int length) { return padLeft(s, length, ' '); }
    public static String padLeft(int s, int length) { return padLeft(Integer.toString(s), length, ' '); }
    public static String padLeft(double s, int length) { return padLeft(Double.toString(s), length, ' '); }
    
    /**
     * Truncate the input string to be at most the input length
     * @param s The string to truncate
     * @param length The maximum length
     * @return A truncated string with length 15, or the input string
     */
    public static String truncate(String s, int length)
    {
        if (s.length() <= length)
            return s;
        return s.substring(0, length);
    }
    public static String truncate(String s) { return truncate(s, 15); }
    
    public static String camelCase(String s) {
        if (s == null || s.length() < 2)
            return null;
        
        String firstLetter = s.substring(0,1).toUpperCase();
        return firstLetter + s.substring(1).toLowerCase();
    }
}
