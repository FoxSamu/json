package net.shadew.json.template;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.shadew.json.JsonNode;

public class RangeIterator implements Iterator<JsonNode> {
    private final int from, to, incr;
    private int i;

    public RangeIterator(int from, int to) {
        boolean back = to < from;
        this.from = back ? to : from;
        this.to = back ? from : to;
        this.incr = back ? -1 : 1;
        i = from;
    }

    private boolean isInRange(int i) {
        return i >= from && i < to;
    }

    @Override
    public boolean hasNext() {
        return isInRange(i + incr);
    }

    @Override
    public JsonNode next() {
        if (!hasNext()) throw new NoSuchElementException();
        int j = i;
        i += incr;
        return JsonNode.number(j);
    }
}
