package dev.runefox.json.codec;

import dev.runefox.json.IncorrectSizeException;
import dev.runefox.json.IncorrectTypeException;
import dev.runefox.json.JsonNode;
import dev.runefox.json.NodeException;

/**
 * An exception thrown by codecs when they fail to encode or decode JSON data, most certainly when a check fails.
 * <p>
 * Note: Do not catch only this exception when encoding or decoding, catch a {@link NodeException} instead. Exceptions
 * thrown when reading {@link JsonNode}s ({@link IncorrectTypeException}, {@link IncorrectSizeException}) may also be
 * thrown, especially when decoding. Additionally, other applications may define other custom {@link NodeException}s.
 */
public class CodecException extends NodeException {
    public CodecException() {
    }

    public CodecException(String message) {
        super(message);
    }

    public CodecException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodecException(Throwable cause) {
        super(cause);
    }
}
