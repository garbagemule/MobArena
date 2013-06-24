package com.garbagemule.MobArena.util;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.bukkit.configuration.InvalidConfigurationException;
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
     * @param resources a list of resources to extract
     * @return a list of all the files that were written
     */
    public static List<File> extractResources(File dir, List<String> resources, Class<?> cls) {
        return extractResources(dir, "", resources, cls);
    }
    
    public static List<File> extractResources(File dir, String path, List<String> filenames, Class<?> cls) {
        List<File> files = new ArrayList<File>();
        
        // If the path is empty, just forget about it.
        if (!path.equals("")) {
            // We want no leading slashes
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            
            // But we do want trailing slashes
            if (!path.endsWith("/")) {
                path = path + "/";
            }
        }
        
        // Extract each resource
        for (String filename : filenames) {
            File file = extractResource(dir, path + filename, cls);
            
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
    public static File extractResource(File dir, String resource, Class<?> cls) {
        if (!dir.exists()) dir.mkdirs();
        
        // Set up our new file.
        String filename = getFilename(resource);
        File file = new File(dir, filename);
        
        // If the file already exists, don't do anything.
        if (file.exists()) return file;
        
        // Grab the resource input stream.
        InputStream in = cls.getResourceAsStream("/res/" + resource);
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
    
    /**
     * Lists all files in the MobArena jar file that exist on the given path.
     * The resulting list contains only filenames (with extensions)
     * @param path the path of the jar file to list files from
     * @param ext the file extension of the file type, can be null or the empty string
     * @return a list of file names
     */
    public static List<String> listFilesOnPath(String path, String ext) {
        try {
            // If the jar can't be found for some odd reason, escape.
            File file = new File("plugins" + File.separator + "MobArena.jar");
            if (file == null || !file.exists()) return null;
            
            // Create a JarFile out of the.. jar file
            JarFile jarFile = new JarFile(file);
            List<String> result = new ArrayList<String>();
            
            // JarEntry names never start with /
            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            
            // If null, replace with the empty string.
            if (ext == null) {
                ext = "";
            }
            // Require that extensions start with a period
            else if (!ext.startsWith(".")) {
                ext = "." + ext;
            }
            
            // Loop through all entries, add the ones that match the path and extension
            for (Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
                String name = entries.nextElement().getName();
                
                if (name.startsWith(path) && name.endsWith(ext)) {
                    result.add(getFilename(name));
                }
            }
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<String>(1);
        }
    }
    
    private static String getFilename(String resource) {
        int slash = resource.lastIndexOf("/");
        return (slash < 0 ? resource : resource.substring(slash + 1));
    }

    private static final String JAR = "plugins/MobArena.jar";
    private static final String RES = "res/";

    /**
     * Get a YamlConfiguration of a given resource.
     * @param filename the name of the resource
     * @return a YamlConfiguration for the given resource
     * @throws IOException if the resource does not exist
     * @throws InvalidConfigurationException if the resource is not a valid config
     */
    public static YamlConfiguration getConfig(String filename) throws IOException, InvalidConfigurationException {
        ZipFile zip  = new ZipFile(JAR);
        ZipEntry entry = zip.getEntry(RES + filename);
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.load(zip.getInputStream(entry));
        return yaml;
    }
}
