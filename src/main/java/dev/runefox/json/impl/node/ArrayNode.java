package dev.runefox.json.impl.node;

import dev.runefox.json.IncorrectTypeException;
import dev.runefox.json.JsonNode;
import dev.runefox.json.NodeType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ArrayNode extends AbstractConstructNode {
    private final List<JsonNode> children = new ArrayList<>();
    private Values values;

    public ArrayNode() {
        super(NodeType.ARRAY);
    }

    public ArrayNode(int len) {
        super(NodeType.ARRAY);
        while (len-- > 0)
            children.add(JsonNode.NULL);
    }

    public ArrayNode(JsonNode... nodes) {
        this();
        for (JsonNode node : nodes)
            add(node);
    }

    public ArrayNode(Iterable<? extends JsonNode> nodes) {
        this();
        for (JsonNode node : nodes)
            add(node);
    }

    @Override
    public JsonNode ifArray(Consumer<JsonNode> action) {
        action.accept(this);
        return this;
    }

    @Override
    public NodeType type(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public boolean isNull(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public boolean isString(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public boolean isNumber(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public boolean isBoolean(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public boolean isObject(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public boolean isArray(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public boolean isPrimitive(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public boolean isConstruct(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public boolean is(String key, NodeType type) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public boolean is(String key, NodeType... types) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode requireHas(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode requireNull(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode requireNotNull(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode requireString(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode requireNotString(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode requireNumber(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode requireNotNumber(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode requireBoolean(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode requireNotBoolean(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode requireObject(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode requireNotObject(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode requireArray(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode requireNotArray(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode requirePrimitive(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode requireNotPrimitive(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode requireConstruct(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode requireNotConstruct(String key) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode require(String key, NodeType type) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode requireNot(String key, NodeType type) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode require(String key, NodeType... types) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode requireNot(String key, NodeType... types) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode ifHas(String key, Consumer<JsonNode> action) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode ifString(String key, BiConsumer<JsonNode, String> action) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode ifNumber(String key, BiConsumer<JsonNode, Number> action) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode ifByte(String key, BiConsumer<JsonNode, Byte> action) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode ifShort(String key, BiConsumer<JsonNode, Short> action) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode ifInt(String key, BiConsumer<JsonNode, Integer> action) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode ifLong(String key, BiConsumer<JsonNode, Long> action) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode ifFloat(String key, BiConsumer<JsonNode, Float> action) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode ifDouble(String key, BiConsumer<JsonNode, Double> action) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode ifBigInteger(String key, BiConsumer<JsonNode, BigInteger> action) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode ifBigDecimal(String key, BiConsumer<JsonNode, BigDecimal> action) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode ifBoolean(String key, BiConsumer<JsonNode, Boolean> action) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode ifNull(String key, Consumer<JsonNode> action) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode ifArray(String key, Consumer<JsonNode> action) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode ifObject(String key, Consumer<JsonNode> action) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode ifPrimitive(String key, Consumer<JsonNode> action) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode ifConstruct(String key, Consumer<JsonNode> action) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public JsonNode ifConstruct(Consumer<JsonNode> action) {
        action.accept(this);
        return this;
    }

    @Override
    public List<JsonNode> asList() {
        return new ArrayList<>(children);
    }

    @Override
    public JsonNode get(int index) {
        if (index < 0) index += size();
        return children.get(index);
    }

    @Override
    public JsonNode set(int index, JsonNode value) {
        if (index < 0) index += size();
        children.set(index, JsonNode.orNull(value));
        return this;
    }

    @Override
    public JsonNode set(int index, String value) {
        if (index < 0) index += size();
        children.set(index, JsonNode.string(value));
        return this;
    }

    @Override
    public JsonNode set(int index, Number value) {
        if (index < 0) index += size();
        children.set(index, JsonNode.number(value));
        return this;
    }

    @Override
    public JsonNode set(int index, Boolean value) {
        if (index < 0) index += size();
        children.set(index, JsonNode.bool(value));
        return this;
    }

    @Override
    public JsonNode add(JsonNode value) {
        children.add(JsonNode.orNull(value));
        return this;
    }

    @Override
    public JsonNode add(String value) {
        children.add(JsonNode.string(value));
        return this;
    }

    @Override
    public JsonNode add(Number value) {
        children.add(JsonNode.number(value));
        return this;
    }

    @Override
    public JsonNode add(Boolean value) {
        children.add(JsonNode.bool(value));
        return this;
    }

    @Override
    public JsonNode insert(int index, JsonNode value) {
        if (index < 0) index += size();
        children.add(index, JsonNode.orNull(value));
        return this;
    }

    @Override
    public JsonNode insert(int index, String value) {
        if (index < 0) index += size();
        children.add(index, JsonNode.string(value));
        return this;
    }

    @Override
    public JsonNode insert(int index, Number value) {
        if (index < 0) index += size();
        children.add(index, JsonNode.number(value));
        return this;
    }

    @Override
    public JsonNode insert(int index, Boolean value) {
        if (index < 0) index += size();
        children.add(index, JsonNode.bool(value));
        return this;
    }

    @Override
    public JsonNode remove(int index) {
        if (index < 0) index += size();
        return children.remove(index);
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
        throw new IncorrectTypeException(NodeType.ARRAY, NodeType.OBJECT);
    }

    @Override
    public JsonNode set(String key, JsonNode value) {
        throw new IncorrectTypeException(NodeType.ARRAY, NodeType.OBJECT);
    }

    @Override
    public JsonNode set(String key, String value) {
        throw new IncorrectTypeException(NodeType.ARRAY, NodeType.OBJECT);
    }

    @Override
    public JsonNode set(String key, Number value) {
        throw new IncorrectTypeException(NodeType.ARRAY, NodeType.OBJECT);
    }

    @Override
    public JsonNode set(String key, Boolean value) {
        throw new IncorrectTypeException(NodeType.ARRAY, NodeType.OBJECT);
    }

    @Override
    public JsonNode remove(String key) {
        throw new IncorrectTypeException(NodeType.ARRAY, NodeType.OBJECT);
    }

    @Override
    public boolean has(String key) {
        throw new IncorrectTypeException(NodeType.ARRAY, NodeType.OBJECT);
    }

    @Override
    public boolean contains(JsonNode value) {
        return children.contains(value);
    }

    @Override
    public Set<String> keySet() {
        throw new IncorrectTypeException(NodeType.ARRAY, NodeType.OBJECT);
    }

    @Override
    public Collection<JsonNode> values() {
        if (values == null)
            return values = new Values(children);
        return values;
    }

    @Override
    public Set<Map.Entry<String, JsonNode>> entrySet() {
        throw new IncorrectTypeException(NodeType.ARRAY, NodeType.OBJECT);
    }

    @Override
    public Stream<JsonNode> stream() {
        return values().stream();
    }

    @Override
    public void forEachEntry(BiConsumer<? super String, ? super JsonNode> fn) {
        throw new IncorrectTypeException(NodeType.ARRAY, NodeType.OBJECT);
    }

    @Override
    public JsonNode deepCopy() {
        ArrayNode copy = new ArrayNode();
        for (JsonNode node : children)
            copy.children.add(node.deepCopy());
        return copy;
    }

    @Override
    public JsonNode copy() {
        return new ArrayNode(children);
    }

    @Override
    public Iterator<JsonNode> iterator() {
        return children.iterator();
    }

    @Override
    public JsonNode clear() {
        children.clear();
        return this;
    }

    @Override
    public JsonNode append(JsonNode other) {
        other.requireArray();
        children.addAll(other.values());
        return this;
    }

    @Override
    public JsonNode prepend(JsonNode other) {
        other.requireArray();
        children.addAll(0, other.values());
        return this;
    }

    @Override
    public JsonNode slice(int from, int to) {
        int size = size();
        if (from < 0) from += size;
        if (to < 0) to += size;
        if (to < from)
            throw new IllegalArgumentException("to < from: " + to + " < " + from);
        if (from < 0 || from > size)
            throw new IndexOutOfBoundsException("from: " + from);
        if (to < 0 || to > size)
            throw new IndexOutOfBoundsException("to: " + to);

        int remaining = to - from;
        if (remaining == 0) {
            children.clear();
            return this;
        }
        while (from > 0) {
            children.remove(0);
            from--;
        }
        while (size() > remaining) {
            children.remove(to);
        }
        return this;
    }

    @Override
    public JsonNode merge(JsonNode other) {
        throw new IncorrectTypeException(type(), NodeType.OBJECT);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        ArrayNode other = (ArrayNode) o;
        return children.equals(other.children);
    }

    @Override
    public int hashCode() {
        return Objects.hash(children);
    }

    @Override
    public String toString() {
        return "[" + children.stream().map(JsonNode::toString).collect(Collectors.joining(", ")) + "]";
    }

    private static class Values implements Collection<JsonNode> {
        private final List<JsonNode> children;

        private Values(List<JsonNode> children) {
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
}
