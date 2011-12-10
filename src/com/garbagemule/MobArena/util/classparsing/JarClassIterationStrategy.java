package com.garbagemule.MobArena.util.classparsing;

import java.io.*;
import java.util.*;
import java.util.jar.*;

public class JarClassIterationStrategy implements ClassIterationStrategy
{
    private JarFile jarFile;
    
    /**
     * Jar iterator. The input file is turned into a JarFile object,
     * which is later used to extract a list of entries to iterate
     * over. The implemented method filters through the entries and
     * returns any matches.
     * @param file a jar file
     * @throws Exception
     */
    public JarClassIterationStrategy(File file) throws Exception {
        this.jarFile = new JarFile(file);
    }
    
    @Override
    public Collection<String> getClassesFromPackage(String packagePath) {
        // The resulting list.
        Collection<String> result = new LinkedList<String>();
        
        // Convert to an actual path.
        String path = packageToPath(packagePath);

        // Grab the entries from the jar file.
        Enumeration<JarEntry> entries = jarFile.entries();
        
        /* Iterate through all the entries in the jar file, and
         * if a file starts with the package path and ends with
         * .class, we have a match, in which case we strip the
         * extension and convert to a package representation. */
        while (entries.hasMoreElements()) {
            String entry = entries.nextElement().getName();
            
            if (entry.startsWith(path) && entry.endsWith(".class")) {
                result.add( pathToPackage( stripExtension(entry) ) );
            }
        }
        
        // Return the resulting list.
        return result;
    }

    private String stripExtension(String file) {
        int dot = file.lastIndexOf(".");
        return file.substring(0, dot);
    }
    
    private String packageToPath(String path) {
        return path.replace(".", "/");
    }
    
    private String pathToPackage(String path) {
        return path.replace("/", ".");
    }
}
