package com.garbagemule.MobArena;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;


public enum Msg {
    ARENA_START("&9Let the slaughter begin!"),
    ARENA_END("&9Arena finished."),
    ARENA_DOES_NOT_EXIST("&9That arena does not exist. Type &e/ma arenas&9 for a list."),
    ARENA_END_GLOBAL("&9Arena &e%&9 finished! Type &e/ma j %&9 to join a new game!"),
    ARENA_JOIN_GLOBAL("&9Arena &e%&9 is about to start! Type &e/ma j %&9 to join!"),
    ARENA_LBOARD_NOT_FOUND("&9That arena does not have a leaderboard set up."),
    ARENA_AUTO_START("&9Arena will auto-start in &c%&9 seconds."),
    ARENA_START_DELAY("&9Arena can start in &e%&9 seconds."),
    JOIN_NOT_ENABLED("&9MobArena is not enabled."),
    JOIN_IN_OTHER_ARENA("&9You are already in an arena! Leave that one first."),
    JOIN_ARENA_NOT_ENABLED("&9This arena is not enabled."),
    JOIN_ARENA_NOT_SETUP("&9This arena has not been set up yet."),
    JOIN_ARENA_EDIT_MODE("&9This arena is in edit mode."),
    JOIN_ARENA_PERMISSION("&9You don't have permission to join this arena."),
    JOIN_FEE_REQUIRED("&9Insufficient funds. Price: &c%&9"),
    JOIN_FEE_PAID("&9Price to join was: &c%&9"),
    JOIN_ARENA_IS_RUNNING("&9This arena is already in progress."),
    JOIN_ALREADY_PLAYING("&9You are already playing!"),
    JOIN_ARG_NEEDED("&9You must specify an arena."),
    JOIN_NO_PERMISSION("&9You don't have permission to join any arenas."),
    JOIN_TOO_FAR("&9You are too far away from the arena to join/spectate."),
    JOIN_EMPTY_INV("&9You must empty your inventory to join the arena."),
    JOIN_PLAYER_LIMIT_REACHED("&9The player limit of this arena has been reached."),
    JOIN_STORE_INV_FAIL("&9Failed to store inventory. Try again."),
    JOIN_EXISTING_INV_RESTORED("&9Your old inventory items have been restored."),
    JOIN_PLAYER_JOINED("&9You joined the arena. Have fun!"),
    LEAVE_NOT_PLAYING("&9You are not in the arena."),
    LEAVE_NOT_READY("&9You did not ready up in time! Next time, ready up by clicking an iron block."),
    LEAVE_PLAYER_LEFT("&9You left the arena. Thanks for playing!"),
    PLAYER_DIED("&9&c%&9 died!"),
    GOLEM_DIED("&9A friendly Golem has died!"),
    SPEC_PLAYER_SPECTATE("&9Enjoy the show!"),
    SPEC_FROM_ARENA("&9Enjoy the rest of the show!"),
    SPEC_NOT_RUNNING("&9This arena isn't running."),
    SPEC_EMPTY_INV("&9Empty your inventory first!"),
    SPEC_ALREADY_PLAYING("&9Can't spectate when in the arena!"),
    NOT_READY_PLAYERS("&9Not ready: &c%&9"),
    FORCE_START_RUNNING("&9Arena has already started."),
    FORCE_START_NOT_READY("&9Can't force start, no players are ready."),
    FORCE_START_STARTED("&9Forced arena start."),
    FORCE_END_EMPTY("&9No one is in the arena."),
    FORCE_END_ENDED("&9Forced arena end."),
    FORCE_END_IDLE("&9You weren't quick enough!"),
    REWARDS_GIVE("&9Here are all of your rewards!"),
    LOBBY_DROP_ITEM("&9No sharing allowed at this time!"),
    LOBBY_PLAYER_READY("&9You have been flagged as ready!"),
    LOBBY_PICK_CLASS("&9You must first pick a class!"),
    LOBBY_CLASS_FULL("&9This class can no longer be selected, class limit reached!"),
    LOBBY_NOT_ENOUGH_PLAYERS("&9Not enough players to start. Need at least &c%&9 players."),
    LOBBY_RIGHT_CLICK("&9Punch the sign. Don't right-click."),
    LOBBY_CLASS_PICKED("&9You have chosen &e%&9 as your class!"),
    LOBBY_CLASS_RANDOM("&9You will get a random class on arena start."),
    LOBBY_CLASS_PERMISSION("&9You don't have permission to use this class!"),
    LOBBY_CLASS_PRICE("&9This class costs &c%&9 (paid on arena start)."),
    LOBBY_CLASS_TOO_EXPENSIVE("&9You can't afford that class (&c%&9)"),
    LOBBY_NO_SUCH_CLASS("&9There is no class named &c%&9."),
    WARP_TO_ARENA("&9Warping to the arena not allowed!"),
    WARP_FROM_ARENA("&9Warping from the arena not allowed!"),
    WAVE_DEFAULT("&9Wave &b#%&9!"),
    WAVE_SPECIAL("&9Wave &b#%&9! [SPECIAL]"),
    WAVE_SWARM("&9Wave &b#%&9! [SWARM]"),
    WAVE_SUPPLY("&9Wave &b#%&9! [SUPPLY]"),
    WAVE_UPGRADE("&9Wave &b#%&9! [UPGRADE]"),
    WAVE_BOSS("&9Wave &b#%&9! [BOSS]"),
    WAVE_BOSS_ABILITY("&9Boss used ability: &c%&9!"),
    WAVE_BOSS_LOW_HEALTH("&9Boss is almost dead!"),
    WAVE_REWARD("&9You just earned a reward: &e%&9"),
    MISC_LIST_PLAYERS("&9Live players: &a%&9"),
    MISC_LIST_ARENAS("&9Available arenas: %"),
    MISC_COMMAND_NOT_ALLOWED("&9You can't use that command in the arena!"),
    MISC_NO_ACCESS("&9You don't have access to this command."),
    MISC_NOT_FROM_CONSOLE("&9You can't use this command from the console."),
    MISC_HELP("&9For a list of commands, type &e/ma help&9"),
    MISC_MULTIPLE_MATCHES("&9Did you mean one of these commands?"),
    MISC_NO_MATCHES("&9Command not found. Type &e/ma help&9"),
    MISC_MA_LEAVE_REMINDER("&9Remember to use &e/ma leave&9 when you are done."),
    MISC_NONE("&9&6<none>&9");

    private String value;

    private Msg(String value) {
        set(value);
    }

    void set(String value) {
        this.value = value;
    }

    public String toString() {
        return ChatColor.translateAlternateColorCodes('&', value);
    }

    public String format(String s) {
        return (s == null) ? "" : toString().replace("&9%", s);
    }

    static void load(ConfigurationSection config) {
        for (Msg msg : values()) {
            // ARENA_END_GLOBAL => arena-end-global
            String key = msg.name().toLowerCase().replace("&9_","-");
            msg.set(config.getString(key, ""));
        }
    }

    static YamlConfiguration toYaml() {
        YamlConfiguration yaml = new YamlConfiguration();
        for (Msg msg : values()) {
            // ARENA_END_GLOBAL => arena-end-global
            String key = msg.name().replace("&9_","-").toLowerCase();
            yaml.set(key, msg.value);
        }
        return yaml;
    }
}