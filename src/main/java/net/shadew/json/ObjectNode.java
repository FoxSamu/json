package net.shadew.json;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class ObjectNode extends AbstractConstructNode {
    private final Map<String, JsonNode> children = new LinkedHashMap<>();
    private EntrySet entrySet;

    ObjectNode() {
        super(JsonType.OBJECT);
    }

    ObjectNode(Map<? extends String, ? extends JsonNode> map) {
        this();
        children.putAll(map);
    }

    @Override
    public JsonNode ifObject(Consumer<JsonNode> action) {
        action.accept(this);
        return this;
    }

    @Override
    public Map<String, JsonNode> asMap() {
        return new LinkedHashMap<>(children);
    }

    @Override
    public JsonNode get(int index) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode set(int index, JsonNode value) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode set(int index, String value) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode set(int index, Number value) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode set(int index, Boolean value) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode add(JsonNode value) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode add(String value) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode add(Number value) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode add(Boolean value) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode insert(int index, JsonNode value) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode insert(int index, String value) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode insert(int index, Number value) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode insert(int index, Boolean value) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode remove(int index) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public int size() {
        return children.size();
    }

    @Override
    public int length() {
        return children.size();
    }

    @Override
    public JsonNode get(String key) {
        if (key == null) key = "null";
        return children.get(key);
    }

    @Override
    public JsonNode set(String key, JsonNode value) {
        if (key == null) key = "null";
        children.put(key, JsonNode.orNull(value));
        return this;
    }

    @Override
    public JsonNode set(String key, String value) {
        if (key == null) key = "null";
        children.put(key, JsonNode.string(value));
        return this;
    }

    @Override
    public JsonNode set(String key, Number value) {
        if (key == null) key = "null";
        children.put(key, JsonNode.number(value));
        return this;
    }

    @Override
    public JsonNode set(String key, Boolean value) {
        if (key == null) key = "null";
        children.put(key, JsonNode.bool(value));
        return this;
    }

    @Override
    public JsonNode remove(String key) {
        if (key == null) key = "null";
        children.remove(key);
        return this;
    }

    @Override
    public boolean has(String key) {
        if (key == null) key = "null";
        return children.containsKey(key);
    }

    @Override
    public boolean contains(JsonNode value) {
        return children.containsValue(value);
    }

    @Override
    public Set<String> keySet() {
        return children.keySet();
    }

    @Override
    public Collection<JsonNode> values() {
        return children.values();
    }

    @Override
    public Set<Map.Entry<String, JsonNode>> entrySet() {
        if (entrySet == null)
            return entrySet = new EntrySet(children.entrySet());
        return entrySet;
    }

    @Override
    public Stream<JsonNode> stream() {
        return children.values().stream();
    }

    @Override
    public void forEachEntry(BiConsumer<? super String, ? super JsonNode> fn) {
        children.forEach(fn);
    }

    @Override
    public JsonNode deepCopy() {
        ObjectNode copy = new ObjectNode();
        for (Map.Entry<String, JsonNode> e : children.entrySet())
            copy.children.put(e.getKey(), e.getValue().deepCopy());
        return copy;
    }

    @Override
    public JsonNode copy() {
        return new ObjectNode(children);
    }

    @Override
    public Iterator<JsonNode> iterator() {
        return children.values().iterator();
    }

    @Override
    public JsonNode clear() {
        children.clear();
        return this;
    }

    @Override
    public JsonNode append(JsonNode other) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode prepend(JsonNode other) {
        throw new IncorrectTypeException(JsonType.OBJECT, JsonType.ARRAY);
    }

    @Override
    public JsonNode slice(int from, int to) {
        throw new IncorrectTypeException(type(), JsonType.ARRAY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        ObjectNode other = (ObjectNode) o;
        return children.equals(other.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(children);
    }

    @Override
    public String toString() {
        return "{" + children.entrySet().stream()
                             .map(e -> StringNode.quote(e.getKey()) + ": " + e.getValue())
                             .collect(Collectors.joining(", "))
                   + "}";
    }

    private static class EntrySet implements Set<Map.Entry<String, JsonNode>> {
        private final Set<Map.Entry<String, JsonNode>> set;
        private final WeakHashMap<Map.Entry<String, JsonNode>, EntryWrapper> wrapperCache = new WeakHashMap<>();

        private EntrySet(Set<Map.Entry<String, JsonNode>> set) {
            this.set = set;
        }

        @Override
        public int size() {
            return set.size();
        }

        @Override
        public boolean isEmpty() {
            return set.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return set.contains(o);
        }

        @Override
        public Iterator<Map.Entry<String, JsonNode>> iterator() {
            return new Itr(set.iterator());
        }

        @Override
        public Object[] toArray() {
            return set.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return set.toArray(a);
        }

        @Override
        public boolean add(Map.Entry<String, JsonNode> stringJsonNodeEntry) {
            throw new UnsupportedOperationException("add");
        }

        @Override
        public boolean remove(Object o) {
            return set.remove(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return set.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends Map.Entry<String, JsonNode>> c) {
            throw new UnsupportedOperationException("addAll");
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return set.retainAll(c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return set.removeAll(c);
        }

        @Override
        public void clear() {
            set.clear();
        }

        @Override
        public void forEach(Consumer<? super Map.Entry<String, JsonNode>> action) {
            set.forEach(e -> action.accept(wrap(e)));
        }

        private Map.Entry<String, JsonNode> wrap(Map.Entry<String, JsonNode> e) {
            return wrapperCache.computeIfAbsent(e, EntryWrapper::new);
        }

        private class Itr implements Iterator<Map.Entry<String, JsonNode>> {
            private final Iterator<Map.Entry<String, JsonNode>> itr;

            private Itr(Iterator<Map.Entry<String, JsonNode>> itr) {
                this.itr = itr;
            }

            @Override
            public boolean hasNext() {
                return itr.hasNext();
            }

            @Override
            public Map.Entry<String, JsonNode> next() {
                return wrap(itr.next());
            }

            @Override
            public void remove() {
                itr.remove();
            }

            @Override
            public void forEachRemaining(Consumer<? super Map.Entry<String, JsonNode>> action) {
                itr.forEachRemaining(e -> action.accept(wrap(e)));
            }
        }
    }

    private static class EntryWrapper implements Map.Entry<String, JsonNode> {
        private final Map.Entry<String, JsonNode> entry;

        private EntryWrapper(Map.Entry<String, JsonNode> entry) {
            this.entry = entry;
        }

        @Override
        public String getKey() {
            return entry.getKey();
        }

        @Override
        public JsonNode getValue() {
            return entry.getValue();
        }

        @Override
        public JsonNode setValue(JsonNode value) {
            return entry.setValue(JsonNode.orNull(value));
        }
    }
}
