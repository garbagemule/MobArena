package com.garbagemule.MobArena.steps;

import org.bukkit.entity.Player;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * PlayerMultiSteps group lists of {@link StepFactory StepFactories} into a
 * single unit, effectively realizing the Composite Pattern.
 * <p>
 * Each factory is invoked to create a given {@link Step}, which is executed
 * right away. If the Step succeeds, it is pushed to a history stack. The undo
 * operation pops the successful Steps off the history stack one by one and
 * runs the undo operation on each one in reverse order.
 * <p>
 * If one Step fails, any previous successful Steps are rolled back by running
 * their undo operations.
 */
class PlayerMultiStep extends PlayerStep {
    private final List<StepFactory> factories;
    private final Logger logger;

    private final Deque<Step> history;

    private PlayerMultiStep(Player player, List<StepFactory> factories, Logger logger) {
        super(player);
        this.factories = factories;
        this.logger = logger;
        this.history = new ArrayDeque<>();
    }

    @Override
    public void run() {
        history.clear();

        factories.forEach(factory -> {
            Step step = factory.create(player);
            try {
                step.run();
                history.push(step);
            } catch (RuntimeException up) {
                logger.log(Level.SEVERE, up, () -> "Failed to run step " + step);
                undo();
                throw up;
            }
        });
    }

    @Override
    public void undo() {
        while (!history.isEmpty()) {
            Step step = history.pop();
            try {
                step.undo();
            } catch (RuntimeException e) {
                logger.log(Level.SEVERE, e, () -> "Failed to undo step " + step);
            }
        }
    }

    static StepFactory create(List<StepFactory> factories, Logger logger) {
        return player -> new PlayerMultiStep(player, factories, logger);
    }
}
