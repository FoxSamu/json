package dev.runefox.json.impl.parse.json;

import dev.runefox.json.JsonNode;
import dev.runefox.json.JsonParsingConfig;
import dev.runefox.json.SyntaxException;

import java.io.IOException;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonParser {
    private static final ThreadLocal<JsonParser> PARSER_INSTANCE = ThreadLocal.withInitial(JsonParser::new);
    private JsonReader reader;
    private final Stack<Object> valueStack = new Stack<>();
    private final Stack<State> stateStack = new Stack<>();
    private boolean end = false;

    private boolean stream = false;

    private static final JsonTokenType[] VALUE_TOKENS = {
        JsonTokenType.NULL,
        JsonTokenType.NUMBER,
        JsonTokenType.BOOLEAN,
        JsonTokenType.STRING,
        JsonTokenType.OBJECT_START,
        JsonTokenType.ARRAY_START
    };

    // We can reuse this array to reduce instantiations
    private final JsonTokenType[] valueTokensOr = {
        null,
        JsonTokenType.NULL,
        JsonTokenType.NUMBER,
        JsonTokenType.BOOLEAN,
        JsonTokenType.STRING,
        JsonTokenType.OBJECT_START,
        JsonTokenType.ARRAY_START
    };

    public JsonTokenType[] valueTokensOr(JsonTokenType other) {
        valueTokensOr[0] = other;
        return valueTokensOr;
    }

    public void streamed() {
        stream = true;
    }

    public void end() {
        end = true;
    }

    public void pushState(State state) {
        stateStack.push(state);
    }

    public void popState() {
        stateStack.pop();
    }

    public void switchState(State state) {
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

    public boolean hasValue() {
        return !valueStack.empty();
    }

    public SyntaxException expected(JsonTokenType type) {
        return reader.error("Expected " + type.getErrorName());
    }

    public SyntaxException expected(JsonTokenType... types) {
        return reader.error(Stream.of(types).map(JsonTokenType::getErrorName).collect(Collectors.joining(", ", "Expected ", "")));
    }

    public void parse0(JsonReader reader, JsonParsingConfig config) throws IOException {
        this.reader = reader;
        valueStack.clear();
        stateStack.clear();
        if (!stream) {
            stateStack.push(JsonState.END_OF_FILE);
        }
        stateStack.push(
            config.json5()
            ? config.anyValue() ? Json5State.VALUE : Json5State.ROOT
            : config.anyValue() ? JsonState.VALUE : JsonState.ROOT
        );
        if (stream) {
            stateStack.push(StreamState.PROBABLE_EOF);
        }
        end = false;

        while (!end && !stateStack.empty()) {
            stateStack.peek().parseToken(reader.peekToken(), reader, this);
        }
    }

    public static JsonNode parse(JsonReader reader, JsonParsingConfig config) throws IOException {
        JsonParser parser = PARSER_INSTANCE.get();
        parser.parse0(reader, config);
        return parser.popValue(JsonNode.class);
    }

    private interface State {
        void parseToken(JsonTokenType next, JsonReader reader, JsonParser parser) throws IOException;
    }

    private enum StreamState implements State {
        PROBABLE_EOF {
            @Override
            public void parseToken(JsonTokenType next, JsonReader reader, JsonParser parser) throws IOException {
                if (next == JsonTokenType.EOF)
                    parser.end();
                parser.popState();
            }
        }
    }

    private enum JsonState implements State {
        ROOT {
            @Override
            public void parseToken(JsonTokenType next, JsonReader reader, JsonParser parser) throws IOException {
                if (next == JsonTokenType.OBJECT_START) {
                    reader.readToken();
                    parser.switchState(BEGIN_OBJECT);
                    parser.pushValue(JsonNode.object());
                    return;
                }
                if (next == JsonTokenType.ARRAY_START) {
                    reader.readToken();
                    parser.switchState(BEGIN_ARRAY);
                    parser.pushValue(JsonNode.array());
                    return;
                }
                throw parser.expected(JsonTokenType.OBJECT_START, JsonTokenType.ARRAY_START);
            }
        },
        VALUE {
            @Override
            public void parseToken(JsonTokenType next, JsonReader reader, JsonParser parser) throws IOException {
                if (next == JsonTokenType.OBJECT_START) {
                    reader.readToken();
                    parser.switchState(BEGIN_OBJECT);
                    parser.pushValue(JsonNode.object());
                    return;
                }
                if (next == JsonTokenType.ARRAY_START) {
                    reader.readToken();
                    parser.switchState(BEGIN_ARRAY);
                    parser.pushValue(JsonNode.array());
                    return;
                }
                if (next == JsonTokenType.STRING) {
                    String str = reader.readString();
                    parser.pushValue(JsonNode.string(str));
                    parser.popState();
                    return;
                }
                if (next == JsonTokenType.NUMBER) {
                    Number num = reader.readNumber();
                    parser.pushValue(JsonNode.number(num));
                    parser.popState();
                    return;
                }
                if (next == JsonTokenType.BOOLEAN) {
                    boolean bool = reader.readBoolean();
                    parser.pushValue(JsonNode.bool(bool));
                    parser.popState();
                    return;
                }
                if (next == JsonTokenType.NULL) {
                    reader.readToken();
                    parser.pushValue(JsonNode.NULL);
                    parser.popState();
                    return;
                }

                throw parser.expected(VALUE_TOKENS);
            }
        },
        BEGIN_ARRAY {
            @Override
            public void parseToken(JsonTokenType next, JsonReader reader, JsonParser parser) throws IOException {
                if (next == JsonTokenType.ARRAY_END) {
                    reader.readToken();
                    parser.popState();
                    return;
                }

                if (next.isValue()) {
                    parser.switchState(ARRAY_AFTER_VALUE);
                    parser.pushState(VALUE);
                    return;
                }

                throw parser.expected(parser.valueTokensOr(JsonTokenType.ARRAY_END));
            }
        },
        ARRAY_AFTER_VALUE {
            @Override
            public void parseToken(JsonTokenType next, JsonReader reader, JsonParser parser) throws IOException {
                JsonNode value = parser.popValue(JsonNode.class);
                parser.peekValue(JsonNode.class).add(value);

                if (next == JsonTokenType.ARRAY_END) {
                    reader.readToken();
                    parser.popState();
                    return;
                }

                if (next == JsonTokenType.COMMA) {
                    reader.readToken();
                    parser.switchPushState(VALUE, ARRAY_AFTER_VALUE);
                    return;
                }

                throw parser.expected(JsonTokenType.ARRAY_END, JsonTokenType.COMMA);
            }
        },
        BEGIN_OBJECT {
            @Override
            public void parseToken(JsonTokenType next, JsonReader reader, JsonParser parser) throws IOException {
                if (next == JsonTokenType.OBJECT_END) {
                    reader.readToken();
                    parser.popState();
                    return;
                }

                if (next == JsonTokenType.STRING) {
                    parser.switchState(OBJECT_KEY);
                    return;
                }

                throw parser.expected(JsonTokenType.STRING, JsonTokenType.OBJECT_END);
            }
        },
        OBJECT_KEY {
            @Override
            public void parseToken(JsonTokenType next, JsonReader reader, JsonParser parser) throws IOException {
                if (next == JsonTokenType.STRING) {
                    parser.pushValue(reader.readString());
                    parser.switchState(OBJECT_AFTER_KEY);
                    return;
                }

                throw parser.expected(JsonTokenType.STRING);
            }
        },
        OBJECT_AFTER_KEY {
            @Override
            public void parseToken(JsonTokenType next, JsonReader reader, JsonParser parser) throws IOException {
                if (next == JsonTokenType.COLON) {
                    reader.readToken();
                    parser.switchPushState(VALUE, OBJECT_AFTER_VALUE);
                    return;
                }

                throw parser.expected(JsonTokenType.COLON);
            }
        },
        OBJECT_AFTER_VALUE {
            @Override
            public void parseToken(JsonTokenType next, JsonReader reader, JsonParser parser) throws IOException {
                JsonNode value = parser.popValue(JsonNode.class);
                String key = parser.popValue(String.class);
                parser.peekValue(JsonNode.class).set(key, value);

                if (next == JsonTokenType.OBJECT_END) {
                    reader.readToken();
                    parser.popState();
                    return;
                }

                if (next == JsonTokenType.COMMA) {
                    reader.readToken();
                    parser.switchState(OBJECT_KEY);
                    return;
                }

                throw parser.expected(JsonTokenType.COMMA, JsonTokenType.OBJECT_END);
            }
        },
        END_OF_FILE {
            @Override
            public void parseToken(JsonTokenType next, JsonReader reader, JsonParser parser) throws IOException {
                if (next != JsonTokenType.EOF && !parser.stream)
                    throw parser.expected(JsonTokenType.EOF);
                parser.end();
            }
        }
    }

    private enum Json5State implements State {
        ROOT {
            @Override
            public void parseToken(JsonTokenType next, JsonReader reader, JsonParser parser) throws IOException {
                if (next == JsonTokenType.OBJECT_START) {
                    reader.readToken();
                    parser.switchState(OBJECT_BEFORE_KEY);
                    parser.pushValue(JsonNode.object());
                    return;
                }
                if (next == JsonTokenType.ARRAY_START) {
                    reader.readToken();
                    parser.switchState(ARRAY_BEFORE_VALUE);
                    parser.pushValue(JsonNode.array());
                    return;
                }
                throw parser.expected(JsonTokenType.OBJECT_START, JsonTokenType.ARRAY_START);
            }
        },
        VALUE {
            @Override
            public void parseToken(JsonTokenType next, JsonReader reader, JsonParser parser) throws IOException {
                if (next == JsonTokenType.OBJECT_START) {
                    reader.readToken();
                    parser.switchState(OBJECT_BEFORE_KEY);
                    parser.pushValue(JsonNode.object());
                    return;
                }
                if (next == JsonTokenType.ARRAY_START) {
                    reader.readToken();
                    parser.switchState(ARRAY_BEFORE_VALUE);
                    parser.pushValue(JsonNode.array());
                    return;
                }
                if (next == JsonTokenType.STRING) {
                    String str = reader.readString();
                    parser.pushValue(JsonNode.string(str));
                    parser.popState();
                    return;
                }
                if (next == JsonTokenType.NUMBER) {
                    Number num = reader.readNumber();
                    parser.pushValue(JsonNode.number(num));
                    parser.popState();
                    return;
                }
                if (next == JsonTokenType.BOOLEAN) {
                    boolean bool = reader.readBoolean();
                    parser.pushValue(JsonNode.bool(bool));
                    parser.popState();
                    return;
                }
                if (next == JsonTokenType.NULL) {
                    reader.readToken();
                    parser.pushValue(JsonNode.NULL);
                    parser.popState();
                    return;
                }

                throw parser.expected(VALUE_TOKENS);
            }
        },
        ARRAY_BEFORE_VALUE {
            @Override
            public void parseToken(JsonTokenType next, JsonReader reader, JsonParser parser) throws IOException {
                if (next == JsonTokenType.ARRAY_END) {
                    reader.readToken();
                    parser.popState();
                    return;
                }

                if (next.isValue()) {
                    parser.switchPushState(VALUE, ARRAY_AFTER_VALUE);
                    return;
                }

                throw parser.expected(parser.valueTokensOr(JsonTokenType.ARRAY_END));
            }
        },
        ARRAY_AFTER_VALUE {
            @Override
            public void parseToken(JsonTokenType next, JsonReader reader, JsonParser parser) throws IOException {
                JsonNode value = parser.popValue(JsonNode.class);
                parser.peekValue(JsonNode.class).add(value);

                if (next == JsonTokenType.ARRAY_END) {
                    reader.readToken();
                    parser.popState();
                    return;
                }

                if (next == JsonTokenType.COMMA) {
                    reader.readToken();
                    parser.switchState(ARRAY_BEFORE_VALUE);
                    return;
                }

                throw parser.expected(JsonTokenType.ARRAY_END, JsonTokenType.COMMA);
            }
        },
        OBJECT_BEFORE_KEY {
            @Override
            public void parseToken(JsonTokenType next, JsonReader reader, JsonParser parser) throws IOException {
                if (next == JsonTokenType.OBJECT_END) {
                    reader.readToken();
                    parser.popState();
                    return;
                }

                if (next == JsonTokenType.STRING || next == JsonTokenType.IDENTIFIER) {
                    parser.switchState(OBJECT_KEY);
                    return;
                }

                throw parser.expected(JsonTokenType.STRING, JsonTokenType.IDENTIFIER, JsonTokenType.OBJECT_END);
            }
        },
        OBJECT_KEY {
            @Override
            public void parseToken(JsonTokenType next, JsonReader reader, JsonParser parser) throws IOException {
                if (next == JsonTokenType.STRING) {
                    parser.pushValue(reader.readString());
                    parser.switchState(OBJECT_AFTER_KEY);
                    return;
                }
                if (next == JsonTokenType.IDENTIFIER) {
                    parser.pushValue(reader.readIdentifier());
                    parser.switchState(OBJECT_AFTER_KEY);
                    return;
                }

                throw parser.expected(JsonTokenType.STRING, JsonTokenType.IDENTIFIER);
            }
        },
        OBJECT_AFTER_KEY {
            @Override
            public void parseToken(JsonTokenType next, JsonReader reader, JsonParser parser) throws IOException {
                if (next == JsonTokenType.COLON) {
                    reader.readToken();
                    parser.switchPushState(VALUE, OBJECT_AFTER_VALUE);
                    return;
                }

                throw parser.expected(JsonTokenType.COLON);
            }
        },
        OBJECT_AFTER_VALUE {
            @Override
            public void parseToken(JsonTokenType next, JsonReader reader, JsonParser parser) throws IOException {
                JsonNode value = parser.popValue(JsonNode.class);
                String key = parser.popValue(String.class);
                parser.peekValue(JsonNode.class).set(key, value);

                if (next == JsonTokenType.OBJECT_END) {
                    reader.readToken();
                    parser.popState();
                    return;
                }

                if (next == JsonTokenType.COMMA) {
                    reader.readToken();
                    parser.switchState(OBJECT_BEFORE_KEY);
                    return;
                }

                throw parser.expected(JsonTokenType.COMMA, JsonTokenType.OBJECT_END);
            }
        }
    }
}
