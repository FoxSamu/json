package net.shadew.json.template.parser;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class LexerTests {
    @Test
    void testLexer() throws IOException {
        Reader reader = new FileReader(new File("testfiles/file.tjson"));
        TemplateLexer lexer = new TemplateLexer(reader);
        Token token = new Token();

        while (token.getType() != TokenType.EOF) {
            lexer.token(token);
            System.out.println(token.getType().errorName() + " (value: " + token.getValue() + ")");
        }
    }
}
