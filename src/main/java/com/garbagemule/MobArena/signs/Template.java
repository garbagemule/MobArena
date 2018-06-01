package com.garbagemule.MobArena.signs;

class Template {

    final String[] idle;
    final String[] joining;
    final String[] running;

    private Template(String[] idle, String[] joining, String[] running) {
        this.idle = idle;
        this.joining = joining;
        this.running = running;
    }

    static class Builder {

        private String id;
        private String[] base;
        private String[] idle;
        private String[] joining;
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

        Builder withRunning(String[] lines) {
            this.running = lines;
            return this;
        }

        Template build() {
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
            if (running == null) {
                running = base;
            }
            return new Template(idle, joining, running);
        }

        private void missing(String state) {
            String msg = "Missing either base template '" + id + "' or state template '" + id + "-" + state + "'";
            throw new IllegalArgumentException(msg);
        }

    }

}
