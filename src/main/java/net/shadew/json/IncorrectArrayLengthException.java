package net.shadew.json;

public class IncorrectArrayLengthException extends RuntimeException {
    public IncorrectArrayLengthException() {
    }

    public IncorrectArrayLengthException(String message) {
        super(message);
    }

    public IncorrectArrayLengthException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectArrayLengthException(Throwable cause) {
        super(cause);
    }

    public IncorrectArrayLengthException(int length, int required) {
        super(makeMessage(length, required));
    }

    private static String makeMessage(int length, int required) {
        return String.format(
            "Unmatched array length, required %d, found %d",
            required,
            length
        );
    }
}
