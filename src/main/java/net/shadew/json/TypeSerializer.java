package net.shadew.json;

public interface TypeSerializer<T> {
    T deserialize(JsonNode node);
    JsonNode serialize(T obj);
}
