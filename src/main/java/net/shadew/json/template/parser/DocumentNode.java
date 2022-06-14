package net.shadew.json.template.parser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.shadew.json.template.Instructions;
import net.shadew.json.template.JsonTemplate;

public class DocumentNode extends ParserNode implements EntityNode.EntityBlockBase<DocumentNode> {
    public final List<EntityNode> entities = new ArrayList<>();

    @Override
    public List<EntityNode> entities() {
        return entities;
    }

    @Override
    protected List<ParserNode> childList() {
        return List.copyOf(entities);
    }

    @Override
    public NodeType type() {
        return NodeType.DOCUMENT;
    }

    @Override
    public EntityNode asEntity() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String asString() {
        return listString();
    }

    public static DocumentNode document() {
        return new DocumentNode();
    }

    public static DocumentNode document(ParserNode... entities) {
        return document().append(entities);
    }

    public static DocumentNode document(Collection<? extends ParserNode> entities) {
        return document().append(entities);
    }

    public JsonTemplate compile() {
        Instructions.Sink sink = new Instructions.Sink();
        compileEntities(sink);
        Instructions insns = sink.build();
        return context -> context.evaluate(insns);
    }
}
