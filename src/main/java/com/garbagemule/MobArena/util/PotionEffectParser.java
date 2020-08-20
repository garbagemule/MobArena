package com.garbagemule.MobArena.util;

import org.bukkit.Bukkit;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class PotionEffectParser
{
    private static final int TICKS_PER_SECOND = 20;
    private static final int DEFAULT_POTION_AMPLIFIER = 0;
    private static final int DEFAULT_POTION_DURATION = Integer.MAX_VALUE;

    public static List<PotionEffect> parsePotionEffects(String s) {
        if (s == null || s.isEmpty())
            return null;

        List<PotionEffect> potions = new ArrayList<>();
        for (String potion : s.split(",")) {
            PotionEffect eff = parsePotionEffect(potion.trim());
            if (eff != null) {
                potions.add(eff);
            }
        }

        return potions;
    }

    public static PotionEffect parsePotionEffect(String p) {
        return parsePotionEffect(p, true);
    }

    public static PotionEffect parsePotionEffect(String p, boolean logFailure) {
        if (p == null || p.isEmpty())
            return null;

        String[] parts = p.split(":");
        PotionEffect result = null;

        switch (parts.length) {
            case 1:
                result = parseSingle(parts[0]);
                break;
            case 2:
                result = withAmplifier(parts[0], parts[1]);
                break;
            case 3:
                result = withAmplifierAndDuration(parts[0], parts[1], parts[2]);
                break;
        }

        if (result == null) {
            if (logFailure) {
                Bukkit.getLogger().warning("[MobArena] Failed to parse potion effect: " + p);
            }
            return null;
        }

        return result;
    }

    private static PotionEffect parseSingle(String type) {
        PotionEffectType effect = PotionEffectType.getByName(type);

        if (effect == null) {
            return null;
        } else {
            return new PotionEffect(effect, DEFAULT_POTION_DURATION, DEFAULT_POTION_AMPLIFIER);
        }
    }

    private static PotionEffect withAmplifier(String type, String amplifier) {
        PotionEffectType effect = PotionEffectType.getByName(type);
        int amp = getAmplification(amplifier);

        if (effect == null || amp == -1) {
            return null;
        } else {
            return new PotionEffect(effect, DEFAULT_POTION_DURATION, amp);
        }
    }

    private static PotionEffect withAmplifierAndDuration(String type, String amplifier, String duration) {
        PotionEffectType effect = PotionEffectType.getByName(type);
        int amp = getAmplification(amplifier);
        int dur = getDuration(duration);

        if (effect == null || dur == -1 || amp == -1) {
            return null;
        } else {
            return new PotionEffect(effect, dur * TICKS_PER_SECOND, amp);
        }
    }

    private static int getDuration(String duration) {
        int dur = -1;

        if (duration.matches("[0-9]+")) {
            dur = Integer.parseInt(duration);
        }

        return dur;
    }

    private static int getAmplification(String amplifier) {
        int amp = -1;

        if (amplifier.matches("[0-9]+")) {
            amp = Integer.parseInt(amplifier);
        }

        return amp;
    }
}
