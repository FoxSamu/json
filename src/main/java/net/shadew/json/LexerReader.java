package net.shadew.json;

import java.io.IOException;
import java.io.UncheckedIOException;

class LexerReader implements JsonReader {
    private final Token nextToken = new Token();
    private boolean hasNext;

    private final AbstractLexer lexer;

    LexerReader(AbstractLexer lexer, boolean skipNonExecute) {
        this.lexer = lexer;
        if (skipNonExecute) {
            try {
                lexer.skipNonExecutePrefixes();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private Token next() throws JsonSyntaxException {
        if (!hasNext) {
            try {
                lexer.token(nextToken);
                hasNext = true;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return nextToken;
    }

    private Token next(TokenType expectedType) throws JsonSyntaxException {
        Token next = next();
        TokenType nextType = next.getType();
        if (nextType == expectedType) {
            return next;
        } else {
            throw next.error("Expected " + expectedType.getErrorName());
        }
    }

    @Override
    public boolean readBoolean() throws JsonSyntaxException {
        Token next = next(TokenType.BOOLEAN);
        hasNext = false;
        return (boolean) next.getValue();
    }

    @Override
    public String readString() throws JsonSyntaxException {
        Token next = next(TokenType.STRING);
        hasNext = false;
        return (String) next.getValue();
    }

    @Override
    public String readIdentifier() throws JsonSyntaxException {
        Token next = next(TokenType.IDENTIFIER);
        hasNext = false;
        return (String) next.getValue();
    }

    @Override
    public String readKey() throws JsonSyntaxException {
        Token next = next();
        TokenType nextType = next.getType();
        if (nextType == TokenType.STRING || nextType == TokenType.IDENTIFIER) {
            hasNext = false;
            return next.getValue().toString();
        } else {
            throw next.error("Expected " + TokenType.STRING.getErrorName() + ", " + TokenType.IDENTIFIER.getErrorName());
        }
    }

    @Override
    public Number readNumber() throws JsonSyntaxException {
        Token next = next(TokenType.NUMBER);
        hasNext = false;
        return (Number) next.getValue();
    }

    @Override
    public void readNull() throws JsonSyntaxException {
        next(TokenType.NULL);
        hasNext = false;
    }

    @Override
    public void readObjectStart() throws JsonSyntaxException {
        next(TokenType.OBJECT_START);
        hasNext = false;
    }

    @Override
    public void readObjectEnd() throws JsonSyntaxException {
        next(TokenType.OBJECT_END);
        hasNext = false;
    }

    @Override
    public void readArrayStart() throws JsonSyntaxException {
        next(TokenType.ARRAY_START);
        hasNext = false;
    }

    @Override
    public void readArrayEnd() throws JsonSyntaxException {
        next(TokenType.ARRAY_END);
        hasNext = false;
    }

    @Override
    public void readColon() throws JsonSyntaxException {
        next(TokenType.COLON);
        hasNext = false;
    }

    @Override
    public void readComma() throws JsonSyntaxException {
        next(TokenType.COMMA);
        hasNext = false;
    }

    @Override
    public TokenType peekToken() throws JsonSyntaxException {
        return next().getType();
    }

    @Override
    public void readToken() throws JsonSyntaxException {
        next();
        hasNext = false;
    }

    @Override
    public void close() {
        try {
            lexer.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public JsonSyntaxException error(String message) {
        try {
            return next().error(message);
        } catch (JsonSyntaxException exc) {
            return exc; // I mean, why not?
        }
    }
}
