package dev.runefox.json;

import java.io.IOException;

class LexerReader implements JsonReader {
    private final Token nextToken = new Token();
    private boolean hasNext;

    private final AbstractLexer lexer;

    LexerReader(AbstractLexer lexer, boolean skipNonExecute) throws IOException {
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

    private Token next(TokenType expectedType) throws IOException {
        Token next = next();
        TokenType nextType = next.getType();
        if (nextType == expectedType) {
            return next;
        } else {
            throw next.error("Expected " + expectedType.getErrorName());
        }
    }

    @Override
    public boolean readBoolean() throws IOException {
        Token next = next(TokenType.BOOLEAN);
        hasNext = false;
        return (boolean) next.getValue();
    }

    @Override
    public String readString() throws IOException {
        Token next = next(TokenType.STRING);
        hasNext = false;
        return (String) next.getValue();
    }

    @Override
    public String readIdentifier() throws IOException {
        Token next = next(TokenType.IDENTIFIER);
        hasNext = false;
        return (String) next.getValue();
    }

    @Override
    public String readKey() throws IOException {
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
    public Number readNumber() throws IOException {
        Token next = next(TokenType.NUMBER);
        hasNext = false;
        return (Number) next.getValue();
    }

    @Override
    public void readNull() throws IOException {
        next(TokenType.NULL);
        hasNext = false;
    }

    @Override
    public void readObjectStart() throws IOException {
        next(TokenType.OBJECT_START);
        hasNext = false;
    }

    @Override
    public void readObjectEnd() throws IOException {
        next(TokenType.OBJECT_END);
        hasNext = false;
    }

    @Override
    public void readArrayStart() throws IOException {
        next(TokenType.ARRAY_START);
        hasNext = false;
    }

    @Override
    public void readArrayEnd() throws IOException {
        next(TokenType.ARRAY_END);
        hasNext = false;
    }

    @Override
    public void readColon() throws IOException {
        next(TokenType.COLON);
        hasNext = false;
    }

    @Override
    public void readComma() throws IOException {
        next(TokenType.COMMA);
        hasNext = false;
    }

    @Override
    public TokenType peekToken() throws IOException {
        return next().getType();
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
    public JsonSyntaxException error(String message) {
        if (hasNext) {
            return nextToken.error(message);
        } else {
            return lexer.error(message);
        }
    }
}
