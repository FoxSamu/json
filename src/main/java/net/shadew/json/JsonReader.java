package net.shadew.json;

interface JsonReader {
    boolean readBoolean() throws JsonSyntaxException;
    String readString() throws JsonSyntaxException;
    String readIdentifier() throws JsonSyntaxException;
    @Deprecated
    String readKey() throws JsonSyntaxException;
    Number readNumber() throws JsonSyntaxException;
    void readNull() throws JsonSyntaxException;
    void readObjectStart() throws JsonSyntaxException;
    void readObjectEnd() throws JsonSyntaxException;
    void readArrayStart() throws JsonSyntaxException;
    void readArrayEnd() throws JsonSyntaxException;
    void readColon() throws JsonSyntaxException;
    void readComma() throws JsonSyntaxException;
    TokenType peekToken() throws JsonSyntaxException;
    void readToken() throws JsonSyntaxException;
    void close();

    JsonSyntaxException error(String message);
}
