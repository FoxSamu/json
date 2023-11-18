package dev.runefox.json.impl.parse;

import java.io.Reader;

public class TomlLexer extends AbstractLexer {
    public TomlLexer(Reader reader) {
        super(reader);
    }

    @Override
    protected LexerState defaultState() {
        return null;
    }

    private enum TomlLexerState {

    }
}
