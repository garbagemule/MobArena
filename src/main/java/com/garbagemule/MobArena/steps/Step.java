package com.garbagemule.MobArena.steps;

/**
 * Steps are small, self-contained pieces of behavior.
 * <p>
 * The {@link #run()} method of a Step executes its behavior. This could be
 * anything, but it usually involves a change in state, <em>somewhere</em>,
 * and some Steps support the optional {@link #undo()} operation that attempts
 * to revert the state back to what it was before the Step was executed.
 * <p>
 * Steps realize the Command role of the Command Pattern.
 *
 * @see StepFactory
 */
public interface Step {
    /**
     * Execute the behavior of this Step.
     */
    void run();

    /**
     * Undo a previous execution of this Step (optional operation).
     * <p>
     * This method can be called without first invoking the {@link #run()}
     * method. Doing so is silly.
     */
    void undo();
}
