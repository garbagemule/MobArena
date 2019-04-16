package com.garbagemule.MobArena;

import org.bukkit.Server;

import java.util.StringJoiner;

class ServerVersionCheck {

    private static final String[] EXACTS = {"1.8", "1.9", "1.10", "1.11", "1.12"};
    private static final String[] PREFIXES = {"1.8.", "1.9.", "1.10.", "1.11.", "1.12."};

    static void check(Server server) {
        String version = getMinecraftVersion(server);

        for (String exact : EXACTS) {
            if (version.equals(exact)) {
                fail(version);
            }
        }
        for (String prefix : PREFIXES) {
            if (version.startsWith(prefix)) {
                fail(version);
            }
        }
    }

    private static String getMinecraftVersion(Server server) {
        // Same substring as the one bStats uses, so should be safe
        String version = server.getVersion();
        int start = version.indexOf("MC: ") + 4;
        int end = version.length() - 1;
        return version.substring(start, end);
    }

    private static void fail(String version) {
        throw new IllegalStateException(new StringJoiner(" ")
            .add("Incompatible server version!")
            .add("This build does not work on " + version + ".")
            .add("Perhaps you downloaded the wrong build?")
            .toString()
        );
    }

}
