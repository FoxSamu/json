package net.shadew.json.template;

import net.shadew.json.JsonNode;

class RecursionCheck {
    static boolean perform(JsonNode find, JsonNode in) {
        if (in == find)
            return true;
        if (in.isArray()) {
            for (JsonNode elem : in) {
                if (perform(elem, in)) return true;
            }
        }
        if (in.isObject()) {
            for (JsonNode elem : in.values()) {
                if (perform(elem, in)) return true;
            }
        }
        return false;
    }
}
