package com.garbagemule.MobArena;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Msg {
    ARENA_START("Let the slaughter begin!"),
    ARENA_END("Arena finished."),
    ARENA_DOES_NOT_EXIST("That arena does not exist. Type &e/ma arenas&r for a list."),
    ARENA_END_GLOBAL("Arena &e%&r finished! Type &e/ma j %&r to join a new game!"),
    ARENA_JOIN_GLOBAL("Arena &e%&r is about to start! Type &e/ma j %&r to join!"),
    ARENA_LBOARD_NOT_FOUND("That arena does not have a leaderboard set up."),
    ARENA_AUTO_START("Arena will auto-start in &c%&r seconds."),
    ARENA_START_DELAY("Arena can start in &e%&r seconds."),
    JOIN_NOT_ENABLED("MobArena is not enabled."),
    JOIN_IN_OTHER_ARENA("You are already in an arena! Leave that one first."),
    JOIN_ARENA_NOT_ENABLED("This arena is not enabled."),
    JOIN_ARENA_NOT_SETUP("This arena has not been set up yet."),
    JOIN_ARENA_EDIT_MODE("This arena is in edit mode."),
    JOIN_ARENA_PERMISSION("You don't have permission to join this arena."),
    JOIN_FEE_REQUIRED("Insufficient funds. Price: &c%&r"),
    JOIN_FEE_PAID("Price to join was: &c%&r"),
    JOIN_ARENA_IS_RUNNING("This arena is already in progress."),
    JOIN_ALREADY_PLAYING("You are already playing!"),
    JOIN_ARG_NEEDED("You must specify an arena."),
    JOIN_NO_PERMISSION("You don't have permission to join any arenas."),
    JOIN_TOO_FAR("You are too far away from the arena to join/spectate."),
    JOIN_EMPTY_INV("You must empty your inventory to join the arena."),
    JOIN_PLAYER_LIMIT_REACHED("The player limit of this arena has been reached."),
    JOIN_STORE_INV_FAIL("Failed to store inventory. Try again."),
    JOIN_EXISTING_INV_RESTORED("Your old inventory items have been restored."),
    JOIN_AFTER_DELAY("Joining arena in &c%&r seconds..."),
    JOIN_INTERRUPTED_BY_DAMAGE("Join aborted due to taking damage."),
    JOIN_INTERRUPTED_BY_MOVEMENT("Join aborted due to movement."),
    JOIN_PLAYER_JOINED("You joined the arena. Have fun!"),
    LEAVE_NOT_PLAYING("You are not in the arena."),
    LEAVE_NOT_READY("You did not ready up in time! Next time, ready up by clicking an iron block."),
    LEAVE_PLAYER_LEFT("You left the arena. Thanks for playing!"),
    PLAYER_DIED("&c%&r died!"),
    GOLEM_DIED("A friendly Golem has died!"),
    SPEC_PLAYER_SPECTATE("Enjoy the show!"),
    SPEC_FROM_ARENA("Enjoy the rest of the show!"),
    SPEC_NOT_RUNNING("This arena isn't running."),
    SPEC_EMPTY_INV("Empty your inventory first!"),
    SPEC_ALREADY_PLAYING("Can't spectate when in the arena!"),
    NOT_READY_PLAYERS("Not ready: &c%&r"),
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
    LOBBY_CLASS_FULL("This class can no longer be selected, class limit reached!"),
    LOBBY_NOT_ENOUGH_PLAYERS("Not enough players to start. Need at least &c%&r players."),
    LOBBY_RIGHT_CLICK("Punch the sign. Don't right-click."),
    LOBBY_CLASS_PICKED("You have chosen &e%&r as your class!"),
    LOBBY_CLASS_RANDOM("You will get a random class on arena start."),
    LOBBY_CLASS_PERMISSION("You don't have permission to use this class!"),
    LOBBY_CLASS_PRICE("This class costs &c%&r (paid on arena start)."),
    LOBBY_CLASS_TOO_EXPENSIVE("You can't afford that class (&c%&r)"),
    LOBBY_NO_SUCH_CLASS("There is no class named &c%&r."),
    NOT_IN_LOBBY("You are not in the lobby"),
    WARP_TO_ARENA("Warping to the arena not allowed!"),
    WARP_FROM_ARENA("Warping from the arena not allowed!"),
    WAVE_DEFAULT("Wave &b#%&r!"),
    WAVE_SPECIAL("Wave &b#%&r! [SPECIAL]"),
    WAVE_SWARM("Wave &b#%&r! [SWARM]"),
    WAVE_SUPPLY("Wave &b#%&r! [SUPPLY]"),
    WAVE_UPGRADE("Wave &b#%&r! [UPGRADE]"),
    WAVE_BOSS("Wave &b#%&r! [BOSS]"),
    WAVE_BOSS_ABILITY("Boss used ability: &c%&r!"),
    WAVE_BOSS_LOW_HEALTH("Boss is almost dead!"),
    WAVE_BOSS_KILLED("&a%&r killed the boss!"),
    WAVE_BOSS_REWARD_EARNED("You earned: &e%"),
    WAVE_REWARD("You just earned a reward: &e%&r"),
    MISC_REWARD_ADDED("You were just given a reward: &e%&r"),
    MISC_LIST_PLAYERS("Live players: &a%&r"),
    MISC_LIST_ARENAS("Available arenas: %"),
    MISC_COMMAND_NOT_ALLOWED("You can't use that command in the arena!"),
    MISC_NO_ACCESS("You don't have access to this command."),
    MISC_NOT_FROM_CONSOLE("You can't use this command from the console."),
    MISC_HELP("For a list of commands, type &e/ma help&r"),
    MISC_MULTIPLE_MATCHES("Did you mean one of these commands?"),
    MISC_NO_MATCHES("Command not found. Type &e/ma help&r"),
    MISC_MA_LEAVE_REMINDER("Remember to use &e/ma leave&r when you are done."),
    MISC_NONE("&6<none>&r");

    private String value;

    Msg(String value) {
        set(value);
    }

    void set(String value) {
        this.value = value;
    }

    public String toString() {
        return ChatColor.translateAlternateColorCodes('&', value);
    }

    public String format(String s) {
        return (s == null) ? "" : toString().replace("%", s);
    }

    static void load(ConfigurationSection config) {
        for (Msg msg : values()) {
            // ARENA_END_GLOBAL => arena-end-global
            String key = msg.name().toLowerCase().replace("_","-");
            msg.set(config.getString(key, ""));
        }
    }

    static YamlConfiguration toYaml() {
        YamlConfiguration yaml = new YamlConfiguration();
        for (Msg msg : values()) {
            // ARENA_END_GLOBAL => arena-end-global
            String key = msg.name().replace("_","-").toLowerCase();
            yaml.set(key, msg.value);
        }
        return yaml;
    }
}
