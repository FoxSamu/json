package dev.runefox.json.impl.parse.toml;

import dev.runefox.json.SyntaxException;
import dev.runefox.json.impl.LazyParseNumber;
import dev.runefox.json.impl.LazyParseRadix;
import dev.runefox.json.impl.parse.CharUtil;
import dev.runefox.json.impl.parse.StateLexer;
import dev.runefox.json.impl.parse.Token;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum LexValue implements StateLexer.LexerState {
    DEFAULT {
        @Override
        public Token lex(int c, StateLexer lex) throws SyntaxException {
            // EOL
            if (CharUtil.isNewline(c)) {
                lex.startToken();
                return lex.newToken(TomlTokenType.EOL, null);
            }

            // COMMENT
            if (c == '#') {
                lex.state(LexComment.COMMENT);
                return null;
            }

            // IGNORE WHITESPACE
            if (CharUtil.isTomlWhitespace(c)) {
                return null;
            }

            // EOF
            if (CharUtil.isEof(c)) {
                lex.startToken();
                return lex.newToken(TomlTokenType.EOF, null);
            }

            // ATOM VALUE (number, boolean, date)
            if (CharUtil.isTomlValueStart(c)) {
                lex.clear();
                lex.store(c);
                lex.state(KEYWORD);
                lex.startToken();
                return null;
            }

            // STRING
            if (c == '\'') {
                lex.clear();
                lex.startToken();
                lex.state(LexString.VALUE.literal);
                return null;
            }
            if (c == '\"') {
                lex.clear();
                lex.startToken();
                lex.state(LexString.VALUE.string);
                return null;
            }

            // TODO
            //   - Multiline strings
            //   - Arrays
            //   - Inline tables

            throw lex.localError("Illegal character");
        }
    },

    KEYWORD {
        // I know this is lazy, but TOML values are just very complicated :(

        private static final Pattern DEC_INT = Pattern.compile("[+-]?[1-9](?:[0-9]|_[0-9])*");
        private static final Pattern HEX_INT = Pattern.compile("[+-]?0x[0-9a-fA-F](?:[0-9a-fA-F]|_[0-9a-fA-F])*");
        private static final Pattern BIN_INT = Pattern.compile("[+-]?0b[01](?:[01]|_[01])*");
        private static final Pattern OCT_INT = Pattern.compile("[+-]?0x[0-7](?:[0-7]|_[0-7])*");
        private static final Pattern F_FLOAT = Pattern.compile("[+-]?(?:0|[1-9](?:[0-9]|_[0-9])*)\\.(?:[0-9](?:[0-9]|_[0-9])*)");
        private static final Pattern E_FLOAT = Pattern.compile("[+-]?(?:0|[1-9](?:[0-9]|_[0-9])*)[eE][+-]?(?:[0-9](?:[0-9]|_[0-9])*)");
        private static final Pattern FE_FLOAT = Pattern.compile("[+-]?(?:0|[1-9](?:[0-9]|_[0-9])*)\\.(?:[0-9](?:[0-9]|_[0-9])*)[eE][+-]?(?:[0-9](?:[0-9]|_[0-9])*)");

        private static String skipUnderscores(String str) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0, l = str.length(); i < l; i++) {
                char c = str.charAt(i);
                if (c != '_') builder.append(c);
            }
            return builder.toString();
        }

        @Override
        public Token lex(int c, StateLexer lex) throws SyntaxException {
            if (CharUtil.isTomlValue(c)) {
                lex.store(c);
                return null;
            }

            String buffer = lex.buffered();
            return switch (buffer) {
                case "inf", "+inf" -> lex.newToken(TomlTokenType.FLOAT, Double.POSITIVE_INFINITY);
                case "-inf" -> lex.newToken(TomlTokenType.FLOAT, Double.NEGATIVE_INFINITY);
                case "nan", "+nan", "-nan" -> lex.newToken(TomlTokenType.FLOAT, Double.NaN);
                case "true" -> lex.newToken(TomlTokenType.BOOLEAN, true);
                case "false" -> lex.newToken(TomlTokenType.BOOLEAN, false);

                // Some fast and common number cases
                case "0", "+0", "-0" -> lex.newToken(TomlTokenType.INTEGER, 0);
                case "1", "+1" -> lex.newToken(TomlTokenType.INTEGER, 1);
                case "-1" -> lex.newToken(TomlTokenType.INTEGER, -1);
                case "2", "+2" -> lex.newToken(TomlTokenType.INTEGER, 2);
                case "-2" -> lex.newToken(TomlTokenType.INTEGER, -2);

                default -> {
                    Matcher m = DEC_INT.matcher(buffer);
                    if (m.matches())
                        yield lex.newToken(TomlTokenType.INTEGER, new LazyParseNumber(skipUnderscores(buffer)));

                    m.reset().usePattern(HEX_INT);
                    if (m.matches())
                        yield lex.newToken(TomlTokenType.INTEGER, LazyParseRadix.hex(skipUnderscores(buffer)));

                    m.reset().usePattern(OCT_INT);
                    if (m.matches())
                        yield lex.newToken(TomlTokenType.INTEGER, LazyParseRadix.oct(skipUnderscores(buffer)));

                    m.reset().usePattern(BIN_INT);
                    if (m.matches())
                        yield lex.newToken(TomlTokenType.INTEGER, LazyParseRadix.bin(skipUnderscores(buffer)));

                    m.reset().usePattern(F_FLOAT);
                    if (m.matches())
                        yield lex.newToken(TomlTokenType.FLOAT, new LazyParseNumber(skipUnderscores(buffer)));

                    m.reset().usePattern(E_FLOAT);
                    if (m.matches())
                        yield lex.newToken(TomlTokenType.FLOAT, new LazyParseNumber(skipUnderscores(buffer)));

                    m.reset().usePattern(FE_FLOAT);
                    if (m.matches())
                        yield lex.newToken(TomlTokenType.FLOAT, new LazyParseNumber(skipUnderscores(buffer)));

                    OffsetDateTime odt = TomlDates.offsetDateTime(buffer);
                    if (odt != null) yield lex.newToken(TomlTokenType.OFFSET_DATE_TIME, odt);

                    LocalDateTime ldt = TomlDates.localDateTime(buffer);
                    if (ldt != null) yield lex.newToken(TomlTokenType.LOCAL_DATE_TIME, ldt);

                    LocalDate ld = TomlDates.localDate(buffer);
                    if (ld != null) yield lex.newToken(TomlTokenType.LOCAL_DATE, ld);

                    LocalTime lt = TomlDates.localTime(buffer);
                    if (lt != null) yield lex.newToken(TomlTokenType.LOCAL_TIME, lt);

                    throw lex.error("Misformatted value: " + buffer);
                }
            };
        }
    }
}
