package com.garbagemule.MobArena.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.MobArena;

public class FileUtils
{
    /**
     * Extracts all of the given resources to the given directory.
     * Note that even if the resources have different paths, they will all
     * be extracted to the given directory.
     * @param dir a directory
     * @param resources an array of resources to extract
     * @return a list of all the files that were written
     */
    public static List<File> extractResources(File dir, String... resources) {
        List<File> files = new ArrayList<File>();
        
        for (String resource : resources) {
            File file = extractResource(dir, resource);
            
            if (file != null) {
                files.add(file);
            }
        }
        return files;
    }
    
    /**
     * Extracts the given resource to the given directory.
     * @param dir a directory
     * @param resource a resource to extract
     * @return the file that was written, or null
     */
    public static File extractResource(File dir, String resource) {
        if (!dir.exists()) dir.mkdirs();
        
        // Set up our new file.
        String filename = getFilename(resource);
        File file = new File(dir, filename);
        
        // If the file already exists, don't do anything.
        if (file.exists()) return file;
        
        // Grab the resource input stream.
        InputStream in = MobArena.class.getResourceAsStream("/res/" + resource);
        if (in == null) return null;
        
        try {
            // Set up an output stream.
            FileOutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            
            // Read into the buffer and write it out to the file.
            int read = 0;
            while ((read = in.read(buffer)) > 0) {
                out.write(buffer, 0, read);
            }
            
            // Close stuff.
            in.close();
            out.close();
            
            // Return the new file.
            return file;
        }
        catch (Exception e) {}
        
        return null;
    }
    
    private static String getFilename(String resource) {
        int slash = resource.lastIndexOf("/");
        return (slash < 0 ? resource : resource.substring(slash + 1));
    }
    
    public static YamlConfiguration getConfig(MobArena plugin, String filename) {
        InputStream in = MobArena.class.getResourceAsStream("/res/" + filename);
        if (in == null) {
            Messenger.severe("Failed to load '" + filename + "', the server must be restarted!");
            return null;
        }

        try {
            YamlConfiguration result = new YamlConfiguration();
            result.load(in);
            
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            Messenger.warning("Couldn't load '" + filename + "' as stream!");
        }
        
        return null;
    }
}
