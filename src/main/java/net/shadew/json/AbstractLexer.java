package net.shadew.json;

import java.io.IOException;
import java.io.Reader;
import java.nio.BufferUnderflowException;

abstract class AbstractLexer {
    private final Reader reader;
    private char[] buffer = new char[128];
    private int bufferPos = 0;

    private int lastPos;
    private int lastLine = 1;
    private int lastCol = 1;

    private int startPos;
    private int startLine = 1;
    private int startCol = 1;

    private int pos;
    private int line = 1;
    private int col = 1;

    protected int c;
    protected LexerState state;

    private boolean canSeeCrlf;
    private boolean retain;

    private final char[] readBuffer = new char[4096];
    private int readBufferSize = 0;
    private int readBufferPos = 0;

    private Token reuseToken;

    private int remember;

    AbstractLexer(Reader reader) {
        this.reader = reader;
    }

    void skipNonExecutePrefixes() throws IOException {
        int len = CharUtil.NOEXEC_CRLF.length();
        char[] buf = new char[len];
        int l = 0;
        while (l < len) {
            int r = reader.read(buf, l, len - l);
            if (r < 0) break;
            l += r;
        }
        String prefix = new String(buf, 0, l);
        if (prefix.equals(CharUtil.NOEXEC_CRLF)) {
            return;
        }
        if (prefix.startsWith(CharUtil.NOEXEC_LF) || prefix.startsWith(CharUtil.NOEXEC_CR))
            // Store in read buffer if there's no prefix so we can read it again
            System.arraycopy(buf, 0, readBuffer, 0, l);
        readBufferSize = l;
    }

    private void extendBuffer() {
        char[] oldbuf = buffer;
        int oldlen = oldbuf.length;
        char[] newbuf = new char[oldlen + 128];
        System.arraycopy(oldbuf, 0, newbuf, 0, oldlen);
        buffer = newbuf;
    }

    public LexerState state() {
        return state;
    }

    public void state(LexerState state) {
        this.state = state;
    }

    public void remember(int c) {
        remember = c;
    }

    public int remembered() {
        return remember;
    }

    public String buffered() {
        return new String(buffer, 0, bufferPos);
    }

    public void store(char c) {
        if (bufferPos >= buffer.length)
            extendBuffer();
        buffer[bufferPos] = c;
        bufferPos += 1;
    }

    public char unstore() {
        if (bufferPos == 0) {
            throw new BufferUnderflowException();
        }
        return buffer[--bufferPos];
    }

    public void clear() {
        bufferPos = 0;
    }

    public void startToken() {
        startPos = lastPos;
        startLine = lastLine;
        startCol = lastCol;
    }

    public Token newToken(TokenType type, Object val) {
        Token reuse = reuseToken;
        if (reuse != null) {
            reuseToken = null;
            reuse.set(type, val, startPos, startLine, startCol, pos, line, col);
            return reuse;
        }
        return new Token(type, val, startPos, startLine, startCol, pos, line, col);
    }

    protected abstract LexerState defaultState();

//    private int read() {
//        try {
//            int r = reader.read();
//            if (r >= 0) {
//                pos ++;
//
//            }
//        } catch (IOException e) {
//            throw new UncheckedIOException(e);
//        }
//    }

    private int readBuffered() throws IOException {
        while (readBufferPos >= readBufferSize) {
            if (readBufferSize < 0) return -1;
            readBufferPos = 0;
            readBufferSize = reader.read(readBuffer);
        }
        return readBuffer[readBufferPos++];
    }

    private int readIncrPos() throws IOException {
        int c = readBuffered();
        pos++;
        return c;
    }

    private int read() throws IOException {
        int c = readIncrPos();
        if (canSeeCrlf && c == '\n') {
            c = readIncrPos();
        }
        canSeeCrlf = false;
        if (c == '\r') {
            canSeeCrlf = true;
            c = '\n';
        }
        if (c == '\n') {
            line++;
            col = 1;
        } else {
            col++;
        }
        return c;
    }

    public void retain() {
        retain = true;
    }

    public Token token(Token reuse) throws JsonSyntaxException, IOException {
        state = defaultState();
        reuseToken = reuse;

        while (true) {
            lastPos = pos;
            lastLine = line;
            lastCol = col;

            if (!retain) {
                c = read();
            }
            retain = false;

            Token token = state().lex(c, this);
            if (token != null) {
                return token;
            }
        }
    }

    public JsonSyntaxException error(String problem) {
        return new JsonSyntaxException(startPos, startLine, startCol, pos, line, col, problem);
    }

    public JsonSyntaxException localError(String problem) {
        return new JsonSyntaxException(lastPos, lastLine, lastCol, pos, line, col, problem);
    }

    public void close() throws IOException {
        reader.close();
    }


    @FunctionalInterface
    protected interface LexerState {
        Token lex(int c, AbstractLexer lex) throws JsonSyntaxException;
    }
}
