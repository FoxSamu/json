package dev.runefox.json.codec;

import dev.runefox.json.JsonNode;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

class EnumCodec<E extends Enum<E>> implements JsonCodec<E> {
    private final JsonNode[] toName;
    private final Map<String, E> fromName;

    EnumCodec(Class<E> type, Function<E, String> namer, Predicate<E> test) {
        this(type.getEnumConstants(), namer, test);
    }

    EnumCodec(E[] values, Function<E, String> namer, Predicate<E> test) {
        JsonNode[] names = new JsonNode[values.length];
        Map<String, E> map = new HashMap<>();
        for (E e : values) {
            if (test.test(e)) {
                String name = namer.apply(e);
                if (name == null)
                    throw new IllegalArgumentException("Produced null name");
                if (map.containsKey(name))
                    throw new IllegalArgumentException("Produced name '" + name + "' for multiple constants");

                names[e.ordinal()] = JsonNode.string(name);
                map.put(name, e);
            }
        }

        this.toName = names;
        this.fromName = map;
    }

    @Override
    public JsonNode encode(E obj) {
        JsonNode node = toName[obj.ordinal()];
        if (node == null)
            throw new CodecException("Unencodable enum constant " + obj.getClass().getName() + "." + obj.name());
        return node;
    }

    @Override
    public E decode(JsonNode json) {
        String name = json.asString();
        if (!fromName.containsKey(name))
            throw new CodecException("Unknown enum constant: '" + name + "'");
        return fromName.get(name);
    }
}
