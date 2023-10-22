package com.garbagemule.MobArena.formula;

import com.garbagemule.MobArena.framework.Arena;

import java.util.List;

public class FormulaManager {

    private final Environment env;
    private final Lexer lexer;
    private final Parser parser;

    FormulaManager(
        Environment env,
        Lexer lexer,
        Parser parser
    ) {
        this.env = env;
        this.lexer = lexer;
        this.parser = parser;
    }

    public void registerConstant(String name, double value) {
        env.registerConstant(name, value);
    }

    public void registerVariable(String name, Formula formula) {
        env.registerVariable(name, formula);
    }

    public void registerUnaryOperator(String symbol, int precedence, UnaryOperation operation) {
        env.registerUnaryOperator(symbol, precedence, operation);
    }

    public void registerBinaryOperator(String symbol, int precedence, boolean left, BinaryOperation operation) {
        env.registerBinaryOperator(symbol, precedence, left, operation);
    }

    public void registerUnaryFunction(String name, UnaryOperation operation) {
        env.registerUnaryFunction(name, operation);
    }

    public void registerBinaryFunction(String name, BinaryOperation operation) {
        env.registerBinaryFunction(name, operation);
    }

    public Formula parse(String input) {
        List<Lexeme> infix = lexer.tokenize(input);
        return parser.parse(input, infix);
    }

    public static FormulaManager createDefault() {
        Environment env = Environment.createDefault();

        // Wave number variables
        env.registerVariable("current-wave", a -> a.getWaveManager().getWaveNumber());
        env.registerVariable("final-wave", a -> a.getWaveManager().getFinalWave());

        // Player count variables
        env.registerVariable("initial-players", Arena::getPlayerCount);
        env.registerVariable("live-players", a -> a.getPlayersInArena().size());
        env.registerVariable("dead-players", a -> a.getPlayerCount() - a.getPlayersInArena().size());
        env.registerVariable("min-players", Arena::getMinPlayers);
        env.registerVariable("max-players", Arena::getMaxPlayers);

        // Monster count variables
        env.registerVariable("live-monsters", a -> a.getMonsterManager().getMonsters().size());

        Lexer lexer = new Lexer(env);
        Parser parser = new Parser(env);

        return new FormulaManager(env, lexer, parser);
    }

}
