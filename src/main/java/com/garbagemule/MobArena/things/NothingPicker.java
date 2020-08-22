package com.garbagemule.MobArena.things;

class NothingPicker implements ThingPicker {

    private static final NothingPicker instance = new NothingPicker();

    @Override
    public Thing pick() {
        return null;
    }

    @Override
    public String toString() {
        return "nothing";
    }

    public static NothingPicker getInstance() {
        return instance;
    }

}
