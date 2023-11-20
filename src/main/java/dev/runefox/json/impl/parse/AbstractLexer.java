package dev.runefox.json.impl.parse;

import dev.runefox.json.SyntaxException;
import dev.runefox.json.impl.Debug;

import java.io.IOException;
import java.io.Reader;
import java.nio.BufferUnderflowException;

public abstract class AbstractLexer {
    private static final int READ_BUFFER_SIZE = 4096;

    private final Reader reader;
    private int[] buffer = new int[128];
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

    private boolean canSeeCrlf;

    private final char[] readBuffer = new char[READ_BUFFER_SIZE + 1]; // + 1 for when we find 32-bit code points
    private int readBufferSize = 0;
    private int readBufferPos = 0;

    private Token reuseToken;

    private int remember;

    public AbstractLexer(Reader reader) {
        this.reader = reader;
    }

    public int pos() {
        return pos;
    }

    public int line() {
        return line;
    }

    public int col() {
        return col;
    }

    public void skipNonExecutePrefixes() throws IOException {
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
        int[] oldbuf = buffer;
        int oldlen = oldbuf.length;
        int[] newbuf = new int[oldlen + 128];
        System.arraycopy(oldbuf, 0, newbuf, 0, oldlen);
        buffer = newbuf;
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

    public void store(int c) {
        if (bufferPos >= buffer.length)
            extendBuffer();
        buffer[bufferPos] = c;
        bufferPos += 1;
    }

    public int unstore() {
        if (bufferPos == 0) {
            throw new BufferUnderflowException();
        }
        return buffer[--bufferPos];
    }

    public void clear() {
        bufferPos = 0;
    }

    /**
     * Indicates to the lexer that a token starts here. The lexer will record the location of the current character as
     * the start of the token.
     */
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

    private int readBuffered() throws IOException {
        if (readBufferSize < 0)
            return -1;

        if (readBufferPos >= readBufferSize) {
            readBufferPos = 0;

            // Manually specify range, since buffer size has one extra space for low-surrogate characters if needed
            int size = reader.read(readBuffer, 0, READ_BUFFER_SIZE);
            if (size < 0) {
                readBufferSize = -1;
                return -1;
            }

            // If we read a high-surrogate but haven't read a low-surrogate, read low-surrogate as well
            if (Character.isHighSurrogate(readBuffer[size - 1])) {
                int lo = reader.read();
                if (lo >= 0) { // .... if one is available at least
                    readBuffer[size] = (char) lo;
                    size++;
                }
            }

            readBufferSize = size;
        }

        char hi = readBuffer[readBufferPos++];
        if (Character.isHighSurrogate(hi)) {
            // Join code points
            char lo = readBuffer[readBufferPos++];
            return Character.toCodePoint(hi, lo);
        }
        return hi;
    }

    private int readIncrPos() throws IOException {
        int c = readBuffered();
        pos++;
        return c;
    }

    protected int read() throws IOException {
        lastPos = pos;
        lastLine = line;
        lastCol = col;

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

    public Token token(Token reuse) throws IOException {
        reuseToken = reuse;

        Token t = readToken();
        if (Debug.debug)
            Debug.tokenConsumer.accept(t);
        return t;
    }

    protected abstract Token readToken() throws IOException;

    public SyntaxException error(String problem) {
        return new SyntaxException(startPos, startLine, startCol, pos, line, col, problem);
    }

    public SyntaxException localError(String problem) {
        return new SyntaxException(lastPos, lastLine, lastCol, pos, line, col, problem);
    }

    public void close() throws IOException {
        reader.close();
    }
}
