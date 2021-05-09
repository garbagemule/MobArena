package com.garbagemule.MobArena.signs;

import org.bukkit.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class SignReader {

    private final SignFile file;
    private final SignSerializer serializer;
    private final Logger log;

    SignReader(
        SignFile file,
        SignSerializer serializer,
        Logger log
    ) {
        this.file = file;
        this.serializer = serializer;
        this.log = log;
    }

    List<ArenaSign> read(World world) throws IOException {
        List<ArenaSign> result = new ArrayList<>();
        String id = world.getUID().toString();
        String prefix = id + ";";
        for (String line : file.lines()) {
            if (line.startsWith(prefix)) {
                try {
                    ArenaSign sign = serializer.deserialize(line, world);
                    result.add(sign);
                } catch (Exception e) {
                    log.log(Level.SEVERE, "Failed to deserialize arena sign from line:\n" + line, e);
                }
            }
        }
        return result;
    }

}
