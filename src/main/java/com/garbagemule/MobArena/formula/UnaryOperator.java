package com.garbagemule.MobArena.formula;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class UnaryOperator {

    final String symbol;
    final int precedence;
    final UnaryOperation operation;

    Formula create(Formula argument) {
        return new UnaryFormula(operation, argument);
    }

}
