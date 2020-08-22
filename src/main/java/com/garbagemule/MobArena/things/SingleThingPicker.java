package com.garbagemule.MobArena.things;

public class SingleThingPicker implements ThingPicker {

    private final Thing thing;

    public SingleThingPicker(Thing thing) {
        this.thing = thing;
    }

    @Override
    public Thing pick() {
        return thing;
    }

    @Override
    public String toString() {
        return thing.toString();
    }

}
