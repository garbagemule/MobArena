package com.garbagemule.MobArena;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class MAMessages
{
    public static void init(MobArena plugin) {
        // Grab the file
        File msgFile = new File(MobArena.dir, "anouncements.properties");
        
        // If it couldn't be loaded for some reason
        if (!load(msgFile))
            return;
        
        // Otherwise, start parsing!
        parseFile(msgFile);
    }
    
    private static boolean load(File file) {
        // If the file exists, continue on!
        if (file.exists()) {
            return true;
        }
        
        // Otherwise, create it, and populate it with the defaults.
        try {
            file.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            
            for (Msg m : Msg.values()) {
                if (m.hasSpoutMsg()) {
                    bw.write(m.name() + "=" + m + "|" + m.toSpoutString());
                } else {
                    bw.write(m.name() + "=" + m);
                }
                bw.newLine();
            }
            bw.close();
            
            return true;
        }
        catch (Exception e) {
            MobArena.warning("Couldn't initialize announcements-file. Using defaults.");
            return false;
        }
    }
    
    private static void parseFile(File file) {
        try {
            FileInputStream   fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            BufferedReader    br  = new BufferedReader(isr);
        
            // Check for BOM character.
            br.mark(1);
            int bom = br.read();
            if (bom != 65279)
                br.reset();
            
            String s;
            while ((s = br.readLine()) != null)
                process(s);
            
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            MobArena.warning("Problem with announcements-file. Using defaults.");
            return;
        }
    }
    
    /**
     * Helper-method for parsing the strings from the
     * announcements-file.
     */
    private static void process(String s) {
        // If the line ends with = or |, just add a space
        if (s.endsWith("=") || s.endsWith("|")) s += " ";
        
        // Split the string by the equals-sign.
        String[] split = s.split("=");
        if (split.length != 2) {
            MobArena.warning("Couldn't parse \"" + s + "\". Check announcements-file.");
            return;
        }
        
        // Split the value by the pipe-sign.
        String[] vals = split[1].split("\\|");
        
        // For simplicity...
        String key = split[0];
        String val = vals.length == 2 ? vals[0] : split[1];
        String spoutVal = vals.length == 2 ? vals[1] : null;
        
        try {
            Msg msg = Msg.valueOf(key);
            msg.set(val);
            msg.setSpout(spoutVal);
        }
        catch (Exception e) {
            MobArena.warning(key + " is not a valid key. Check announcements-file.");
            return;
        }
    }
}