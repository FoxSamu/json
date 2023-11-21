package dev.runefox.json.impl.parse.toml;

import java.io.IOException;
import java.io.StringReader;

public class TomlTest {
    public static void main(String[] args) throws IOException {
        String toml = """
            [fruit]
            apple.color = "red"
            apple.taste.sweet = true
                        
            # [fruit.apple]  # INVALID
            # [fruit.apple.taste]  # INVALID
                        
            [fruit.apple.texture]  # you can add sub-tables
            smooth = true
            """;

        StringReader reader = new StringReader(toml);
        TomlLexer lexer = new TomlLexer(reader);
        TomlParser parser = new TomlParser(lexer);

        parser.parse();
        System.out.println(parser.finishDocument());
    }
}
