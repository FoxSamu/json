package dev.runefox.json.impl.parse.toml;

import dev.runefox.json.impl.LazyParseNumber;
import dev.runefox.json.impl.LazyParseRadix;
import dev.runefox.json.impl.parse.AbstractLexer;
import dev.runefox.json.impl.parse.CharUtil;
import dev.runefox.json.impl.parse.Token;

import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TomlLexer extends AbstractLexer {
    private int c = -2;
    private Mode mode = Mode.KEY;

    public TomlLexer(Reader reader) {
        super(reader);
    }

    public void mode(Mode mode) {
        this.mode = mode;
    }

    @Override
    protected Token readToken() throws IOException {
        return switch (mode) {
            case KEY -> readKeyToken();
            case VALUE -> readValueToken();
        };
    }

    private Token readKeyToken() throws IOException {
        int c = start();

        if (c == '[') {
            startToken();
            if (next() == '[') {
                consume();
                return newToken(TomlTokenType.ARR_TABLE_HEADER_L, null);
            }
            return newToken(TomlTokenType.TABLE_HEADER_L, null);
        }

        if (c == ']') {
            startToken();
            if (next() == ']') {
                consume();
                return newToken(TomlTokenType.ARR_TABLE_HEADER_R, null);
            }
            return newToken(TomlTokenType.TABLE_HEADER_R, null);
        }

        if (CharUtil.isTomlIdentifier(c)) {
            startToken();
            clear();
            do {
                store(c);
                c = next();
            } while (CharUtil.isTomlIdentifier(c));
            return newToken(TomlTokenType.IDENTIFIER, buffered());
        }

        if (c == '\'') {
            startToken();
            clear();
            c = next();

            while (CharUtil.isTomlCommentValid(c) && c != '\'') {
                store(c);
                c = next();
            }
            if (c == '\'') {
                consume();
                return newToken(TomlTokenType.IDENTIFIER, buffered());
            } else {
                throw error("Unclosed string literal");
            }
        }

        if (c == '"') {
            startToken();
            clear();
            c = next();

            while (CharUtil.isTomlCommentValid(c) && c != '"') {
                if (c == '\\') {
                    readStringEscape(false);
                    c = peek();
                    continue;
                } else {
                    store(c);
                }
                c = next();
            }
            if (c == '"') {
                consume();
                return newToken(TomlTokenType.IDENTIFIER, buffered());
            } else {
                throw error("Unclosed string literal");
            }
        }

        return common(c);
    }

    private Token readValueToken() throws IOException {
        int c = start();

        if (c == '[') {
            startToken();
            consume();
            return newToken(TomlTokenType.ARRAY_L, null);
        }

        if (c == ']') {
            startToken();
            consume();
            return newToken(TomlTokenType.ARRAY_R, null);
        }

        if (CharUtil.isTomlValueStart(c)) {
            startToken();
            clear();
            do {
                store(c);
                c = next();
            } while (CharUtil.isTomlValue(c));
            return handleValue(buffered());
        }

        if (c == '\'') {
            startToken();
            clear();
            c = next();

            if (c == '\'') {
                c = next();
                if (c == '\'') {
                    consume();
                    return readMultiLiteral();
                }

                return newToken(TomlTokenType.STRING, "");
            }

            while (CharUtil.isTomlCommentValid(c) && c != '\'') {
                store(c);
                c = next();
            }
            if (c == '\'') {
                consume();
                return newToken(TomlTokenType.STRING, buffered());
            } else {
                throw error("Unclosed string literal");
            }
        }

        if (c == '"') {
            startToken();
            clear();
            c = next();

            if (c == '\"') {
                c = next();
                if (c == '\"') {
                    consume();
                    return readMultiEscaped();
                }

                return newToken(TomlTokenType.STRING, "");
            }

            while (CharUtil.isTomlCommentValid(c) && c != '"') {
                if (c == '\\') {
                    readStringEscape(false);
                    c = peek();
                    continue;
                } else {
                    store(c);
                }
                c = next();
            }
            if (c == '"') {
                consume();
                return newToken(TomlTokenType.STRING, buffered());
            } else {
                throw error("Unclosed string literal");
            }
        }

        return common(c);
    }

    private Token readMultiLiteral() throws IOException {
        int c = peek();
        if (CharUtil.isNewline(c))
            c = next();

        while (CharUtil.isTomlCommentValid(c) || CharUtil.isNewline(c)) {
            if (c != '\'') {
                store(c);
                c = next();
                continue;
            }

            c = next();
            if (c != '\'') {
                store('\'');
                store(c);
                c = next();
                continue;
            }

            c = next();
            if (c != '\'') {
                store('\'');
                store('\'');
                store(c);
                c = next();
                continue;
            }
            break;
        }
        if (c == '\'') {
            consume();
            return newToken(TomlTokenType.STRING, buffered());
        } else {
            throw error("Unclosed string literal");
        }
    }


    private Token readMultiEscaped() throws IOException {
        int c = peek();
        if (CharUtil.isNewline(c))
            c = next();

        while (CharUtil.isTomlCommentValid(c) || CharUtil.isNewline(c)) {
            if (c == '\\') {
                readStringEscape(true);
                c = peek();
                continue;
            }

            if (c != '"') {
                store(c);
                c = next();
                continue;
            }

            c = next();
            if (c != '"') {
                store('"');
                store(c);
                c = next();
                continue;
            }

            c = next();
            if (c != '"') {
                store('"');
                store('"');
                store(c);
                c = next();
                continue;
            }
            break;
        }
        if (c == '"') {
            consume();
            return newToken(TomlTokenType.STRING, buffered());
        } else {
            throw error("Unclosed string literal");
        }
    }

    private int start() throws IOException {
        skipSpaces();

        int c = peek();
        if (c == '#') {
            skipComment();
            c = peek();
        }
        return c;
    }

    private Token common(int c) throws IOException {
        if (c == '{') {
            startToken();
            consume();
            return newToken(TomlTokenType.INLINE_TABLE_L, null);
        }

        if (c == '}') {
            startToken();
            consume();
            return newToken(TomlTokenType.INLINE_TABLE_R, null);
        }

        if (c == '=') {
            consume();
            return newToken(TomlTokenType.EQUALS, null);
        }

        if (c == '.') {
            consume();
            return newToken(TomlTokenType.DOT, null);
        }

        if (c == ',') {
            consume();
            return newToken(TomlTokenType.COMMA, null);
        }

        if (CharUtil.isNewline(c)) {
            startToken();
            consume();
            return newToken(TomlTokenType.EOL, null);
        }

        if (CharUtil.isEof(c)) {
            startToken();
            consume();
            return newToken(TomlTokenType.EOF, null);
        }

        throw localError("Illegal character");
    }

    private static final Pattern DEC_INT = Pattern.compile("[+-]?[1-9](?:[0-9]|_[0-9])*");
    private static final Pattern HEX_INT = Pattern.compile("[+-]?0x[0-9a-fA-F](?:[0-9a-fA-F]|_[0-9a-fA-F])*");
    private static final Pattern BIN_INT = Pattern.compile("[+-]?0b[01](?:[01]|_[01])*");
    private static final Pattern OCT_INT = Pattern.compile("[+-]?0o[0-7](?:[0-7]|_[0-7])*");
    private static final Pattern F_FLOAT = Pattern.compile("[+-]?(?:0|[1-9](?:[0-9]|_[0-9])*)\\.(?:[0-9](?:[0-9]|_[0-9])*)");
    private static final Pattern E_FLOAT = Pattern.compile("[+-]?(?:0|[1-9](?:[0-9]|_[0-9])*)[eE][+-]?(?:[0-9](?:[0-9]|_[0-9])*)");
    private static final Pattern FE_FLOAT = Pattern.compile("[+-]?(?:0|[1-9](?:[0-9]|_[0-9])*)\\.(?:[0-9](?:[0-9]|_[0-9])*)[eE][+-]?(?:[0-9](?:[0-9]|_[0-9])*)");


    private String skipUnderscores(String str) {
        clear();
        str.codePoints().forEachOrdered(i -> {
            if (i != '_')
                store(i);
        });
        return buffered();
    }

    private Token handleValue(String value) throws IOException {
        value = value.trim();
        return switch (value) {
            case "inf", "+inf" -> newToken(TomlTokenType.FLOAT, Double.POSITIVE_INFINITY);
            case "-inf" -> newToken(TomlTokenType.FLOAT, Double.NEGATIVE_INFINITY);
            case "nan", "+nan", "-nan" -> newToken(TomlTokenType.FLOAT, Double.NaN);
            case "true" -> newToken(TomlTokenType.BOOLEAN, true);
            case "false" -> newToken(TomlTokenType.BOOLEAN, false);

            // Some fast and common number cases
            case "0", "+0", "-0" -> newToken(TomlTokenType.INTEGER, 0);
            case "1", "+1" -> newToken(TomlTokenType.INTEGER, 1);
            case "-1" -> newToken(TomlTokenType.INTEGER, -1);
            case "2", "+2" -> newToken(TomlTokenType.INTEGER, 2);
            case "-2" -> newToken(TomlTokenType.INTEGER, -2);

            default -> {
                Matcher m = DEC_INT.matcher(value);
                if (m.matches()) yield newToken(TomlTokenType.INTEGER, new LazyParseNumber(skipUnderscores(value)));

                m.reset().usePattern(HEX_INT);
                if (m.matches()) yield newToken(TomlTokenType.INTEGER, LazyParseRadix.hex(skipUnderscores(value)));

                m.reset().usePattern(OCT_INT);
                if (m.matches()) yield newToken(TomlTokenType.INTEGER, LazyParseRadix.oct(skipUnderscores(value)));

                m.reset().usePattern(BIN_INT);
                if (m.matches()) yield newToken(TomlTokenType.INTEGER, LazyParseRadix.bin(skipUnderscores(value)));

                m.reset().usePattern(F_FLOAT);
                if (m.matches()) yield newToken(TomlTokenType.FLOAT, new LazyParseNumber(skipUnderscores(value)));

                m.reset().usePattern(E_FLOAT);
                if (m.matches()) yield newToken(TomlTokenType.FLOAT, new LazyParseNumber(skipUnderscores(value)));

                m.reset().usePattern(FE_FLOAT);
                if (m.matches()) yield newToken(TomlTokenType.FLOAT, new LazyParseNumber(skipUnderscores(value)));

                OffsetDateTime odt = TomlDates.offsetDateTime(value);
                if (odt != null) yield newToken(TomlTokenType.OFFSET_DATE_TIME, odt);

                LocalDateTime ldt = TomlDates.localDateTime(value);
                if (ldt != null) yield newToken(TomlTokenType.LOCAL_DATE_TIME, ldt);

                LocalDate ld = TomlDates.localDate(value);
                if (ld != null) yield newToken(TomlTokenType.LOCAL_DATE, ld);

                LocalTime lt = TomlDates.localTime(value);
                if (lt != null) yield newToken(TomlTokenType.LOCAL_TIME, lt);

                throw error("Misformatted value: " + value);
            }
        };
    }

    private void readStringEscape(boolean allowStripLine) throws IOException {
        int c = next();
        switch (c) {
            case '\\' -> store('\\');
            case '"' -> store('"');
            case 'n' -> store('\n');
            case 'b' -> store('\b');
            case 'r' -> store('\r');
            case 't' -> store('\t');
            case 'f' -> store('\f');
            case 'x', 'u', 'U' -> {
                int codePoint = 0;
                int amt = switch (c) {
                    case 'x' -> 2;
                    case 'u' -> 4;
                    default -> 8;
                };

                for (int i = 0; i < amt; i++) {
                    codePoint <<= 4;
                    c = next();
                    if (!CharUtil.isHexit(c)) {
                        throw localError("Expected hex digit");
                    }
                    codePoint |= CharUtil.hexit(c);
                }
                if (!Character.isValidCodePoint(codePoint)) {
                    throw localError("Invalid code point");
                }
                store(codePoint);
            }
            default -> {
                if (allowStripLine) {
                    skipSpaces();
                    if (CharUtil.isNewline(peek())) {
                        consume();
                        skipSpaces();
                        return;
                    }
                }
                throw localError("Invalid escape");
            }
        }
        consume();
    }

    private int peek() throws IOException {
        if (c == -2) {
            c = read();
        }
        return c;
    }

    private void consume() {
        c = -2;
    }

    private int next() throws IOException {
        consume();
        return peek();
    }

    private void skipSpaces() throws IOException {
        while (CharUtil.isTomlWhitespace(peek())) {
            consume();
        }
    }

    private void skipComment() throws IOException {
        while (CharUtil.isTomlCommentValid(peek())) {
            consume();
        }
    }

    public enum Mode {
        KEY,
        VALUE
    }
}
