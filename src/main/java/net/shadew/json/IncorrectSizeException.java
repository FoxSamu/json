package net.shadew.json;

// Inherits IncorrectArrayLengthException so catch blocks catching that also catch this exception
@SuppressWarnings("deprecation")
public class IncorrectSizeException extends IncorrectArrayLengthException {
    public IncorrectSizeException() {
    }

    public IncorrectSizeException(String message) {
        super(message);
    }

    public IncorrectSizeException(String message, Throwable cause) {
        super(message, cause);
    }

    public IncorrectSizeException(Throwable cause) {
        super(cause);
    }

    public IncorrectSizeException(int length, int required) {
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
