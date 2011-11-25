package com.prosicraft.MobArena.util;

import com.prosicraft.MobArena.MAUtils;
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


import com.prosicraft.MobArena.MobArena;
import com.prosicraft.mighty.logger.MLog;
import org.bukkit.configuration.file.FileConfiguration;

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
     * @param config The MobArena config-file !arenas.yml!
     */
    public static void fetchLibs(FileConfiguration config)
    {
        // Get all arenas
        Set<String> arenas = MAUtils.getKeys(config, "");
        if (arenas == null) return;
        
        // Add all the logging types
        Set<Library> libs = new HashSet<Library>();
        for (String a : arenas)
        {
            String type = config.getString(a + ".settings.logging", "").toLowerCase();
            
            Library lib = Library.fromString(type.toUpperCase());
            if (lib != null)
                libs.add(lib);
        }
        
        // Download all libraries
        for (Library lib : libs)
            if (!libraryExists(lib))
                fetchLib(lib);
    }
    
    /**
     * Download a given library.
     * @param lib The Library to download
     */
    private static synchronized void fetchLib(Library lib)
    {        
        MobArena.info("Downloading library '" + lib.filename + "' for log-method '" + lib.name().toLowerCase() + "'...");
        
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
                System.out.println("Connection issues");
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
                System.out.println("Connection issues");
                return;
            }
        }
        
        try
        {
            File libdir = new File(MobArena.dir, "lib");
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
            MobArena.info(lib.filename + " downloaded in " + ((System.currentTimeMillis()-startTime)/1000.0) + " seconds.");
            
            addLibraryToClassLoader(file);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            MobArena.warning("Couldn't download library: " + lib.filename);
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
    
    private static boolean libraryExists(Library lib)
    {
        return new File(MobArena.dir + File.separator + "lib", lib.filename).exists();
    }
    
    private static void addLibraryToClassLoader(File file)
    {
        try
        {
            // Grab the class loader and its addURL method
            URLClassLoader cl = (URLClassLoader) ClassLoader.getSystemClassLoader();
            Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{ URL.class });
            addURL.setAccessible(true);
            
            // Add the library
            addURL.invoke(cl, new Object[]{file.toURI().toURL()});
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Create default files from the res/ directory, if they exist.
     * @param filenames Files to be created.
     */
    public static void extractDefaults(String... filenames)
    {
        for (String filename : filenames)
            extractFile(MobArena.dir, filename);
    }
    
    public static File extractFile(File dir, String filename)
    {
        // Skip if file exists
        File file = new File(dir, filename);
        if (file.exists()) return file;
        
        // Skip if there is no resource with that name
        InputStream in = MobArena.class.getResourceAsStream("/res/" + filename);
        if (in == null) {
            MLog.e("The plugin seems to be damaged: Missing ressource folder.");
            return null;
        }
        
        try
        {
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
        catch (Exception e)
        {
            e.printStackTrace();
            MobArena.warning("Problem creating file '" + filename + "'!");
        }
        
        return null;
    }
}
