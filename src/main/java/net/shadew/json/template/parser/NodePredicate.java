package net.shadew.json.template.parser;

public interface NodePredicate {
    boolean matches(ParserNode node);

    static NodePredicate type(GrammarSymbol type) {
        return node -> node.type() == type;
    }

    default NodePredicate not() {
        NodePredicate self = this;
        return new NodePredicate() {
            @Override
            public boolean matches(ParserNode node) {
                return !self.matches(node);
            }

            @Override
            public NodePredicate not() {
                return self;
            }
        };
    }

    default NodePredicate or(NodePredicate... others) {
        return or(this, others);
    }

    default NodePredicate and(NodePredicate... others) {
        return and(this, others);
    }

    static NodePredicate not(NodePredicate pred) {
        return pred.not();
    }

    static NodePredicate or(NodePredicate first, NodePredicate other) {
        return node -> first.matches(node) || other.matches(node);
    }

    static NodePredicate or(NodePredicate first, NodePredicate... others) {
        if (others.length == 0)
            return first;
        if (others.length == 1)
            return or(first, others[0]);
        return node -> {
            if (first.matches(node)) return true;
            for (NodePredicate pred : others)
                if (pred.matches(node)) return true;
            return false;
        };
    }

    static NodePredicate and(NodePredicate first, NodePredicate other) {
        return node -> first.matches(node) && other.matches(node);
    }

    static NodePredicate and(NodePredicate first, NodePredicate... others) {
        if (others.length == 0)
            return first;
        if (others.length == 1)
            return and(first, others[0]);
        return node -> {
            if (!first.matches(node)) return false;
            for (NodePredicate pred : others)
                if (!pred.matches(node)) return false;
            return true;
        };
    }
}
