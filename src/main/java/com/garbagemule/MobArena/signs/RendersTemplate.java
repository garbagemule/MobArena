package com.garbagemule.MobArena.signs;

import static java.lang.String.valueOf;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

class RendersTemplate {

    // Regex Pattern for player list variables.
    // Changes to the list types must also be made in getPlayerList.
    // group(1) is the list type as a String.
    // group(2) is player index+1 as a String.
    private final Pattern playerListPattern = Pattern.compile("<(arena|lobby|ready|notready)-([1-9][0-9]?)>");

    String[] render(Template template, Arena arena) {
        String[] lines = getTemplateByState(template, arena);

        String[] result = new String[lines.length];
        for (int i = 0; i < lines.length; i++) {
            String rendered = render(lines[i], arena);
            result[i] = truncate(rendered);
        }
        return result;
    }

    private String[] getTemplateByState(Template template, Arena arena) {
        if (arena.isRunning()) {
            return template.running;
        }
        if (arena.getPlayersInLobby().size() > 0) {
            if (arena.getNonreadyPlayers().size() == 0) {
                return template.ready;
            }
            return template.joining;
        }
        return template.idle;
    }

    private String render(String line, Arena arena) {
        String result = generic(line, arena);
        if (arena.isRunning()) {
            result = running(result, arena);
        } else {
            result = joining(result, arena);
        }
        return ChatColor.translateAlternateColorCodes('&', result);
    }

    private String generic(String line, Arena arena) {
        return line
            .replace("<arena-name>", arena.configName())
            .replace("<min-players>", valueOf(arena.getMinPlayers()))
            .replace("<max-players>", valueOf(arena.getMaxPlayers()));
    }

    private String running(String line, Arena arena) {
        String result = replacePlayerListEntry(line, arena);
        return result
            .replace("<initial-players>", valueOf(arena.getPlayerCount()))
            .replace("<live-players>", valueOf(arena.getPlayersInArena().size()))
            .replace("<dead-players>", valueOf(arena.getPlayerCount() - arena.getPlayersInArena().size()))
            .replace("<current-wave>", valueOf(arena.getWaveManager().getWaveNumber()))
            .replace("<final-wave>", valueOf(arena.getWaveManager().getFinalWave()))
            .replace("<lobby-players>", "-")
            .replace("<ready-players>", "-");
    }

    private String joining(String line, Arena arena) {
        String result = replacePlayerListEntry(line, arena);
        return result
            .replace("<initial-players>", valueOf(arena.getPlayersInLobby().size()))
            .replace("<live-players>", valueOf(arena.getPlayersInLobby().size()))
            .replace("<dead-players>", "-")
            .replace("<current-wave>", "-")
            .replace("<lobby-players>", valueOf(arena.getPlayersInLobby().size()))
            .replace("<ready-players>", valueOf(arena.getReadyPlayersInLobby().size()));
    }

    private String replacePlayerListEntry(String line, Arena arena) {
        Matcher matcher = playerListPattern.matcher(line);
        if (!matcher.find()) {
            return line;
        }
        List<String> list = getNameList(matcher.group(1), arena);
        int index = Integer.parseInt(matcher.group(2)) - 1;

        if (index < list.size()) {
            String value = list.get(index);
            return matcher.replaceFirst(value);
        } else {
            return matcher.replaceFirst("");
        }
    }

    private List<String> getNameList(String name, Arena arena) {
        return getPlayerList(name, arena)
            .stream()
            .map(Player::getName)
            .sorted()
            .collect(Collectors.toList());
    }

    private Collection<Player> getPlayerList(String name, Arena arena) {
        switch (name) {
            case "arena": {
                return arena.getPlayersInArena();
            }
            case "lobby": {
                return arena.getPlayersInLobby();
            }
            case "ready": {
                return arena.getReadyPlayersInLobby();
            }
            case "notready": {
                return arena.getNonreadyPlayers();
            }
            default: {
                return Collections.emptyList();
            }
        }
    }

    private String truncate(String rendered) {
        if (rendered.length() <= 15) {
            return rendered;
        }
        return rendered.substring(0, 15);
    }

}
