package com.garbagemule.MobArena.signs;

class Template {

    final String[] idle;
    final String[] joining;
    final String[] ready;
    final String[] running;

    private Template(String[] idle, String[] joining, String[] ready, String[] running) {
        this.idle = idle;
        this.joining = joining;
        this.ready = ready;
        this.running = running;
    }

    static class Builder {

        private final String id;
        private String[] base;
        private String[] idle;
        private String[] joining;
        private String[] ready;
        private String[] running;

        Builder(String id) {
            this.id = id;
        }

        Builder withBase(String[] lines) {
            this.base = lines;
            return this;
        }

        Builder withIdle(String[] lines) {
            this.idle = lines;
            return this;
        }

        Builder withJoining(String[] lines) {
            this.joining = lines;
            return this;
        }

        Builder withReady(String[] lines) {
            this.ready = lines;
            return this;
        }

        Builder withRunning(String[] lines) {
            this.running = lines;
            return this;
        }

        Template build() {
            // If the base template has not been defined, there must be
            // templates for the idle, joining, and running states.
            // A template for the ready state is optional; If not defined,
            // it will inherit from the joining template.
            if (base == null) {
                if (idle == null) {
                    missing("idle");
                }
                if (joining == null) {
                    missing("joining");
                }
                if (running == null) {
                    missing("running");
                }
            }
            if (idle == null) {
                idle = base;
            }
            if (joining == null) {
                joining = base;
            }
            if (ready == null) {
                ready = joining;
            }
            if (running == null) {
                running = base;
            }
            return new Template(idle, joining, ready, running);
        }

        private void missing(String state) {
            String msg = "Missing either base template '" + id + "' or state template '" + id + "-" + state + "'";
            throw new IllegalArgumentException(msg);
        }

    }

}
