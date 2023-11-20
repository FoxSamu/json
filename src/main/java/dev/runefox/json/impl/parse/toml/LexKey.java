package dev.runefox.json.impl.parse.toml;

import dev.runefox.json.SyntaxException;
import dev.runefox.json.impl.parse.CharUtil;
import dev.runefox.json.impl.parse.StateLexer;
import dev.runefox.json.impl.parse.Token;

// Lexer states when a key or something is expected. That can be a key for a 'key = value' pair but also a
// [table.header] or an [[array.of.tables.header]].
// Give DEFAULT to AbstractLexer and the following tokens are recognized:
// - '.'
// - ','
// - '='
// - EOL
// - EOF
// - '['  (the table header one, not the array one)
// - ']'  (... likewise)
// - '[['
// - ']]'
// - identifer  (of all kinds, even quoted identifers become an identifier token and not a string)
public enum LexKey implements StateLexer.LexerState {
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

            if (CharUtil.isEof(c)) {
                lex.startToken();
                return lex.newToken(TomlTokenType.EOF, null);
            }

            // HEADER
            if (c == '[') {
                lex.startToken();
                lex.state(HEADER_IN);
                return null;
            }
            if (c == ']') {
                lex.startToken();
                lex.state(HEADER_OUT);
                return null;
            }

            // SOME SYMBOLS
            if (c == '.') {
                lex.startToken();
                return lex.newToken(TomlTokenType.DOT, null);
            }
            if (c == ',') {
                lex.startToken();
                return lex.newToken(TomlTokenType.COMMA, null);
            }
            if (c == '=') {
                lex.startToken();
                return lex.newToken(TomlTokenType.EQUALS, null);
            }

            // IDENTIFIER
            if (CharUtil.isTomlIdentifier(c)) {
                lex.clear();
                lex.store(c);
                lex.state(IDENTIFIER);
                lex.startToken();
                return null;
            }
            if (c == '\'') {
                lex.clear();
                lex.state(LexString.IDENTIFIER.literal);
                lex.startToken();
                return null;
            }
            if (c == '\"') {
                lex.clear();
                lex.state(LexString.IDENTIFIER.string);
                lex.startToken();
                return null;
            }

            throw lex.localError("illegal character");
        }
    },


    HEADER_IN {
        @Override
        public Token lex(int c, StateLexer lex) {
            if (c == '[') {
                return lex.newToken(TomlTokenType.ARR_TABLE_HEADER_L, null);
            }

            lex.retain();
            lex.state(DEFAULT);
            return lex.newToken(TomlTokenType.TABLE_HEADER_L, null);
        }
    },
    HEADER_OUT {
        @Override
        public Token lex(int c, StateLexer lex) {
            if (c == ']') {
                return lex.newToken(TomlTokenType.ARR_TABLE_HEADER_R, null);
            }

            lex.retain();
            lex.state(DEFAULT);
            return lex.newToken(TomlTokenType.TABLE_HEADER_R, null);
        }
    },

    IDENTIFIER {
        @Override
        public Token lex(int c, StateLexer lex) {
            if (CharUtil.isTomlIdentifier(c)) {
                lex.store(c);
                return null;
            }

            String iden = lex.buffered();
            lex.retain();
            lex.state(DEFAULT);
            return lex.newToken(TomlTokenType.IDENTIFIER, iden);
        }
    }
}
