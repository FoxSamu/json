package net.shadew.json.template.parser;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import net.shadew.json.JsonSyntaxException;
import net.shadew.json.template.TemplateDebug;

class TemplateParser {
    private static final ThreadLocal<TemplateParser> PARSER_INSTANCE = ThreadLocal.withInitial(TemplateParser::new);
    private final ParserTable table = ParserDefinition.table();

    private TemplateLexer lexer;
    private final Stack<ParserNode> stack = new Stack<>();
    private int startState = 0;

    private static final int BASE_STACK_SIZE = 16;
    private static final int STACK_GROW = 16;
    private static final int STACK_SHRINK_THRESHOLD = 1024;

    private final Token lookahead = new Token();

    private boolean accepted = false;

    void shift() {
        push(new TokenNode(lookahead));
        lookahead();
    }

    private void lookahead() {
        try {
            lexer.token(lookahead);
        } catch (IOException exc) {
            throw new UncheckedIOException(exc);
        }
    }

    void shift(int state) {
        shift();
        state(state);
    }

    void reduce(Grammar.Rule rule) {
        Reduction reduction = rule.reduction;
        int amount = reduction.amount();
        ParserNode[] nodes = new ParserNode[amount];
        for (int i = amount - 1; i >= 0; i--) {
            nodes[i] = pop();
        }

        int next = table.goTo(state(), rule.lhs);
        push(reduction.reduce(nodes));
        state(next);
    }

    void accept() {
        accepted = true;
    }

    void state(int state) {
        peek().parserState = state;
    }

    int state() {
        if (stack.empty()) return 0;
        return peek().parserState;
    }

    void push(ParserNode value) {
        stack.push(value);
    }

    ParserNode peek() {
        return stack.peek();
    }

    ParserNode pop() {
        return stack.pop();
    }

    boolean matches(NodePredicate... pattern) {
        int l = pattern.length;
        int s = stack.size() - l;
        if (s < 0) {
            return false;
        }

        for (int i = 0; i < l; i++) {
            if (!pattern[i].matches(stack.get(s + i))) {
                return false;
            }
        }
        return true;
    }

    JsonSyntaxException makeError(WrongSyntax wrongSyntax) {
        return lookahead.error(wrongSyntax.getMessage());
    }

    private void parse0(TemplateLexer lexer) throws JsonSyntaxException {
        this.lexer = lexer;
        stack.clear();
        lookahead();
        while (!accepted) {
            ParserTable.Action action = table.action(state(), lookahead.getType());

            if (TemplateDebug.debug && TemplateDebug.sleepParser > 0) {
                System.out.println(action.name());
                try {
                    Thread.sleep(TemplateDebug.sleepParser);
                } catch (InterruptedException exc) {
                    return;
                }
            }

            if (action == null) {
                ParserTable.State state = table.state(state());
                List<String> expected = new ArrayList<>();
                for (TokenType terminal : TokenType.values()) {
                    if (state.action(terminal) != null)
                        expected.add(terminal.errorName());
                }
                throw lookahead.error("Expected " + String.join(", ", expected));
            }
            action.accept(this);
        }
    }

    public static ParserNode parse(TemplateLexer lexer) throws JsonSyntaxException {
        TemplateParser parser = PARSER_INSTANCE.get();
        parser.parse0(lexer);
        return parser.pop();
    }
}
