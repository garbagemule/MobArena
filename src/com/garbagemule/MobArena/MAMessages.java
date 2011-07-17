package com.garbagemule.MobArena;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class MAMessages
{
    protected static Map<Msg,String> msgMap;
    private static Map<Msg,String> defaults = new HashMap<Msg,String>();
    protected static enum Msg
    {
        ARENA_START,
        ARENA_END,
        ARENA_DOES_NOT_EXIST,
        JOIN_PLAYER_JOINED,
        JOIN_NOT_ENABLED,
        JOIN_IN_OTHER_ARENA,
        JOIN_ARENA_NOT_ENABLED,
        JOIN_ARENA_NOT_SETUP,
        JOIN_ARENA_PERMISSION,
        JOIN_FEE_REQUIRED,
        JOIN_FEE_PAID,
        JOIN_ARENA_IS_RUNNING,
        JOIN_ALREADY_PLAYING,
        JOIN_ARG_NEEDED,
        JOIN_TOO_FAR,
        JOIN_EMPTY_INV,
        JOIN_PLAYER_LIMIT_REACHED,
        JOIN_STORE_INV_FAIL,
        LEAVE_PLAYER_LEFT,
        LEAVE_NOT_PLAYING,
        PLAYER_DIED,
        SPEC_PLAYER_SPECTATE,
        SPEC_NOT_RUNNING,
        SPEC_ARG_NEEDED,
        SPEC_EMPTY_INV,
        SPEC_ALREADY_PLAYING,
        NOT_READY_PLAYERS,
        FORCE_START_STARTED,
        FORCE_START_RUNNING,
        FORCE_START_NOT_READY,
        FORCE_END_ENDED,
        FORCE_END_EMPTY,
        FORCE_END_IDLE,
        REWARDS_GIVE,
        LOBBY_CLASS_PICKED,
        LOBBY_CLASS_RANDOM,
        LOBBY_CLASS_PERMISSION,
        LOBBY_PLAYER_READY,
        LOBBY_DROP_ITEM,
        LOBBY_PICK_CLASS,
        LOBBY_RIGHT_CLICK,
        WARP_TO_ARENA,
        WARP_FROM_ARENA,
        WAVE_DEFAULT,
        WAVE_SPECIAL,
        WAVE_REWARD,
        MISC_LIST_ARENAS,
        MISC_LIST_PLAYERS,
        MISC_COMMAND_NOT_ALLOWED,
        MISC_NO_ACCESS,
        MISC_NONE
    }
    
    // Populate the defaults map.
    static
    {
        defaults.put(Msg.ARENA_START, "Let the slaughter begin!");
        defaults.put(Msg.ARENA_END, "Arena finished.");
        defaults.put(Msg.ARENA_DOES_NOT_EXIST, "That arena does not exist. Type /ma arenas for a list.");
        defaults.put(Msg.JOIN_NOT_ENABLED, "MobArena is not enabled.");
        defaults.put(Msg.JOIN_IN_OTHER_ARENA, "You are already in an arena! Leave that one first.");
        defaults.put(Msg.JOIN_ARENA_NOT_ENABLED, "This arena is not enabled.");
        defaults.put(Msg.JOIN_ARENA_NOT_SETUP, "This arena has not been set up yet.");
        defaults.put(Msg.JOIN_ARENA_PERMISSION, "You don't have permission to join this arena.");
        defaults.put(Msg.JOIN_FEE_REQUIRED, "Insufficient funds. Price: %");
        defaults.put(Msg.JOIN_FEE_PAID, "Price to join was: %");
        defaults.put(Msg.JOIN_ARENA_IS_RUNNING, "This arena is in already progress.");
        defaults.put(Msg.JOIN_ALREADY_PLAYING, "You are already playing!");
        defaults.put(Msg.JOIN_ARG_NEEDED, "You must specify an arena. Type /ma arenas for a list.");
        defaults.put(Msg.JOIN_TOO_FAR, "You are too far away from the arena to join/spectate.");
        defaults.put(Msg.JOIN_EMPTY_INV, "You must empty your inventory to join the arena.");
        defaults.put(Msg.JOIN_PLAYER_LIMIT_REACHED, "The player limit of this arena has been reached.");
        defaults.put(Msg.JOIN_STORE_INV_FAIL, "Failed to store inventory. Try again.");
        defaults.put(Msg.JOIN_PLAYER_JOINED, "You joined the arena. Have fun!");
        defaults.put(Msg.LEAVE_NOT_PLAYING, "You are not in the arena.");
        defaults.put(Msg.LEAVE_PLAYER_LEFT, "You left the arena. Thanks for playing!");
        defaults.put(Msg.PLAYER_DIED, "% died!");
        defaults.put(Msg.SPEC_PLAYER_SPECTATE, "Enjoy the show!");
        defaults.put(Msg.SPEC_NOT_RUNNING, "This arena isn't running.");
        defaults.put(Msg.SPEC_ARG_NEEDED, "You must specify an arena. Type /ma arenas for a list.");
        defaults.put(Msg.SPEC_EMPTY_INV, "Empty your inventory first!");
        defaults.put(Msg.SPEC_ALREADY_PLAYING, "Can't spectate when in the arena!");
        defaults.put(Msg.NOT_READY_PLAYERS, "Not ready: %");
        defaults.put(Msg.FORCE_START_RUNNING, "Arena has already started.");
        defaults.put(Msg.FORCE_START_NOT_READY, "Can't force start, no players are ready.");
        defaults.put(Msg.FORCE_START_STARTED, "Forced arena start.");
        defaults.put(Msg.FORCE_END_EMPTY, "No one is in the arena.");
        defaults.put(Msg.FORCE_END_ENDED, "Forced arena end.");
        defaults.put(Msg.FORCE_END_IDLE, "You weren't quick enough!");
        defaults.put(Msg.REWARDS_GIVE, "Here are all of your rewards!");
        defaults.put(Msg.LOBBY_DROP_ITEM, "No sharing allowed at this time!");
        defaults.put(Msg.LOBBY_PLAYER_READY, "You have been flagged as ready!");
        defaults.put(Msg.LOBBY_PICK_CLASS, "You must first pick a class!");
        defaults.put(Msg.LOBBY_RIGHT_CLICK, "Punch the sign. Don't right-click.");
        defaults.put(Msg.LOBBY_CLASS_PICKED, "You have chosen % as your class!");
        defaults.put(Msg.LOBBY_CLASS_RANDOM, "You will get a random class on arena start.");
        defaults.put(Msg.LOBBY_CLASS_PERMISSION, "You don't have permission to use this class!");
        defaults.put(Msg.WARP_TO_ARENA, "Can't warp to the arena during battle!");
        defaults.put(Msg.WARP_FROM_ARENA, "Warping not allowed in the arena!");
        defaults.put(Msg.WAVE_DEFAULT, "Get ready for wave #%!");
        defaults.put(Msg.WAVE_SPECIAL, "Get ready for wave #%! [SPECIAL]");
        defaults.put(Msg.WAVE_REWARD, "You just earned a reward: %");
        defaults.put(Msg.MISC_LIST_PLAYERS, "Live players: %");
        defaults.put(Msg.MISC_LIST_ARENAS, "Available arenas: %");
        defaults.put(Msg.MISC_COMMAND_NOT_ALLOWED, "You can't use that command in the arena!");
        defaults.put(Msg.MISC_NO_ACCESS, "You don't have access to this command.");
        defaults.put(Msg.MISC_NONE, "<none>");
    }  
    
    /**
     * Initializes the msgMap by reading from the announcements-file.
     */
    public static void init(MobArena plugin, boolean update)
    {
        // Use defaults in case of any errors.
        msgMap = defaults;
        
        // Grab the announcements-file.
        File msgFile;
        try
        {
            msgFile = new File(plugin.getDataFolder(), "announcements.properties");
            
            // If it doesn't exist, create it.
            if (!msgFile.exists())
            {
                System.out.println("[MobArena] Announcements-file not found. Creating one...");
                msgFile.createNewFile();
                
                BufferedWriter bw = new BufferedWriter(new FileWriter(msgFile));
                for (Msg m : Msg.values())
                {
                    bw.write(m.toString() + "=" + defaults.get(m));
                    bw.newLine();
                }
                bw.close();
                
                return;
            }
        }
        catch (Exception e)
        {
            System.out.println("[MobArena] ERROR! Couldn't initialize announcements-file. Using defaults.");
            return;
        }

        // If the file was found, populate the msgMap.
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(msgFile), "UTF-8"));
        
            // Check for BOM character.
            br.mark(1); int bom = br.read();
            if (bom != 65279) br.reset();
            
            String s;
            while ((s = br.readLine()) != null)
                process(s);
            
            br.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("[MobArena] ERROR! Problem with announcements-file. Using defaults.");
            return;
        }
    }
    
    public static void init(MobArena plugin)
    {
        init(plugin, false);
    }
    
    /**
     * Grabs the announcement from the msgMap, and in case of
     * s not being null, replaces the % with s.
     */
    public static String get(Msg msg, String s)
    {
        // If p is null, just return the announcement as is.
        if (s == null)
            return msgMap.get(msg);

        // Otherwise, replace the % with the input string.
        return msgMap.get(msg).replace("%", s);
    }
    
    /**
     * Grabs the announcement from the msgMap.
     */
    public static String get(Msg msg)
    {
        return get(msg, null);
    }
    
    /**
     * Helper-method for parsing the strings from the
     * announcements-file.
     */
    private static void process(String s)
    {
        // Split the string by the equals-sign.
        String[] split = s.split("=");
        if (split.length != 2)
        {
            System.out.println("[MobArena] ERROR! Couldn't parse \"" + s + "\". Check announcements-file.");
            return;
        }
        
        // For simplicity...
        String key = split[0];
        String val = split[1];
        Msg msg;
        
        try
        {
            msg = Msg.valueOf(key);
            msgMap.put(msg, val);
        }
        catch (Exception e)
        {
            System.out.println("[MobArena] ERROR! " + key + " is not a valid key. Check announcements-file.");
            return;
        }
    }
}