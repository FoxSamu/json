package net.shadew.json.template.parser;

import java.util.AbstractList;
import java.util.List;
import java.util.function.Supplier;

public class SimpleChildList extends AbstractList<ParserNode> {
    private final List<Supplier<ParserNode>> list;

    @SafeVarargs
    public SimpleChildList(Supplier<ParserNode>... fields) {
        this.list = List.of(fields);
    }

    @Override
    public ParserNode get(int index) {
        return list.get(index).get();
    }

    @Override
    public int size() {
        return list.size();
    }
}
