package com.garbagemule.MobArena.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

//import org.bukkit.util.config.Configuration;

import com.garbagemule.MobArena.MobArena;
import com.garbagemule.MobArena.util.config.Config;
import com.garbagemule.MobArena.waves.WaveUtils;

public class FileUtils
{
    public static enum Library
    {
        XML("jdom.jar", "http://mirrors.ibiblio.org/pub/mirrors/maven2/org/jdom/jdom/1.1/jdom-1.1.jar", "http://garbagemule.binhoster.com/Minecraft/MobArena/jdom-1.1.jar");//"org.jdom.Content");
        
        public String filename, url, backup;
        
        private Library(String filename, String url, String backup)
        {
            this.filename = filename;
            this.url = url;
            this.backup = backup;
        }

        public static Library fromString(String string)
        {
            return WaveUtils.getEnumFromString(Library.class, string);
        }
    }
    
    /**
     * Download all necessary libraries.
     * @param config The MobArena config-file
     */
    public static void fetchLibs(MobArena plugin, Config config)
    {
        // Get all arenas
        Set<String> arenas = config.getKeys("arenas");
        if (arenas == null) return;
        
        // Add all the logging types
        Set<Library> libs = new HashSet<Library>();
        for (String a : arenas)
        {
            String type = config.getString("arenas." + a + ".settings.logging", "").toLowerCase();
            
            Library lib = Library.fromString(type.toUpperCase());
            if (lib != null)
                libs.add(lib);
        }
        
        // Download all libraries
        for (Library lib : libs)
            if (!libraryExists(plugin, lib))
                fetchLib(plugin, lib);
    }
    
    /**
     * Download a given library.
     * @param lib The Library to download
     */
    private static synchronized void fetchLib(MobArena plugin, Library lib)
    {        
        plugin.info("Downloading library '" + lib.filename + "' for log-method '" + lib.name().toLowerCase() + "'...");
        
        URLConnection con = null;
        InputStream  in   = null;
        OutputStream out  = null;
        
        // Open a connection
        try
        {
            con = new URL(lib.url).openConnection();
            con.setConnectTimeout(2000);
            con.setUseCaches(true);
        }
        catch (Exception e)
        {
            if (lib.backup == null)
            {
                e.printStackTrace();
                plugin.warning("Connection issues");
                return;
            }

            try
            {
                con = new URL(lib.backup).openConnection();
                con.setConnectTimeout(2000);
                con.setUseCaches(true);
            }
            catch (Exception e2)
            {
                e2.printStackTrace();
                plugin.warning("Connection issues");
                return;
            }
        }
        
        try
        {
            File libdir = new File(plugin.getDataFolder(), "lib");
            libdir.mkdir();
            File file = new File(libdir, lib.filename);

            long startTime = System.currentTimeMillis();
            
            // Set up the streams
            in  = con.getInputStream();
            out = new FileOutputStream(file);
            if (in == null || out == null) return;
            
            byte[] buffer = new byte[65536];
            int length = 0;

            // Write the library to disk
            while ((length = in.read(buffer)) > 0)
                out.write(buffer, 0, length);
            
            // Announce successful download
            plugin.info(lib.filename + " downloaded in " + ((System.currentTimeMillis()-startTime)/1000.0) + " seconds.");
            
            addLibraryToClassLoader(file);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            plugin.warning("Couldn't download library: " + lib.filename);
        }
        finally
        {
            try
            {
                if (in  != null) in.close();
                if (out != null) out.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    private static boolean libraryExists(JavaPlugin plugin, Library lib) {
        return new File(plugin.getDataFolder() + File.separator + "lib", lib.filename).exists();
    }
    
    private static void addLibraryToClassLoader(File file) {
        try {
            // Grab the class loader and its addURL method
            URLClassLoader cl = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{ URL.class });
            addURL.setAccessible(true);
            
            // Add the library
            addURL.invoke(cl, new Object[]{file.toURI().toURL()});
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Create default files from the res/ directory, if they exist.
     * @param filenames Files to be created.
     */
    public static void extractDefaults(MobArena plugin, String... filenames) {
        File dir = plugin.getDataFolder();
        if (!dir.exists()) dir.mkdir();
        
        for (String filename : filenames)
            extractFile(plugin, dir, filename);
    }
    
    public static File extractFile(MobArena plugin, File dir, String filename) {
        // Skip if file exists
        File file = new File(dir, filename);
        if (file.exists()) return file;
        
        // Skip if there is no resource with that name
        InputStream in = MobArena.class.getResourceAsStream("/res/" + filename);
        if (in == null) return null;
        
        try {
            // Set up an output stream
            FileOutputStream out = new FileOutputStream(file);
            byte[] buffer = new byte[8192];
            int length = 0;
            
            // Write the resource data to the file
            while ((length = in.read(buffer)) > 0)
                out.write(buffer, 0, length);
            
            if (in != null)  in.close();
            if (out != null) out.close();
            
            return file;
        }
        catch (Exception e) {
            e.printStackTrace();
            plugin.warning("Problem creating file '" + filename + "'!");
        }
        
        return null;
    }
    
    public static YamlConfiguration getConfig(MobArena plugin, String filename) {
        InputStream in = MobArena.class.getResourceAsStream("/res/" + filename);
        if (in == null) {
            plugin.error("Failed to load '" + filename + "', the server must be restarted!");
            return null;
        }

        try {
            YamlConfiguration result = new YamlConfiguration();
            result.load(in);
            
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            plugin.warning("Couldn't load '" + filename + "' as stream!");
        }
        
        return null;
    }
}
