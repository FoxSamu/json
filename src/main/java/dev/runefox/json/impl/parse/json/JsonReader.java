package dev.runefox.json.impl.parse.json;

import dev.runefox.json.SyntaxException;

import java.io.IOException;

public interface JsonReader {
    boolean readBoolean() throws IOException;
    String readString() throws IOException;
    String readIdentifier() throws IOException;
    Number readNumber() throws IOException;
    void readNull() throws IOException;
    void readObjectStart() throws IOException;
    void readObjectEnd() throws IOException;
    void readArrayStart() throws IOException;
    void readArrayEnd() throws IOException;
    void readColon() throws IOException;
    void readComma() throws IOException;
    JsonTokenType peekToken() throws IOException;
    void readToken() throws IOException;
    void close() throws IOException;

    SyntaxException error(String message);
}
