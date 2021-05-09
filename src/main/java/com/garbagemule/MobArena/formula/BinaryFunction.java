package com.garbagemule.MobArena.formula;

class BinaryFunction {

    final String name;
    final BinaryOperation operation;

    BinaryFunction(String name, BinaryOperation operation) {
        this.name = name;
        this.operation = operation;
    }

    Formula create(Formula left, Formula right) {
        return new BinaryFormula(operation, left, right);
    }

}
