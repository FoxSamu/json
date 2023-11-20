package dev.runefox.json.impl.node;

import dev.runefox.json.JsonNode;

import java.util.Collection;
import java.util.Iterator;

public class Values implements Collection<JsonNode> {
    private final Collection<JsonNode> children;

    public Values(Collection<JsonNode> children) {
        this.children = children;
    }

    @Override
    public int size() {
        return children.size();
    }

    @Override
    public boolean isEmpty() {
        return children.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return children.contains(o);
    }

    @Override
    public Iterator<JsonNode> iterator() {
        return children.iterator();
    }

    @Override
    public Object[] toArray() {
        return children.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return children.toArray(a);
    }

    @Override
    public boolean add(JsonNode jsonNode) {
        throw new UnsupportedOperationException("add");
    }

    @Override
    public boolean remove(Object o) {
        return children.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return children.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends JsonNode> c) {
        throw new UnsupportedOperationException("addAll");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return children.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return children.retainAll(c);
    }

    @Override
    public void clear() {
        children.clear();
    }

    @Override
    public String toString() {
        return children.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o.getClass() == Values.class)
            return children.equals(((Values) o).children);
        return false;
    }

    @Override
    public int hashCode() {
        return children.hashCode();
    }
}
