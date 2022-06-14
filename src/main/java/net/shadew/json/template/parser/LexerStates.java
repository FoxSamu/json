package net.shadew.json.template.parser;

import java.util.*;
import java.util.stream.Collectors;

import net.shadew.json.JsonSyntaxException;
import net.shadew.json.JsonUtil;

class LexerStates {
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    //                                                                       //
    //                             S Y M B O L S                             //
    //                                                                       //
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    private static final Map<String, TokenType> SYMBOLS;
    private static final List<String> SYMBOL_LIST;
    private static final String SYMBOL_STARTS;
    private static final Map<String, SymbolPredictions> PREDICTIONS_CACHE = new HashMap<>();

    static {
        LinkedHashMap<String, TokenType> symbols = new LinkedHashMap<>();
        // Sorted by length!!!

        symbols.put(">>>=", TokenType.RRSH_IS);

        symbols.put(">>>", TokenType.RRSH);
        symbols.put("<<=", TokenType.LSH_IS);
        symbols.put(">>=", TokenType.RSH_IS);
        symbols.put("...", TokenType.TRIPLE_PERIOD);

        symbols.put("<<", TokenType.LSH);
        symbols.put(">>", TokenType.RSH);
        symbols.put("..", TokenType.DOUBLE_PERIOD);
        symbols.put("<=", TokenType.LESS_EQUAL);
        symbols.put(">=", TokenType.GREATER_EQUAL);
        symbols.put("==", TokenType.EQUAL);
        symbols.put("!=", TokenType.INEQUAL);
        symbols.put("++", TokenType.PLUS2);
        symbols.put("--", TokenType.MINUS2);
        symbols.put("&&", TokenType.AND2);
        symbols.put("||", TokenType.OR2);
        symbols.put("=", TokenType.ASSIGN);
        symbols.put("+=", TokenType.PLUS_IS);
        symbols.put("-=", TokenType.MINUS_IS);
        symbols.put("*=", TokenType.STAR_IS);
        symbols.put("/=", TokenType.SLASH_IS);
        symbols.put("%=", TokenType.PERCENT_IS);
        symbols.put("&=", TokenType.AND_IS);
        symbols.put("|=", TokenType.OR_IS);
        symbols.put("^=", TokenType.XOR_IS);
        symbols.put("->", TokenType.ARROW);

        symbols.put("(", TokenType.PAREN_OPEN);
        symbols.put(")", TokenType.PAREN_CLOSE);
        symbols.put("[", TokenType.BRACKET_OPEN);
        symbols.put("]", TokenType.BRACKET_CLOSE);
        symbols.put("{", TokenType.BRACE_OPEN);
        symbols.put("}", TokenType.BRACE_CLOSE);
        symbols.put(",", TokenType.COMMA);
        symbols.put(":", TokenType.COLON);
        symbols.put("+", TokenType.PLUS);
        symbols.put("-", TokenType.DASH);
        symbols.put("*", TokenType.STAR);
        symbols.put("/", TokenType.SLASH);
        symbols.put("%", TokenType.PERCENT);
        symbols.put(".", TokenType.PERIOD);
        symbols.put("!", TokenType.EXCL);
        symbols.put("~", TokenType.TILDE);
        symbols.put("#", TokenType.HASH);
        symbols.put("<", TokenType.LESS_THAN);
        symbols.put(">", TokenType.GREATER_THAN);
        symbols.put("&", TokenType.AND);
        symbols.put("|", TokenType.OR);
        symbols.put("^", TokenType.XOR);
        symbols.put("?", TokenType.QUESTION);
        symbols.put("@", TokenType.AT);

        SYMBOLS = Map.copyOf(symbols);
        SYMBOL_LIST = List.copyOf(symbols.keySet());
        SYMBOL_STARTS = SYMBOL_LIST.stream().map(s -> s.charAt(0) + "").distinct().collect(Collectors.joining(""));
    }

    private static TemplateLexer.LexerState getSymbolPredictions(String start) {
        return PREDICTIONS_CACHE.computeIfAbsent(start, SymbolPredictions::new);
    }

    public static class SymbolPredictions implements TemplateLexer.LexerState {
        private final String currentSymbol;
        private final List<String> options;
        private final char[] next;

        public SymbolPredictions(String current) {
            this.currentSymbol = current;
            this.options = SYMBOL_LIST.stream().filter(sym -> sym.startsWith(current)).collect(Collectors.toUnmodifiableList());

            Set<Character> nextChars = new LinkedHashSet<>();
            int charAt = current.length();
            for (String opt : options) {
                if (opt.length() > charAt) {
                    nextChars.add(opt.charAt(charAt));
                }
            }

            next = new char[nextChars.size()];
            int i = 0;
            for (Character c : nextChars) {
                next[i] = c;
            }
        }

        @Override
        public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
            for (char n : next) {
                if (c == n) {
                    String newSymbol = currentSymbol + (char) c;
                    lex.state(getSymbolPredictions(newSymbol));
                    return null;
                }
            }
            TokenType type = SYMBOLS.get(currentSymbol);
            lex.retain();
            return lex.newToken(type, null);
        }
    }

    // Periods are a special case, since they are part of a number literal as well if a digit follows
    // At the moment of encounter, we don't know this, and have to enter a special state to see what the future brings,
    // and handle appropriately (.4 is one token, while .b is two)
    public static class PeriodPredictions extends SymbolPredictions {
        public static final PeriodPredictions PERIOD = new PeriodPredictions();

        private PeriodPredictions() {
            super(".");
        }

        @Override
        public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
            if (JsonUtil.isDigit(c)) {
                lex.clear();
                lex.store('.');
                lex.store(c);
                lex.state(NumberStates.NUMBER_DECIMAL);
                return null;
            }
            return super.lex(c, lex);
        }
    }





    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    //                                                                       //
    //                                B A S E                                //
    //                                                                       //
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    public static final TemplateLexer.LexerMode DEFAULT_MODE = lex -> lex.state(BaseStates.DEFAULT);
    public static final TemplateLexer.LexerMode BRACKETS_MODE = lex -> lex.state(BaseStates.BRACKETS);

    public enum BaseStates implements TemplateLexer.LexerState {
        DEFAULT {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                if (JsonUtil.isEof(c)) {
                    lex.startToken();
                    return lex.newToken(TokenType.EOF, null);
                } else if (JsonUtil.isWhitespace(c)) {
                    return null;
                } else if (c == '[') {
                    lex.startToken();
                    lex.state(BRACKETS);
                    lex.pushMode(BRACKETS_MODE);
                    return lex.newToken(TokenType.BRACKET_OPEN, null);
                } else if (c == '.') {
                    lex.startToken();
                    lex.state(PeriodPredictions.PERIOD);
                    return null;
                } else if (SYMBOL_STARTS.indexOf(c) >= 0) {
                    lex.startToken();
                    lex.state(getSymbolPredictions((char) c + ""));
                    return null;
                } else if (JsonUtil.isIdentifierStart(c)) {
                    lex.clear();
                    lex.store(c);
                    lex.state(IdentifierKeywordStates.IDENTIFIER);
                    lex.startToken();
                    return null;
                } else if (JsonUtil.isDigit(c)) {
                    lex.clear();
                    lex.retain();
                    lex.state(NumberStates.NUMBER_START);
                    lex.startToken();
                    return null;
                } else if (c == '\'') {
                    lex.clear();
                    lex.state(BaseStringStates.SINGLE_QUOTE_1);
                    lex.startToken();
                    return null;
                } else if (c == '\"') {
                    lex.clear();
                    lex.state(BaseStringStates.DOUBLE_QUOTE_1);
                    lex.startToken();
                    return null;
                }
                lex.startToken();
                throw lex.localError("Illegal character");
            }
        },
        BRACKETS {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                if (c == ']') {
                    lex.startToken();
                    lex.popMode();
                    return lex.newToken(TokenType.BRACKET_CLOSE, null);
                }
                return DEFAULT.lex(c, lex);
            }
        }
    }





    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    //                                                                       //
    //              K E Y W O R D S   &   I D E N T I F I E R S              //
    //                                                                       //
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    private static final Map<String, TokenType> KEYWORDS;

    static {
        Map<String, TokenType> keywords = new HashMap<>();
        keywords.put("_", TokenType.UNDERSCORE);
        keywords.put("$", TokenType.DOLLAR);
        keywords.put("null", TokenType.NULL);
        keywords.put("copy", TokenType.COPY);
        keywords.put("is", TokenType.IS);
        keywords.put("isnt", TokenType.ISNT);
        keywords.put("has", TokenType.HAS);
        keywords.put("hasnt", TokenType.HASNT);
        keywords.put("if", TokenType.IF);
        keywords.put("else", TokenType.ELSE);
        keywords.put("for", TokenType.FOR);
        keywords.put("in", TokenType.IN);
        keywords.put("from", TokenType.FROM);
        keywords.put("to", TokenType.TO);
        keywords.put("switch", TokenType.SWITCH);
        keywords.put("match", TokenType.MATCH);
        keywords.put("case", TokenType.CASE);
        keywords.put("do", TokenType.DO);
        keywords.put("then", TokenType.THEN);
        keywords.put("def", TokenType.DEF);
        keywords.put("gen", TokenType.GEN);
        KEYWORDS = Map.copyOf(keywords);
    }

    public enum IdentifierKeywordStates implements TemplateLexer.LexerState {
        IDENTIFIER {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                if (JsonUtil.isIdentifier(c)) {
                    lex.store(c);
                    return null;
                } else {
                    lex.state(BaseStates.DEFAULT);
                    lex.retain();

                    String kw = lex.buffered();
                    if (kw.equals("true"))
                        return lex.newToken(TokenType.BOOLEAN, true);
                    if (kw.equals("false"))
                        return lex.newToken(TokenType.BOOLEAN, true);
                    if (kw.equals("Infinity"))
                        return lex.newToken(TokenType.NUMBER, Double.POSITIVE_INFINITY);
                    if (kw.equals("NaN"))
                        return lex.newToken(TokenType.NUMBER, Double.NaN);

                    TokenType keyword = KEYWORDS.get(kw);
                    if (keyword != null)
                        return lex.newToken(keyword, null);

                    return lex.newToken(TokenType.IDENTIFIER, kw);
                }
            }
        }
    }





    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    //                                                                       //
    //                             N U M B E R S                             //
    //                                                                       //
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    public enum NumberStates implements TemplateLexer.LexerState {
        NUMBER_SIGN {
            @Override
            public Token lex(int c, TemplateLexer lex) {
                if (JsonUtil.isDigit(c) || c == '.' || c == 'e' || c == 'E') {
                    lex.state(NUMBER_START);
                    lex.retain();
                    return null;
                } else if (JsonUtil.isIdentifierStart(c)) {
                    lex.state(SIGNED_INFINITY_NAN);
                    lex.retain();
                    return null;
                } else {
                    lex.state(BaseStates.DEFAULT);
                    lex.retain();
                    return lex.newToken(TokenType.NUMBER, JsonUtil.unparsedNumber(lex.buffered()));
                }
            }
        },
        SIGNED_INFINITY_NAN {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                if (JsonUtil.isIdentifier(c)) {
                    lex.store(c);
                    return null;
                } else {
                    lex.state(BaseStates.DEFAULT);
                    lex.retain();

                    String kw = lex.buffered();
                    if ("-Infinity".equals(kw)) {
                        return lex.newToken(TokenType.NUMBER, Double.NEGATIVE_INFINITY);
                    }
                    if ("+Infinity".equals(kw)) {
                        return lex.newToken(TokenType.NUMBER, Double.POSITIVE_INFINITY);
                    }
                    if ("-NaN".equals(kw) || "+NaN".equals(kw)) {
                        return lex.newToken(TokenType.NUMBER, Double.NaN);
                    }
                    throw lex.error("Illegal number");
                }
            }
        },
        NUMBER_START {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                if (c == '0') {
                    lex.store(c);
                    lex.state(ZERO);
                } else if (JsonUtil.isDigit(c)) {
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
            public Token lex(int c, TemplateLexer lex) {
                if (JsonUtil.isDigit(c)) {
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
            public Token lex(int c, TemplateLexer lex) {
                if (JsonUtil.isHexDigit(c)) {
                    lex.store(c);
                } else {
                    lex.retain();
                    lex.state(BaseStates.DEFAULT);
                    String nr = lex.buffered();
                    return lex.newToken(TokenType.NUMBER, JsonUtil.unparsedHexNumber(nr));
                }
                return null;
            }
        },
        NUMBER_INTEGER {
            @Override
            public Token lex(int c, TemplateLexer lex) {
                if (JsonUtil.isDigit(c)) {
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
            public Token lex(int c, TemplateLexer lex) {
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
            public Token lex(int c, TemplateLexer lex) {
                if (JsonUtil.isDigit(c)) {
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
            public Token lex(int c, TemplateLexer lex) {
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
            public Token lex(int c, TemplateLexer lex) {
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
            public Token lex(int c, TemplateLexer lex) {
                if (JsonUtil.isDigit(c)) {
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
            public Token lex(int c, TemplateLexer lex) {
                lex.retain();
                String nr = lex.buffered();
                return lex.newToken(TokenType.NUMBER, JsonUtil.unparsedNumber(nr));
            }
        }
    }





    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    //                                                                       //
    //                             S T R I N G S                             //
    //                                                                       //
    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    public enum BaseStringStates implements TemplateLexer.LexerState {
        // '
        SINGLE_QUOTE_1 {
            @Override
            public Token lex(int c, TemplateLexer lex) {
                if (c == '\'') {
                    lex.state(SINGLE_QUOTE_2);
                } else {
                    lex.retain();
                    lex.state(SingleQuoteStringStates.STRING);
                }
                return null;
            }
        },
        // ''
        SINGLE_QUOTE_2 {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                if (c == '\'') {
                    // Multiline string start
                    lex.clear();
                    lex.pushMode(MULTILINE_STRING_MODE_SQ);
                    lex.state(MultilineStringStates.BASE_SQ);
                    return lex.newToken(TokenType.ML_DELIMITER, null);
                } else {
                    // Empty string
                    lex.retain();
                    lex.state(BaseStates.DEFAULT);
                    return lex.newToken(TokenType.PURE_STRING, "");
                }
            }
        },
        // "
        DOUBLE_QUOTE_1 {
            @Override
            public Token lex(int c, TemplateLexer lex) {
                if (c == '\"') {
                    lex.state(DOUBLE_QUOTE_2);
                } else {
                    lex.retain();
                    lex.pushMode(DOUBLE_QUOTE_STRING_MODE);
                    lex.state(DoubleQuoteStringStates.STRING);
                    return lex.newToken(TokenType.DQ_DELIMITER, null);
                }
                return null;
            }
        },
        // ""
        DOUBLE_QUOTE_2 {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                if (c == '\"') {
                    // Multiline string start
                    lex.clear();
                    lex.pushMode(MULTILINE_STRING_MODE_DQ);
                    lex.state(MultilineStringStates.BASE_DQ);
                    return lex.newToken(TokenType.DQ_ML_DELIMITER, null);
                } else {
                    // Empty string
                    lex.retain();
                    lex.state(BaseStates.DEFAULT);
                    return lex.newToken(TokenType.PURE_STRING, "");
                }
            }
        }
    }

    private static final int RETURN_TO_SINGLE_QUOTE_STRING = 0;
    private static final int RETURN_TO_DOUBLE_QUOTE_STRING = 1;
    private static final int RETURN_TO_SINGLE_QUOTE_MLSTRING = 2;
    private static final int RETURN_TO_DOUBLE_QUOTE_MLSTRING = 3;
    private static final TemplateLexer.LexerState[] RETURN_AFTER_ESCAPE = {
        SingleQuoteStringStates.STRING,
        DoubleQuoteStringStates.CONTENT,
        MultilineStringStates.BASE_SQ,
        MultilineStringStates.BASE_DQ
    };

    public enum StringEscapeStates implements TemplateLexer.LexerState {
        ESCAPE {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                if (JsonUtil.isNewline(c) || JsonUtil.isEof(c)) {
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
                } else if (c == '#') {
                    lex.store('#');
                } else if (c == 'u') {
                    lex.store(0);
                    lex.state(UNICODE_1);
                    return null;
                } else {
                    throw lex.localError("Illegal string escape");
                }
                lex.state(RETURN_AFTER_ESCAPE[lex.remembered()]);
                return null;
            }
        },
        UNICODE_1 {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                int hex = JsonUtil.getHexDigitValue(c);
                if (hex < 0) {
                    throw lex.localError("Expected 4 more hex digits");
                } else {
                    int s = lex.unstore();
                    s |= hex << 12;
                    lex.store(s);
                    lex.state(UNICODE_2);
                }
                return null;
            }
        },
        UNICODE_2 {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                int hex = JsonUtil.getHexDigitValue(c);
                if (hex < 0) {
                    throw lex.localError("Expected 3 more hex digits");
                } else {
                    int s = lex.unstore();
                    s |= hex << 8;
                    lex.store(s);
                    lex.state(UNICODE_3);
                }
                return null;
            }
        },
        UNICODE_3 {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                int hex = JsonUtil.getHexDigitValue(c);
                if (hex < 0) {
                    throw lex.localError("Expected 2 more hex digits");
                } else {
                    int s = lex.unstore();
                    s |= hex << 4;
                    lex.store(s);
                    lex.state(UNICODE_4);
                }
                return null;
            }
        },
        UNICODE_4 {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                int hex = JsonUtil.getHexDigitValue(c);
                if (hex < 0) {
                    throw lex.localError("Expected 1 more hex digit");
                } else {
                    int s = lex.unstore();
                    s |= hex;
                    lex.store(s);
                    lex.state(RETURN_AFTER_ESCAPE[lex.remembered()]);
                }
                return null;
            }
        }
    }

    public enum SingleQuoteStringStates implements TemplateLexer.LexerState {
        STRING {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                if (JsonUtil.isNewline(c) || JsonUtil.isEof(c)) {
                    throw lex.localError("Expected string end");
                } else if (c == '\\') {
                    lex.remember(RETURN_TO_SINGLE_QUOTE_STRING);
                    lex.state(StringEscapeStates.ESCAPE);
                } else if (c == '\'') {
                    lex.state(BaseStates.DEFAULT);
                    return lex.newToken(TokenType.PURE_STRING, lex.buffered());
                } else {
                    lex.store(c);
                }
                return null;
            }
        },
    }


    public static final TemplateLexer.LexerMode DOUBLE_QUOTE_STRING_MODE = lex -> {
        lex.clear();
        if (!(lex.state() instanceof DoubleQuoteStringStates))
            lex.state(DoubleQuoteStringStates.STRING);
    };

    public enum DoubleQuoteStringStates implements TemplateLexer.LexerState {
        STRING {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                if (JsonUtil.isNewline(c) || JsonUtil.isEof(c)) {
                    throw lex.localError("Expected string end");
                } else {
                    lex.retain();
                    lex.startToken();
                    lex.clear();
                    lex.state(CONTENT);
                    return null;
                }
            }
        },
        CONTENT {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                if (JsonUtil.isNewline(c) || JsonUtil.isEof(c)) {
                    throw lex.localError("Expected string end");
                } else if (c == '\\') {
                    lex.remember(RETURN_TO_DOUBLE_QUOTE_STRING);
                    lex.state(StringEscapeStates.ESCAPE);
                } else if (c == '\"') {
                    lex.retain();
                    lex.state(END);
                    return lex.newToken(TokenType.STRING_CONTENT, lex.buffered());
                } else if (c == '#') {
                    lex.state(HASH);
                    return null;
                } else {
                    lex.store(c);
                }
                return null;
            }
        },
        HASH {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                if (JsonUtil.isNewline(c) || JsonUtil.isEof(c)) {
                    throw lex.localError("Expected string end");
                } else if (c == '[') {
                    lex.retain();
                    lex.state(INTERPOLATION);
                    return lex.newToken(TokenType.STRING_CONTENT, lex.buffered());
                } else if (c == '#') {
                    lex.store('#');
                    // Just try again
                    lex.state(HASH);
                } else {
                    lex.store('#');
                    lex.retain();
                    lex.state(CONTENT);
                }
                return null;
            }
        },
        INTERPOLATION {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                if (c == '[') {
                    lex.state(BaseStates.BRACKETS);
                    lex.pushMode(BRACKETS_MODE);
                    lex.startToken();
                    return lex.newToken(TokenType.INTERPOLATION, null);
                } else {
                    lex.retain();
                    lex.state(CONTENT);
                }
                return null;
            }
        },
        END {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                if (c == '\"') {
                    lex.state(BaseStates.DEFAULT);
                    lex.popMode();
                    lex.startToken();
                    return lex.newToken(TokenType.DQ_DELIMITER, null);
                } else {
                    throw lex.localError("Expected string end");
                }
            }
        }
    }

    public static final TemplateLexer.LexerMode MULTILINE_STRING_MODE_DQ = lex -> {
        lex.clear();
        lex.state(MultilineStringStates.BASE_DQ);
    };

    public static final TemplateLexer.LexerMode MULTILINE_STRING_MODE_SQ = lex -> {
        lex.clear();
        lex.state(MultilineStringStates.BASE_SQ);
    };

    public enum MultilineStringStates implements TemplateLexer.LexerState {
        BASE {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                if (JsonUtil.isEof(c)) {
                    lex.startToken();
                    throw lex.localError("Expected string end");
                } else if (c == '\t') {
                    lex.startToken();
                    throw lex.localError("Illegal tab character in multiline string");
                } else if (c == ' ') {
                    lex.startToken();
                    lex.clear();
                    lex.store(c);
                    lex.state(WHITESPACE);
                    return null;
                } else if (JsonUtil.isNewline(c)) {
                    lex.startToken();
                    return lex.newToken(TokenType.ML_LINE_BREAK, null);
                } else {
                    lex.startToken();
                    lex.clear();
                    lex.store(c);
                    lex.state(CONTENT);
                    return null;
                }
            }
        },
        BASE_SQ {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                lex.remember(RETURN_TO_SINGLE_QUOTE_MLSTRING);
                if (c == '\'') {
                    lex.startToken();
                    lex.state(SQ_QUOTE1);
                    return null;
                } else if (c == '\\') {
                    lex.startToken();
                    lex.state(ESCAPE);
                    return null;
                }
                return BASE.lex(c, lex);
            }
        },
        BASE_DQ {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                lex.remember(RETURN_TO_DOUBLE_QUOTE_MLSTRING);
                if (c == '"') {
                    lex.startToken();
                    lex.state(DQ_QUOTE1);
                    return null;
                } else if (c == '\\') {
                    lex.startToken();
                    lex.state(ESCAPE);
                    return null;
                } else if (c == '#') {
                    lex.startToken();
                    lex.state(HASH);
                    return null;
                }
                return BASE.lex(c, lex);
            }
        },
        CONTENT {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                boolean dq = lex.remembered() == RETURN_TO_DOUBLE_QUOTE_MLSTRING;
                char quote = dq ? '"' : '\'';
                if (c != ' ' && c != quote && c != '\\' && (!dq || c != '#') && !JsonUtil.isNewline(c) && c != '\t' && !JsonUtil.isEof(c)) {
                    lex.store(c);
                    return null;
                } else {
                    lex.retain();
                    lex.state(dq ? BASE_DQ : BASE_SQ);
                    return lex.newToken(TokenType.STRING_CONTENT, lex.buffered());
                }
            }
        },
        HASH {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                if (JsonUtil.isEof(c)) {
                    throw lex.localError("Expected string end");
                } else if (c == '[') {
                    lex.state(BaseStates.BRACKETS);
                    lex.pushMode(BRACKETS_MODE);
                    return lex.newToken(TokenType.INTERPOLATION, null);
                } else {
                    lex.retain();
                    lex.state(BASE_DQ);
                    return lex.newToken(TokenType.STRING_CONTENT, "#");
                }
            }
        },
        SQ_QUOTE1 {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                if (c == '\'') {
                    lex.startToken();
                    lex.state(SQ_QUOTE2);
                    return null;
                } else {
                    lex.retain();
                    lex.state(BASE);
                    return lex.newToken(TokenType.STRING_CONTENT, "'");
                }
            }
        },
        SQ_QUOTE2 {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                if (c == '\'') {
                    lex.popMode();
                    return lex.newToken(TokenType.ML_DELIMITER, null);
                } else {
                    lex.retain();
                    lex.state(BASE);
                    return lex.newToken(TokenType.STRING_CONTENT, "''");
                }
            }
        },
        DQ_QUOTE1 {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                if (c == '"') {
                    lex.startToken();
                    lex.state(DQ_QUOTE2);
                    return null;
                } else {
                    lex.retain();
                    lex.state(BASE);
                    return lex.newToken(TokenType.STRING_CONTENT, "\"");
                }
            }
        },
        DQ_QUOTE2 {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                if (c == '"') {
                    lex.popMode();
                    return lex.newToken(TokenType.DQ_ML_DELIMITER, null);
                } else {
                    lex.retain();
                    lex.state(BASE);
                    return lex.newToken(TokenType.STRING_CONTENT, "\"\"");
                }
            }
        },
        WHITESPACE {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                if (JsonUtil.isEof(c)) {
                    throw lex.localError("Expected string end");
                } else if (c == '\t') {
                    throw lex.localError("Illegal tab character in multiline string");
                } else if (c == ' ') {
                    lex.store(c);
                    return null;
                } else if (JsonUtil.isNewline(c)) {
                    lex.state(RETURN_AFTER_ESCAPE[lex.remembered()]);
                    return lex.newToken(TokenType.ML_LINE_BREAK, null);
                } else {
                    lex.retain();
                    lex.state(RETURN_AFTER_ESCAPE[lex.remembered()]);
                    return lex.newToken(TokenType.ML_WHITESPACE, lex.buffered());
                }
            }
        },
        ESCAPE {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                if (c == ' ' || JsonUtil.isNewline(c)) {
                    lex.retain();
                    lex.state(RETURN_AFTER_ESCAPE[lex.remembered()]);
                    return lex.newToken(TokenType.ML_BOUNDARY_INDICATOR, null);
                } else if (c == '~') {
                    lex.state(NO_LINE_BREAK_INDICATOR);
                    return null;
                }
                return StringEscapeStates.ESCAPE.lex(c, lex);
            }
        },
        NO_LINE_BREAK_INDICATOR {
            @Override
            public Token lex(int c, TemplateLexer lex) throws JsonSyntaxException {
                if (JsonUtil.isEof(c)) {
                    throw lex.localError("Expected string end");
                } else if (c == '\t') {
                    throw lex.localError("Illegal tab character in multiline string");
                } else if (c == ' ') {
                    return null;
                } else if (JsonUtil.isNewline(c)) {
                    lex.state(RETURN_AFTER_ESCAPE[lex.remembered()]);
                    return lex.newToken(TokenType.ML_NO_LINE_BREAK, null);
                } else {
                    throw lex.localError("Expected line break");
                }
            }
        }
    }
}
