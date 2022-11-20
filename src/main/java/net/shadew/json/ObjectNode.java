package net.shadew.json;

import java.math.BigDecimal;
import java.math.BigInteger;
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
    public JsonType type(String key) {
        if (!has(key)) return null;
        return get(key).type();
    }

    @Override
    public boolean isNull(String key) {
        if (!has(key)) return false;
        return get(key).isNull();
    }

    @Override
    public boolean isString(String key) {
        if (!has(key)) return false;
        return get(key).isString();
    }

    @Override
    public boolean isNumber(String key) {
        if (!has(key)) return false;
        return get(key).isNumber();
    }

    @Override
    public boolean isBoolean(String key) {
        if (!has(key)) return false;
        return get(key).isBoolean();
    }

    @Override
    public boolean isObject(String key) {
        if (!has(key)) return false;
        return get(key).isObject();
    }

    @Override
    public boolean isArray(String key) {
        if (!has(key)) return false;
        return get(key).isArray();
    }

    @Override
    public boolean isPrimitive(String key) {
        if (!has(key)) return false;
        return get(key).isPrimitive();
    }

    @Override
    public boolean isConstruct(String key) {
        if (!has(key)) return false;
        return get(key).isConstruct();
    }

    @Override
    public boolean is(String key, JsonType type) {
        if (!has(key)) return false;
        return get(key).is(type);
    }

    @Override
    public boolean is(String key, JsonType... types) {
        if (!has(key)) return false;
        return get(key).is(types);
    }

    @Override
    public JsonNode requireHas(String key) {
        if (!has(key)) throw new MissingKeyException(key);
        return this;
    }

    @Override
    public JsonNode requireNull(String key) {
        if (!has(key)) throw new MissingKeyException(key);
        get(key).requireNull();
        return this;
    }

    @Override
    public JsonNode requireNotNull(String key) {
        if (!has(key)) throw new MissingKeyException(key);
        get(key).requireNotNull();
        return this;
    }

    @Override
    public JsonNode requireString(String key) {
        if (!has(key)) throw new MissingKeyException(key);
        get(key).requireString();
        return this;
    }

    @Override
    public JsonNode requireNotString(String key) {
        if (!has(key)) throw new MissingKeyException(key);
        get(key).requireNotString();
        return this;
    }

    @Override
    public JsonNode requireNumber(String key) {
        if (!has(key)) throw new MissingKeyException(key);
        get(key).requireNumber();
        return this;
    }

    @Override
    public JsonNode requireNotNumber(String key) {
        if (!has(key)) throw new MissingKeyException(key);
        get(key).requireNotNumber();
        return this;
    }

    @Override
    public JsonNode requireBoolean(String key) {
        if (!has(key)) throw new MissingKeyException(key);
        get(key).requireBoolean();
        return this;
    }

    @Override
    public JsonNode requireNotBoolean(String key) {
        if (!has(key)) throw new MissingKeyException(key);
        get(key).requireNotBoolean();
        return this;
    }

    @Override
    public JsonNode requireObject(String key) {
        if (!has(key)) throw new MissingKeyException(key);
        get(key).requireObject();
        return this;
    }

    @Override
    public JsonNode requireNotObject(String key) {
        if (!has(key)) throw new MissingKeyException(key);
        get(key).requireNotObject();
        return this;
    }

    @Override
    public JsonNode requireArray(String key) {
        if (!has(key)) throw new MissingKeyException(key);
        get(key).requireArray();
        return this;
    }

    @Override
    public JsonNode requireNotArray(String key) {
        if (!has(key)) throw new MissingKeyException(key);
        get(key).requireNotArray();
        return this;
    }

    @Override
    public JsonNode requirePrimitive(String key) {
        if (!has(key)) throw new MissingKeyException(key);
        get(key).requirePrimitive();
        return this;
    }

    @Override
    public JsonNode requireNotPrimitive(String key) {
        if (!has(key)) throw new MissingKeyException(key);
        get(key).requireNotPrimitive();
        return this;
    }

    @Override
    public JsonNode requireConstruct(String key) {
        if (!has(key)) throw new MissingKeyException(key);
        get(key).requireConstruct();
        return this;
    }

    @Override
    public JsonNode requireNotConstruct(String key) {
        if (!has(key)) throw new MissingKeyException(key);
        get(key).requireNotConstruct();
        return this;
    }

    @Override
    public JsonNode require(String key, JsonType type) {
        if (!has(key)) throw new MissingKeyException(key);
        get(key).require(type);
        return this;
    }

    @Override
    public JsonNode requireNot(String key, JsonType type) {
        if (!has(key)) throw new MissingKeyException(key);
        get(key).requireNot(type);
        return this;
    }

    @Override
    public JsonNode require(String key, JsonType... types) {
        if (!has(key)) throw new MissingKeyException(key);
        get(key).require(types);
        return this;
    }

    @Override
    public JsonNode requireNot(String key, JsonType... types) {
        if (!has(key)) throw new MissingKeyException(key);
        get(key).requireNot(types);
        return this;
    }

    @Override
    public JsonNode ifHas(String key, Consumer<JsonNode> action) {
        JsonNode n = get(key);
        if (n != null) {
            action.accept(n);
        }
        return this;
    }

    @Override
    public JsonNode ifString(String key, BiConsumer<JsonNode, String> action) {
        JsonNode n = get(key);
        if (n != null) {
            n.ifString(action);
        }
        return this;
    }

    @Override
    public JsonNode ifNumber(String key, BiConsumer<JsonNode, Number> action) {
        JsonNode n = get(key);
        if (n != null) {
            n.ifNumber(action);
        }
        return this;
    }

    @Override
    public JsonNode ifByte(String key, BiConsumer<JsonNode, Byte> action) {
        JsonNode n = get(key);
        if (n != null) {
            n.ifByte(action);
        }
        return this;
    }

    @Override
    public JsonNode ifShort(String key, BiConsumer<JsonNode, Short> action) {
        JsonNode n = get(key);
        if (n != null) {
            n.ifShort(action);
        }
        return this;
    }

    @Override
    public JsonNode ifInt(String key, BiConsumer<JsonNode, Integer> action) {
        JsonNode n = get(key);
        if (n != null) {
            n.ifInt(action);
        }
        return this;
    }

    @Override
    public JsonNode ifLong(String key, BiConsumer<JsonNode, Long> action) {
        JsonNode n = get(key);
        if (n != null) {
            n.ifLong(action);
        }
        return this;
    }

    @Override
    public JsonNode ifFloat(String key, BiConsumer<JsonNode, Float> action) {
        JsonNode n = get(key);
        if (n != null) {
            n.ifFloat(action);
        }
        return this;
    }

    @Override
    public JsonNode ifDouble(String key, BiConsumer<JsonNode, Double> action) {
        JsonNode n = get(key);
        if (n != null) {
            n.ifDouble(action);
        }
        return this;
    }

    @Override
    public JsonNode ifBigInteger(String key, BiConsumer<JsonNode, BigInteger> action) {
        JsonNode n = get(key);
        if (n != null) {
            n.ifBigInteger(action);
        }
        return this;
    }

    @Override
    public JsonNode ifBigDecimal(String key, BiConsumer<JsonNode, BigDecimal> action) {
        JsonNode n = get(key);
        if (n != null) {
            n.ifBigDecimal(action);
        }
        return this;
    }

    @Override
    public JsonNode ifBoolean(String key, BiConsumer<JsonNode, Boolean> action) {
        JsonNode n = get(key);
        if (n != null) {
            n.ifBoolean(action);
        }
        return this;
    }

    @Override
    public JsonNode ifNull(String key, Consumer<JsonNode> action) {
        JsonNode n = get(key);
        if (n != null) {
            n.ifNull(action);
        }
        return this;
    }

    @Override
    public JsonNode ifArray(String key, Consumer<JsonNode> action) {
        JsonNode n = get(key);
        if (n != null) {
            n.ifArray(action);
        }
        return this;
    }

    @Override
    public JsonNode ifObject(String key, Consumer<JsonNode> action) {
        JsonNode n = get(key);
        if (n != null) {
            n.ifObject(action);
        }
        return this;
    }

    @Override
    public JsonNode ifPrimitive(String key, Consumer<JsonNode> action) {
        JsonNode n = get(key);
        if (n != null) {
            n.ifPrimitive(action);
        }
        return this;
    }

    @Override
    public JsonNode ifConstruct(String key, Consumer<JsonNode> action) {
        JsonNode n = get(key);
        if (n != null) {
            n.ifConstruct(action);
        }
        return this;
    }

    @Override
    public JsonNode ifConstruct(Consumer<JsonNode> action) {
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
