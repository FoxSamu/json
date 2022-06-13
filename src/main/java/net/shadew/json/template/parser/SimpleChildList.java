package net.shadew.json.template.parser;

import java.util.AbstractList;
import java.util.List;
import java.util.function.Supplier;

public class SimpleChildList extends AbstractList<ParsedTemplateNode> {
    private final List<Supplier<ParsedTemplateNode>> list;

    @SafeVarargs
    public SimpleChildList(Supplier<ParsedTemplateNode>... fields) {
        this.list = List.of(fields);
    }

    @Override
    public ParsedTemplateNode get(int index) {
        return list.get(index).get();
    }

    @Override
    public int size() {
        return list.size();
    }
}
