package com.garbagemule.MobArena.formula;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Environment {

    final List<Token> unary;
    final List<Token> binary;
    final List<Token> symbols;

    final Map<String, Double> constants;
    final Map<String, Formula> variables;
    final Map<String, UnaryOperator> unaryOperators;
    final Map<String, BinaryOperator> binaryOperators;
    final Map<String, UnaryFunction> unaryFunctions;
    final Map<String, BinaryFunction> binaryFunctions;

    private Environment() {
        unary = new ArrayList<>();
        binary = new ArrayList<>();
        symbols = Arrays.asList(
            Token.LEFT_PAREN,
            Token.RIGHT_PAREN,
            Token.COMMA
        );

        constants = new HashMap<>();
        variables = new HashMap<>();
        unaryOperators = new HashMap<>();
        binaryOperators = new HashMap<>();
        unaryFunctions = new HashMap<>();
        binaryFunctions = new HashMap<>();
    }

    void registerConstant(String name, double value) {
        constants.put(name, value);
    }

    void registerVariable(String name, Formula formula) {
        variables.put(name, formula);
    }

    void registerUnaryOperator(String symbol, int precedence, UnaryOperation operation) {
        unaryOperators.put(symbol, new UnaryOperator(symbol, precedence, operation));
        registerOperatorToken(TokenType.UNARY_OPERATOR, symbol, unary);
    }

    void registerBinaryOperator(String symbol, int precedence, boolean left, BinaryOperation operation) {
        binaryOperators.put(symbol, new BinaryOperator(symbol, precedence, left, operation));
        registerOperatorToken(TokenType.BINARY_OPERATOR, symbol, binary);
    }

    void registerUnaryFunction(String name, UnaryOperation operation) {
        unaryFunctions.put(name, new UnaryFunction(name, operation));
    }

    void registerBinaryFunction(String name, BinaryOperation operation) {
        binaryFunctions.put(name, new BinaryFunction(name, operation));
    }

    boolean isConstant(String identifier) {
        return constants.containsKey(identifier);
    }

    boolean isVariable(String identifier) {
        return variables.containsKey(identifier);
    }

    boolean isUnaryOperator(String symbol) {
        return unaryOperators.containsKey(symbol);
    }

    boolean isBinaryOperator(String symbol) {
        return binaryOperators.containsKey(symbol);
    }

    boolean isUnaryFunction(String identifier) {
        return unaryFunctions.containsKey(identifier);
    }

    boolean isBinaryFunction(String identifier) {
        return binaryFunctions.containsKey(identifier);
    }

    boolean isFunction(String identifier) {
        return isUnaryFunction(identifier)
            || isBinaryFunction(identifier);
    }

    double getConstant(String identifier) {
        return constants.get(identifier);
    }

    Formula getVariable(String identifier) {
        return variables.get(identifier);
    }

    UnaryOperator getUnaryOperator(String symbol) {
        return unaryOperators.get(symbol);
    }

    BinaryOperator getBinaryOperator(String symbol) {
        return binaryOperators.get(symbol);
    }

    UnaryFunction getUnaryFunction(String identifier) {
        return unaryFunctions.get(identifier);
    }

    BinaryFunction getBinaryFunction(String identifier) {
        return binaryFunctions.get(identifier);
    }

    private void registerOperatorToken(TokenType type, String operator, List<Token> operators) {
        int i;
        for (i = 0; i < operators.size(); i++) {
            if (operators.get(i).symbol.length() < operator.length()) {
                break;
            }
        }
        String escaped = operator.replaceAll("", "\\\\");
        String trimmed = escaped.substring(0, escaped.length() - 1);
        Token token = new Token(type, trimmed, operator);
        operators.add(i, token);
    }

    @SuppressWarnings("Convert2MethodRef")
    static Environment createDefault() {
        Environment result = new Environment();

        // Constants
        result.registerConstant("pi", Math.PI);
        result.registerConstant("e", Math.E);

        // Unary operators
        result.registerUnaryOperator("+", 4, value -> +value);
        result.registerUnaryOperator("-", 4, value -> -value);

        // Binary operators
        result.registerBinaryOperator("+", 2, true, (a, b) -> a + b);
        result.registerBinaryOperator("-", 2, true, (a, b) -> a - b);
        result.registerBinaryOperator("*", 3, true, (a, b) -> a * b);
        result.registerBinaryOperator("/", 3, true, (a, b) -> a / b);
        result.registerBinaryOperator("%", 3, true, (a, b) -> a % b);
        result.registerBinaryOperator("^", 4, false, (a, b) -> Math.pow(a, b));

        // Unary functions
        result.registerUnaryFunction("sqrt", Math::sqrt);
        result.registerUnaryFunction("abs", Math::abs);

        result.registerUnaryFunction("ceil", Math::ceil);
        result.registerUnaryFunction("floor", Math::floor);
        result.registerUnaryFunction("round", value -> (double) Math.round(value));

        result.registerUnaryFunction("sin", Math::sin);
        result.registerUnaryFunction("cos", Math::cos);
        result.registerUnaryFunction("tan", Math::tan);

        // Binary functions
        result.registerBinaryFunction("min", Math::min);
        result.registerBinaryFunction("max", Math::max);

        return result;
    }

}
