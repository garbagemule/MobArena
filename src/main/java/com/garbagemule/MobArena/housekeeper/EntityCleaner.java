package com.garbagemule.MobArena.housekeeper;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.region.ArenaRegion;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.util.EnumSet;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

class EntityCleaner implements Housekeeper {

    private static final EntityCleaner DEFAULT = new EntityCleaner(EnumSet.of(
        EntityType.ARROW,
        EntityType.BOAT,
        EntityType.DROPPED_ITEM,
        EntityType.EXPERIENCE_ORB,
        EntityType.MINECART,
        EntityType.SHULKER_BULLET
    ));

    private final EnumSet<EntityType> entities;

    EntityCleaner(EnumSet<EntityType> entities) {
        this.entities = entities;
    }

    @Override
    public void clean(Arena arena) {
        ArenaRegion region = arena.getRegion();

        for (Chunk chunk : region.getChunks()) {
            for (Entity entity : chunk.getEntities()) {
                if (entity == null) {
                    continue;
                }
                if (entities.contains(entity.getType())) {
                    entity.remove();
                }
            }
        }
    }

    static EntityCleaner getDefault() {
        return DEFAULT;
    }

    static EntityCleaner create(HousekeeperConfig config, Logger log) {
        if (config.entities == null || config.entities.isEmpty()) {
            return EntityCleaner.getDefault();
        }

        EnumSet<EntityType> entities = config.entities
            .stream()
            .map(value -> parse(value, log))
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(EntityType.class)));

        return new EntityCleaner(entities);
    }

    private static EntityType parse(String value, Logger log) {
        try {
            return EntityType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            if (log != null) {
                log.warning("Unknown housekeeper entity type '" + value + "', skipping...");
            }
            return null;
        }
    }

}
