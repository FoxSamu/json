package dev.runefox.json;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IncorrectTypeException extends NodeException {
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

    public IncorrectTypeException(NodeType found, NodeType... required) {
        super(makeMessage(found, (Object[]) required));
    }

    public IncorrectTypeException(int index, NodeType found, NodeType... required) {
        super(makeMessage(index, found, (Object[]) required));
    }

    public IncorrectTypeException(String found, String... required) {
        super(makeMessage(found, (Object[]) required));
    }

    public IncorrectTypeException(int index, String found, String... required) {
        super(makeMessage(index, found, (Object[]) required));
    }


    private static String makeMessage(Object found, Object... required) {
        return String.format(
            "Unmatched types, required %s, found %s",
            Stream.of(required).map(Objects::toString).collect(Collectors.joining(", ")),
            found
        );
    }

    private static String makeMessage(int index, Object found, Object... required) {
        return String.format(
            "Unmatched types for index %d of array, required %s, found %s",
            index,
            Stream.of(required).map(Objects::toString).collect(Collectors.joining(", ")),
            found
        );
    }
}
