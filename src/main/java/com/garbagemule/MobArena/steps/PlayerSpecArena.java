package com.garbagemule.MobArena.steps;

import com.garbagemule.MobArena.framework.Arena;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class PlayerSpecArena {
    public static StepFactory create(Arena arena) {
        ConfigurationSection settings = arena.getSettings();

        List<StepFactory> factories = new ArrayList<>();

        if (arena.getRegion().getExitWarp() != null) {
            factories.add(Defer.it(MoveToExit.create(arena)));
        }
        factories.add(SitPets.create());
        factories.add(MoveToSpec.create(arena));
        factories.add(SetGameMode.create());
        factories.add(ClearInventory.create(arena));
        factories.add(ClearPotionEffects.create());
        factories.add(SetFlying.create());
        factories.add(SetHealth.create());
        factories.add(SetHunger.create());
        factories.add(SetExperience.create());
        factories.add(SetPlayerTime.create(settings));

        return PlayerMultiStep.create(factories, arena.getPlugin().getLogger());
    }
}
