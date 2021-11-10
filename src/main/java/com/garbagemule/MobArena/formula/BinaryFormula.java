package com.garbagemule.MobArena.formula;

import com.garbagemule.MobArena.framework.Arena;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class BinaryFormula implements Formula {

    private final BinaryOperation operation;
    private final Formula left;
    private final Formula right;

    @Override
    public double evaluate(Arena arena) {
        double a = left.evaluate(arena);
        double b = right.evaluate(arena);
        return operation.apply(a, b);
    }

}
