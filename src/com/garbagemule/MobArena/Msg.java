package com.garbagemule.MobArena;

import org.bukkit.Material;

public enum Msg
{
    ARENA_START("Let the slaughter begin!", "Arena started!", Material.REDSTONE_TORCH_ON),
    ARENA_END("Arena finished.", "Arena finished.", Material.REDSTONE_TORCH_OFF),
    ARENA_DOES_NOT_EXIST("That arena does not exist. Type /ma arenas for a list.", "Can't find arena."),
    ARENA_LBOARD_NOT_FOUND("That arena does not have a leaderboard set up."),
    ARENA_AUTO_START("Arena will auto-start in % seconds."),
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
    JOIN_ARG_NEEDED("You must specify an arena."),
    JOIN_NO_PERMISSION("You don't have permission to join any arenas."),
    JOIN_TOO_FAR("You are too far away from the arena to join/spectate.", "Too far from arena.", Material.COMPASS),
    JOIN_EMPTY_INV("You must empty your inventory to join the arena.", "Empty your inventory.", Material.CHEST),
    JOIN_PLAYER_LIMIT_REACHED("The player limit of this arena has been reached.", "No spots left.", Material.MILK_BUCKET),
    JOIN_STORE_INV_FAIL("Failed to store inventory. Try again."),
    JOIN_EXISTING_INV_RESTORED("Your old inventory items have been restored."),
    JOIN_PLAYER_JOINED("You joined the arena. Have fun!", "Joined arena.", Material.IRON_SWORD),
    LEAVE_NOT_PLAYING("You are not in the arena.", "Not in arena."),
    LEAVE_PLAYER_LEFT("You left the arena. Thanks for playing!", "Left arena.", Material.WOOD_DOOR),
    PLAYER_DIED("% died!", "% died!", Material.BONE),
    GOLEM_DIED("A friendly Golem has died!", "A Golem has died!", Material.PUMPKIN),
    SPEC_PLAYER_SPECTATE("Enjoy the show!", "Enjoy the show!"),
    SPEC_FROM_ARENA("Enjoy the rest of the show!", "Enjoy the show!"),
    SPEC_NOT_RUNNING("This arena isn't running.", "Arena not running.", Material.REDSTONE_TORCH_OFF),
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
    LOBBY_CLASS_FULL("This class can no longer be selected, class limit reached!", "Class limit reached!"),
    LOBBY_NOT_ENOUGH_PLAYERS("Not enough players to start. Need at least % players.", "Need more players."),
    LOBBY_RIGHT_CLICK("Punch the sign. Don't right-click.", "Punch the sign."),
    LOBBY_CLASS_PICKED("You have chosen % as your class!", "%"),
    LOBBY_CLASS_RANDOM("You will get a random class on arena start."),
    LOBBY_CLASS_PERMISSION("You don't have permission to use this class!", "No permission!", Material.FENCE),
    WARP_TO_ARENA("Warping to the arena not allowed!"),
    WARP_FROM_ARENA("Warping from the arena not allowed!"),
    WAVE_DEFAULT("Wave #%!", "Wave #%!", Material.YELLOW_FLOWER),
    WAVE_SPECIAL("Wave #%! [SPECIAL]", "Wave #%! [SPECIAL]", Material.RED_ROSE),
    WAVE_SWARM("Wave #%! [SWARM]", "Wave #%! [SWARM]", Material.LONG_GRASS),
    WAVE_SUPPLY("Wave #%! [SUPPLY]", "Wave #%! [SUPPLY]", Material.BREAD),
    WAVE_UPGRADE("Wave #%! [UPGRADE]", "Wave #%! [UPGRADE]", Material.DIAMOND), 
    WAVE_BOSS("Wave #%! [BOSS]", "Wave #%! [BOSS]", Material.FIRE),
    WAVE_BOSS_ABILITY("Boss used ability: %!", "Boss: %", Material.FIRE),
    WAVE_BOSS_LOW_HEALTH("Boss is almost dead!", "Boss almost dead!", Material.FIRE),
    WAVE_REWARD("You just earned a reward: %", "Reward: %"),
    MISC_LIST_PLAYERS("Live players: %"),
    MISC_LIST_ARENAS("Available arenas: %"),
    MISC_COMMAND_NOT_ALLOWED("You can't use that command in the arena!"),
    MISC_NO_ACCESS("You don't have access to this command."),
    MISC_NOT_FROM_CONSOLE("You can't use this command from the console."),
    MISC_HELP("For a list of commands, type /ma help"),
    MISC_MULTIPLE_MATCHES("Did you mean one of these commands?"),
    MISC_NO_MATCHES("Command not found. Type /ma help"),
    MISC_MA_LEAVE_REMINDER("Remember to use /ma leave when you are done."),
    MISC_NONE("<none>");
    
    private String msg, spoutMsg;
    private Material logo;
    
    /**
     * Default constructor.
     * @param msg a string for the chat window
     * @param spoutMsg a string for Spout
     * @param logo a logo for Spout
     */
    private Msg(String msg, String spoutMsg, Material logo) {
        this.msg      = msg;
        this.spoutMsg = spoutMsg;
        this.logo     = logo;
    }
    
    /**
     * Custom Spout constructor.
     * @param msg a string for the chat window
     * @param spoutMsg a string for Spout
     */
    private Msg(String msg, String spoutMsg) {
        this(msg, spoutMsg, null);
    }
    
    /**
     * Custom normal constructor.
     * @param msg a string for the chat window
     */
    private Msg(String msg) {
        this(msg, null);
    }
    
    /**
     * Change this enum's chat window string.
     * @param msg a string for the chat window
     */
    public void set(String msg) {
        this.msg = msg;
    }
    
    /**
     * Change this enum's Spout string.
     * @param spoutMsg a string for Spout
     */
    public void setSpout(String spoutMsg) {
        this.spoutMsg = spoutMsg;
        
        if (spoutMsg == null)
            logo = null;
    }
    
    /**
     * Check if this enum has a Spout string associated with it.
     * @return true, if a Spout string is available, false otherwise.
     */
    public boolean hasSpoutMsg() {
        return spoutMsg != null;
    }
    
    /**
     * Get the logo of this enum.
     * The default logo is Material.SLIME_BALL if none is specified.
     * @return the logo for this enum
     */
    public Material getLogo() {
        return logo == null ? Material.SLIME_BALL : logo;
    }

    @Override
    public String toString() {
        return msg;
    }
    
    /**
     * Same as above, just returning the string for Spout instead.
     * @return the string for Spout
     */
    public String toSpoutString() {
        return spoutMsg;
    }
    
    /**
     * Extended toString method that allows a variable.
     * @param s the variable value
     * @return the string for the chat window, with all %'s replaced by the input
     */
    public String toString(String s) {
        return (s != null) ? msg.replace("%", s) : msg;
    }
    
    /**
     * Same as above, but for the Spout string.
     * @param s the variable value
     * @return the string for Spout, with all %'s replaced by the input
     */
    public String toSpoutString(String s) {
        if (spoutMsg == null)
            return toString(s);
        
        return (s != null) ? spoutMsg.replace("%", s) : spoutMsg;
    }
}