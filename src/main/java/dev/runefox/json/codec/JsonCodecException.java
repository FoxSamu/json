package dev.runefox.json.codec;

import dev.runefox.json.IncorrectSizeException;
import dev.runefox.json.IncorrectTypeException;
import dev.runefox.json.JsonException;
import dev.runefox.json.JsonNode;

/**
 * An exception thrown by codecs when they fail to encode or decode JSON data, most certainly when a check fails.
 * <p>
 * Note: Do not catch only this exception when encoding or decoding, catch a {@link JsonException} instead. Exceptions
 * thrown when reading {@link JsonNode}s ({@link IncorrectTypeException}, {@link IncorrectSizeException}) may also be
 * thrown, especially when decoding. Additionally, other applications may define other custom {@link JsonException}s.
 */
public class JsonCodecException extends JsonException {
    public JsonCodecException() {
    }

    public JsonCodecException(String message) {
        super(message);
    }

    public JsonCodecException(String message, Throwable cause) {
        super(message, cause);
    }

    public JsonCodecException(Throwable cause) {
        super(cause);
    }
}
