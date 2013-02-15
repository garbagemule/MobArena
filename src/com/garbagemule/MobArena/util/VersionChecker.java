package com.garbagemule.MobArena.util;

import java.io.InputStream;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.MobArena;

public class VersionChecker
{
    public static final String url = "http://dev.bukkit.org/server-mods/mobarena/files.rss";
    
    public static void checkForUpdates(final MobArena plugin, final Player player) {
        // Thread the entire thing to avoid lag
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Grab the version string from the feed
                final String latestVersion = getLatestVersion();
                if (latestVersion == null) return;
                
                // First check equality with the current version
                final String currentVersion = plugin.getDescription().getVersion();
                if (currentVersion.equals(latestVersion)) {
                    return;
                }
                
                // Split into major-minor-patch
                String[] latestParts  = latestVersion.split("\\.");
                String[] currentParts = currentVersion.split("\\.");
                
                // Number of comparisons
                int parts = Math.max(latestParts.length, currentParts.length);
                
                // Check version numbers
                for (int i = 0; i < parts; i++) {
                    int latest  = getPart(latestParts, i);
                    int current = getPart(currentParts, i);
                    
                    // Early returns
                    if (current > latest) {
                        return;
                    }
                    
                    if (current < latest) {
                        // Use the scheduler to avoid concurrency complaints
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                            @Override
                            public void run() {
                                if (player == null) {
                                    Messenger.info("MobArena v" + latestVersion + " is now available!");
                                    Messenger.info("Your version: v" + currentVersion);
                                } else if (player.isOnline()) {
                                    Messenger.tellPlayer(player, "MobArena v" + latestVersion + " is now available!");
                                    Messenger.tellPlayer(player, "Your version: v" + currentVersion);
                                }
                            }
                        }, (player == null ? 0 : 60));
                        return;
                    }
                }
                Messenger.info("Later!");
            }
        });
        
        // Start the damn thing.
        thread.start();
    }
    
    private static int getPart(String[] parts, int index) {
        try {
            return Integer.parseInt(parts[index]);
        } catch (Exception e) {
            return 0;
        }
    }
    
    // Format is "<MAJOR>.<MINOR>.<PATCH>"
    private static String getLatestVersion() {
        // Input stream for the feed
        InputStream is = null;
        
        // The version string
        String version = null;
        
        try {
            // Open the stream
            is = new URL(url).openStream();

            // Build the document
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
            
            // Get all the "title" elements (where the version string will be)
            NodeList list = doc.getElementsByTagName("title");
            
            // Loop through the items, and break when a version string has been found.
            for (int i = 0; i < list.getLength(); i++) {
                String line = list.item(i).getTextContent();
                if (line.matches("MobArena v.*")) {
                    version = line.substring("MobArena v".length(), line.length());
                    break;
                }
            }
        } catch (Exception e) {
            Messenger.warning("The version checker failed.");
            Messenger.warning("This is harmless, but check for updates manually!");
        } finally {
            try {
                if (is != null) is.close();
            } catch (Exception ex) {
                Messenger.severe("Failed to close the version checker stream!");
            }
        }
        return version;
    }
}
