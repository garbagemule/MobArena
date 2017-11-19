package com.garbagemule.MobArena.things;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

public class CommandThing implements Thing {
    private final String command;
    private final String title;

    public CommandThing(String command) {
        this(command, null);
    }

    public CommandThing(String command, String title) {
        this.command = trimSlash(command);
        this.title = title;
    }

    @Override
    public boolean giveTo(Player player) {
        return Bukkit.getServer().dispatchCommand(
            Bukkit.getConsoleSender(),
            command.replace("<player>", player.getName())
        );
    }

    @Override
    public boolean takeFrom(Player player) {
        return false;
    }

    @Override
    public boolean heldBy(Player player) {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof CommandThing)) return false;

        CommandThing other = (CommandThing) o;
        return Objects.equals(command, other.command)
            && Objects.equals(title, other.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(command, title);
    }

    @Override
    public String toString() {
        if (title != null) {
            return title;
        }
        return "/" + command;
    }

    private String trimSlash(String command) {
        if (command.startsWith("/")) {
            return command.substring(1);
        }
        return command;
    }
}
