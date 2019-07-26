package com.garbagemule.MobArena.steps;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sittable;
import org.bukkit.entity.Tameable;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

class SitPets extends PlayerStep {
    private List<Entity> pets;

    private SitPets(Player player) {
        super(player);
        pets = Collections.emptyList();
    }

    @Override
    public void run() {
        pets = findNearbyPets(player);

        pets.forEach(SitPets.setSitting(true));
    }

    @Override
    public void undo() {
        pets.forEach(SitPets.setSitting(false));
    }

    static StepFactory create() {
        return SitPets::new;
    }

    private static List<Entity> findNearbyPets(Player player) {
        return player.getNearbyEntities(80, 40, 80).stream()
            .filter(SitPets.isPetOwnedBy(player))
            .filter(SitPets::isFollowing)
            .collect(Collectors.toList());
    }

    private static Predicate<Entity> isPetOwnedBy(Player player) {
        return entity -> {
            if (entity instanceof Tameable) {
                Tameable tameable = (Tameable) entity;
                return player.equals(tameable.getOwner());
            }
            return false;
        };
    }

    private static boolean isFollowing(Entity entity) {
        if (entity instanceof Sittable) {
            Sittable sittable = (Sittable) entity;
            return !sittable.isSitting();
        }
        return false;
    }

    private static Consumer<Entity> setSitting(boolean sitting) {
        return entity -> {
            if (entity instanceof Sittable) {
                Sittable sittable = (Sittable) entity;
                sittable.setSitting(sitting);
            }
        };
    }
}
