package com.garbagemule.MobArena.formula;

import com.garbagemule.MobArena.framework.Arena;

class UnaryFormula implements Formula {

    private final UnaryOperation operation;
    private final Formula argument;

    UnaryFormula(UnaryOperation operation, Formula argument) {
        this.operation = operation;
        this.argument = argument;
    }

    @Override
    public double evaluate(Arena arena) {
        double value = argument.evaluate(arena);
        return operation.apply(value);
    }

}
