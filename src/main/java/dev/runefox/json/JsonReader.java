package dev.runefox.json;

import java.io.IOException;

interface JsonReader {
    boolean readBoolean() throws IOException;
    String readString() throws IOException;
    String readIdentifier() throws IOException;
    @Deprecated
    String readKey() throws IOException;
    Number readNumber() throws IOException;
    void readNull() throws IOException;
    void readObjectStart() throws IOException;
    void readObjectEnd() throws IOException;
    void readArrayStart() throws IOException;
    void readArrayEnd() throws IOException;
    void readColon() throws IOException;
    void readComma() throws IOException;
    TokenType peekToken() throws IOException;
    void readToken() throws IOException;
    void close();

    JsonSyntaxException error(String message);
}
