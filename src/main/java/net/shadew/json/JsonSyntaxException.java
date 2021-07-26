package net.shadew.json;

public class JsonSyntaxException extends Exception {
    private final int fromIndex;
    private final int fromLine;
    private final int fromCol;
    private final int toIndex;
    private final int toLine;
    private final int toCol;
    private final String problem;

    public JsonSyntaxException(int fromIndex, int fromLine, int fromCol, int toIndex, int toLine, int toCol, String problem) {
        this.fromIndex = fromIndex;
        this.fromLine = fromLine;
        this.fromCol = fromCol;
        this.toIndex = toIndex;
        this.toLine = toLine;
        this.toCol = toCol;
        this.problem = problem;
    }

    public JsonSyntaxException(int fromIndex, int fromLine, int fromCol, String problem) {
        this.fromIndex = fromIndex;
        this.fromLine = fromLine;
        this.fromCol = fromCol;
        this.toIndex = fromIndex + 1;
        this.toLine = fromLine;
        this.toCol = fromCol + 1;
        this.problem = problem;
    }

    @Override
    public String getMessage() {
        return "Line " + fromLine + ", col " + fromCol + ": " + problem;
    }

    public JsonSyntaxException withMessage(String message) {
        return new JsonSyntaxException(fromIndex, fromLine, fromCol, toIndex, toLine, toCol, message);
    }
}
