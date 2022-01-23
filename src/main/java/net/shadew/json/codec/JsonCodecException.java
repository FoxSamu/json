package net.shadew.json.codec;

import net.shadew.json.JsonException;

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
