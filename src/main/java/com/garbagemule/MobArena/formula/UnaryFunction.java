package com.garbagemule.MobArena.formula;

class UnaryFunction {

    final String name;
    final UnaryOperation operation;

    UnaryFunction(String name, UnaryOperation operation) {
        this.name = name;
        this.operation = operation;
    }

    Formula create(Formula argument) {
        return new UnaryFormula(operation, argument);
    }

}
