package dev.runefox.json.impl.parse;

public final class CharUtil {
    public static final String NOEXEC_NOLF = ")]}'";
    public static final String NOEXEC_LF = ")]}'\n";
    public static final String NOEXEC_CR = ")]}'\r";
    public static final String NOEXEC_CRLF = ")]}'\r\n";

    public static boolean isNewline(int c) {
        return c == '\n' || c == '\r';
    }

    public static boolean isNewline5(int c) {
        return c == '\u2028' || c == '\u2029' || isNewline(c);
    }

    public static boolean isWhitespace(int c) {
        return c == ' ' || c == '\t' || isNewline(c);
    }

    public static boolean isTomlWhitespace(int c) {
        return c == ' ' || c == '\t';
    }

    public static boolean isTomlCommentValid(int c) {
        return (c == '\t' || c >= ' ') && c != 0x7F;
    }

    public static boolean isWhitespace5(int c) {
        return isNewline5(c) || isWhitespace(c)
               || c == '\uFEFF'
               || c == '\u00A0'
               || c == '\u000B'
               || c == '\f'
               || Character.isSpaceChar(c);
    }

    public static boolean isValidEscapeSequence(int c) {
        return c == 'u' || c == 'n' || c == 'r' || c == 't' || c == 'f'
               || c == 'b' || c == '/' || c == '\\' || c == '"';
    }

    public static boolean isDigit1to9(int c) {
        return c >= '1' && c <= '9';
    }

    public static boolean isDigit(int c) {
        return c >= '0' && c <= '9';
    }

    public static boolean isBit(int c) {
        return c >= '0' && c <= '1';
    }

    public static boolean isOctit(int c) {
        return c >= '0' && c <= '7';
    }

    public static boolean isHexit(int c) {
        return c >= '0' && c <= '9' || c >= 'a' && c <= 'f' || c >= 'A' && c <= 'F';
    }

    public static int hexit(int c) {
        if (c >= '0' && c <= '9') return c - '0';
        if (c >= 'a' && c <= 'f') return c - 'a' + 10;
        if (c >= 'A' && c <= 'F') return c - 'A' + 10;
        return -1;
    }

    public static int bit(int c) {
        if (c == '0') return 0;
        if (c == '1') return 1;
        return -1;
    }

    public static int octit(int c) {
        if (c >= '0' && c <= '7') return c - '0';
        return -1;
    }

    public static int digit(int c) {
        if (c >= '0' && c <= '9') return c - '0';
        return -1;
    }

    public static int radit(int c, int rx) {
        if (rx <= 10) {
            if (c >= '0' && c < '0' + rx) return c - '0';
        } else {
            if (c >= '0' && c <= '9') return c - '0';
            if (c >= 'a' && c < 'a' + rx) return c - 'a' + 10;
            if (c >= 'A' && c < 'A' + rx) return c - 'a' + 10;
        }
        return -1;
    }

    public static boolean isControl(int c) {
        return c >= 0 && c < 0x20;
    }

    public static boolean isIdentifier(int c) {
        return c >= 'a' && c <= 'z'
               || c >= 'A' && c <= 'Z'
               || c >= '0' && c <= '9'
               || c == '$' || c == '_'
               || c == '\u200C' || c == '\u200D'
               || Character.isUnicodeIdentifierPart(c);
    }

    public static boolean isIdentifierStart(int c) {
        return c >= 'a' && c <= 'z'
               || c >= 'A' && c <= 'Z'
               || c == '$' || c == '_'
               || c == '\u200C' || c == '\u200D'
               || Character.isUnicodeIdentifierStart(c);
    }

    public static boolean isIdentifierValid(String key) {
        int l = key.length();
        for (int i = 0; i < l; i++) {
            char c = key.charAt(i);

            if (i == 0 && !CharUtil.isIdentifierStart(c))
                return false;
            if (i > 0 && !CharUtil.isIdentifier(c))
                return false;
        }

        return true;
    }

    public static boolean isEof(int c) {
        return c < 0;
    }

    public static boolean isTomlIdentifier(int c) {
        return c >= 'a' && c <= 'z'
               || c >= 'A' && c <= 'Z'
               || c >= '0' && c <= '9'
               || c == '-' || c == '_';
    }

    public static boolean isTomlValueStart(int c) {
        return c >= 'a' && c <= 'z'
               || c >= 'A' && c <= 'Z'
               || c >= '0' && c <= '9'
               || c == '-' || c == '+';
    }

    public static boolean isTomlValue(int c) {
        return c >= 'a' && c <= 'z'
               || c >= 'A' && c <= 'Z'
               || c >= '0' && c <= '9'
               || c == '-' || c == '_'
               || c == '+' || c == ':'
               || c == '.' || c == ' ';
    }
}
