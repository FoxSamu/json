package net.shadew.json;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IncorrectTypeException extends RuntimeException {
    public IncorrectTypeException() {
    }

    public IncorrectTypeException(String message) {
        super(message);
    }

    public IncorrectTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectTypeException(Throwable cause) {
        super(cause);
    }

    public IncorrectTypeException(JsonType found, JsonType... required) {
        super(makeMessage(found, required));
    }

    public IncorrectTypeException(int index, JsonType found, JsonType... required) {
        super(makeMessage(index, found, required));
    }

    private static String makeMessage(JsonType found, JsonType... required) {
        return String.format(
            "Unmatched types, required %s, found %s",
            Stream.of(required).map(JsonType::name).collect(Collectors.joining(", ")),
            found
        );
    }

    private static String makeMessage(int index, JsonType found, JsonType... required) {
        return String.format(
            "Unmatched types for index %d of array, required %s, found %s",
            index,
            Stream.of(required).map(JsonType::name).collect(Collectors.joining(", ")),
            found
        );
    }
}
