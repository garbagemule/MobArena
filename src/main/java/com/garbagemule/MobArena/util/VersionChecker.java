package com.garbagemule.MobArena.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.util.Updater.UpdateResult;
import com.garbagemule.MobArena.util.Updater.UpdateType;

public class VersionChecker
{
    static Updater updater;
    
    public static void checkForUpdates(final MobArena plugin, final Player player) {
        if (updater == null) {
            updater = new Updater(plugin, 262634, plugin.getPluginFile(), UpdateType.NO_DOWNLOAD, false);
        }

        // Async for anti-lag
        final Updater cache = updater;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            public void run() {
                if (cache.getResult() == UpdateResult.UPDATE_AVAILABLE) {
                    final String latest  = getLatestVersionString();
                    final String current = plugin.getDescription().getVersion();

                    if (latest == null || current == null) {
                        String msg = "Update checker failed. Please check manually!";
                        message(plugin, player, msg);
                    }

                    else if (isUpdateAvailable(latest, current)) {
                        String msg1 = "MobArena v" + latest + " is now available!";
                        String msg2 = "Your version: v" + current;
                        message(plugin, player, msg1, msg2);
                    }
                }
            }
        });
    }

    private static String getLatestVersionString() {
        String latestName = updater.getLatestName();
        if (!latestName.matches("MobArena v.*")) {
            return null;
        }
        return latestName.substring("MobArena v".length());
    }

    private static boolean isUpdateAvailable(String latestVersion, String currentVersion) {
        // Split into major.minor(.patch(.build))
        String[] latestParts  = latestVersion.split("\\.");
        String[] currentParts = currentVersion.split("\\.");

        // Figure out how many numbers to compare
        int parts = Math.max(latestParts.length, currentParts.length);

        // Check each part
        for (int i = 0; i < parts; i++) {
            int latest  = getPart(latestParts,  i);
            int current = getPart(currentParts, i);

            // Return early if current is more recent
            if (current > latest) {
                return false;
            }

            // And also if latest is more recent
            if (latest > current) {
                return true;
            }
        }

        // Otherwise, we're completely up-to-date!
        return false;
    }

    private static int getPart(String[] parts, int i) {
        // Out of bounds or not an int? Bail with 0.
        if (i >= parts.length || !parts[i].matches("[0-9]+")) {
            return 0;
        }
        return Integer.parseInt(parts[i]);
    }

    private static void message(final MobArena plugin, final Player player, final String... messages) {
        Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
            public void run() {
                for (String message : messages) {
                    if (player == null) {
                        plugin.getLogger().info(message);
                    } else if (player.isOnline()) {
                        plugin.getGlobalMessenger().tell(player, message);
                    }
                }
            }
        }, (player == null) ? 0 : 60); // Message player after login spam
    }

    public static void shutdown() {
        updater = null;
    }
}
