package dev.runefox.json.impl.parse;

import dev.runefox.json.SyntaxException;
import dev.runefox.json.impl.Debug;

import java.io.IOException;
import java.io.Reader;

public abstract class StateLexer extends AbstractLexer {

    protected int c;
    protected LexerState state;
    private boolean retain;


    public StateLexer(Reader reader) {
        super(reader);
    }

    public LexerState state() {
        return state;
    }

    public void state(LexerState state) {
        this.state = state;
    }

    protected abstract LexerState defaultState();

    public void retain() {
        retain = true;
    }

    @Override
    protected Token readToken() throws IOException {
        state = defaultState();

        while (true) {
            if (!retain) {
                c = read();
            }
            retain = false;

            Token token = state().lex(c, this);
            if (token != null) {
                if (Debug.debug)
                    Debug.tokenConsumer.accept(token);
                return token;
            }
        }
    }

    @FunctionalInterface
    public interface LexerState {
        Token lex(int c, StateLexer lex) throws SyntaxException;
    }
}
