package com.garbagemule.MobArena.formula;

class ParserError extends FormulaError {

    ParserError(String message, String input, Lexeme lexeme) {
        super(message, input, lexeme.pos);
    }

}
