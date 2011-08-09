package com.garbagemule.MobArena;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class MAMessages
{
    public static enum Msg
    {
        ARENA_START("Let the slaughter begin!"),
        ARENA_END("Arena finished."),
        ARENA_DOES_NOT_EXIST("That arena does not exist. Type /ma arenas for a list."),
        JOIN_NOT_ENABLED("MobArena is not enabled."),
        JOIN_IN_OTHER_ARENA("You are already in an arena! Leave that one first."),
        JOIN_ARENA_NOT_ENABLED("This arena is not enabled."),
        JOIN_ARENA_NOT_SETUP("This arena has not been set up yet."),
        JOIN_ARENA_EDIT_MODE("This arena is in edit mode."),
        JOIN_ARENA_PERMISSION("You don't have permission to join this arena."),
        JOIN_FEE_REQUIRED("Insufficient funds. Price: %"),
        JOIN_FEE_PAID("Price to join was: %"),
        JOIN_ARENA_IS_RUNNING("This arena is in already progress."),
        JOIN_ALREADY_PLAYING("You are already playing!"),
        JOIN_ARG_NEEDED("You must specify an arena. Type /ma arenas for a list."),
        JOIN_TOO_FAR("You are too far away from the arena to join/spectate."),
        JOIN_EMPTY_INV("You must empty your inventory to join the arena."),
        JOIN_PLAYER_LIMIT_REACHED("The player limit of this arena has been reached."),
        JOIN_STORE_INV_FAIL("Failed to store inventory. Try again."),
        JOIN_PLAYER_JOINED("You joined the arena. Have fun!"),
        LEAVE_NOT_PLAYING("You are not in the arena."),
        LEAVE_PLAYER_LEFT("You left the arena. Thanks for playing!"),
        PLAYER_DIED("% died!"),
        SPEC_PLAYER_SPECTATE("Enjoy the show!"),
        SPEC_NOT_RUNNING("This arena isn't running."),
        SPEC_ARG_NEEDED("You must specify an arena. Type /ma arenas for a list."),
        SPEC_EMPTY_INV("Empty your inventory first!"),
        SPEC_ALREADY_PLAYING("Can't spectate when in the arena!"),
        NOT_READY_PLAYERS("Not ready: %"),
        FORCE_START_RUNNING("Arena has already started."),
        FORCE_START_NOT_READY("Can't force start, no players are ready."),
        FORCE_START_STARTED("Forced arena start."),
        FORCE_END_EMPTY("No one is in the arena."),
        FORCE_END_ENDED("Forced arena end."),
        FORCE_END_IDLE("You weren't quick enough!"),
        REWARDS_GIVE("Here are all of your rewards!"),
        LOBBY_DROP_ITEM("No sharing allowed at this time!"),
        LOBBY_PLAYER_READY("You have been flagged as ready!"),
        LOBBY_PICK_CLASS("You must first pick a class!"),
        LOBBY_NOT_ENOUGH_PLAYERS("Not enough players to start. Need at least % players."),
        LOBBY_RIGHT_CLICK("Punch the sign. Don't right-click."),
        LOBBY_CLASS_PICKED("You have chosen % as your class!"),
        LOBBY_CLASS_RANDOM("You will get a random class on arena start."),
        LOBBY_CLASS_PERMISSION("You don't have permission to use this class!"),
        WARP_TO_ARENA("Can't warp to the arena during battle!"),
        WARP_FROM_ARENA("Warping not allowed in the arena!"),
        WAVE_DEFAULT("Wave #%!"),
        WAVE_SPECIAL("Wave #%! [SPECIAL]"),
        WAVE_SWARM("Wave #%! [SWARM]"),
        WAVE_BOSS("Wave #%! [BOSS]"),
        WAVE_BOSS_ABILITY("Boss used ability: %!"),
        WAVE_REWARD("You just earned a reward: %"),
        MISC_LIST_PLAYERS("Live players: %"),
        MISC_LIST_ARENAS("Available arenas: %"),
        MISC_COMMAND_NOT_ALLOWED("You can't use that command in the arena!"),
        MISC_NO_ACCESS("You don't have access to this command."),
        MISC_NONE("<none>");
        
        private String msg;
        
        private Msg(String msg)
        {
            this.msg = msg;
        }
        
        public String get()
        {
            return msg;
        }
        
        public String get(String s)
        {
            return msg.replace("%", s);
        }
        
        public static String get(Msg m)
        {
            return m.msg;
        }
        
        public static String get(Msg m, String s)
        {
            return m.msg.replace("%", s);   
        }
        
        public void set(String msg)
        {
            this.msg = msg;
        }
        
        public static void set(Msg m, String msg)
        {
            m.msg = msg;
        }
    }
    
    /**
     * Initializes the msgMap by reading from the announcements-file.
     */
    public static void init(MobArena plugin)
    {        
        // Grab the announcements-file.
        File msgFile = new File(MobArena.dir, "announcements.properties");
        
        // If the file doesn't exist, create it and use defaults.
        if (!msgFile.exists())
        {
            try
            {
                msgFile.createNewFile();
                BufferedWriter bw = new BufferedWriter(new FileWriter(msgFile));
                
                for (Msg m : Msg.values())
                {
                    bw.write(m + "=" + m.msg);
                    bw.newLine();
                }
                
                bw.close();
                return;
            }
            catch (Exception e)
            {
                MobArena.warning("Couldn't initialize announcements-file. Using defaults.");
            }
            
            return;
        }
        
        // Otherwise, read the file's contents.
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
            MobArena.warning("Problem with announcements-file. Using defaults.");
            return;
        }
    }
    
    /**
     * Helper-method for parsing the strings from the
     * announcements-file.
     */
    private static void process(String s)
    {
        // If the line ends with =, just add a space
        if (s.endsWith("=")) s += " ";
        
        // Split the string by the equals-sign.
        String[] split = s.split("=");
        if (split.length != 2)
        {
            MobArena.warning("Couldn't parse \"" + s + "\". Check announcements-file.");
            return;
        }
        
        // For simplicity...
        String key = split[0];
        String val = split[1];
        
        try
        {
            Msg msg = Msg.valueOf(key);
            msg.set(val);
        }
        catch (Exception e)
        {
            MobArena.warning(key + " is not a valid key. Check announcements-file.");
            return;
        }
    }
}