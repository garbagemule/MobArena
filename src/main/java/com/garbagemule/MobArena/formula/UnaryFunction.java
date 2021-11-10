package com.garbagemule.MobArena.formula;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class UnaryFunction {

    final String name;
    final UnaryOperation operation;

    Formula create(Formula argument) {
        return new UnaryFormula(operation, argument);
    }

}
