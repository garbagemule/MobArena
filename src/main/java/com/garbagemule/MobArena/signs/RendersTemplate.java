package com.garbagemule.MobArena.signs;

import static java.lang.String.valueOf;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

class RendersTemplate {

    // Regex Pattern for ready/notready variables
    // find() is true if a variable was found
    // group(0) is matched variable
    // group(1) is null for ready variable, !null for notready variable
    // group(2) is player index+1 as a String
    final private Pattern readyPattern = Pattern.compile("<(not)?ready-(\\d+)>");

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
        if (matcher.find()) {
            int i = Integer.parseInt(matcher.group(2));
            if (i > 0) {
                if (matcher.group(1) == null) {
                    // Variable is ready i
                    List<Player> ready = arena.getReadyPlayers();
                    return matcher.replaceFirst((ready.size()>=i) ? ready.get(i-1).getName() : "");
                } else {
                    // Variable is notready i
                    List<Player> nonready = arena.getNonreadyPlayers();
                    return matcher.replaceFirst((nonready.size()>=i) ? nonready.get(i-1).getName() : "");
                }
            }
        }
        return line;
    }

    private String truncate(String rendered) {
        if (rendered.length() <= 15) {
            return rendered;
        }
        return rendered.substring(0, 15);
    }

}
