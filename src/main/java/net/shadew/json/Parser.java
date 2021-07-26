package net.shadew.json;

import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.shadew.json.TokenType.*;

class Parser {
    private static final ThreadLocal<Parser> PARSER_INSTANCE = ThreadLocal.withInitial(Parser::new);
    private JsonReader reader;
    private final Stack<Object> valueStack = new Stack<>();
    private final Stack<State> stateStack = new Stack<>();
    private boolean end = false;

    private static final TokenType[] VALUE_TOKENS = {NULL, NUMBER, BOOLEAN, STRING, OBJECT_START, ARRAY_START};

    // We can reuse this array to reduce instantiations
    private final TokenType[] valueTokensOr = {null, NULL, NUMBER, BOOLEAN, STRING, OBJECT_START, ARRAY_START};

    TokenType[] valueTokensOr(TokenType other) {
        valueTokensOr[0] = other;
        return valueTokensOr;
    }

    void end() {
        end = true;
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

    void pushValue(Object node) {
        valueStack.push(node);
    }

    <T> T peekValue(Class<T> type) {
        return type.cast(valueStack.peek());
    }

    <T> T popValue(Class<T> type) {
        return type.cast(valueStack.pop());
    }

    JsonSyntaxException expected(TokenType type) {
        return reader.error("Expected " + type.getErrorName());
    }

    JsonSyntaxException expected(TokenType... types) {
        return reader.error(Stream.of(types).map(TokenType::getErrorName).collect(Collectors.joining(", ", "Expected ", "")));
    }

    private void parse0(JsonReader reader, boolean json5) throws JsonSyntaxException {
        this.reader = reader;
        valueStack.clear();
        stateStack.clear();
        stateStack.push(json5 ? Json5State.ROOT : JsonState.ROOT);
        end = false;

        while (!end) {
            stateStack.peek().parseToken(reader.peekToken(), reader, this);
        }
    }

    public static JsonNode parse(JsonReader reader, boolean json5) throws JsonSyntaxException {
        Parser parser = PARSER_INSTANCE.get();
        parser.parse0(reader, json5);
        return parser.popValue(JsonNode.class);
    }

    private interface State {
        void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException;
    }

    private enum JsonState implements State {
        ROOT {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next == OBJECT_START) {
                    reader.readToken();
                    parser.switchPushState(BEGIN_OBJECT, END_OF_FILE);
                    parser.pushValue(JsonNode.object());
                    return;
                }
                if (next == ARRAY_START) {
                    reader.readToken();
                    parser.switchPushState(BEGIN_ARRAY, END_OF_FILE);
                    parser.pushValue(JsonNode.array());
                    return;
                }
                throw parser.expected(OBJECT_START, ARRAY_START);
            }
        },
        VALUE {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next == OBJECT_START) {
                    reader.readToken();
                    parser.switchState(BEGIN_OBJECT);
                    parser.pushValue(JsonNode.object());
                    return;
                }
                if (next == ARRAY_START) {
                    reader.readToken();
                    parser.switchState(BEGIN_ARRAY);
                    parser.pushValue(JsonNode.array());
                    return;
                }
                if (next == STRING) {
                    String str = reader.readString();
                    parser.pushValue(JsonNode.string(str));
                    parser.popState();
                    return;
                }
                if (next == NUMBER) {
                    Number num = reader.readNumber();
                    parser.pushValue(JsonNode.number(num));
                    parser.popState();
                    return;
                }
                if (next == BOOLEAN) {
                    boolean bool = reader.readBoolean();
                    parser.pushValue(JsonNode.bool(bool));
                    parser.popState();
                    return;
                }
                if (next == NULL) {
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
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next == ARRAY_END) {
                    reader.readToken();
                    parser.popState();
                    return;
                }

                if (next.isValue()) {
                    parser.switchState(ARRAY_AFTER_VALUE);
                    parser.pushState(VALUE);
                    return;
                }

                throw parser.expected(parser.valueTokensOr(ARRAY_END));
            }
        },
        ARRAY_AFTER_VALUE {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                JsonNode value = parser.popValue(JsonNode.class);
                parser.peekValue(JsonNode.class).add(value);

                if (next == ARRAY_END) {
                    reader.readToken();
                    parser.popState();
                    return;
                }

                if (next == COMMA) {
                    reader.readToken();
                    parser.switchPushState(VALUE, ARRAY_AFTER_VALUE);
                    return;
                }

                throw parser.expected(ARRAY_END, COMMA);
            }
        },
        BEGIN_OBJECT {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next == OBJECT_END) {
                    reader.readToken();
                    parser.popState();
                    return;
                }

                if (next == STRING) {
                    parser.switchState(OBJECT_KEY);
                    return;
                }

                throw parser.expected(STRING, OBJECT_END);
            }
        },
        OBJECT_KEY {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next == STRING) {
                    parser.pushValue(reader.readString());
                    parser.switchState(OBJECT_AFTER_KEY);
                    return;
                }

                throw parser.expected(STRING);
            }
        },
        OBJECT_AFTER_KEY {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next == COLON) {
                    reader.readToken();
                    parser.switchPushState(VALUE, OBJECT_AFTER_VALUE);
                    return;
                }

                throw parser.expected(COLON);
            }
        },
        OBJECT_AFTER_VALUE {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                JsonNode value = parser.popValue(JsonNode.class);
                String key = parser.popValue(String.class);
                parser.peekValue(JsonNode.class).set(key, value);

                if (next == OBJECT_END) {
                    reader.readToken();
                    parser.popState();
                    return;
                }

                if (next == COMMA) {
                    reader.readToken();
                    parser.switchState(OBJECT_KEY);
                    return;
                }

                throw parser.expected(COMMA, OBJECT_END);
            }
        },
        END_OF_FILE {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next != EOF)
                    throw parser.expected(EOF);
                parser.end();
            }
        }
    }

    private enum Json5State implements State {
        ROOT {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next == OBJECT_START) {
                    reader.readToken();
                    parser.switchPushState(OBJECT_BEFORE_KEY, END_OF_FILE);
                    parser.pushValue(JsonNode.object());
                    return;
                }
                if (next == ARRAY_START) {
                    reader.readToken();
                    parser.switchPushState(ARRAY_BEFORE_VALUE, END_OF_FILE);
                    parser.pushValue(JsonNode.array());
                    return;
                }
                throw parser.expected(OBJECT_START, ARRAY_START);
            }
        },
        VALUE {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next == OBJECT_START) {
                    reader.readToken();
                    parser.switchState(OBJECT_BEFORE_KEY);
                    parser.pushValue(JsonNode.object());
                    return;
                }
                if (next == ARRAY_START) {
                    reader.readToken();
                    parser.switchState(ARRAY_BEFORE_VALUE);
                    parser.pushValue(JsonNode.array());
                    return;
                }
                if (next == STRING) {
                    String str = reader.readString();
                    parser.pushValue(JsonNode.string(str));
                    parser.popState();
                    return;
                }
                if (next == NUMBER) {
                    Number num = reader.readNumber();
                    parser.pushValue(JsonNode.number(num));
                    parser.popState();
                    return;
                }
                if (next == BOOLEAN) {
                    boolean bool = reader.readBoolean();
                    parser.pushValue(JsonNode.bool(bool));
                    parser.popState();
                    return;
                }
                if (next == NULL) {
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
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next == ARRAY_END) {
                    reader.readToken();
                    parser.popState();
                    return;
                }

                if (next.isValue()) {
                    parser.switchPushState(VALUE, ARRAY_AFTER_VALUE);
                    return;
                }

                throw parser.expected(parser.valueTokensOr(ARRAY_END));
            }
        },
        ARRAY_AFTER_VALUE {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                JsonNode value = parser.popValue(JsonNode.class);
                parser.peekValue(JsonNode.class).add(value);

                if (next == ARRAY_END) {
                    reader.readToken();
                    parser.popState();
                    return;
                }

                if (next == COMMA) {
                    reader.readToken();
                    parser.switchState(ARRAY_BEFORE_VALUE);
                    return;
                }

                throw parser.expected(ARRAY_END, COMMA);
            }
        },
        OBJECT_BEFORE_KEY {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next == OBJECT_END) {
                    reader.readToken();
                    parser.popState();
                    return;
                }

                if (next == STRING || next == IDENTIFIER) {
                    parser.switchState(OBJECT_KEY);
                    return;
                }

                throw parser.expected(STRING, IDENTIFIER, OBJECT_END);
            }
        },
        OBJECT_KEY {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next == STRING) {
                    parser.pushValue(reader.readString());
                    parser.switchState(OBJECT_AFTER_KEY);
                    return;
                }
                if (next == IDENTIFIER) {
                    parser.pushValue(reader.readIdentifier());
                    parser.switchState(OBJECT_AFTER_KEY);
                    return;
                }

                throw parser.expected(STRING, IDENTIFIER);
            }
        },
        OBJECT_AFTER_KEY {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next == COLON) {
                    reader.readToken();
                    parser.switchPushState(VALUE, OBJECT_AFTER_VALUE);
                    return;
                }

                throw parser.expected(COLON);
            }
        },
        OBJECT_AFTER_VALUE {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                JsonNode value = parser.popValue(JsonNode.class);
                String key = parser.popValue(String.class);
                parser.peekValue(JsonNode.class).set(key, value);

                if (next == OBJECT_END) {
                    reader.readToken();
                    parser.popState();
                    return;
                }

                if (next == COMMA) {
                    reader.readToken();
                    parser.switchState(OBJECT_BEFORE_KEY);
                    return;
                }

                throw parser.expected(COMMA, OBJECT_END);
            }
        },
        END_OF_FILE {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next != EOF)
                    throw parser.expected(EOF);
                parser.end();
            }
        }
    }
}
