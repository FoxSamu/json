package dev.runefox.json;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class StreamWriteTests {
    private static final String EXPECTED = """
        {"a":3}{"b":5}{"c":8}[3]""";

    private static final Json JSON = Json.jsonBuilder().formatConfig(
        JsonSerializingConfig.compact()
    ).build();

    @Test
    void streamReadTest() throws IOException {
        Writer writer = new StringWriter();

        JsonOutput out = JSON.output(writer);
        out.write(JSON.parse("{\"a\": 3}"));
        out.write(JSON.parse("{\"b\": 5}"));
        out.write(JSON.parse("{\"c\": 8}"));
        out.write(JSON.parse("[3]"));
        out.close();

        Assertions.assertEquals(EXPECTED, writer.toString());
    }
}
