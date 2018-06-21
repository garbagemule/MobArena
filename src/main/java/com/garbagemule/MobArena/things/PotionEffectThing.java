package com.garbagemule.MobArena.things;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

class PotionEffectThing implements Thing {
    private final PotionEffect effect;

    PotionEffectThing(PotionEffect effect) {
        this.effect = effect;
    }

    @Override
    public boolean giveTo(Player player) {
        return player.addPotionEffect(effect, true);
    }

    @Override
    public boolean takeFrom(Player player) {
        player.removePotionEffect(effect.getType());
        return true;
    }

    @Override
    public boolean heldBy(Player player) {
        return player.hasPotionEffect(effect.getType());
    }
}
