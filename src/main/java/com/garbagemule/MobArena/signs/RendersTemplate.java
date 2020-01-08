package com.garbagemule.MobArena.signs;

import static java.lang.String.valueOf;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

class RendersTemplate {

    // Regex Pattern for ready/notready variables
    // group(1) is null for ready variable, !null for notready variable
    // group(2) is player index+1 as a String
    private final Pattern readyPattern = Pattern.compile("<(not)?ready-(\\d+)>");

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
        String result = replaceReady(line, arena);
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
        String result = replaceReady(line, arena);
        return result
            .replace("<initial-players>", valueOf(arena.getPlayersInLobby().size()))
            .replace("<live-players>", valueOf(arena.getPlayersInLobby().size()))
            .replace("<dead-players>", "-")
            .replace("<current-wave>", "-")
            .replace("<lobby-players>", valueOf(arena.getPlayersInLobby().size()))
            .replace("<ready-players>", valueOf(arena.getReadyPlayersInLobby().size()));
    }

    private String replaceReady(String line, Arena arena) {
        Matcher matcher = readyPattern.matcher(line);
        if (!matcher.find()) {
            return line;
        }
        int i;
        try {
            i = Integer.parseInt(matcher.group(2));
        }
        catch(NumberFormatException e) {
            return line;
        }
        if (i <= 0) {
            return line;
        }
        List<Player> ready;
        if (matcher.group(1) == null) {
            // Variable is ready i
            Set<Player> readyPlayersInLobby = arena.getReadyPlayersInLobby();
            ready = new ArrayList<>(readyPlayersInLobby);

        } else {
            // Variable is notready i
            ready = arena.getNonreadyPlayers();
        }
        if (ready.size() >= i) {
            ready.sort(Comparator.comparing(Player:: getName));
            String name = ready.get(i-1).getName();
            return matcher.replaceFirst(name);
        }
        return matcher.replaceFirst("");
    }

    private String truncate(String rendered) {
        if (rendered.length() <= 15) {
            return rendered;
        }
        return rendered.substring(0, 15);
    }

}
