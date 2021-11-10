package com.garbagemule.MobArena.formula;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class Lexeme {

    final Token token;
    final String value;
    final int pos;

    @Override
    public String toString() {
        return token.type + " '" + value + "'";
    }

}
