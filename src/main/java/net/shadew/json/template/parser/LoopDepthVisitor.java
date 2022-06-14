package net.shadew.json.template.parser;

import java.util.EnumSet;
import java.util.Stack;

/**
 * Computes loop depths, to validate break depths
 */
public class LoopDepthVisitor implements ParseTreeVisitor {
    private static final EnumSet<NodeType> LOOPS = EnumSet.of(
        NodeType.ENT_FOR_IN,
        NodeType.ENT_FOR_FROM_TO,
        NodeType.ENT_FOR_IN_OBJ
    );

    private static final EnumSet<NodeType> SCOPES = EnumSet.of(
        NodeType.DOCUMENT,
        NodeType.EXPR_OBJECT,
        NodeType.EXPR_ARRAY,
        NodeType.EXPR_SUBTEMPLATE,
        NodeType.ENT_DEF_SUBTEMPLATE_FN,
        NodeType.ENT_DEF_EXPRESSION_FN
    );

    private static final EnumSet<NodeType> ASSIGN_TO = EnumSet.of(
        NodeType.ENT_BREAK,
        NodeType.ENT_CONTINUE,
        NodeType.ENT_FOR_IN,
        NodeType.ENT_FOR_FROM_TO,
        NodeType.ENT_FOR_IN_OBJ
    );

    private final Stack<Counter> counterStack = new Stack<>();

    @Override
    public void enter(NodeType type, ParserNode node) {
        if (SCOPES.contains(type)) {
            counterStack.push(new Counter());
        }
        if (LOOPS.contains(type)) {
            counterStack.peek().enterLoop();
        }
        if (ASSIGN_TO.contains(type)) {
            node.property("loop_depth", counterStack.peek().depth);
        }
    }

    @Override
    public void exit(NodeType type, ParserNode node) {
        if (SCOPES.contains(type)) {
            counterStack.pop();
        }
        if (LOOPS.contains(type)) {
            counterStack.peek().exitLoop();
        }
    }

    private static class Counter {
        int depth;

        void enterLoop() {
            depth++;
        }

        void exitLoop() {
            depth--;
        }
    }
}
