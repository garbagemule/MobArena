package com.garbagemule.MobArena.formula;

class Lexeme {

    final Token token;
    final String value;
    final int pos;

    Lexeme(Token token, String value, int pos) {
        this.token = token;
        this.value = value;
        this.pos = pos;
    }

    @Override
    public String toString() {
        return token.type + " '" + value + "'";
    }

}
