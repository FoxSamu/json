package dev.runefox.json;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class StreamReadTests {
    private static final String STREAM = """
        {"a": 3}
        {"b": 5}{"c": 8}
                
        [3]
        """;

    private static final Json JSON = Json.json();

    @Test
    void streamReadTestNoReadBeyond() {
        assertTimeoutPreemptively(Duration.ofSeconds(1), () -> {
            PipedOutputStream pipeout = new PipedOutputStream();
            PipedInputStream pipein = new PipedInputStream();
            pipeout.connect(pipein);

            // If the system tries to read only ONE byte more, it will hang
            // forever
            byte[] data = "{\"a\": 3}".getBytes(StandardCharsets.UTF_8);
            pipeout.write(data);

            JsonInput in = JSON.input(pipein);
            assertEquals(
                JSON.parse("{\"a\": 3}"),
                in.read()
            );
        });
    }

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
