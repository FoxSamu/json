package dev.runefox.json.impl.parse.toml;

import dev.runefox.json.JsonNode;
import dev.runefox.json.SyntaxException;
import dev.runefox.json.impl.parse.Token;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TomlParser {
    private final TomlLexer lexer;
    private final Token token = new Token();
    private final Token start = new Token();
    private boolean hasToken;
    private boolean end;

    private final Document doc = new Document();

    private final Stack<Object> valueStack = new Stack<>();
    private final Stack<State> stateStack = new Stack<>();
    private final List<String> path = new ArrayList<>();

    private JsonNode document, current;

    public TomlParser(TomlLexer lexer) {
        this.lexer = lexer;
    }

    public void init() {
        document = JsonNode.object();
        current = document;
    }

    public SyntaxException expected(TomlTokenType type) {
        return token.error("Expected " + type.errorName());
    }

    public SyntaxException expected(TomlTokenType... types) {
        return token.error(Stream.of(types).map(TomlTokenType::errorName).collect(Collectors.joining(", ", "Expected ", "")));
    }

    public Token peek(TomlLexer.Mode mode) throws IOException {
        if (!hasToken) {
            lexer.mode(mode);
            lexer.token(token);
            hasToken = true;
        }
        return token;
    }

    public void consume() {
        hasToken = false;
    }

    public void end() {
        end = true;
    }

    public void start() {
        start.set(token);
    }

    void pushState(State state) {
        stateStack.push(state);
    }

    void popState() {
        stateStack.pop();
    }

    void switchState(State state) {
        stateStack.pop();
        stateStack.push(state);
    }

    void switchPushState(State push, State then) {
        stateStack.pop();
        stateStack.push(then);
        stateStack.push(push);
    }

    public void pushValue(Object node) {
        valueStack.push(node);
    }

    public <T> T peekValue(Class<T> type) {
        return type.cast(valueStack.peek());
    }

    public <T> T popValue(Class<T> type) {
        return type.cast(valueStack.pop());
    }

    public void path(String path) {
        this.path.add(path);
    }

    public List<String> path() {
        return this.path;
    }

    public void resetPath() {
        this.path.clear();
    }

    public JsonNode document() {
        return document;
    }

    public JsonNode current() {
        return current;
    }

    public void current(JsonNode node) {
        current = node;
    }

    public void parse() throws IOException {
        stateStack.push(KeyState.DOCUMENT);
        while (!end) {
            State state = stateStack.peek();
            Token t = peek(state.mode());
            state.parse(t, (TomlTokenType) t.type(), this);
        }
    }

    public JsonNode finishDocument() throws SyntaxException {
        return doc.toJson();
    }

    private interface State {
        void parse(Token token, TomlTokenType type, TomlParser parser) throws SyntaxException;
        TomlLexer.Mode mode();
    }

    private enum KeyState implements State {
        DOCUMENT {
            @Override
            public void parse(Token token, TomlTokenType type, TomlParser parser) throws SyntaxException {
                switch (type) {
                    case TABLE_HEADER_L -> {
                        parser.start();
                        parser.consume();
                        parser.resetPath();
                        parser.pushState(TABLE_HEADER);
                        parser.pushState(KEY);
                    }
                    case ARR_TABLE_HEADER_L -> {
                        parser.start();
                        parser.consume();
                        parser.resetPath();
                        parser.pushState(ARRAY_HEADER);
                        parser.pushState(KEY);
                    }
                    case IDENTIFIER -> {
                        parser.start();
                        parser.resetPath();
                        parser.switchState(DOCUMENT_APPLY_KV);
                        parser.pushState(KEY_VALUE);
                        parser.pushState(KEY);
                    }
                    case EOL -> parser.consume();
                    case EOF -> parser.end();

                    default -> throw parser.expected(
                        TomlTokenType.TABLE_HEADER_L,
                        TomlTokenType.ARR_TABLE_HEADER_L,
                        TomlTokenType.IDENTIFIER,
                        TomlTokenType.EOL,
                        TomlTokenType.EOF
                    );
                }
            }
        },

        TABLE_HEADER {
            @Override
            public void parse(Token token, TomlTokenType type, TomlParser parser) throws SyntaxException {
                if (type == TomlTokenType.TABLE_HEADER_R) {
                    parser.consume();
                    parser.popState();

                    parser.doc.table(
                        new Header(
                            parser.path(),
                            parser.start,
                            token
                        )
                    );
                    return;
                }
                throw parser.expected(TomlTokenType.TABLE_HEADER_R, TomlTokenType.DOT);
            }
        },

        ARRAY_HEADER {
            @Override
            public void parse(Token token, TomlTokenType type, TomlParser parser) throws SyntaxException {
                if (type == TomlTokenType.ARR_TABLE_HEADER_R) {
                    parser.consume();
                    parser.popState();

                    parser.doc.tableArray(
                        new Header(
                            parser.path(),
                            parser.start,
                            token
                        )
                    );
                    return;
                }
                throw parser.expected(TomlTokenType.ARR_TABLE_HEADER_R, TomlTokenType.DOT);
            }
        },

        KEY_VALUE {
            @Override
            public void parse(Token token, TomlTokenType type, TomlParser parser) throws SyntaxException {
                if (type == TomlTokenType.EQUALS) {
                    parser.consume();
                    parser.switchPushState(ValueState.VALUE, KEY_VALUE_END);
                    parser.pushValue(new KeyValue(parser.path(), parser.start.fromPos(), parser.start.fromLine(), parser.start.fromCol()));
                    return;
                }
                throw parser.expected(TomlTokenType.EQUALS, TomlTokenType.DOT);
            }
        },

        KEY_VALUE_END {
            @Override
            public void parse(Token token, TomlTokenType type, TomlParser parser) throws SyntaxException {
                parser.popState();
                TomlValue value = parser.popValue(TomlValue.class);
                KeyValue kv = parser.peekValue(KeyValue.class);
                kv.value(value);
            }
        },

        DOCUMENT_APPLY_KV {
            @Override
            public void parse(Token token, TomlTokenType type, TomlParser parser) throws SyntaxException {
                KeyValue kv = parser.popValue(KeyValue.class);
                parser.doc.apply(kv);

                parser.switchState(EXPECT_EOL);
            }
        },

        EXPECT_EOL {
            @Override
            public void parse(Token token, TomlTokenType type, TomlParser parser) throws SyntaxException {
                switch (type) {
                    case EOL -> {
                        parser.consume();
                        parser.switchState(DOCUMENT);
                    }
                    case EOF -> parser.end();

                    default -> throw parser.expected(
                        TomlTokenType.EOL,
                        TomlTokenType.EOF
                    );
                }
            }
        },

        KEY {
            @Override
            public void parse(Token token, TomlTokenType type, TomlParser parser) throws SyntaxException {
                switch (type) {
                    case IDENTIFIER -> {
                        parser.consume();
                        parser.path(token.value());
                        parser.switchState(KEY_END);
                    }

                    default -> throw parser.expected(
                        TomlTokenType.IDENTIFIER
                    );
                }
            }
        },

        KEY_END {
            @Override
            public void parse(Token token, TomlTokenType type, TomlParser parser) throws SyntaxException {
                switch (type) {
                    case DOT -> {
                        parser.consume();
                        parser.switchState(KEY);
                    }

                    default -> parser.popState();
                }
            }
        },

        INLINE_TABLE_START {
            @Override
            public void parse(Token token, TomlTokenType type, TomlParser parser) throws SyntaxException {
                switch (type) {
                    case INLINE_TABLE_R -> {
                        parser.popState();
                        parser.consume();

                        Table builder = parser.peekValue(Table.class);
                        builder.end(token);
                    }

                    default -> parser.switchState(INLINE_TABLE);
                }
            }
        },

        INLINE_TABLE {
            @Override
            public void parse(Token token, TomlTokenType type, TomlParser parser) throws SyntaxException {
                switch (type) {
                    case IDENTIFIER -> {
                        parser.start();
                        parser.resetPath();
                        parser.switchState(INLINE_TABLE_APPLY_KV);
                        parser.pushState(KEY_VALUE);
                        parser.pushState(KEY);
                    }

                    default -> throw parser.expected(
                        TomlTokenType.IDENTIFIER
                    );
                }
            }
        },

        INLINE_TABLE_APPLY_KV {
            @Override
            public void parse(Token token, TomlTokenType type, TomlParser parser) throws SyntaxException {
                KeyValue kv = parser.popValue(KeyValue.class);
                Table builder = parser.peekValue(Table.class);
                builder.add(kv);

                parser.switchState(INLINE_TABLE_END);
            }
        },

        INLINE_TABLE_END {
            @Override
            public void parse(Token token, TomlTokenType type, TomlParser parser) throws SyntaxException {
                switch (type) {
                    case INLINE_TABLE_R -> {
                        parser.popState();
                        parser.consume();

                        Table builder = parser.peekValue(Table.class);
                        builder.end(token);
                    }

                    case COMMA -> {
                        parser.switchState(INLINE_TABLE);
                        parser.consume();
                    }

                    default -> throw parser.expected(
                        TomlTokenType.COMMA,
                        TomlTokenType.INLINE_TABLE_R
                    );
                }
            }
        };

        @Override
        public TomlLexer.Mode mode() {
            return TomlLexer.Mode.KEY;
        }
    }

    private enum ValueState implements State {
        VALUE {
            @Override
            public void parse(Token token, TomlTokenType type, TomlParser parser) throws SyntaxException {
                switch (type) {
                    case INTEGER, FLOAT -> {
                        parser.consume();
                        parser.popState();
                        parser.pushValue(new Primitive(JsonNode.number(token.value()), token));
                    }
                    case STRING -> {
                        parser.consume();
                        parser.popState();
                        parser.pushValue(new Primitive(JsonNode.string(token.value()), token));
                    }
                    case BOOLEAN -> {
                        parser.consume();
                        parser.popState();
                        parser.pushValue(new Primitive(JsonNode.bool(token.value()), token));
                    }
                    case OFFSET_DATE_TIME -> {
                        parser.consume();
                        parser.popState();
                        parser.pushValue(new Primitive(JsonNode.offsetDateTime(token.value()), token));
                    }
                    case LOCAL_DATE_TIME -> {
                        parser.consume();
                        parser.popState();
                        parser.pushValue(new Primitive(JsonNode.localDateTime(token.value()), token));
                    }
                    case LOCAL_DATE -> {
                        parser.consume();
                        parser.popState();
                        parser.pushValue(new Primitive(JsonNode.localDate(token.value()), token));
                    }
                    case LOCAL_TIME -> {
                        parser.consume();
                        parser.popState();
                        parser.pushValue(new Primitive(JsonNode.localTime(token.value()), token));
                    }

                    case ARRAY_L -> {
                        parser.consume();
                        parser.switchState(ARRAY);

                        Array array = new Array();
                        array.begin(token);
                        parser.pushValue(array);
                    }

                    case INLINE_TABLE_L -> {
                        parser.consume();
                        parser.switchState(KeyState.INLINE_TABLE_START);

                        Table table = new Table();
                        table.begin(token);
                        parser.pushValue(table);
                    }

                    default -> throw parser.expected(
                        TomlTokenType.INTEGER,
                        TomlTokenType.FLOAT,
                        TomlTokenType.STRING,
                        TomlTokenType.BOOLEAN,
                        TomlTokenType.OFFSET_DATE_TIME,
                        TomlTokenType.LOCAL_DATE_TIME,
                        TomlTokenType.LOCAL_DATE,
                        TomlTokenType.LOCAL_TIME,
                        TomlTokenType.INLINE_TABLE_L,
                        TomlTokenType.ARRAY_L
                    );
                }
            }
        },

        ARRAY {
            @Override
            public void parse(Token token, TomlTokenType type, TomlParser parser) throws SyntaxException {
                switch (type) {
                    case ARRAY_R -> {
                        parser.consume();
                        parser.popState();

                        Array arr = parser.peekValue(Array.class);
                        arr.end(token);
                    }

                    case EOL -> parser.consume();

                    default -> {
                        parser.switchState(ARRAY_APPLY_VALUE);
                        parser.pushState(VALUE);
                    }
                }
            }
        },

        ARRAY_APPLY_VALUE {
            @Override
            public void parse(Token token, TomlTokenType type, TomlParser parser) throws SyntaxException {
                parser.switchState(ARRAY_END);

                TomlValue value = parser.popValue(TomlValue.class);
                Array builder = parser.peekValue(Array.class);
                builder.add(value);
            }
        },

        ARRAY_END {
            @Override
            public void parse(Token token, TomlTokenType type, TomlParser parser) throws SyntaxException {
                switch (type) {
                    case ARRAY_R -> {
                        parser.consume();
                        parser.popState();

                        Array arr = parser.peekValue(Array.class);
                        arr.end(token);
                    }

                    case COMMA -> {
                        parser.consume();
                        parser.switchState(ARRAY);
                    }

                    case EOL -> parser.consume();

                    default -> throw parser.expected(
                        TomlTokenType.COMMA,
                        TomlTokenType.ARRAY_R
                    );
                }
            }
        };

        @Override
        public TomlLexer.Mode mode() {
            return TomlLexer.Mode.VALUE;
        }
    }
}
