package com.garbagemule.MobArena.formula;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class BinaryFunction {

    final String name;
    final BinaryOperation operation;

    Formula create(Formula left, Formula right) {
        return new BinaryFormula(operation, left, right);
    }

}
