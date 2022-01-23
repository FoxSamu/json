package net.shadew.json.codec;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import net.shadew.json.JsonNode;

class EnumCodec<E extends Enum<E>> implements JsonCodec<E> {
    private final JsonNode[] toName;
    private final Map<String, E> fromName;

    EnumCodec(Class<E> type, Function<E, String> namer, Predicate<E> test) {
        E[] values = type.getEnumConstants();
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
            throw new JsonCodecException("Unencodable enum constant " + obj.getClass().getName() + "." + obj.name());
        return node;
    }

    @Override
    public E decode(JsonNode json) {
        String name = json.asExactString();
        if (!fromName.containsKey(name))
            throw new JsonCodecException("Unknown enum constant: '" + name + "'");
        return fromName.get(name);
    }
}
