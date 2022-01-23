package net.shadew.json;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IncorrectTypeException extends JsonException {
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

    IncorrectTypeException(Void invertMark, JsonType found, JsonType... prohibited) {
        super(makeMessageInv(found, prohibited));
    }

    private static String makeMessage(JsonType found, JsonType... required) {
        return String.format(
            "Unmatched types, required %s, found %s",
            Stream.of(required).map(JsonType::name).collect(Collectors.joining(", ")),
            found
        );
    }

    private static String makeMessageInv(JsonType found, JsonType... prohibited) {
        Set<JsonType> types = new HashSet<>(Arrays.asList(prohibited));
        return String.format(
            "Unmatched types, required %s, found %s",
            Stream.of(JsonType.VALUES).filter(types::contains).map(JsonType::name).collect(Collectors.joining(", ")),
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
