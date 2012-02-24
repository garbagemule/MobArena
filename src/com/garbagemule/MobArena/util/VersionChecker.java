package com.garbagemule.MobArena.util;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.garbagemule.MobArena.MobArena;

public class VersionChecker
{
    public static String site = "http://forums.bukkit.org/threads/19144/";
    
    /**
     * Check if the current plugin version is the latest version.
     * @param plugin a MobArena instance
     * @return false, if the version is not the latest, true otherwise
     */
    public static boolean isLatest(MobArena plugin) {
        if (!plugin.getMAConfig().getBoolean("global-settings.update-notification", false)) {
            return true;
        }
        
        try {
            // Make a URI of the site address
            URI baseURI = new URI(site);
            
            // Open the connection and don't redirect.
            HttpURLConnection con = (HttpURLConnection) baseURI.toURL().openConnection();
            con.setConnectTimeout(5000);
            con.setInstanceFollowRedirects(false);
            
            String header = con.getHeaderField("Location");
            
            // If something's wrong with the connection...
            if (header == null) {
                return true;
            }
            
            // Otherwise, grab the location header to get the real URI.
            String url = new URI(con.getHeaderField("Location")).toString();
            
            // Set up the regex and matcher
            Pattern regex   = Pattern.compile("v([0-9]+-)*[0-9]+");
            Matcher matcher = regex.matcher(url);
            if (!matcher.find()) {
                return true;
            }
            
            String thisVersion  = plugin.getDescription().getVersion();
            String forumVersion = matcher.group().substring(1).replace("-", ".");
            
            return isLatest(thisVersion, forumVersion);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return true;
    }
    
    /**
     * Check if thisVersion is newer than or equal to forumVersion
     * @param thisVersion a version string
     * @param forumVersion a version string
     * @return true, if thisVersion is newer than or equal to forumVersion, false otherwise
     */
    public static boolean isLatest(String thisVersion, String forumVersion) {
        String[] forumParts = forumVersion.split("\\.");
        String[] thisParts  = thisVersion.split("\\.");
        
        int current, forum;
        boolean thisIsNewer = false;

        for (int i = 0; i < Math.max(forumParts.length, thisParts.length); i++) {
            forum   = forumParts.length <= i ? 0 : Integer.parseInt(forumParts[i]);
            current = thisParts.length  <= i ? 0 : Integer.parseInt(thisParts[i]);
            
            if (forum < current) {
                thisIsNewer = true;
            }
            
            if (forum > current && !thisIsNewer) {
                return false;
            }
        }
        
        return true;
    }
}
