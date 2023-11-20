package dev.runefox.json.impl.parse.toml;

import dev.runefox.json.SyntaxException;
import dev.runefox.json.impl.parse.CharUtil;
import dev.runefox.json.impl.parse.StateLexer;
import dev.runefox.json.impl.parse.Token;

// Lines come in three types:
// - Empty
// - A key-value pair:       x = 3.14
// - A table header:         [something]
public enum LexLineStart implements StateLexer.LexerState {
    LINE_START {
        @Override
        public Token lex(int c, StateLexer lex) throws SyntaxException {
            if (CharUtil.isTomlWhitespace(c)) {
                return null;
            }


            return null;
        }
    }
}
