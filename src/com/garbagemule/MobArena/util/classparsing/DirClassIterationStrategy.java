package com.garbagemule.MobArena.util.classparsing;

import java.io.*;
import java.util.*;

public class DirClassIterationStrategy implements ClassIterationStrategy
{
    private File baseDir;
    
    /**
     * Dir iterator. The input file must be a directory, whose
     * contents are iterated over upon calling the implemented
     * method. This dir could be bin/ or build/.
     * @param file a directory
     * @throws Exception
     */
    public DirClassIterationStrategy(File baseDir) throws Exception {
        this.baseDir = baseDir;
    }
    
    @Override
    public Collection<String> getClassesFromPackage(String packagePath) {
        // The resulting list.
        Collection<String> result = new LinkedList<String>();
        
        // Convert to an actual path.
        String path = packageToPath(packagePath);
        
        // Make a new dir from it.
        File dir = new File(baseDir, path);

        for (File f : dir.listFiles()) {
            String name = f.getName();
            String absolutePath = f.getAbsolutePath();
            
            if (absolutePath.contains(path) && name.endsWith(".class")) {
                result.add( packagePath + "." + stripExtension(name) );
            }
        }
        
        return result;
    }

    private String stripExtension(String file) {
        int dot = file.lastIndexOf(".");
        return file.substring(0, dot);
    }
    
    private String packageToPath(String path) {
        return path.replace(".", File.separator);
    }
}
