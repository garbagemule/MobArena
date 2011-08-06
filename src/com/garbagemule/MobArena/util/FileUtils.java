package com.garbagemule.MobArena.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.util.config.Configuration;

import com.garbagemule.MobArena.MobArena;

public class FileUtils
{
    public static enum Libs
    {
        xml("jdom.jar", "http://mirrors.ibiblio.org/pub/mirrors/maven2/org/jdom/jdom/1.1/jdom-1.1.jar");
        
        public String url, filename;
        private Libs(String filename, String url)
        {
            this.filename = filename;
            this.url = url;
        }
        
        public static Libs getLib(String filename)
        {
            for (Libs l : Libs.values())
                if (l.filename.equals(filename))
                    return l;
            return null;
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
    
    public static void extractFile(File dir, String filename)
    {
        // Skip if file exists
        File file = new File(dir, filename);
        if (file.exists()) return;
        
        // Skip if there is no resource with that name
        InputStream in = MobArena.class.getResourceAsStream("/res/" + filename);
        if (in == null) return;
        
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
        }
        catch (Exception e)
        {
            e.printStackTrace();
            MobArena.warning("Problem creating file '" + filename + "'!");
        }
    }
    
    /**
     * Download all necessary libraries.
     * @param config The MobArena config-file
     */
    public static void fetchLibs(Configuration config)
    {
        // Get all arenas
        List<String> arenas = config.getKeys("arenas");
        if (arenas == null) return;
        
        // Add all the logging types
        Set<String> libs = new HashSet<String>();
        for (String a : arenas)
        {
            String type = config.getString("arenas." + a + ".settings.logging", "").toLowerCase();
            if (type.equals("xml"))
                libs.add(type);
        }
        
        // Download all libraries
        for (String lib : libs)
        {
            if (download(Libs.valueOf(lib)))
                continue;
            
            // If a library couldn't be downloaded, default to false.
            for (String a : arenas)
            {
                if (!config.getString("arenas." + a + ".settings.logging", "").equalsIgnoreCase(lib))
                    continue;
                
                MobArena.warning("Unrecognized format for arena '" + a + "': " + lib + ". Logging disabled.");
                config.setProperty("arenas." + a + ".logging", "false");
            }
        }
    }
    
    private static synchronized boolean download(Libs lib)
    {
        if (lib == null) return false;
        
        InputStream  in  = null;
        OutputStream out = null;
        
        try
        {
            URLConnection con = new URL(lib.url).openConnection();
            con.setUseCaches(false);
            
            // Library folder: plugin/MobArena/lib
            File libdir = new File(MobArena.dir, "lib");
            libdir.mkdir();
            
            // Create the file if it doesn't exist, if it does, return
            File file = new File(libdir, lib.filename);
            if (file.exists()) return true;

            long startTime = System.currentTimeMillis();
            MobArena.info("Downloading library: " + lib.filename + "...");
            
            // Set up the streams
            in  = con.getInputStream();
            out = new FileOutputStream(file);
            if (in == null || out == null) return false;
            
            byte[] buffer = new byte[65536];
            int length = 0;

            // Write the library to disk
            while ((length = in.read(buffer)) > 0)
                out.write(buffer, 0, length);
            
            MobArena.info(lib.filename + " downloaded in " + ((System.currentTimeMillis()-startTime)/1000.0) + " seconds.");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            MobArena.warning("Couldn't download library: " + lib.filename);
            return false;
        }
        finally
        {
            try
            {
                if (in  != null) in.close();
                if (out != null) out.close();
            }
            catch (Exception e) { e.printStackTrace(); }
        }
        return true;
    }

    public static File getMostRecent(String folder)
    {
        return getMostRecent(new File(folder));
    }
    
    public static File getMostRecent(File dir)
    {
        if (!dir.exists()) dir.mkdir();
        if (!dir.isDirectory()) return null;
        
        long mostRecent = 0;
        File result = null;
        
        for (File file : dir.listFiles())
        {
            if (file.isDirectory() || file.lastModified() > mostRecent)
                continue;
            
            result = file;
            mostRecent = file.lastModified();
        }
        
        return result;
    }
    
    public static Configuration parseXML(File file)
    {
        return null;
    }
    
    public static Configuration parseCSV(File file)
    {
        return null;
    }
    
    public static Configuration parsePlainText(File file)
    {
        return null;
    }
}
