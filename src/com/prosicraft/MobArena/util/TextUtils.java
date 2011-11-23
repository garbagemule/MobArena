package com.prosicraft.MobArena.util;

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
}
