package net.shadew.json;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

public class LexerTests {
    @Test
    void testLexer() throws IOException {
        Json5Lexer lexer = new Json5Lexer(new StringReader(".3"));
        Token token = new Token();

        while (token.getType() != TokenType.EOF) {
            lexer.token(token);
            System.out.println(token.getType().getErrorName() + " (value: " + token.getValue() + ")");
        }
    }
}
