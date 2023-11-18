package dev.runefox.json.impl;

import dev.runefox.json.impl.parse.Token;

import java.util.function.Consumer;

public class Debug {
    public static boolean debug;
    public static Consumer<Token> tokenConsumer = token -> { };
}
