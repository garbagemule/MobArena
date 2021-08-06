package com.garbagemule.MobArena.things;

import org.bukkit.entity.Player;

/**
 * A thing is something that can be given to a player, taken from a player,
 * or something that a player can have or be in possession of. Things may have
 * any number of these three properties, which means that just because a thing
 * can be given to a player, it doesn't mean that it can also be taken away,
 * or that the player is in possession of it.
 * <p>
 * The interface exposes three methods that are all optional operations. An
 * operation returns false if it fails or if it isn't applicable to the given
 * thing (which is the same as failing).
 * <p>
 * A thing is automatically a {@link ThingPicker} for itself, which means it
 * can be used in any place that a thing picker is expected, avoiding the need
 * to wrap things in a "dummy picker".
 */
public interface Thing extends ThingPicker {

    /**
     * Give this thing to the given player.
     *
     * @param player a player, non-null
     * @return true, if this thing was given to the player, false otherwise
     */
    boolean giveTo(Player player);

    /**
     * Take this thing from the given player.
     *
     * @param player a player, non-null
     * @return true, if this thing was taken from the player, false otherwise
     */
    boolean takeFrom(Player player);

    /**
     * Check if the given player has this thing.
     *
     * @param player a player, non-null
     * @return true, if the player has this thing, false otherwise
     */
    boolean heldBy(Player player);

    @Override
    default Thing pick() {
        return this;
    }

}
