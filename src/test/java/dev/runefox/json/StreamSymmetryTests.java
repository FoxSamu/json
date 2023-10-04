package dev.runefox.json;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.Closeable;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StreamSymmetryTests {
    private JsonOutput out;
    private JsonInput in;

    private static final Json JSON = Json.jsonBuilder().formatConfig(
        FormattingConfig.compact()
    ).build();

    private Closeable close;

    @BeforeEach
    void before() throws Throwable {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(in);

        this.in = JSON.input(in);
        this.out = JSON.output(out);

        close = () -> {
            in.close();
            out.close();
        };
    }

    @AfterEach
    void after() throws Throwable {
        close.close();
    }

    @Test
    void symmetryTest() throws Exception {
        List<Throwable> excs = new ArrayList<>();

        Thread a = new Thread(() -> {
            try {
                out.write(JsonNode.object());
                out.write(JsonNode.array());
                out.write(JsonNode.string("Hello").wrap());
                out.flush();
                System.out.println("Written");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        Thread b = new Thread(() -> {
            try {
                assertEquals(JsonNode.object(), in.read());
                assertEquals(JsonNode.array(), in.read());
                assertEquals(JsonNode.string("Hello").wrap(), in.read());
                System.out.println("Read");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Thread.UncaughtExceptionHandler h = (thread, throwable) -> {
            excs.add(throwable);
        };

        a.setUncaughtExceptionHandler(h);
        b.setUncaughtExceptionHandler(h);

        a.start();
        b.start();
        a.join(10000);
        b.join(10000);

        if (a.isAlive() || b.isAlive()) {
            Assertions.fail("Timeout");
        }

        a.stop();
        b.stop();

        if (!excs.isEmpty()) {
            fail(excs.get(0));
        }
    }
}
