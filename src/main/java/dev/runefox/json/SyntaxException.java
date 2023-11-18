package dev.runefox.json;

import java.io.IOException;

public class SyntaxException extends IOException {
    private final int fromIndex;
    private final int fromLine;
    private final int fromCol;
    private final int toIndex;
    private final int toLine;
    private final int toCol;
    private final String problem;

    public SyntaxException(int fromIndex, int fromLine, int fromCol, int toIndex, int toLine, int toCol, String problem) {
        this.fromIndex = fromIndex;
        this.fromLine = fromLine;
        this.fromCol = fromCol;
        this.toIndex = toIndex;
        this.toLine = toLine;
        this.toCol = toCol;
        this.problem = problem;
    }

    public SyntaxException(int fromIndex, int fromLine, int fromCol, String problem) {
        this.fromIndex = fromIndex;
        this.fromLine = fromLine;
        this.fromCol = fromCol;
        this.toIndex = fromIndex + 1;
        this.toLine = fromLine;
        this.toCol = fromCol + 1;
        this.problem = problem;
    }

    public String problem() {
        return problem;
    }

    public int fromIndex() {
        return fromIndex;
    }

    public int fromLine() {
        return fromLine;
    }

    public int fromCol() {
        return fromCol;
    }

    public int toIndex() {
        return toIndex;
    }

    public int toLine() {
        return toLine;
    }

    public int toCol() {
        return toCol;
    }

    @Override
    public String getMessage() {
        return "Line " + fromLine + ", col " + fromCol + ": " + problem;
    }

    public SyntaxException withMessage(String message) {
        return new SyntaxException(fromIndex, fromLine, fromCol, toIndex, toLine, toCol, message);
    }
}
