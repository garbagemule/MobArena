package com.garbagemule.MobArena.formula;

class BinaryOperator {

    final String symbol;
    final int precedence;
    final boolean left;
    final BinaryOperation operation;

    BinaryOperator(String symbol, int precedence, boolean left, BinaryOperation operation) {
        this.symbol = symbol;
        this.precedence = precedence;
        this.left = left;
        this.operation = operation;
    }

    Formula create(Formula left, Formula right) {
        return new BinaryFormula(operation, left, right);
    }

}
