package dev.runefox.json.impl.parse;

import dev.runefox.json.SyntaxException;

public class Token {
    private TokenType type;
    private Object value;
    private int fromPos, fromLine, fromCol;
    private int toPos, toLine, toCol;

    public Token() {

    }

    public Token(TokenType type, Object value, int fromPos, int fromLine, int fromCol, int toPos, int toLine, int toCol) {
        this.type = type;
        this.value = value;
        this.fromPos = fromPos;
        this.fromLine = fromLine;
        this.fromCol = fromCol;
        this.toPos = toPos;
        this.toLine = toLine;
        this.toCol = toCol;
    }

    public void set(TokenType type, Object value, int fromPos, int fromLine, int fromCol, int toPos, int toLine, int toCol) {
        this.type = type;
        this.value = value;
        this.fromPos = fromPos;
        this.fromLine = fromLine;
        this.fromCol = fromCol;
        this.toPos = toPos;
        this.toLine = toLine;
        this.toCol = toCol;
    }

    public TokenType type() {
        return type;
    }

    public Object value() {
        return value;
    }

    public SyntaxException error(String problem) {
        return new SyntaxException(fromPos, fromLine, fromCol, toPos, toLine, toCol, problem);
    }
}
