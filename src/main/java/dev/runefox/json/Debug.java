package dev.runefox.json;

import java.util.function.Consumer;

class Debug {
    static boolean debug;
    static Consumer<Token> tokenConsumer = token -> { };
}
