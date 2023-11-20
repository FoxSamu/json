package dev.runefox.json.impl.parse.toml;

import java.io.IOException;
import java.io.StringReader;

public class TomlTest {
    public static void main(String[] args) throws IOException {
        String toml = """
            key-a = 1
            key-b = 2
            key-c = 3
                        
            [[table.x.y]]
            x = 3
            y = 3
            z = 3
            """;

        StringReader reader = new StringReader(toml);
        TomlLexer lexer = new TomlLexer(reader);
        TomlParser parser = new TomlParser(lexer);

        parser.parse();
        System.out.println(parser.finishDocument());
    }
}
