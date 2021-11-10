package com.garbagemule.MobArena.formula;

import com.garbagemule.MobArena.framework.Arena;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class ValueFormula implements Formula {

    private final double value;

    @Override
    public double evaluate(Arena arena) {
        return value;
    }

}
