package dev.runefox.json;

// Inherits IncorrectArrayLengthException so catch blocks catching that also catch this exception
@SuppressWarnings("deprecation")
public class IncorrectSizeException extends RuntimeException {
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

    public IncorrectSizeException(int length, int min, int max) {
        super(makeMessage(length, min, max));
    }

    private static String makeMessage(int length, int required) {
        return String.format(
            "Unmatched array length, required %d, found %d",
            required,
            length
        );
    }

    private static String makeMessage(int length, int min, int max) {
        if (max == Integer.MAX_VALUE) {
            return String.format(
                "Unmatched array length, required >=%d, found %d",
                min,
                length
            );
        } else if (min == Integer.MIN_VALUE) {
            return String.format(
                "Unmatched array length, required <=%d, found %d",
                max,
                length
            );
        }
        return String.format(
            "Unmatched array length, required %d-%d, found %d",
            min, max,
            length
        );
    }
}
