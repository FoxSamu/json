package net.shadew.json.template;

import java.util.Iterator;
import java.util.NoSuchElementException;

import net.shadew.json.JsonNode;

enum EmptyIterator implements Iterator<JsonNode> {
    INSTANCE;

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public JsonNode next() {
        throw new NoSuchElementException();
    }
}
