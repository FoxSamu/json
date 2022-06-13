package net.shadew.json.template.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.shadew.json.JsonSyntaxException;

public abstract class ParsedTemplateNode {
    private int fromPos, fromLine, fromCol;
    private int toPos, toLine, toCol;
    private List<ParsedTemplateNode> children;
    private ParsedTemplateNode parent;
    private ParsedTemplateNode next;
    private ParsedTemplateNode prev;
    private int nthChild;
    private final Map<String, Object> properties = new HashMap<>();

    public final void position(int fromPos, int fromLine, int fromCol, int toPos, int toLine, int toCol) {
        this.fromPos = fromPos;
        this.fromLine = fromLine;
        this.fromCol = fromCol;
        this.toPos = toPos;
        this.toLine = toLine;
        this.toCol = toCol;
    }

    public final void start(int fromPos, int fromLine, int fromCol) {
        this.fromPos = fromPos;
        this.fromLine = fromLine;
        this.fromCol = fromCol;
    }

    public final void end(int toPos, int toLine, int toCol) {
        this.toPos = toPos;
        this.toLine = toLine;
        this.toCol = toCol;
    }

    protected abstract List<ParsedTemplateNode> childList();
    public abstract NodeType type();
    public abstract EntityNode asEntity();
    public abstract String asString();

    @Override
    public String toString() {
        return "Node " + type() + " [ " + asString() + " ]";
    }

    public final void updateTree(ParsedTemplateNode parent) {
        this.parent = parent;
        this.children = childList();
        this.nthChild = 0;
        this.prev = null;
        this.next = null;
        int i = 0;
        ParsedTemplateNode last = null;

        for (ParsedTemplateNode c : children) {
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

    public final ParsedTemplateNode prev() {
        return prev;
    }

    public final ParsedTemplateNode next() {
        return next;
    }

    public final ParsedTemplateNode parent() {
        return parent;
    }

    public final List<ParsedTemplateNode> children() {
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

    public final void visit(ParseTreeVisitor visitor) {
        visitor.enter(type(), this);
        for (ParsedTemplateNode node : children)
            node.visit(visitor);
        visitor.exit(type(), this);
    }

    public JsonSyntaxException error(String problem) {
        return new JsonSyntaxException(fromPos, fromLine, fromCol, toPos, toLine, toCol, problem);
    }
}
