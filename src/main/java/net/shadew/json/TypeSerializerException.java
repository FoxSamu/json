package net.shadew.json;

public class TypeSerializerException extends RuntimeException {
    public TypeSerializerException() {
    }

    public TypeSerializerException(String message) {
        super(message);
    }

    public TypeSerializerException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeSerializerException(Throwable cause) {
        super(cause);
    }
}
