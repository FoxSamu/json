package dev.runefox.json;

import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Parser {
    private static final ThreadLocal<Parser> PARSER_INSTANCE = ThreadLocal.withInitial(Parser::new);
    private JsonReader reader;
    private final Stack<Object> valueStack = new Stack<>();
    private final Stack<State> stateStack = new Stack<>();
    private boolean end = false;

    private static final TokenType[] VALUE_TOKENS = {
        TokenType.NULL,
        TokenType.NUMBER,
        TokenType.BOOLEAN,
        TokenType.STRING,
        TokenType.OBJECT_START,
        TokenType.ARRAY_START
    };

    // We can reuse this array to reduce instantiations
    private final TokenType[] valueTokensOr = {
        null,
        TokenType.NULL,
        TokenType.NUMBER,
        TokenType.BOOLEAN,
        TokenType.STRING,
        TokenType.OBJECT_START,
        TokenType.ARRAY_START
    };

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

    private void parse0(JsonReader reader, ParsingConfig config) throws JsonSyntaxException {
        this.reader = reader;
        valueStack.clear();
        stateStack.clear();
        if (config.anyValue()) {
            // Must push EOF because we aren't using ROOT
            stateStack.push(JsonState.END_OF_FILE);
        }
        stateStack.push(
            config.json5()
            ? config.anyValue() ? Json5State.VALUE : Json5State.ROOT
            : config.anyValue() ? JsonState.VALUE : JsonState.ROOT
        );
        end = false;

        while (!end) {
            stateStack.peek().parseToken(reader.peekToken(), reader, this);
        }
    }

    public static JsonNode parse(JsonReader reader, ParsingConfig config) throws JsonSyntaxException {
        Parser parser = PARSER_INSTANCE.get();
        parser.parse0(reader, config);
        return parser.popValue(JsonNode.class);
    }

    private interface State {
        void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException;
    }

    private enum JsonState implements State {
        ROOT {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next == TokenType.OBJECT_START) {
                    reader.readToken();
                    parser.switchPushState(BEGIN_OBJECT, END_OF_FILE);
                    parser.pushValue(JsonNode.object());
                    return;
                }
                if (next == TokenType.ARRAY_START) {
                    reader.readToken();
                    parser.switchPushState(BEGIN_ARRAY, END_OF_FILE);
                    parser.pushValue(JsonNode.array());
                    return;
                }
                throw parser.expected(TokenType.OBJECT_START, TokenType.ARRAY_START);
            }
        },
        VALUE {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next == TokenType.OBJECT_START) {
                    reader.readToken();
                    parser.switchState(BEGIN_OBJECT);
                    parser.pushValue(JsonNode.object());
                    return;
                }
                if (next == TokenType.ARRAY_START) {
                    reader.readToken();
                    parser.switchState(BEGIN_ARRAY);
                    parser.pushValue(JsonNode.array());
                    return;
                }
                if (next == TokenType.STRING) {
                    String str = reader.readString();
                    parser.pushValue(JsonNode.string(str));
                    parser.popState();
                    return;
                }
                if (next == TokenType.NUMBER) {
                    Number num = reader.readNumber();
                    parser.pushValue(JsonNode.number(num));
                    parser.popState();
                    return;
                }
                if (next == TokenType.BOOLEAN) {
                    boolean bool = reader.readBoolean();
                    parser.pushValue(JsonNode.bool(bool));
                    parser.popState();
                    return;
                }
                if (next == TokenType.NULL) {
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
                if (next == TokenType.ARRAY_END) {
                    reader.readToken();
                    parser.popState();
                    return;
                }

                if (next.isValue()) {
                    parser.switchState(ARRAY_AFTER_VALUE);
                    parser.pushState(VALUE);
                    return;
                }

                throw parser.expected(parser.valueTokensOr(TokenType.ARRAY_END));
            }
        },
        ARRAY_AFTER_VALUE {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                JsonNode value = parser.popValue(JsonNode.class);
                parser.peekValue(JsonNode.class).add(value);

                if (next == TokenType.ARRAY_END) {
                    reader.readToken();
                    parser.popState();
                    return;
                }

                if (next == TokenType.COMMA) {
                    reader.readToken();
                    parser.switchPushState(VALUE, ARRAY_AFTER_VALUE);
                    return;
                }

                throw parser.expected(TokenType.ARRAY_END, TokenType.COMMA);
            }
        },
        BEGIN_OBJECT {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next == TokenType.OBJECT_END) {
                    reader.readToken();
                    parser.popState();
                    return;
                }

                if (next == TokenType.STRING) {
                    parser.switchState(OBJECT_KEY);
                    return;
                }

                throw parser.expected(TokenType.STRING, TokenType.OBJECT_END);
            }
        },
        OBJECT_KEY {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next == TokenType.STRING) {
                    parser.pushValue(reader.readString());
                    parser.switchState(OBJECT_AFTER_KEY);
                    return;
                }

                throw parser.expected(TokenType.STRING);
            }
        },
        OBJECT_AFTER_KEY {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next == TokenType.COLON) {
                    reader.readToken();
                    parser.switchPushState(VALUE, OBJECT_AFTER_VALUE);
                    return;
                }

                throw parser.expected(TokenType.COLON);
            }
        },
        OBJECT_AFTER_VALUE {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                JsonNode value = parser.popValue(JsonNode.class);
                String key = parser.popValue(String.class);
                parser.peekValue(JsonNode.class).set(key, value);

                if (next == TokenType.OBJECT_END) {
                    reader.readToken();
                    parser.popState();
                    return;
                }

                if (next == TokenType.COMMA) {
                    reader.readToken();
                    parser.switchState(OBJECT_KEY);
                    return;
                }

                throw parser.expected(TokenType.COMMA, TokenType.OBJECT_END);
            }
        },
        END_OF_FILE {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next != TokenType.EOF)
                    throw parser.expected(TokenType.EOF);
                parser.end();
            }
        }
    }

    private enum Json5State implements State {
        ROOT {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next == TokenType.OBJECT_START) {
                    reader.readToken();
                    parser.switchPushState(OBJECT_BEFORE_KEY, END_OF_FILE);
                    parser.pushValue(JsonNode.object());
                    return;
                }
                if (next == TokenType.ARRAY_START) {
                    reader.readToken();
                    parser.switchPushState(ARRAY_BEFORE_VALUE, END_OF_FILE);
                    parser.pushValue(JsonNode.array());
                    return;
                }
                throw parser.expected(TokenType.OBJECT_START, TokenType.ARRAY_START);
            }
        },
        VALUE {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next == TokenType.OBJECT_START) {
                    reader.readToken();
                    parser.switchState(OBJECT_BEFORE_KEY);
                    parser.pushValue(JsonNode.object());
                    return;
                }
                if (next == TokenType.ARRAY_START) {
                    reader.readToken();
                    parser.switchState(ARRAY_BEFORE_VALUE);
                    parser.pushValue(JsonNode.array());
                    return;
                }
                if (next == TokenType.STRING) {
                    String str = reader.readString();
                    parser.pushValue(JsonNode.string(str));
                    parser.popState();
                    return;
                }
                if (next == TokenType.NUMBER) {
                    Number num = reader.readNumber();
                    parser.pushValue(JsonNode.number(num));
                    parser.popState();
                    return;
                }
                if (next == TokenType.BOOLEAN) {
                    boolean bool = reader.readBoolean();
                    parser.pushValue(JsonNode.bool(bool));
                    parser.popState();
                    return;
                }
                if (next == TokenType.NULL) {
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
                if (next == TokenType.ARRAY_END) {
                    reader.readToken();
                    parser.popState();
                    return;
                }

                if (next.isValue()) {
                    parser.switchPushState(VALUE, ARRAY_AFTER_VALUE);
                    return;
                }

                throw parser.expected(parser.valueTokensOr(TokenType.ARRAY_END));
            }
        },
        ARRAY_AFTER_VALUE {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                JsonNode value = parser.popValue(JsonNode.class);
                parser.peekValue(JsonNode.class).add(value);

                if (next == TokenType.ARRAY_END) {
                    reader.readToken();
                    parser.popState();
                    return;
                }

                if (next == TokenType.COMMA) {
                    reader.readToken();
                    parser.switchState(ARRAY_BEFORE_VALUE);
                    return;
                }

                throw parser.expected(TokenType.ARRAY_END, TokenType.COMMA);
            }
        },
        OBJECT_BEFORE_KEY {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next == TokenType.OBJECT_END) {
                    reader.readToken();
                    parser.popState();
                    return;
                }

                if (next == TokenType.STRING || next == TokenType.IDENTIFIER) {
                    parser.switchState(OBJECT_KEY);
                    return;
                }

                throw parser.expected(TokenType.STRING, TokenType.IDENTIFIER, TokenType.OBJECT_END);
            }
        },
        OBJECT_KEY {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next == TokenType.STRING) {
                    parser.pushValue(reader.readString());
                    parser.switchState(OBJECT_AFTER_KEY);
                    return;
                }
                if (next == TokenType.IDENTIFIER) {
                    parser.pushValue(reader.readIdentifier());
                    parser.switchState(OBJECT_AFTER_KEY);
                    return;
                }

                throw parser.expected(TokenType.STRING, TokenType.IDENTIFIER);
            }
        },
        OBJECT_AFTER_KEY {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next == TokenType.COLON) {
                    reader.readToken();
                    parser.switchPushState(VALUE, OBJECT_AFTER_VALUE);
                    return;
                }

                throw parser.expected(TokenType.COLON);
            }
        },
        OBJECT_AFTER_VALUE {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                JsonNode value = parser.popValue(JsonNode.class);
                String key = parser.popValue(String.class);
                parser.peekValue(JsonNode.class).set(key, value);

                if (next == TokenType.OBJECT_END) {
                    reader.readToken();
                    parser.popState();
                    return;
                }

                if (next == TokenType.COMMA) {
                    reader.readToken();
                    parser.switchState(OBJECT_BEFORE_KEY);
                    return;
                }

                throw parser.expected(TokenType.COMMA, TokenType.OBJECT_END);
            }
        },
        END_OF_FILE {
            @Override
            public void parseToken(TokenType next, JsonReader reader, Parser parser) throws JsonSyntaxException {
                if (next != TokenType.EOF)
                    throw parser.expected(TokenType.EOF);
                parser.end();
            }
        }
    }
}
