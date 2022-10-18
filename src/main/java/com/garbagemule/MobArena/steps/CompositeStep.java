package com.garbagemule.MobArena.steps;

import org.bukkit.entity.Player;

public class CompositeStep extends PlayerStep {

    private final Step original;
    private final Step next;


    private CompositeStep(Player player, Step original, Step next) {
        super(player);
        this.original = original;
        this.next = next;
    }

    @Override
    public void run() {
        this.original.run();
        this.next.run();
    }

    @Override
    public void undo() {
        this.original.undo();
        this.next.undo();
    }


    public static StepFactory create(Step original, Step next) {
        return player -> new CompositeStep(player, original, next);
    }

}
