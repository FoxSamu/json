package net.shadew.json;

public class TypeSerializerFormatException extends RuntimeException {
    public TypeSerializerFormatException() {
    }

    public TypeSerializerFormatException(String message) {
        super(message);
    }

    public TypeSerializerFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeSerializerFormatException(Throwable cause) {
        super(cause);
    }
}
