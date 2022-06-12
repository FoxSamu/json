package net.shadew.json.template;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.shadew.json.JsonNode;

class StringIterator implements Iterator<JsonNode> {
    private final String str;
    private int i = 0;

    StringIterator(String str) {
        this.str = str;
    }

    @Override
    public boolean hasNext() {
        return i < str.length();
    }

    @Override
    public JsonNode next() {
        if (!hasNext()) throw new NoSuchElementException();
        return JsonNode.string("" + str.charAt(i++));
    }
}
