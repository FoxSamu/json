package net.shadew.json.template.parser;

import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.shadew.json.JsonSyntaxException;
import net.shadew.json.ParsingConfig;

class TemplateParser {
    private static final ThreadLocal<TemplateParser> PARSER_INSTANCE = ThreadLocal.withInitial(TemplateParser::new);
    private TemplateLexer lexer;
    private final Stack<Object> valueStack = new Stack<>();
    private final Stack<State> stateStack = new Stack<>();
    private boolean end = false;

    private final Token token = new Token();
    private boolean retain;

    void retain() {
        retain = true;
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
        return lexer.error("Expected " + type.getErrorName());
    }

    JsonSyntaxException expected(TokenType... types) {
        return lexer.error(Stream.of(types).map(TokenType::getErrorName).collect(Collectors.joining(", ", "Expected ", "")));
    }

    private void parse0(TemplateLexer lexer, ParsingConfig config) throws JsonSyntaxException {
//        this.lexer = lexer;
//        valueStack.clear();
//        stateStack.clear();
//        if (config.anyValue()) {
//            // Must push EOF because we aren't using ROOT
//            stateStack.push(Parser.JsonState.END_OF_FILE);
//        }
//        stateStack.push(
//            config.json5()
//            ? config.anyValue() ? Parser.Json5State.VALUE : Parser.Json5State.ROOT
//            : config.anyValue() ? Parser.JsonState.VALUE : Parser.JsonState.ROOT
//        );
//        end = false;
//
//        while (!end) {
//            try {
//                if (!retain)
//                    lexer.token(token);
//                retain = false;
//                stateStack.peek().parseToken(token, this);
//            } catch (IOException e) {
//                throw new UncheckedIOException(e);
//            }
//        }
    }

    public static ParsedTemplateNode parse(TemplateLexer lexer, ParsingConfig config) throws JsonSyntaxException {
        TemplateParser parser = PARSER_INSTANCE.get();
        parser.parse0(lexer, config);
        return parser.popValue(ParsedTemplateNode.class);
    }

    private interface State {
        void parseToken(Token next, TemplateParser parser) throws JsonSyntaxException;
    }
}
