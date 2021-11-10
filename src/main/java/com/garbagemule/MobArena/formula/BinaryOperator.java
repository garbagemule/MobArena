package com.garbagemule.MobArena.formula;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class BinaryOperator {

    final String symbol;
    final int precedence;
    final boolean left;
    final BinaryOperation operation;

    Formula create(Formula left, Formula right) {
        return new BinaryFormula(operation, left, right);
    }

}
