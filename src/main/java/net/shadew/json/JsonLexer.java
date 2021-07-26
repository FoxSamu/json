package net.shadew.json;

import java.io.Reader;

class JsonLexer extends AbstractLexer {

    JsonLexer(Reader reader) {
        super(reader);
    }

    @Override
    protected LexerState defaultState() {
        return JsonLexerState.DEFAULT;
    }

    private enum JsonLexerState implements LexerState {
        DEFAULT {
            @Override
            public Token lex(int c, AbstractLexer lex) throws JsonSyntaxException {
                if (CharUtil.isEof(c)) {
                    lex.startToken();
                    return lex.newToken(TokenType.EOF, null);
                } else if (CharUtil.isWhitespace(c)) {
                    return null;
                } else if (c == '{') {
                    lex.startToken();
                    return lex.newToken(TokenType.OBJECT_START, null);
                } else if (c == '}') {
                    lex.startToken();
                    return lex.newToken(TokenType.OBJECT_END, null);
                } else if (c == '[') {
                    lex.startToken();
                    return lex.newToken(TokenType.ARRAY_START, null);
                } else if (c == ']') {
                    lex.startToken();
                    return lex.newToken(TokenType.ARRAY_END, null);
                } else if (c == ':') {
                    lex.startToken();
                    return lex.newToken(TokenType.COLON, null);
                } else if (c == ',') {
                    lex.startToken();
                    return lex.newToken(TokenType.COMMA, null);
                } else if (c == '"') {
                    lex.clear();
                    lex.state(STRING);
                    lex.startToken();
                    return null;
                } else if (CharUtil.isDigit(c)) {
                    lex.clear();
                    lex.retain();
                    lex.state(NUMBER_START);
                    lex.startToken();
                    return null;
                } else if (c == '-') {
                    lex.clear();
                    lex.store('-');
                    lex.state(NUMBER_SIGN);
                    lex.startToken();
                    return null;
                } else if (CharUtil.isIdentifierStart(c)) {
                    lex.clear();
                    lex.store((char) c);
                    lex.state(KEYWORD);
                    lex.startToken();
                    return null;
                }
                lex.startToken();
                throw lex.localError("Illegal character");
            }
        },
        STRING {
            @Override
            public Token lex(int c, AbstractLexer lex) throws JsonSyntaxException {
                if (CharUtil.isNewline(c) || CharUtil.isEof(c)) {
                    throw lex.localError("Expected string end");
                } else if (c == '\\') {
                    lex.state(STRING_ESCAPE);
                } else if (c == '"') {
                    lex.state(DEFAULT);
                    return lex.newToken(TokenType.STRING, lex.buffered());
                } else {
                    lex.store((char) c);
                }
                return null;
            }
        },
        STRING_ESCAPE {
            @Override
            public Token lex(int c, AbstractLexer lex) throws JsonSyntaxException {
                if (CharUtil.isNewline(c) || CharUtil.isEof(c)) {
                    throw lex.localError("Expected string escape");
                } else if (c == '\\') {
                    lex.store('\\');
                } else if (c == '"') {
                    lex.store('"');
                } else if (c == '/') {
                    lex.store('/');
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
                } else if (c == 'u') {
                    lex.store((char) 0);
                    lex.state(UNICODE_ESCAPE_1);
                    return null;
                } else {
                    throw lex.localError("Illegal string escape");
                }
                lex.state(STRING);
                return null;
            }
        },
        UNICODE_ESCAPE_1 {
            @Override
            public Token lex(int c, AbstractLexer lex) throws JsonSyntaxException {
                int hex = CharUtil.getHexDigitValue(c);
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
            public Token lex(int c, AbstractLexer lex) throws JsonSyntaxException {
                int hex = CharUtil.getHexDigitValue(c);
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
            public Token lex(int c, AbstractLexer lex) throws JsonSyntaxException {
                int hex = CharUtil.getHexDigitValue(c);
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
            public Token lex(int c, AbstractLexer lex) throws JsonSyntaxException {
                int hex = CharUtil.getHexDigitValue(c);
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
        KEYWORD {
            @Override
            public Token lex(int c, AbstractLexer lex) throws JsonSyntaxException {
                if (CharUtil.isIdentifier(c)) {
                    lex.store((char) c);
                    return null;
                } else {
                    lex.state(DEFAULT);
                    lex.retain();

                    String kw = lex.buffered();
                    switch (kw) {
                        case "false":
                            return lex.newToken(TokenType.BOOLEAN, false);
                        case "true":
                            return lex.newToken(TokenType.BOOLEAN, true);
                        case "null":
                            return lex.newToken(TokenType.NULL, null);
                    }
                    throw lex.error("Illegal identifier");
                }
            }
        },
        NUMBER_SIGN {
            @Override
            public Token lex(int c, AbstractLexer lex) throws JsonSyntaxException {
                if (CharUtil.isDigit(c)) {
                    lex.state(NUMBER_START);
                    lex.retain();
                    return null;
                } else {
                    throw lex.localError("Expected number");
                }
            }
        },
        NUMBER_START {
            @Override
            public Token lex(int c, AbstractLexer lex) throws JsonSyntaxException {
                if (c == '0') {
                    lex.store((char) c);
                    lex.state(NUMBER_DECIMAL_START);
                } else if (CharUtil.isDigit1to9(c)) {
                    lex.store((char) c);
                    lex.state(NUMBER_INTEGER);
                } else {
                    throw lex.localError("Expected number");
                }
                return null;
            }
        },
        NUMBER_INTEGER {
            @Override
            public Token lex(int c, AbstractLexer lex) {
                if (CharUtil.isDigit(c)) {
                    lex.store((char) c);
                } else {
                    lex.retain();
                    lex.state(NUMBER_DECIMAL_START);
                }
                return null;
            }
        },
        NUMBER_DECIMAL_START {
            @Override
            public Token lex(int c, AbstractLexer lex) throws JsonSyntaxException {
                if (c == '.') {
                    lex.store((char) c);
                    lex.state(NUMBER_DECIMAL_FIRST);
                } else if (!CharUtil.isDigit(c)) {
                    lex.retain();
                    lex.state(NUMBER_EXPONENT_START);
                } else {
                    throw lex.error("Illegal digit");
                }
                return null;
            }
        },
        NUMBER_DECIMAL_FIRST {
            @Override
            public Token lex(int c, AbstractLexer lex) throws JsonSyntaxException {
                if (CharUtil.isDigit(c)) {
                    lex.store((char) c);
                    lex.state(NUMBER_DECIMAL);
                } else {
                    throw lex.localError("Expected decimal digit");
                }
                return null;
            }
        },
        NUMBER_DECIMAL {
            @Override
            public Token lex(int c, AbstractLexer lex) {
                if (CharUtil.isDigit(c)) {
                    lex.store((char) c);
                } else {
                    lex.retain();
                    lex.state(NUMBER_EXPONENT_START);
                }
                return null;
            }
        },
        NUMBER_EXPONENT_START {
            @Override
            public Token lex(int c, AbstractLexer lex) throws JsonSyntaxException {
                if (c == 'e' || c == 'E') {
                    lex.store((char) c);
                    lex.state(NUMBER_EXPONENT_SIGN);
                } else if (!CharUtil.isDigit(c)) {
                    lex.retain();
                    lex.state(NUMBER_END);
                } else {
                    throw lex.error("Illegal digit");
                }
                return null;
            }
        },
        NUMBER_EXPONENT_SIGN {
            @Override
            public Token lex(int c, AbstractLexer lex) {
                if (c == '-' || c == '+') {
                    lex.store((char) c);
                } else {
                    lex.retain();
                }
                lex.state(NUMBER_EXPONENT_FIRST);
                return null;
            }
        },
        NUMBER_EXPONENT_FIRST {
            @Override
            public Token lex(int c, AbstractLexer lex) throws JsonSyntaxException {
                if (CharUtil.isDigit(c)) {
                    lex.store((char) c);
                    lex.state(NUMBER_EXPONENT);
                } else {
                    throw lex.localError("Expected exponent digit");
                }
                return null;
            }
        },
        NUMBER_EXPONENT {
            @Override
            public Token lex(int c, AbstractLexer lex) {
                if (CharUtil.isDigit(c)) {
                    lex.store((char) c);
                } else {
                    lex.retain();
                    lex.state(NUMBER_END);
                }
                return null;
            }
        },
        NUMBER_END {
            @Override
            public Token lex(int c, AbstractLexer lex) {
                lex.retain();
                String nr = lex.buffered();
                return lex.newToken(TokenType.NUMBER, new UnparsedNumber(nr));
            }
        }
    }
}
