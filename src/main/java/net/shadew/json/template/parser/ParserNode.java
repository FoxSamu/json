package net.shadew.json.template.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.shadew.json.JsonSyntaxException;

public abstract class ParserNode {
    private int fromPos, fromLine, fromCol;
    private int toPos, toLine, toCol;
    private List<ParserNode> children;
    private ParserNode parent;
    private ParserNode next;
    private ParserNode prev;
    private int nthChild;
    private final Map<String, Object> properties = new HashMap<>();

    public int parserState;

    public final <T extends ParserNode> T as(Class<T> type) {
        return type.cast(this);
    }

    public final ExpressionNode asExpr() {
        return (ExpressionNode) this;
    }

    public final EntityNode asEnt() {
        return (EntityNode) this;
    }

    public final DocumentNode asDoc() {
        return (DocumentNode) this;
    }

    public final TokenNode asToken() {
        return (TokenNode) this;
    }

    public final void position(int fromPos, int fromLine, int fromCol, int toPos, int toLine, int toCol) {
        this.fromPos = fromPos;
        this.fromLine = fromLine;
        this.fromCol = fromCol;
        this.toPos = toPos;
        this.toLine = toLine;
        this.toCol = toCol;
    }

    public final void position(ParserNode node) {
        this.fromPos = node.fromPos;
        this.fromLine = node.fromLine;
        this.fromCol = node.fromCol;
        this.toPos = node.toPos;
        this.toLine = node.toLine;
        this.toCol = node.toCol;
    }

    public final void start(int fromPos, int fromLine, int fromCol) {
        this.fromPos = fromPos;
        this.fromLine = fromLine;
        this.fromCol = fromCol;
    }

    public final void start(ParserNode node) {
        this.fromPos = node.fromPos;
        this.fromLine = node.fromLine;
        this.fromCol = node.fromCol;
    }

    public final void end(int toPos, int toLine, int toCol) {
        this.toPos = toPos;
        this.toLine = toLine;
        this.toCol = toCol;
    }

    public final void end(ParserNode node) {
        this.toPos = node.toPos;
        this.toLine = node.toLine;
        this.toCol = node.toCol;
    }

    protected abstract List<ParserNode> childList();
    public abstract GrammarSymbol type();
    public abstract EntityNode asEntity();
    public abstract String asString();

    @Override
    public String toString() {
        return "Node " + type() + " [ " + asString() + " ]";
    }

    public final void updateTree(ParserNode parent) {
        this.parent = parent;
        this.children = childList();
        this.nthChild = 0;
        this.prev = null;
        this.next = null;
        int i = 0;
        ParserNode last = null;

        for (ParserNode c : children) {
            c.updateTree(this);
            c.nthChild = i++;

            if (last != null) {
                last.next = c;
                c.prev = last;
            }

            last = c;
        }
    }

    public final int nthChild() {
        return nthChild;
    }

    public final ParserNode prev() {
        return prev;
    }

    public final ParserNode next() {
        return next;
    }

    public final ParserNode parent() {
        return parent;
    }

    public final List<ParserNode> children() {
        return children;
    }

    public final boolean hasProperty(String name) {
        return properties.containsKey(name);
    }

    public final void property(String name, Object val) {
        properties.put(name, val);
    }

    @SuppressWarnings("unchecked")
    public final <T> T property(String name) {
        return (T) properties.get(name);
    }

    public final Map<String, Object> properties() {
        return properties;
    }

    public final int fromPos() {
        return fromPos;
    }

    public final int fromLine() {
        return fromLine;
    }

    public final int fromCol() {
        return fromCol;
    }

    public final int toPos() {
        return toPos;
    }

    public final int toLine() {
        return toLine;
    }

    public final int toCol() {
        return toCol;
    }

    public final void visit(ParseTreeVisitor visitor) {
        GrammarSymbol type = type();
        if (!type.isTerminal()) {
            NodeType ntype = (NodeType) type;
            visitor.enter(ntype, this);
            for (ParserNode node : children)
                node.visit(visitor);
            visitor.exit(ntype, this);
        }
    }

    public JsonSyntaxException error(String problem) {
        return new JsonSyntaxException(fromPos, fromLine, fromCol, toPos, toLine, toCol, problem);
    }
}
