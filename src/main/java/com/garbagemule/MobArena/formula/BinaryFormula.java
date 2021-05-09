package com.garbagemule.MobArena.formula;

import com.garbagemule.MobArena.framework.Arena;

class BinaryFormula implements Formula {

    private final BinaryOperation operation;
    private final Formula left;
    private final Formula right;

    BinaryFormula(BinaryOperation operation, Formula left, Formula right) {
        this.operation = operation;
        this.left = left;
        this.right = right;
    }

    @Override
    public double evaluate(Arena arena) {
        double a = left.evaluate(arena);
        double b = right.evaluate(arena);
        return operation.apply(a, b);
    }

}
