package dev.runefox.json.impl.parse.json;

import dev.runefox.json.SyntaxException;
import dev.runefox.json.impl.parse.AbstractLexer;
import dev.runefox.json.impl.parse.Token;

import java.io.IOException;

public class JsonLexerReader implements JsonReader {
    private final Token nextToken = new Token();
    private boolean hasNext;

    private final AbstractLexer lexer;

    public JsonLexerReader(AbstractLexer lexer, boolean skipNonExecute) throws IOException {
        this.lexer = lexer;
        if (skipNonExecute) {
            lexer.skipNonExecutePrefixes();
        }
    }

    private Token next() throws IOException {
        if (!hasNext) {
            lexer.token(nextToken);
            hasNext = true;
        }
        return nextToken;
    }

    private Token next(JsonTokenType expectedType) throws IOException {
        Token next = next();
        JsonTokenType nextType = (JsonTokenType) next.type();
        if (nextType == expectedType) {
            return next;
        } else {
            throw next.error("Expected " + expectedType.getErrorName());
        }
    }

    @Override
    public boolean readBoolean() throws IOException {
        Token next = next(JsonTokenType.BOOLEAN);
        hasNext = false;
        return (boolean) next.value();
    }

    @Override
    public String readString() throws IOException {
        Token next = next(JsonTokenType.STRING);
        hasNext = false;
        return (String) next.value();
    }

    @Override
    public String readIdentifier() throws IOException {
        Token next = next(JsonTokenType.IDENTIFIER);
        hasNext = false;
        return (String) next.value();
    }

    @Override
    public Number readNumber() throws IOException {
        Token next = next(JsonTokenType.NUMBER);
        hasNext = false;
        return (Number) next.value();
    }

    @Override
    public void readNull() throws IOException {
        next(JsonTokenType.NULL);
        hasNext = false;
    }

    @Override
    public void readObjectStart() throws IOException {
        next(JsonTokenType.OBJECT_START);
        hasNext = false;
    }

    @Override
    public void readObjectEnd() throws IOException {
        next(JsonTokenType.OBJECT_END);
        hasNext = false;
    }

    @Override
    public void readArrayStart() throws IOException {
        next(JsonTokenType.ARRAY_START);
        hasNext = false;
    }

    @Override
    public void readArrayEnd() throws IOException {
        next(JsonTokenType.ARRAY_END);
        hasNext = false;
    }

    @Override
    public void readColon() throws IOException {
        next(JsonTokenType.COLON);
        hasNext = false;
    }

    @Override
    public void readComma() throws IOException {
        next(JsonTokenType.COMMA);
        hasNext = false;
    }

    @Override
    public JsonTokenType peekToken() throws IOException {
        return (JsonTokenType) next().type();
    }

    @Override
    public void readToken() throws IOException {
        next();
        hasNext = false;
    }

    @Override
    public void close() throws IOException {
        lexer.close();
    }

    @Override
    public SyntaxException error(String message) {
        if (hasNext) {
            return nextToken.error(message);
        } else {
            return lexer.error(message);
        }
    }
}
