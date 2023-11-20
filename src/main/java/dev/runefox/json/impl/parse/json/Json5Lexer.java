package dev.runefox.json.impl.parse.json;

import dev.runefox.json.SyntaxException;
import dev.runefox.json.impl.LazyParseNumber;
import dev.runefox.json.impl.LazyParseRadix;
import dev.runefox.json.impl.parse.CharUtil;
import dev.runefox.json.impl.parse.StateLexer;
import dev.runefox.json.impl.parse.Token;

import java.io.Reader;

public class Json5Lexer extends StateLexer {

    public Json5Lexer(Reader reader) {
        super(reader);
    }

    @Override
    protected LexerState defaultState() {
        return JsonLexerState.DEFAULT;
    }

    private enum JsonLexerState implements LexerState {
        DEFAULT {
            @Override
            public Token lex(int c, StateLexer lex) throws SyntaxException {
                if (CharUtil.isEof(c)) {
                    lex.startToken();
                    return lex.newToken(JsonTokenType.EOF, null);
                } else if (CharUtil.isWhitespace5(c)) {
                    return null;
                } else if (c == '{') {
                    lex.startToken();
                    return lex.newToken(JsonTokenType.OBJECT_START, null);
                } else if (c == '}') {
                    lex.startToken();
                    return lex.newToken(JsonTokenType.OBJECT_END, null);
                } else if (c == '[') {
                    lex.startToken();
                    return lex.newToken(JsonTokenType.ARRAY_START, null);
                } else if (c == ']') {
                    lex.startToken();
                    return lex.newToken(JsonTokenType.ARRAY_END, null);
                } else if (c == ':') {
                    lex.startToken();
                    return lex.newToken(JsonTokenType.COLON, null);
                } else if (c == ',') {
                    lex.startToken();
                    lex.state(COMMA);
                    return null;
                } else if (c == '"' || c == '\'') {
                    lex.clear();
                    lex.state(STRING);
                    lex.remember(c);
                    lex.startToken();
                    return null;
                } else if (c == '/') {
                    lex.state(COMMENT_START);
                    lex.startToken();
                    return null;
                } else if (CharUtil.isDigit(c) || c == '.') {
                    lex.clear();
                    lex.retain();
                    lex.state(NUMBER_START);
                    lex.startToken();
                    return null;
                } else if (c == '-' || c == '+') {
                    lex.clear();
                    lex.store(c);
                    lex.state(NUMBER_SIGN);
                    lex.startToken();
                    return null;
                } else if (c == 'e' || c == 'E') {
                    lex.clear();
                    lex.store(c);
                    lex.state(E_IDENTIFIER);
                    lex.startToken();
                    return null;
                } else if (c == '\\') {
                    lex.clear();
                    lex.state(START_IDENTIFIER_ESCAPE);
                    lex.startToken();
                    return null;
                } else if (CharUtil.isIdentifierStart(c)) {
                    lex.clear();
                    lex.store(c);
                    lex.state(IDENTIFIER);
                    lex.startToken();
                    return null;
                }
                lex.startToken();
                throw lex.localError("Illegal character");
            }
        },
        COMMA {
            @Override
            public Token lex(int c, StateLexer lex) {
                if (CharUtil.isWhitespace5(c)) {
                    return null;
                } else if (c == ']') {
                    return lex.newToken(JsonTokenType.ARRAY_END, null);
                } else if (c == '}') {
                    return lex.newToken(JsonTokenType.OBJECT_END, null);
                } else {
                    lex.retain();
                    lex.state(DEFAULT);
                    return lex.newToken(JsonTokenType.COMMA, null);
                }
            }
        },
        STRING {
            @Override
            public Token lex(int c, StateLexer lex) throws SyntaxException {
                if (CharUtil.isNewline5(c) || CharUtil.isEof(c)) {
                    throw lex.localError("Expected string end");
                } else if (c == '\\') {
                    lex.state(STRING_ESCAPE);
                } else if (c == lex.remembered()) {
                    lex.state(DEFAULT);
                    return lex.newToken(JsonTokenType.STRING, lex.buffered());
                } else {
                    lex.store(c);
                }
                return null;
            }
        },
        STRING_ESCAPE {
            @Override
            public Token lex(int c, StateLexer lex) throws SyntaxException {
                if (CharUtil.isEof(c)) {
                    throw lex.localError("Expected string escape");
                } else if (CharUtil.isNewline5(c)) {
                    // nope
                } else if (c == 'n') {
                    lex.store('\n');
                } else if (c == 'b') {
                    lex.store('\b');
                } else if (c == 'r') {
                    lex.store('\r');
                } else if (c == 'f') {
                    lex.store('\f');
                } else if (c == 't') {
                    lex.store('\t');
                } else if (c == '0') {
                    lex.store('\0');
                } else if (c == 'u') {
                    lex.store((char) 0);
                    lex.state(UNICODE_ESCAPE_1);
                    return null;
                } else if (c == 'x') {
                    lex.store((char) 0);
                    lex.state(HEX_ESCAPE_1);
                    return null;
                } else if (!CharUtil.isDigit1to9(c)) {
                    lex.store(c);
                } else {
                    throw lex.localError("Illegal escape sequence");
                }
                lex.state(STRING);
                return null;
            }
        },
        UNICODE_ESCAPE_1 {
            @Override
            public Token lex(int c, StateLexer lex) throws SyntaxException {
                int hex = CharUtil.hexit(c);
                if (hex < 0) {
                    throw lex.localError("Expected 4 more hex digits");
                } else {
                    int s = lex.unstore();
                    s |= hex << 12;
                    lex.store((char) s);
                    lex.state(UNICODE_ESCAPE_2);
                }
                return null;
            }
        },
        UNICODE_ESCAPE_2 {
            @Override
            public Token lex(int c, StateLexer lex) throws SyntaxException {
                int hex = CharUtil.hexit(c);
                if (hex < 0) {
                    throw lex.localError("Expected 3 more hex digits");
                } else {
                    int s = lex.unstore();
                    s |= hex << 8;
                    lex.store((char) s);
                    lex.state(UNICODE_ESCAPE_3);
                }
                return null;
            }
        },
        UNICODE_ESCAPE_3 {
            @Override
            public Token lex(int c, StateLexer lex) throws SyntaxException {
                int hex = CharUtil.hexit(c);
                if (hex < 0) {
                    throw lex.localError("Expected 2 more hex digits");
                } else {
                    int s = lex.unstore();
                    s |= hex << 4;
                    lex.store((char) s);
                    lex.state(UNICODE_ESCAPE_4);
                }
                return null;
            }
        },
        UNICODE_ESCAPE_4 {
            @Override
            public Token lex(int c, StateLexer lex) throws SyntaxException {
                int hex = CharUtil.hexit(c);
                if (hex < 0) {
                    throw lex.localError("Expected 1 more hex digit");
                } else {
                    int s = lex.unstore();
                    s |= hex;
                    lex.store((char) s);
                    if (lex.remembered() == 2)
                        lex.state(IDENTIFIER_ESCAPE_END);
                    else if (lex.remembered() == 1)
                        lex.state(START_IDENTIFIER_ESCAPE_END);
                    else
                        lex.state(STRING);
                }
                return null;
            }
        },
        HEX_ESCAPE_1 {
            @Override
            public Token lex(int c, StateLexer lex) throws SyntaxException {
                int hex = CharUtil.hexit(c);
                if (hex < 0) {
                    throw lex.localError("Expected 2 more hex digits");
                } else {
                    int s = lex.unstore();
                    s |= hex << 4;
                    lex.store((char) s);
                    lex.state(HEX_ESCAPE_2);
                }
                return null;
            }
        },
        HEX_ESCAPE_2 {
            @Override
            public Token lex(int c, StateLexer lex) throws SyntaxException {
                int hex = CharUtil.hexit(c);
                if (hex < 0) {
                    throw lex.localError("Expected 1 more hex digit");
                } else {
                    int s = lex.unstore();
                    s |= hex;
                    lex.store((char) s);
                    lex.state(STRING);
                }
                return null;
            }
        },
        E_IDENTIFIER {
            @Override
            public Token lex(int c, StateLexer lex) {
                if (CharUtil.isDigit(c)) {
                    lex.store(c);
                    return null;
                } else if (CharUtil.isIdentifier(c)) {
                    lex.store(c);
                    lex.state(IDENTIFIER);
                    return null;
                } else if (c == '\\') {
                    lex.state(IDENTIFIER_ESCAPE);
                    return null;
                } else {
                    lex.state(DEFAULT);
                    lex.retain();

                    return lex.newToken(JsonTokenType.NUMBER, new LazyParseNumber(lex.buffered()));
                }
            }
        },
        IDENTIFIER {
            @Override
            public Token lex(int c, StateLexer lex) {
                if (CharUtil.isIdentifier(c)) {
                    lex.store(c);
                    return null;
                } else if (c == '\\') {
                    lex.state(IDENTIFIER_ESCAPE);
                    return null;
                } else {
                    lex.state(DEFAULT);
                    lex.retain();

                    String kw = lex.buffered();
                    switch (kw) {
                        case "false":
                            return lex.newToken(JsonTokenType.BOOLEAN, false);
                        case "true":
                            return lex.newToken(JsonTokenType.BOOLEAN, true);
                        case "null":
                            return lex.newToken(JsonTokenType.NULL, null);
                        case "NaN":
                            return lex.newToken(JsonTokenType.NUMBER, Double.NaN);
                        case "Infinity":
                            return lex.newToken(JsonTokenType.NUMBER, Double.POSITIVE_INFINITY);
                    }
                    return lex.newToken(JsonTokenType.IDENTIFIER, kw);
                }
            }
        },
        IDENTIFIER_ESCAPE {
            @Override
            public Token lex(int c, StateLexer lex) throws SyntaxException {
                if (c != 'u') {
                    throw lex.localError("Illegal identifier escape");
                }
                lex.remember(2); // Special sign for unicode escape states that we're in an identifier
                lex.store('\0');
                lex.state(UNICODE_ESCAPE_1);
                return null;
            }
        },
        IDENTIFIER_ESCAPE_END {
            @Override
            public Token lex(int c, StateLexer lex) throws SyntaxException {
                int s = lex.unstore();
                if (!CharUtil.isIdentifier(s)) {
                    throw lex.localError("Invalid identifier escape sequence");
                }
                lex.store(s);
                lex.state(IDENTIFIER);
                lex.retain();
                return null;
            }
        },
        START_IDENTIFIER_ESCAPE {
            @Override
            public Token lex(int c, StateLexer lex) throws SyntaxException {
                if (c != 'u') {
                    throw lex.localError("Illegal identifier escape");
                }
                lex.remember(1); // Special sign for unicode escape states that we're in an identifier start
                lex.store('\0');
                lex.state(UNICODE_ESCAPE_1);
                return null;
            }
        },
        START_IDENTIFIER_ESCAPE_END {
            @Override
            public Token lex(int c, StateLexer lex) throws SyntaxException {
                int s = lex.unstore();
                if (!CharUtil.isIdentifierStart(s)) {
                    throw lex.localError("Invalid identifier start escape sequence");
                }
                lex.store(s);
                lex.state(IDENTIFIER);
                lex.retain();
                return null;
            }
        },
        NUMBER_SIGN {
            @Override
            public Token lex(int c, StateLexer lex) {
                if (CharUtil.isDigit(c) || c == '.' || c == 'e' || c == 'E') {
                    lex.state(NUMBER_START);
                    lex.retain();
                    return null;
                } else if (CharUtil.isIdentifierStart(c)) {
                    lex.state(SIGNED_INFINITY_NAN);
                    lex.retain();
                    return null;
                } else {
                    lex.state(DEFAULT);
                    lex.retain();
                    return lex.newToken(JsonTokenType.NUMBER, new LazyParseNumber(lex.buffered()));
                }
            }
        },
        SIGNED_INFINITY_NAN {
            @Override
            public Token lex(int c, StateLexer lex) throws SyntaxException {
                if (CharUtil.isIdentifier(c)) {
                    lex.store(c);
                    return null;
                } else {
                    lex.state(DEFAULT);
                    lex.retain();

                    String kw = lex.buffered();
                    if ("-Infinity".equals(kw)) {
                        return lex.newToken(JsonTokenType.NUMBER, Double.NEGATIVE_INFINITY);
                    }
                    if ("+Infinity".equals(kw)) {
                        return lex.newToken(JsonTokenType.NUMBER, Double.POSITIVE_INFINITY);
                    }
                    if ("-NaN".equals(kw) || "+NaN".equals(kw)) {
                        return lex.newToken(JsonTokenType.NUMBER, Double.NaN);
                    }
                    throw lex.error("Illegal number");
                }
            }
        },
        NUMBER_START {
            @Override
            public Token lex(int c, StateLexer lex) throws SyntaxException {
                if (c == '0') {
                    lex.store(c);
                    lex.state(ZERO);
                } else if (CharUtil.isDigit(c)) {
                    lex.store(c);
                    lex.state(NUMBER_INTEGER);
                } else if (c == '.') {
                    lex.retain();
                    lex.state(NUMBER_DECIMAL_START);
                } else if (c == 'e' || c == 'E') {
                    lex.retain();
                    lex.state(NUMBER_EXPONENT_START);
                } else {
                    throw lex.localError("Expected number");
                }
                return null;
            }
        },
        ZERO {
            @Override
            public Token lex(int c, StateLexer lex) {
                if (CharUtil.isDigit(c)) {
                    lex.store(c);
                    lex.state(NUMBER_INTEGER);
                } else if (c == 'x' || c == 'X') {
                    lex.store(c);
                    lex.state(NUMBER_HEXADECIMAL);
                } else {
                    lex.retain();
                    lex.state(NUMBER_DECIMAL_START);
                }
                return null;
            }
        },
        NUMBER_HEXADECIMAL {
            @Override
            public Token lex(int c, StateLexer lex) {
                if (CharUtil.isHexit(c)) {
                    lex.store(c);
                } else {
                    lex.retain();
                    lex.state(DEFAULT);
                    String nr = lex.buffered();
                    return lex.newToken(JsonTokenType.NUMBER, LazyParseRadix.hex(nr));
                }
                return null;
            }
        },
        NUMBER_INTEGER {
            @Override
            public Token lex(int c, StateLexer lex) {
                if (CharUtil.isDigit(c)) {
                    lex.store(c);
                } else {
                    lex.retain();
                    lex.state(NUMBER_DECIMAL_START);
                }
                return null;
            }
        },
        NUMBER_DECIMAL_START {
            @Override
            public Token lex(int c, StateLexer lex) {
                if (c == '.') {
                    lex.store(c);
                    lex.state(NUMBER_DECIMAL);
                } else {
                    lex.retain();
                    lex.state(NUMBER_EXPONENT_START);
                }
                return null;
            }
        },
        NUMBER_DECIMAL {
            @Override
            public Token lex(int c, StateLexer lex) {
                if (CharUtil.isDigit(c)) {
                    lex.store(c);
                } else {
                    lex.retain();
                    lex.state(NUMBER_EXPONENT_START);
                }
                return null;
            }
        },
        NUMBER_EXPONENT_START {
            @Override
            public Token lex(int c, StateLexer lex) {
                if (c == 'e' || c == 'E') {
                    lex.store(c);
                    lex.state(NUMBER_EXPONENT_SIGN);
                } else {
                    lex.retain();
                    lex.state(NUMBER_END);
                }
                return null;
            }
        },
        NUMBER_EXPONENT_SIGN {
            @Override
            public Token lex(int c, StateLexer lex) {
                if (c == '-' || c == '+') {
                    lex.store(c);
                } else {
                    lex.retain();
                }
                lex.state(NUMBER_EXPONENT);
                return null;
            }
        },
        NUMBER_EXPONENT {
            @Override
            public Token lex(int c, StateLexer lex) {
                if (CharUtil.isDigit(c)) {
                    lex.store(c);
                } else {
                    lex.retain();
                    lex.state(NUMBER_END);
                }
                return null;
            }
        },
        NUMBER_END {
            @Override
            public Token lex(int c, StateLexer lex) {
                lex.retain();
                String nr = lex.buffered();
                return lex.newToken(JsonTokenType.NUMBER, new LazyParseNumber(nr));
            }
        },
        COMMENT_START {
            @Override
            public Token lex(int c, StateLexer lex) throws SyntaxException {
                if (c == '/') {
                    lex.state(LINE_COMMENT);
                } else if (c == '*') {
                    lex.state(BLOCK_COMMENT);
                } else {
                    throw lex.error("Illegal '/'");
                }
                return null;
            }
        },
        LINE_COMMENT {
            @Override
            public Token lex(int c, StateLexer lex) {
                if (CharUtil.isNewline5(c) || CharUtil.isEof(c)) {
                    lex.state(DEFAULT);
                }
                return null;
            }
        },
        BLOCK_COMMENT {
            @Override
            public Token lex(int c, StateLexer lex) throws SyntaxException {
                if (CharUtil.isEof(c)) {
                    throw lex.localError("Unfinished block comment");
                } else if (c == '*') {
                    lex.state(BLOCK_COMMENT_END);
                }
                return null;
            }
        },
        BLOCK_COMMENT_END {
            @Override
            public Token lex(int c, StateLexer lex) throws SyntaxException {
                if (CharUtil.isEof(c)) {
                    throw lex.localError("Unfinished block comment");
                } else if (c == '/') {
                    lex.state(DEFAULT);
                }
                return null;
            }
        }
    }
}
