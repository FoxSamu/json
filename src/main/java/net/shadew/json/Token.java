package net.shadew.json;

class Token {
    private TokenType type;
    private Object value;
    private int fromPos, fromLine, fromCol;
    private int toPos, toLine, toCol;

    Token() {

    }

    Token(TokenType type, Object value, int fromPos, int fromLine, int fromCol, int toPos, int toLine, int toCol) {
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

    public TokenType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public JsonSyntaxException error(String problem) {
        return new JsonSyntaxException(fromPos, fromLine, fromCol, toPos, toLine, toCol, problem);
    }
}
