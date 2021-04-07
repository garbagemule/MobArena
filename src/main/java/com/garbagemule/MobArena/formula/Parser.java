package com.garbagemule.MobArena.formula;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

class Parser {

    private final Environment env;

    private Deque<Formula> output;
    private Deque<Lexeme> stack;
    private Deque<Integer> args;
    private String source;
    private List<Lexeme> input;

    Parser(Environment env) {
        this.env = env;
    }

    Formula parse(String source, List<Lexeme> input) {
        this.output = new ArrayDeque<>();
        this.stack = new ArrayDeque<>();
        this.args = new ArrayDeque<>();
        this.source = source;
        this.input = input;

        Formula result = parse();
        this.output = null;
        this.stack = null;
        this.args = null;
        this.source = null;
        this.input = null;

        return result;
    }

    private Formula parse() {
        for (Lexeme lexeme : input) {
            switch (lexeme.token.type) {
                case NUMBER: {
                    number(lexeme);
                    break;
                }
                case IDENTIFIER: {
                    identifier(lexeme);
                    break;
                }
                case VARIABLE: {
                    variable(lexeme);
                    break;
                }
                case UNARY_OPERATOR: {
                    unary(lexeme);
                    break;
                }
                case BINARY_OPERATOR: {
                    binary(lexeme);
                    break;
                }
                case LEFT_PAREN: {
                    left(lexeme);
                    break;
                }
                case RIGHT_PAREN: {
                    right(lexeme);
                    break;
                }
                case COMMA: {
                    comma(lexeme);
                    break;
                }
                default: {
                    throw new UnexpectedToken(lexeme, source);
                }
            }
        }

        // Once we get to the end of the infix expression, we'll need to pop
        // off any remaining operators on the stack. Anything other than an
        // operator token is an error, since parenthesized expressions would
        // have already been completely resolved during conversion.
        while (!stack.isEmpty()) {
            Lexeme top = stack.peek();
            if (popIfOperator(top)) {
                continue;
            }
            if (top.token.type == TokenType.LEFT_PAREN) {
                throw new UnmatchedParenthesis(top, source);
            }
            throw new UnexpectedToken(top, source);
        }

        if (output.size() != 1) {
            throw new IllegalArgumentException("wtf bitchhhh");
        }

        return output.pop();
    }

    private void number(Lexeme lexeme) {
        // Number tokens go straight to the output.
        double value = Double.parseDouble(lexeme.value);
        Formula formula = new ValueFormula(value);
        output.push(formula);
    }

    private void identifier(Lexeme lexeme) {
        // Identifiers are either constants or functions.
        String identifier = lexeme.value;

        if (env.isConstant(identifier)) {
            // Constants go straight to the output.
            double value = env.getConstant(identifier);
            Formula formula = new ValueFormula(value);
            output.push(formula);
            return;
        }

        if (env.isFunction(identifier)) {
            // Functions go on the stack.
            stack.push(lexeme);
            args.push(1);
            return;
        }

        throw new UnknownToken("identifier", lexeme, source);
    }

    private void variable(Lexeme lexeme) {
        // Variables go straight to the output.
        String identifier = lexeme.value;

        if (env.isVariable(identifier)) {
            Formula formula = env.getVariable(identifier);
            output.push(formula);
            return;
        }

        throw new UnknownToken("variable", lexeme, source);
    }

    private void unary(Lexeme lexeme) {
        String symbol = lexeme.value;

        if (!env.isUnaryOperator(symbol)) {
            throw new UnknownToken("unary operator", lexeme, source);
        }

        stack.push(lexeme);
    }

    private void binary(Lexeme lexeme) {
        String symbol = lexeme.value;

        if (!env.isBinaryOperator(symbol)) {
            throw new UnknownToken("binary operator", lexeme, source);
        }

        BinaryOperator current = env.getBinaryOperator(symbol);

        while (!stack.isEmpty()) {
            Lexeme peek = stack.peek();
            String top = peek.value;

            if (peek.token.type == TokenType.UNARY_OPERATOR) {
                if (!env.isUnaryOperator(top)) {
                    throw new UnknownToken("unary operator", peek, source);
                }

                // It would be abuse of semantics to call unary operators
                // "associative", but if we treat them as though they are
                // right-associative, we can get robust behavior that will
                // result in -2^4 evaluating to -16 (rather than 16) if we
                // assign unary minus and exponentiation the same operator
                // precedence.
                UnaryOperator candidate = env.getUnaryOperator(top);
                if (candidate.precedence > current.precedence) {
                    popIfOperator(peek);
                    continue;
                }
                break;
            }

            if (peek.token.type == TokenType.BINARY_OPERATOR) {
                if (!env.isBinaryOperator(top)) {
                    throw new UnknownToken("binary operator", peek, source);
                }

                // Other binary operators get popped if they have a higher
                // precedence, or if they have equal precedence and are
                // left-associative.
                BinaryOperator candidate = env.getBinaryOperator(top);
                if (candidate.precedence > current.precedence) {
                    popIfOperator(peek);
                    continue;
                }
                if (candidate.precedence == current.precedence && current.left) {
                    popIfOperator(peek);
                    continue;
                }
                break;
            }

            // A left parenthesis means we need to resolve before a
            // new expression can begin. A right parenthesis means
            // we need to resolve before the current expression ends.
            // Either way, we don't pop anything.
            if (peek.token.type == TokenType.LEFT_PAREN || peek.token.type == TokenType.RIGHT_PAREN) {
                break;
            }

            throw new UnexpectedToken(peek, source);
        }

        stack.push(lexeme);
    }

    private void left(Lexeme lexeme) {
        // Left parentheses go on the stack.
        stack.push(lexeme);
    }

    private void right(Lexeme lexeme) {
        if (stack.isEmpty()) {
            throw new UnmatchedParenthesis(lexeme, source);
        }

        // Right parentheses act as terminators in much the same way
        // infix operators with low precedence do.
        while (!stack.isEmpty()) {
            Lexeme peek = stack.peek();

            // Operators go straight to the output.
            if (popIfOperator(peek)) {
                continue;
            }

            if (peek.token.type == TokenType.LEFT_PAREN) {
                // When we hit a left parenthesis, the expression that
                // the current right parenthesis belongs to is resolved,
                // so it just needs to get popped. Then, if the top of
                // the stack is a function token, it is also popped.
                stack.pop();

                if (!stack.isEmpty()) {
                    popIfFunction(stack.peek());
                }

                break;
            }

            throw new UnexpectedToken(peek, source);
        }
    }

    private void comma(Lexeme lexeme) {
        while (!stack.isEmpty()) {
            Lexeme top = stack.peek();
            TokenType type = top.token.type;

            if (popIfOperator(top)) {
                continue;
            }

            if (type == TokenType.LEFT_PAREN) {
                break;
            }

            if (type == TokenType.RIGHT_PAREN) {
                throw new UnexpectedToken(top, source);
            }

            throw new UnexpectedToken(lexeme, source);
        }

        args.push(args.pop() + 1);
    }

    private boolean popIfOperator(Lexeme lexeme) {
        String symbol = lexeme.value;

        if (lexeme.token.type == TokenType.UNARY_OPERATOR) {
            if (env.isUnaryOperator(symbol)) {
                UnaryOperator operator = env.getUnaryOperator(symbol);
                Formula argument = output.pop();
                output.push(operator.create(argument));
                stack.pop();
                return true;
            }
            throw new UnknownToken("unary operator", lexeme, source);
        }

        if (lexeme.token.type == TokenType.BINARY_OPERATOR) {
            if (env.isBinaryOperator(symbol)) {
                BinaryOperator operator = env.getBinaryOperator(symbol);
                Formula right = output.pop();
                Formula left = output.pop();
                output.push(operator.create(left, right));
                stack.pop();
                return true;
            }
            throw new UnknownToken("binary operator", lexeme, source);
        }

        return false;
    }

    private void popIfFunction(Lexeme lexeme) {
        if (lexeme.token.type != TokenType.IDENTIFIER) {
            return;
        }

        String identifier = lexeme.value;

        if (env.isUnaryFunction(identifier)) {
            UnaryFunction function = env.getUnaryFunction(identifier);
            Integer counted = args.pop();
            if (counted != 1) {
                throw new ArgumentMismatch(lexeme, 1, counted, source);
            }
            Formula argument = output.pop();
            output.push(function.create(argument));
            stack.pop();
        } else if (env.isBinaryFunction(identifier)) {
            BinaryFunction function = env.getBinaryFunction(identifier);
            Integer counted = args.pop();
            if (counted != 2) {
                throw new ArgumentMismatch(lexeme, 2, counted, source);
            }
            Formula right = output.pop();
            Formula left = output.pop();
            output.push(function.create(left, right));
            stack.pop();
        }
    }

}
