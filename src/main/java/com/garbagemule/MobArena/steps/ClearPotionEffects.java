package com.garbagemule.MobArena.steps;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.Collections;

class ClearPotionEffects extends PlayerStep {
    private Collection<PotionEffect> effects;

    private ClearPotionEffects(Player player) {
        super(player);
        effects = Collections.emptyList();
    }

    @Override
    public void run() {
        effects = player.getActivePotionEffects();

        effects.stream()
            .map(PotionEffect::getType)
            .forEach(player::removePotionEffect);
    }

    @Override
    public void undo() {
        player.addPotionEffects(effects);
    }

    static StepFactory create() {
        return ClearPotionEffects::new;
    }
}
