package dev.runefox.json.impl.parse.toml;

import dev.runefox.json.impl.parse.CharUtil;
import dev.runefox.json.impl.parse.StateLexer;
import dev.runefox.json.impl.parse.Token;

public enum LexComment implements StateLexer.LexerState {
    COMMENT {
        @Override
        public Token lex(int c, StateLexer lex) {
            if (CharUtil.isEof(c)) {
                lex.startToken();
                return lex.newToken(TomlTokenType.EOF, null);
            }

            if (c != '\t' && c < ' ' || c == 0x7F) {
                lex.retain();
                lex.state(LexKey.DEFAULT);
                return null;
            }

            // Ignore any other character
            return null;
        }
    }
}
