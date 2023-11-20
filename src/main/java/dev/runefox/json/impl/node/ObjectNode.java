package dev.runefox.json.impl.node;

import dev.runefox.json.JsonNode;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ObjectNode extends ConstructNode {
    private final Map<String, JsonNode> children = new LinkedHashMap<>();
    private EntrySet entrySet;

    public ObjectNode() {
    }

    public ObjectNode(Map<? extends String, ? extends JsonNode> map) {
        children.putAll(map);
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public JsonNode get(int index) {
        throw expectedType("ARRAY");
    }

    @Override
    public JsonNode set(int index, JsonNode value) {
        throw expectedType("ARRAY");
    }

    @Override
    public JsonNode add(JsonNode value) {
        throw expectedType("ARRAY");
    }

    @Override
    public JsonNode insert(int index, JsonNode value) {
        throw expectedType("ARRAY");
    }

    @Override
    public JsonNode remove(int index) {
        throw expectedType("ARRAY");
    }

    @Override
    public int size() {
        return children.size();
    }

    @Override
    public JsonNode clear() {
        children.clear();
        return this;
    }

    @Override
    public JsonNode slice(int from, int to) {
        throw expectedType("ARRAY");
    }

    @Override
    public JsonNode get(String key) {
        return children.get(key);
    }

    @Override
    public JsonNode set(String key, JsonNode value) {
        children.put(key, value);
        return this;
    }

    @Override
    public JsonNode remove(String key) {
        children.remove(key);
        return this;
    }

    @Override
    public boolean has(String key) {
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
            entrySet = new EntrySet(children.entrySet());
        return entrySet;
    }

    @Override
    public Stream<JsonNode> stream() {
        return values().stream();
    }

    @Override
    public void forEachEntry(BiConsumer<? super String, ? super JsonNode> fn) {
        children.forEach(fn);
    }

    @Override
    public JsonNode merge(JsonNode object) {
        object.forEachEntry(this::set);
        return this;
    }

    @Override
    public JsonNode deepCopy() {
        JsonNode obj = JsonNode.object();
        forEachEntry((k, v) -> obj.set(k, v.deepCopy()));
        return this;
    }

    @Override
    public JsonNode copy() {
        JsonNode obj = JsonNode.object();
        forEachEntry(obj::set);
        return this;
    }

    @Override
    protected String describeType() {
        return "OBJECT";
    }

    @Override
    public Iterator<JsonNode> iterator() {
        throw expectedType("ARRAY");
    }

    @Override
    public String toString() {
        return children.entrySet().stream()
                       .map(e -> StringNode.quote(e.getKey()) + ": " + e.getValue().toString())
                       .collect(Collectors.joining(", ", "{", "}"));
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
            return new EntrySet.Itr(set.iterator());
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
