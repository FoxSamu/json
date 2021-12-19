package net.shadew.json;

import java.util.Arrays;
import java.util.List;

import net.shadew.json.type.JsonSerializer;

public class TypeSystem {
    public static void main(String[] args) {
        JsonSerializer<List<String>> serializer = new JsonSerializer<>() {
            @Override
            public List<String> deserialize(JsonNode node) {
                return Arrays.asList(node.asStringArray());
            }

            @Override
            public JsonNode serialize(List<String> obj) {
                return JsonNode.stringArray(obj);
            }
        };

        System.out.println(serializer.getClass());
    }
}
