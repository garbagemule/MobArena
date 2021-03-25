package com.garbagemule.MobArena.signs;

import java.io.IOException;
import java.util.logging.Logger;

class SignWriter {

    private final SignFile file;
    private final SignSerializer serializer;
    private final Logger log;

    SignWriter(
        SignFile file,
        SignSerializer serializer,
        Logger log
    ) {
        this.file = file;
        this.serializer = serializer;
        this.log = log;
    }

    void write(ArenaSign sign) throws IOException {
        String line = serializer.serialize(sign);

        for (String candidate : file.lines()) {
            if (serializer.equal(candidate, line)) {
                log.warning("Erasing conflicting sign entry:\n" + candidate);
                file.erase(candidate);
            }
        }

        file.append(line);
        file.save();
    }

    void erase(ArenaSign sign) throws IOException {
        String line = serializer.serialize(sign);

        for (String candidate : file.lines()) {
            if (serializer.equal(candidate, line)) {
                file.erase(candidate);
                file.save();
                return;
            }
        }

        log.warning("No match found in sign data file for sign:\n" + line);
    }

}
