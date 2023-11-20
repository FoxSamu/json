package dev.runefox.json.impl.parse.toml;

import dev.runefox.json.impl.parse.StateLexer;

import java.io.Reader;

public class TomlLexerOld extends StateLexer {
    private boolean value;

    public TomlLexerOld(Reader reader) {
        super(reader);
    }

    public void value() {
        value = true;
    }

    @Override
    protected LexerState defaultState() {
        if (value) {
            value = false;
            return LexValue.DEFAULT;
        }
        return LexKey.DEFAULT;
    }
}
