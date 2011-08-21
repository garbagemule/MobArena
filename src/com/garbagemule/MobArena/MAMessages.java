package com.garbagemule.MobArena;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import org.bukkit.Material;

public class MAMessages
{
    public static enum Msg
    {
        ARENA_START("Let the slaughter begin!", "Arena started!", Material.REDSTONE_TORCH_ON),
        ARENA_END("Arena finished.", "Arena finished.", Material.REDSTONE_TORCH_OFF),
        ARENA_DOES_NOT_EXIST("That arena does not exist. Type /ma arenas for a list.", "Can't find arena."),
        JOIN_NOT_ENABLED("MobArena is not enabled.", "MobArena disabled.", Material.REDSTONE_TORCH_OFF),
        JOIN_IN_OTHER_ARENA("You are already in an arena! Leave that one first.", "In another arena."),
        JOIN_ARENA_NOT_ENABLED("This arena is not enabled.", "Arena disabled.", Material.REDSTONE_TORCH_OFF),
        JOIN_ARENA_NOT_SETUP("This arena has not been set up yet.", "Arena not set up.", Material.REDSTONE_TORCH_OFF),
        JOIN_ARENA_EDIT_MODE("This arena is in edit mode.", "Arena in edit mode.", Material.IRON_SPADE),
        JOIN_ARENA_PERMISSION("You don't have permission to join this arena.", "No permission!", Material.FENCE),
        JOIN_FEE_REQUIRED("Insufficient funds. Price: %", "Price: %", Material.DIAMOND),
        JOIN_FEE_PAID("Price to join was: %", "Paid: %", Material.DIAMOND),
        JOIN_ARENA_IS_RUNNING("This arena is in already progress.", "Already running!", Material.GOLD_RECORD),
        JOIN_ALREADY_PLAYING("You are already playing!", "Already playing!", Material.GOLD_RECORD),
        JOIN_ARG_NEEDED("You must specify an arena. Type /ma arenas for a list."),
        JOIN_TOO_FAR("You are too far away from the arena to join/spectate.", "Too far from arena.", Material.COMPASS),
        JOIN_EMPTY_INV("You must empty your inventory to join the arena.", "Empty your inventory.", Material.CHEST),
        JOIN_PLAYER_LIMIT_REACHED("The player limit of this arena has been reached.", "No spots left.", Material.MILK_BUCKET),
        JOIN_STORE_INV_FAIL("Failed to store inventory. Try again."),
        JOIN_EXISTING_INV_RESTORED("Your old inventory items have been restored."),
        JOIN_PLAYER_JOINED("You joined the arena. Have fun!", "Joined arena.", Material.IRON_SWORD),
        LEAVE_NOT_PLAYING("You are not in the arena.", "Not in arena."),
        LEAVE_PLAYER_LEFT("You left the arena. Thanks for playing!", "Left arena.", Material.WOOD_DOOR),
        PLAYER_DIED("% died!", "% died!", Material.BONE),
        SPEC_PLAYER_SPECTATE("Enjoy the show!", "Enjoy the show!"),
        SPEC_NOT_RUNNING("This arena isn't running.", "Arena not running.", Material.REDSTONE_TORCH_OFF),
        SPEC_ARG_NEEDED("You must specify an arena. Type /ma arenas for a list.", "Arena name required."),
        SPEC_EMPTY_INV("Empty your inventory first!", "Empty your inventory.", Material.CHEST),
        SPEC_ALREADY_PLAYING("Can't spectate when in the arena!", "Already playing!"),
        NOT_READY_PLAYERS("Not ready: %"),
        FORCE_START_RUNNING("Arena has already started."),
        FORCE_START_NOT_READY("Can't force start, no players are ready."),
        FORCE_START_STARTED("Forced arena start."),
        FORCE_END_EMPTY("No one is in the arena."),
        FORCE_END_ENDED("Forced arena end."),
        FORCE_END_IDLE("You weren't quick enough!"),
        REWARDS_GIVE("Here are all of your rewards!"),
        LOBBY_DROP_ITEM("No sharing allowed at this time!", "Can't drop items here."),
        LOBBY_PLAYER_READY("You have been flagged as ready!", "Flagged as ready!"),
        LOBBY_PICK_CLASS("You must first pick a class!", "Pick a class first!"),
        LOBBY_NOT_ENOUGH_PLAYERS("Not enough players to start. Need at least % players.", "Need more players."),
        LOBBY_RIGHT_CLICK("Punch the sign. Don't right-click.", "Punch the sign."),
        LOBBY_CLASS_PICKED("You have chosen % as your class!", "%"),
        LOBBY_CLASS_RANDOM("You will get a random class on arena start."),
        LOBBY_CLASS_PERMISSION("You don't have permission to use this class!", "No permission!", Material.FENCE),
        WARP_TO_ARENA("Can't warp to the arena during battle!"),
        WARP_FROM_ARENA("Warping not allowed in the arena!"),
        WAVE_DEFAULT("Wave #%!", "Wave #%!", Material.YELLOW_FLOWER),
        WAVE_SPECIAL("Wave #%! [SPECIAL]", "Wave #%! [SPECIAL]", Material.RED_ROSE),
        WAVE_SWARM("Wave #%! [SWARM]", "Wave #%! [SWARM]", Material.LONG_GRASS),
        WAVE_BOSS("Wave #%! [BOSS]", "Wave #%! [BOSS]", Material.FIRE),
        WAVE_BOSS_ABILITY("Boss used ability: %!", "Boss: %", Material.FIRE),
        WAVE_BOSS_LOW_HEALTH("Boss is almost dead!", "Boss almost dead!", Material.FIRE),
        WAVE_REWARD("You just earned a reward: %", "Reward: %"),
        MISC_LIST_PLAYERS("Live players: %"),
        MISC_LIST_ARENAS("Available arenas: %"),
        MISC_COMMAND_NOT_ALLOWED("You can't use that command in the arena!"),
        MISC_NO_ACCESS("You don't have access to this command."),
        MISC_NONE("<none>");
        
        private String msg, spoutMsg;
        private Material logo;
        
        private Msg(String msg)
        {
            this(msg, null);
        }
        
        private Msg(String msg, String spoutMsg)
        {
            this(msg, spoutMsg, null);
        }
        
        private Msg(String msg, String spoutMsg, Material logo)
        {
            this.msg      = msg;
            this.spoutMsg = spoutMsg;
            this.logo     = logo;
        }
        
        public String get()
        {
            return msg;
        }
        
        public String get(String s)
        {
            return (s != null) ? msg.replace("%", s) : msg;
        }
        
        public String getSpout(String s)
        {
            if (spoutMsg == null)
                return get(s);
            
            return (s != null) ? spoutMsg.replace("%", s) : spoutMsg;
        }
        
        public void set(String msg)
        {
            this.msg = msg;
        }
        
        public void setSpout(String spoutMsg)
        {
            this.spoutMsg = spoutMsg;
            
            if (spoutMsg == null)
                logo = null;
        }
        
        public boolean hasSpoutMsg()
        {
            return spoutMsg != null;
        }
        
        public Material getLogo()
        {
            return logo == null ? Material.SLIME_BALL : logo;
        }
        
        public static String get(Msg m)
        {
            return m.msg;
        }
        
        public static String get(Msg m, String s)
        {
            return m.msg.replace("%", s);   
        }
        
        public static void set(Msg m, String msg)
        {
            m.msg = msg;
        }
        
        public String toString()
        {
            return msg;
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
                    if (m.hasSpoutMsg())
                        bw.write(m.name() + "=" + m.msg + "|" + m.spoutMsg);
                    else
                        bw.write(m.name() + "=" + m.msg);
                    
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
        if (s.endsWith("=") || s.endsWith("|")) s += " ";
        
        // Split the string by the equals-sign.
        String[] split = s.split("=");
        if (split.length != 2)
        {
            MobArena.warning("Couldn't parse \"" + s + "\". Check announcements-file.");
            return;
        }
        
        // Split the value by the pipe-sign.
        String[] vals = split[1].split("\\|");
        
        // For simplicity...
        String key = split[0];
        String val = vals.length == 2 ? vals[0] : split[1];
        String spoutVal = vals.length == 2 ? vals[1] : null;
        
        try
        {
            Msg msg = Msg.valueOf(key);
            msg.set(val);
            msg.setSpout(spoutVal);
        }
        catch (Exception e)
        {
            MobArena.warning(key + " is not a valid key. Check announcements-file.");
            return;
        }
    }
}