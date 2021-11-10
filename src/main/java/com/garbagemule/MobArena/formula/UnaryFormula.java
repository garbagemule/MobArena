package com.garbagemule.MobArena.formula;

import com.garbagemule.MobArena.framework.Arena;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class UnaryFormula implements Formula {

    private final UnaryOperation operation;
    private final Formula argument;

    @Override
    public double evaluate(Arena arena) {
        double value = argument.evaluate(arena);
        return operation.apply(value);
    }

}
