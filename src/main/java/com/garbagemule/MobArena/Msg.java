package com.garbagemule.MobArena;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Msg {
    ARENA_START("&9Let the slaughter begin!&r"),
    ARENA_END("&9Arena finished.&r"),
    ARENA_DOES_NOT_EXIST("&9That arena does not exist. Type &e/ma arenas&r for a list.&r"),
    ARENA_END_GLOBAL("&9Arena &e%&r finished! Type &e/ma j %&r to join a new game!&r"),
    ARENA_JOIN_GLOBAL("&9Arena &e%&r is about to start! Type &e/ma j %&r to join!&r"),
    ARENA_LBOARD_NOT_FOUND("&9That arena does not have a leaderboard set up.&r"),
    ARENA_AUTO_START("&9Arena will auto-start in &c%&r seconds.&r"),
    ARENA_START_DELAY("&9Arena can start in &e%&r seconds.&r"),
    JOIN_NOT_ENABLED("&9MobArena is not enabled.&r"),
    JOIN_IN_OTHER_ARENA("&9You are already in an arena! Leave that one first.&r"),
    JOIN_ARENA_NOT_ENABLED("&9This arena is not enabled.&r"),
    JOIN_ARENA_NOT_SETUP("&9This arena has not been set up yet.&r"),
    JOIN_ARENA_EDIT_MODE("&9This arena is in edit mode.&r"),
    JOIN_ARENA_PERMISSION("&9You don't have permission to join this arena.&r"),
    JOIN_FEE_REQUIRED("&9Insufficient funds. Price: &c%&r&r"),
    JOIN_FEE_PAID("&9Price to join was: &c%&r&r"),
    JOIN_ARENA_IS_RUNNING("&9This arena is already in progress.&r"),
    JOIN_ALREADY_PLAYING("&9You are already playing!&r"),
    JOIN_ARG_NEEDED("&9You must specify an arena.&r"),
    JOIN_NO_PERMISSION("&9You don't have permission to join any arenas.&r"),
    JOIN_TOO_FAR("&9You are too far away from the arena to join/spectate.&r"),
    JOIN_EMPTY_INV("&9You must empty your inventory to join the arena.&r"),
    JOIN_PLAYER_LIMIT_REACHED("&9The player limit of this arena has been reached.&r"),
    JOIN_STORE_INV_FAIL("&9Failed to store inventory. Try again.&r"),
    JOIN_EXISTING_INV_RESTORED("&9Your old inventory items have been restored.&r"),
    JOIN_PLAYER_JOINED("&9You joined the arena. Have fun!&r"),
    LEAVE_NOT_PLAYING("&9You are not in the arena.&r"),
    LEAVE_NOT_READY("&9You did not ready up in time! Next time, ready up by clicking an iron block.&r"),
    LEAVE_PLAYER_LEFT("&9You left the arena. Thanks for playing!&r"),
    PLAYER_DIED("&9&c%&r died!&r"),
    GOLEM_DIED("&9A friendly Golem has died!&r"),
    SPEC_PLAYER_SPECTATE("&9Enjoy the show!&r"),
    SPEC_FROM_ARENA("&9Enjoy the rest of the show!&r"),
    SPEC_NOT_RUNNING("&9This arena isn't running.&r"),
    SPEC_EMPTY_INV("&9Empty your inventory first!&r"),
    SPEC_ALREADY_PLAYING("&9Can't spectate when in the arena!&r"),
    NOT_READY_PLAYERS("&9Not ready: &c%&r&r"),
    FORCE_START_RUNNING("&9Arena has already started.&r"),
    FORCE_START_NOT_READY("&9Can't force start, no players are ready.&r"),
    FORCE_START_STARTED("&9Forced arena start.&r"),
    FORCE_END_EMPTY("&9No one is in the arena.&r"),
    FORCE_END_ENDED("&9Forced arena end.&r"),
    FORCE_END_IDLE("&9You weren't quick enough!&r"),
    REWARDS_GIVE("&9Here are all of your rewards!&r"),
    LOBBY_DROP_ITEM("&9No sharing allowed at this time!&r"),
    LOBBY_PLAYER_READY("&9You have been flagged as ready!&r"),
    LOBBY_PICK_CLASS("&9You must first pick a class!&r"),
    LOBBY_CLASS_FULL("&9This class can no longer be selected, class limit reached!&r"),
    LOBBY_NOT_ENOUGH_PLAYERS("&9Not enough players to start. Need at least &c%&r players.&r"),
    LOBBY_RIGHT_CLICK("&9Punch the sign. Don't right-click.&r"),
    LOBBY_CLASS_PICKED("&9You have chosen &e%&r as your class!&r"),
    LOBBY_CLASS_RANDOM("&9You will get a random class on arena start.&r"),
    LOBBY_CLASS_PERMISSION("&9You don't have permission to use this class!&r"),
    LOBBY_CLASS_PRICE("&9This class costs &c%&r (paid on arena start).&r"),
    LOBBY_CLASS_TOO_EXPENSIVE("&9You can't afford that class (&c%&r)&r"),
    LOBBY_NO_SUCH_CLASS("&9There is no class named &c%&r.&r"),
    WARP_TO_ARENA("&9Warping to the arena not allowed!&r"),
    WARP_FROM_ARENA("&9Warping from the arena not allowed!&r"),
    WAVE_DEFAULT("&9Wave &b#%&r!&r"),
    WAVE_SPECIAL("&9Wave &b#%&r! [SPECIAL]&r"),
    WAVE_SWARM("&9Wave &b#%&r! [SWARM]&r"),
    WAVE_SUPPLY("&9Wave &b#%&r! [SUPPLY]&r"),
    WAVE_UPGRADE("&9Wave &b#%&r! [UPGRADE]&r"),
    WAVE_BOSS("&9Wave &b#%&r! [BOSS]&r"),
    WAVE_BOSS_ABILITY("&9Boss used ability: &c%&r!&r"),
    WAVE_BOSS_LOW_HEALTH("&9Boss is almost dead!&r"),
    WAVE_REWARD("&9You just earned a reward: &e%&r&r"),
    MISC_LIST_PLAYERS("&9Live players: &a%&r&r"),
    MISC_LIST_ARENAS("&9Available arenas: %&r"),
    MISC_COMMAND_NOT_ALLOWED("&9You can't use that command in the arena!&r"),
    MISC_NO_ACCESS("&9You don't have access to this command.&r"),
    MISC_NOT_FROM_CONSOLE("&9You can't use this command from the console.&r"),
    MISC_HELP("&9For a list of commands, type &e/ma help&r&r"),
    MISC_MULTIPLE_MATCHES("&9Did you mean one of these commands?&r"),
    MISC_NO_MATCHES("&9Command not found. Type &e/ma help&r&r"),
    MISC_MA_LEAVE_REMINDER("&9Remember to use &e/ma leave&r when you are done.&r"),
    MISC_NONE("&9&6<none>&r&r");

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
            String key = msg.name().toLowerCase().replace("&9_","-&r");
            msg.set(config.getString(key, "&r"));
        }
    }

    static YamlConfiguration toYaml() {
        YamlConfiguration yaml = new YamlConfiguration();
        for (Msg msg : values()) {
            // ARENA_END_GLOBAL => arena-end-global
            String key = msg.name().replace("&9_","-&r").toLowerCase();
            yaml.set(key, msg.value);
        }
        return yaml;
    }
}