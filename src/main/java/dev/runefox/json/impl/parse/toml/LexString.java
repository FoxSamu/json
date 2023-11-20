package dev.runefox.json.impl.parse.toml;

import dev.runefox.json.SyntaxException;
import dev.runefox.json.impl.parse.CharUtil;
import dev.runefox.json.impl.parse.StateLexer;
import dev.runefox.json.impl.parse.Token;
import dev.runefox.json.impl.parse.TokenType;

// Strings in keys and strings in values are essentially the same, but they generate different token types and go back
// to different states, so we have the entire logic twice with just minor differences. Instead of creating two separate
// enum classes, we have dynamically create those states for each of the enum constants here. After all LexerState is
// a functional interface and allows implementation through lambdas, so why not use that?
public enum LexString {
    IDENTIFIER(TomlTokenType.IDENTIFIER, LexKey.DEFAULT),
    VALUE(TomlTokenType.STRING, LexKey.DEFAULT);

    public final TokenType token;
    public final StateLexer.LexerState back;

    public final StateLexer.LexerState literal;
    public final StateLexer.LexerState string;
    public final StateLexer.LexerState escape;
    public final StateLexer.LexerState[] unicode2;
    public final StateLexer.LexerState[] unicode4;
    public final StateLexer.LexerState[] unicode8;

    LexString(TokenType token, StateLexer.LexerState back) {
        this.token = token;
        this.back = back;

        this.literal = this::literal;
        this.string = this::string;
        this.escape = this::escape;
        this.unicode2 = new StateLexer.LexerState[] {
            (c, lex) -> unicode2(c, lex, 0),
            (c, lex) -> unicode2(c, lex, 1)
        };
        this.unicode4 = new StateLexer.LexerState[] {
            (c, lex) -> unicode4(c, lex, 0),
            (c, lex) -> unicode4(c, lex, 1),
            (c, lex) -> unicode4(c, lex, 2),
            (c, lex) -> unicode4(c, lex, 3)
        };
        this.unicode8 = new StateLexer.LexerState[] {
            (c, lex) -> unicode8(c, lex, 0),
            (c, lex) -> unicode8(c, lex, 1),
            (c, lex) -> unicode8(c, lex, 2),
            (c, lex) -> unicode8(c, lex, 3),
            (c, lex) -> unicode8(c, lex, 4),
            (c, lex) -> unicode8(c, lex, 5),
            (c, lex) -> unicode8(c, lex, 6),
            (c, lex) -> unicode8(c, lex, 7)
        };
    }

    public Token literal(int c, StateLexer lex) throws SyntaxException {
        if (c == '\'') {
            String content = lex.buffered();
            lex.state(back);
            return lex.newToken(token, content);
        }
        if (CharUtil.isNewline(c)) {
            throw lex.error("unclosed literal string");
        }

        // String content, for literal strings we simply keep this
        lex.store(c);
        return null;
    }

    public Token string(int c, StateLexer lex) throws SyntaxException {
        if (c == '\"') {
            String content = lex.buffered();
            lex.state(back);
            return lex.newToken(token, content);
        }
        if (CharUtil.isNewline(c)) {
            throw lex.error("unclosed literal string");
        }
        if (c == '\\') {
            lex.state(escape);
            return null;
        }

        // String content, for literal strings we simply keep this
        lex.store(c);
        return null;
    }

    public Token escape(int c, StateLexer lex) throws SyntaxException {
        if (c != '\t' && c < ' ' || c == 0x7F) {
            throw lex.localError("Expected string escape");
        } else if (c == '\\') {
            lex.store('\\');
        } else if (c == '"') {
            lex.store('"');
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
        } else if (c == 'x') {
            lex.store(0);
            lex.state(unicode2[0]);
            return null;
        } else if (c == 'u') {
            lex.store(0);
            lex.state(unicode4[0]);
            return null;
        } else if (c == 'U') {
            lex.remember(0);
            lex.state(unicode8[0]);
            return null;
        } else {
            throw lex.localError("Illegal string escape");
        }
        lex.state(string);
        return null;
    }

    public Token unicode2(int c, StateLexer lex, int lev) throws SyntaxException {
        int hex = CharUtil.hexit(c);
        if (hex < 0) {
            throw lex.localError("Expected " + (2 - lev) + " more hex " + (lev == 1 ? "digit" : "digits"));
        } else {
            int s = lex.unstore();
            s |= hex << 4 - 4 * lev;
            lex.store(s);
            lex.state(lev == 1 ? string : unicode2[lev + 1]);
        }
        return null;
    }

    public Token unicode4(int c, StateLexer lex, int lev) throws SyntaxException {
        int hex = CharUtil.hexit(c);
        if (hex < 0) {
            throw lex.localError("Expected " + (4 - lev) + " more hex " + (lev == 3 ? "digit" : "digits"));
        } else {
            int s = lex.unstore();
            s |= hex << 12 - 4 * lev;
            lex.store(s);
            lex.state(lev == 3 ? string : unicode4[lev + 1]);
        }
        return null;
    }

    public Token unicode8(int c, StateLexer lex, int lev) throws SyntaxException {
        int hex = CharUtil.hexit(c);
        if (hex < 0) {
            throw lex.localError("Expected " + (8 - lev) + " more hex " + (lev == 7 ? "digit" : "digits"));
        } else {
            int s = lex.remembered();
            s |= hex << 28 - 4 * lev;
            lex.remember(s);

            if (lev == 7) {
                if (!Character.isValidCodePoint(s)) {
                    throw lex.localError("Invalid code point!");
                }
                if (Character.isSupplementaryCodePoint(s)) {
                    // Supplementary code point, which is 2 chars in UTF-8
                    lex.store(Character.highSurrogate(c));
                    lex.store(Character.lowSurrogate(c));
                } else {
                    // Non-supplementary code point, which is 1 char in UTF-8
                    lex.store(s);
                }
                lex.state(string);
            } else {
                lex.state(unicode8[lev + 1]);
            }
        }
        return null;
    }
}
