package com.garbagemule.MobArena.steps;

/**
 * This wrapper allows a delegate {@link Step}'s {@link Step#run() run()}
 * operation to be deferred until this wrapper's {@link Step#undo() undo()}
 * operation is invoked. This is useful when a Step should only be invoked
 * during rollback, but not during the initial procedure.
 *
 * @see GrantRewards
 */
class Defer implements Step {
    private Step step;

    private Defer(Step step) {
        this.step = step;
    }

    @Override
    public void run() {
        // OK BOSS
    }

    @Override
    public void undo() {
        step.run();
    }

    @Override
    public String toString() {
        return "deferred(" + step + ")";
    }

    static StepFactory it(StepFactory factory) {
        return player -> new Defer(factory.create(player));
    }
}
