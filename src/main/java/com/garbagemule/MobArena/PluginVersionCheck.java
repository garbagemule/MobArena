package com.garbagemule.MobArena;

import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.function.Consumer;

public class PluginVersionCheck {

    private static final String RESOURCE_ID = "34110";
    private static final String ENDPOINT = "https://api.spigotmc.org/legacy/update.php?resource=" + RESOURCE_ID;
    private static final int TIMEOUT = 5000;
    private static final long CACHE_TTL = 60 * 60 * 1000;

    private static long timeOfLastCheck = 0;
    private static String messageOfLastCheck = null;

    public static void check(Plugin plugin, Consumer<String> block) {
        if (cacheIsFresh()) {
            checkFromCache(plugin, block);
        } else {
            checkFromRemote(plugin, block);
        }
    }

    private static boolean cacheIsFresh() {
        return System.currentTimeMillis() < timeOfLastCheck + CACHE_TTL;
    }

    private static void checkFromCache(Plugin plugin, Consumer<String> block) {
        // Run on the next tick to avoid drowning in login spam
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            if (messageOfLastCheck != null) {
                block.accept(messageOfLastCheck);
            }
        });
    }

    private static void checkFromRemote(Plugin plugin, Consumer<String> block) {
        // Reset the cache before going fishing
        resetCache();

        // Get off the main thread for fetching the remote version
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String local = plugin.getDescription().getVersion();
            String remote = getRemoteVersion();

            // Get back on the main thread for the result
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (remote != null && lessThan(local, remote)) {
                    // Create the update notification and cache it
                    String message = String.format("v%s is now available! You are running v%s.", remote, local);
                    hydrateCache(message);

                    block.accept(message);
                }
            });
        });
    }

    private static void resetCache() {
        timeOfLastCheck = System.currentTimeMillis();
        messageOfLastCheck = null;
    }

    private static void hydrateCache(String message) {
        timeOfLastCheck = System.currentTimeMillis();
        messageOfLastCheck = message;
    }

    private static String getRemoteVersion() {
        try (
            InputStream is = getEndpointStream();
            Scanner scanner = new Scanner(is)
        ) {
            if (scanner.hasNext()) {
                return scanner.next();
            }
        } catch (IOException e) {
            // Update checks are non-essential, so just swallow
        }
        return null;
    }

    private static InputStream getEndpointStream() throws IOException {
        // Create a new URL from the resource endpoint
        URL url = new URL(ENDPOINT);

        // Open the connection and set some timeouts
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(TIMEOUT);

        // Finally, return the stream
        return connection.getInputStream();
    }

    static boolean lessThan(String local, String remote) {
        if (local == null || remote == null) {
            return false;
        }

        String localVersion = local.split("-")[0];
        String remoteVersion = remote.split("-")[0];

        String[] localParts = localVersion.split("\\.");
        String[] remoteParts = remoteVersion.split("\\.");

        int length = Math.max(localParts.length, remoteParts.length);

        for (int i = 0; i < length; i++) {
            int localPart = Integer.parseInt((localParts.length > i) ? localParts[i] : "0");
            int remotePart = Integer.parseInt((remoteParts.length > i) ? remoteParts[i] : "0");

            // We skip to the next part if local and remote are identical,
            // because we only have to short-circuit when they differ.
            if (localPart == remotePart) {
                continue;
            }

            // We've reached a point where local is either greater than or
            // less than remote. Greater than means we're running a bleeding
            // edge build. Less than means we're running an outdated build.
            return localPart < remotePart;
        }

        // The two versions are identical, but if local is a SNAPSHOT, it
        // is technically not the same and actually a lower version.
        return local.endsWith("-SNAPSHOT");
    }

}
