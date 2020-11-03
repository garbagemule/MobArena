package com.garbagemule.MobArena.things;

/**
 * Pickers encapsulate a type of highly specific Factory pattern that revolves
 * around choosing from an arbitrary pool of Thing instances without directly
 * giving them to or taking them from players.
 * <p>
 * The interface exposes a single method, {@link #pick()}. When invoked, the
 * implementation is expected to resolve or create a {@link Thing} instance
 * or null. The how and why is entirely up to the implementation, but callers
 * must not depend on the return value being stable, even though it may be.
 */
public interface ThingPicker {

    /**
     * Pick a thing.
     *
     * @return a {@link Thing} instance, or null
     */
    Thing pick();

}
