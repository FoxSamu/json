package dev.runefox.json;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

public class StreamReadTests {
    private static final String STREAM = """
        {"a": 3}
        {"b": 5}{"c": 8}
                
        [3]
        """;

    private static final Json JSON = Json.json();

    @Test
    void streamReadTest() throws IOException {
        Reader reader = new StringReader(STREAM);

        JsonInput in = JSON.input(reader);
        assertEquals(
            JSON.parse("{\"a\": 3}"),
            in.read()
        );
        assertEquals(
            JSON.parse("{\"b\": 5}"),
            in.read()
        );
        assertEquals(
            JSON.parse("{\"c\": 8}"),
            in.read()
        );
        assertEquals(
            JSON.parse("[3]"),
            in.read()
        );
        assertNull(
            in.read()
        );
    }
}
