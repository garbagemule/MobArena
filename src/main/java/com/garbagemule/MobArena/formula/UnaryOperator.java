package com.garbagemule.MobArena.formula;

class UnaryOperator {

    final String symbol;
    final int precedence;
    final UnaryOperation operation;

    UnaryOperator(String symbol, int precedence, UnaryOperation operation) {
        this.symbol = symbol;
        this.precedence = precedence;
        this.operation = operation;
    }

    Formula create(Formula argument) {
        return new UnaryFormula(operation, argument);
    }

}
