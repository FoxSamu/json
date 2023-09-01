package dev.runefox.json;

import org.junit.jupiter.api.Assertions;

public class JsonAsserts {
    public static void assertPath(JsonPath path, Object... steps) {
        Assertions.assertTrue(path.test(steps));
    }
}
