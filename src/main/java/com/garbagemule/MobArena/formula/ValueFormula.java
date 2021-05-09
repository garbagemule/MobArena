package com.garbagemule.MobArena.formula;

import com.garbagemule.MobArena.framework.Arena;

class ValueFormula implements Formula {

    private final double value;

    ValueFormula(double value) {
        this.value = value;
    }

    @Override
    public double evaluate(Arena arena) {
        return value;
    }

}
