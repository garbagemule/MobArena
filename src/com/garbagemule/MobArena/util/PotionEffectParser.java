package com.garbagemule.MobArena.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.garbagemule.MobArena.Messenger;

public class PotionEffectParser
{
    private static final int TICKS_PER_SECOND = 20;
    private static final int DEFAULT_POTION_DURATION = 1200;
    private static final int DEFAULT_POTION_AMPLIFIER = 0;
    
    public static List<PotionEffect> parsePotionEffects(String s) {
        if (s == null || s.isEmpty())
            return null;
        
        List<PotionEffect> potions = new ArrayList<PotionEffect>();
        for (String potion : s.split(",")) {
            PotionEffect eff = parsePotionEffect(potion);
            if (eff != null) {
                potions.add(eff);
            }
        }
        
        return potions;
    }
    
    public static PotionEffect parsePotionEffect(String p) {
        if (p == null || p.isEmpty())
            return null;
        
        String[] parts = p.split(":");
        PotionEffect result = null;
        
        switch (parts.length) {
            case 1:
                result = parseSingle(parts[0]);
                break;
            case 2:
                result = withDuration(parts[0], parts[1]);
                break;
            case 3:
                result = withDurationAndAmplifier(parts[0], parts[1], parts[2]);
                break;
        }
        
        if (result == null) {
            Messenger.warning("Failed to parse potion effect: " + p);
            return null;
        }
        
        return result;
    }
    
    private static PotionEffect parseSingle(String type) {
        PotionEffectType effect = getType(type);
        
        if (effect == null) {
            return null;
        } else {
            return new PotionEffect(effect, DEFAULT_POTION_DURATION, DEFAULT_POTION_AMPLIFIER);
        }
    }
    
    private static PotionEffect withDuration(String type, String duration) {
        PotionEffectType effect = getType(type);
        int dur = getDuration(duration);
        
        if (effect == null || dur == -1) {
            return null;
        } else {
            return new PotionEffect(effect, dur * TICKS_PER_SECOND, DEFAULT_POTION_AMPLIFIER);
        }
    }
    
    private static PotionEffect withDurationAndAmplifier(String type, String duration, String amplifier) {
        PotionEffectType effect = getType(type);
        int dur = getDuration(duration);
        int amp = getAmplification(amplifier);
        
        if (effect == null || dur == -1 || amp == -1) {
            return null;
        } else {
            return new PotionEffect(effect, dur * TICKS_PER_SECOND, amp);
        }
    }
    
    private static PotionEffectType getType(String type) {
        PotionEffectType effect = null;
        
        if (type.matches("[0-9]*")) {
            effect = PotionEffectType.getById(Integer.parseInt(type));
        } else {
            effect = PotionEffectType.getByName(type);
        }
        
        return effect;
    }
    
    private static int getDuration(String duration) {
        int dur = -1;
        
        if (duration.matches("[0-9]*")) {
            dur = Integer.parseInt(duration);
        }
        
        return dur;
    }
    
    private static int getAmplification(String amplifier) {
        int amp = -1;
        
        if (amplifier.matches("[0-9]*")) {
            amp = Integer.parseInt(amplifier);
        }
        
        return amp;
    }
}
