package net.shadew.json.template.parser;

import java.io.IOException;
import java.io.Reader;
import java.nio.BufferUnderflowException;
import java.util.HashMap;
import java.util.Stack;
import java.util.function.Supplier;

import net.shadew.json.JsonSyntaxException;

class TemplateLexer {
    private static final int READ_BUFFER_SIZE = 4096;

    private final HashMap<String, LexerState> cachedStates = new HashMap<>();
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

    protected int c;
    protected LexerState state;

    private boolean canSeeCrlf;
    private boolean retain;

    private final char[] readBuffer = new char[READ_BUFFER_SIZE + 1]; // + 1 for when we find 32-bit code points
    private int readBufferSize = 0;
    private int readBufferPos = 0;

    private Token reuseToken;

    private int remember;

    private Stack<LexerMode> modeStack = new Stack<>();

    TemplateLexer(Reader reader) {
        this.reader = reader;
        modeStack.push(LexerStates.DEFAULT_MODE);
    }

    public LexerState cacheState(String name, Supplier<LexerState> factory) {
        return cachedStates.computeIfAbsent(name, k -> factory.get());
    }

    private void extendBuffer() {
        int[] oldbuf = buffer;
        int oldlen = oldbuf.length;
        int[] newbuf = new int[oldlen + 128];
        System.arraycopy(oldbuf, 0, newbuf, 0, oldlen);
        buffer = newbuf;
    }

    public LexerState state() {
        return state;
    }

    public void state(LexerState state) {
        this.state = state;
    }

    public LexerMode mode() {
        return modeStack.peek();
    }

    public void pushMode(LexerMode mode) {
        modeStack.push(mode);
    }

    public void popPushMode(LexerMode mode) {
        modeStack.pop();
        modeStack.push(mode);
    }

    public LexerMode popMode() {
        return modeStack.pop();
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

    protected LexerState defaultState() {
        return LexerStates.BaseStates.DEFAULT;
    }

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

    private int read() throws IOException {
        int c = readIncrPos();
        if (canSeeCrlf && c == '\n') {
            c = readIncrPos(); // Ignore the newline character, we already dealt with this
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

    public Token token(Token reuse) throws IOException {
        mode().init(this);
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
        Token lex(int c, TemplateLexer lex) throws JsonSyntaxException;
    }

    protected interface LexerMode {
        void init(TemplateLexer lex);
    }
}
