package net.shadew.json.template;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.shadew.json.JsonNode;

class UpRangeIterator implements Iterator<JsonNode> {
    private final int to;
    private int i;

    UpRangeIterator(int from, int to) {
        this.to = to;
        i = from;
    }

    @Override
    public boolean hasNext() {
        return i < to;
    }

    @Override
    public JsonNode next() {
        if (i >= to) throw new NoSuchElementException();
        return JsonNode.number(i++);
    }
}
