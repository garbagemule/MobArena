/*
 *  Makes the handling of the new Configuration way better.
 */
package com.garbagemule.MobArena.util;

import java.io.File;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author prosicraft
 */
public class configLoader {
    private FileConfiguration   config          = null;
    private File                associatedFile  = null;
    
    public configLoader (FileConfiguration c1, File f1) {
        this.associatedFile = f1;
        this.config         = c1;
    }

    public File getAssociatedFile() {
        return associatedFile;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void setAssociatedFile(File associatedFile) {
        this.associatedFile = associatedFile;
    }

    public void setConfig(FileConfiguration config) {
        this.config = config;
    }        
}
