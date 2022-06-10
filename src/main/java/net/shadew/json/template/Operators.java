package net.shadew.json.template;

import java.util.stream.Collectors;

import net.shadew.json.JsonNode;
import net.shadew.json.JsonType;

class Operators {
    static String stringify(JsonNode node) {
        if (node.isPrimitive()) return node.asString();
        else if (node.isArray()) {
            return node.stream()
                       .map(Operators::stringify)
                       .collect(Collectors.joining(", ", "[", "]"));
        } else {
            return node.entrySet()
                       .stream()
                       .map(entry -> entry.getKey() + ": " + stringify(entry.getValue()))
                       .collect(Collectors.joining(", ", "[", "]"));
        }
    }

    static boolean truthy(JsonNode node) {
        if (node.isBoolean()) return node.asBoolean();
        if (node.isNumber()) return node.asDouble() != 0;
        if (node.isString() || node.isConstruct()) return node.length() != 0;
        return false; // null = false
    }

    // a + b
    static JsonNode add(JsonNode a, JsonNode b) {
        if (a.isNumber() && b.isNumber()) {
            return JsonNode.number(a.asDouble() + b.asDouble());
        } else if (a.isString()) {
            return JsonNode.string(a.asString() + stringify(b));
        } else if (b.isString()) {
            return JsonNode.string(stringify(a) + b.asString());
        } else {
            return null;
        }
    }

    // a - b
    static JsonNode sub(JsonNode a, JsonNode b) {
        if (a.isNumber() && b.isNumber()) {
            return JsonNode.number(a.asDouble() - b.asDouble());
        } else {
            return null;
        }
    }

    // a * b
    static JsonNode mul(JsonNode a, JsonNode b) {
        if (a.isNumber() && b.isNumber()) {
            return JsonNode.number(a.asDouble() * b.asDouble());
        } else {
            return null;
        }
    }

    // a / b
    static JsonNode div(JsonNode a, JsonNode b) {
        if (a.isNumber() && b.isNumber()) {
            return JsonNode.number(a.asDouble() / b.asDouble());
        } else {
            return null;
        }
    }

    // a % b
    static JsonNode mod(JsonNode a, JsonNode b) {
        if (a.isNumber() && b.isNumber()) {
            return JsonNode.number(a.asDouble() % b.asDouble());
        } else {
            return null;
        }
    }

    // + a
    static JsonNode unaryPlus(JsonNode a) {
        if (a.isNumber()) {
            return JsonNode.number(a.asDouble());
        } else {
            return null;
        }
    }

    // - a
    static JsonNode neg(JsonNode a) {
        if (a.isNumber()) {
            return JsonNode.number(-a.asDouble());
        } else {
            return null;
        }
    }

    // ! a
    static JsonNode not(JsonNode a) {
        return JsonNode.bool(!truthy(a));
    }

    // # a
    static JsonNode size(JsonNode a) {
        if (a.isString() || a.isConstruct())
            return JsonNode.number((double) a.length()); // cast to double to comply with 'every number is a double'
        return null;
    }

    // copy a
    static JsonNode copy(JsonNode a) {
        return a.deepCopy();
    }

    // ~ a
    static JsonNode bnot(JsonNode a) {
        if (a.isNumber()) {
            return JsonNode.number((double) ~a.asLong()); // cast to double to comply with 'every number is a double'
        } else {
            return null;
        }
    }

    private static int idx(JsonNode a, JsonNode b) {
        int i = b.asInt();
        if (i < 0) i += a.length();
        if (i >= a.length())
            return -2;
        if (i < 0)
            return -1;
        return i;
    }

    private static int idxb(JsonNode a, JsonNode b) {
        int i = b.asInt();
        if (i < 0) i += a.length();
        if (i > a.length())
            return -2;
        if (i < 0)
            return -1;
        return i;
    }

    // a[b]
    static JsonNode index(JsonNode a, JsonNode b) {
        if (a.isArray() && b.isNumber()) {
            int i = idx(a, b);
            return i < 0 ? JsonNode.NULL : a.get(i);
        } else if (a.isObject()) {
            return JsonNode.orNull(a.get(stringify(b)));
        } else {
            return null;
        }
    }

    // a[b] = c
    static JsonNode indexSet(JsonNode a, JsonNode b, JsonNode c) {
        if (a.isArray() && b.isNumber()) {
            int i = idx(a, b);
            if (i < 0) return null;
            a.set(i, c);
            return c;
        } else if (a.isObject()) {
            a.set(stringify(b), c);
            return c;
        } else {
            return null;
        }
    }

    // a.b
    static JsonNode field(JsonNode a, String b) {
        if (a.isObject()) {
            return JsonNode.orNull(a.get(b));
        } else {
            return null;
        }
    }

    // a.b = c
    static JsonNode fieldSet(JsonNode a, String b, JsonNode c) {
        if (a.isObject()) {
            a.set(b, c);
            return c;
        } else {
            return null;
        }
    }

    // a[b..]
    static JsonNode sliceFrom(JsonNode a, JsonNode b) {
        if (a.isArray() && b.isNumber()) {
            int i = idx(a, b);
            if (i == -2)
                return JsonNode.array();
            if (i == -1)
                return a.copy();

            return a.copy().slice(i, a.length());
        } else {
            return null;
        }
    }

    // a[..b]
    static JsonNode sliceTo(JsonNode a, JsonNode b) {
        if (a.isArray() && b.isNumber()) {
            int i = idx(a, b);
            if (i == -2)
                return a.copy();
            if (i == -1)
                return JsonNode.array();

            return a.copy().slice(0, i);
        } else {
            return null;
        }
    }

    // a[b..c]
    static JsonNode slice(JsonNode a, JsonNode b, JsonNode c) {
        if (a.isArray() && b.isNumber() && c.isNumber()) {
            int i1 = idx(a, b);
            int i2 = idx(a, c);
            if (i1 == i2)
                return JsonNode.array();
            if (i1 == -2) { // End
                if (i2 == -1) return a.copy(); // End .. Start
                return a.copy().slice(i2, a.length()); // End .. Middle
            }
            if (i1 == -1) { // Start
                if (i2 == -2) return a.copy(); // Start .. End
                return a.copy().slice(0, i2); // Start .. Middle
            }
            if (i1 > i2)
                return a.copy().slice(i2, i1);
            return a.copy().slice(i1, i2);
        } else {
            return null;
        }
    }

    // a[..]
    static JsonNode slice(JsonNode a) {
        if (a.isArray()) {
            return a.copy();
        } else {
            return null;
        }
    }

    // a << b
    static JsonNode blsh(JsonNode a, JsonNode b) {
        if (a.isNumber() && b.isNumber()) {
            return JsonNode.number((double) (a.asLong() << b.asInt()));
        } else {
            return null;
        }
    }

    // a >> b
    static JsonNode brsh(JsonNode a, JsonNode b) {
        if (a.isNumber() && b.isNumber()) {
            return JsonNode.number((double) (a.asLong() >> b.asInt()));
        } else {
            return null;
        }
    }

    // a >>> b
    static JsonNode brrsh(JsonNode a, JsonNode b) {
        if (a.isNumber() && b.isNumber()) {
            return JsonNode.number((double) (a.asLong() >>> b.asInt()));
        } else {
            return null;
        }
    }

    // a < b
    static JsonNode lt(JsonNode a, JsonNode b) {
        if (a.isNumber() && b.isNumber()) {
            return JsonNode.bool(a.asDouble() < b.asDouble());
        } else {
            return null;
        }
    }

    // a > b
    static JsonNode gt(JsonNode a, JsonNode b) {
        if (a.isNumber() && b.isNumber()) {
            return JsonNode.bool(a.asDouble() > b.asDouble());
        } else {
            return null;
        }
    }

    // a <= b
    static JsonNode le(JsonNode a, JsonNode b) {
        if (a.isNumber() && b.isNumber()) {
            return JsonNode.bool(a.asDouble() <= b.asDouble());
        } else {
            return null;
        }
    }

    // a >= b
    static JsonNode ge(JsonNode a, JsonNode b) {
        if (a.isNumber() && b.isNumber()) {
            return JsonNode.bool(a.asDouble() >= b.asDouble());
        } else {
            return null;
        }
    }

    // a == b
    static JsonNode eq(JsonNode a, JsonNode b) {
        return JsonNode.bool(a.equals(b));
    }

    // a != b
    static JsonNode neq(JsonNode a, JsonNode b) {
        return JsonNode.bool(!a.equals(b));
    }

    // a is type
    static JsonNode is(JsonNode a, JsonType type) {
        return JsonNode.bool(a.is(type));
    }

    // a isnt type
    static JsonNode isnt(JsonNode a, JsonType type) {
        return JsonNode.bool(!a.is(type));
    }

    // a has b
    static JsonNode has(JsonNode a, JsonNode b) {
        if (a.isObject()) {
            return JsonNode.bool(a.has(stringify(b)));
        } else {
            return null;
        }
    }

    // a hasnt b
    static JsonNode hasnt(JsonNode a, JsonNode b) {
        if (a.isObject()) {
            return JsonNode.bool(!a.has(stringify(b)));
        } else {
            return null;
        }
    }

    // a & b
    static JsonNode band(JsonNode a, JsonNode b) {
        if (a.isNumber() && b.isNumber()) {
            return JsonNode.number((double) (a.asLong() & b.asLong()));
        } else {
            return JsonNode.bool(truthy(a) && truthy(b));
        }
    }

    // a | b
    static JsonNode bor(JsonNode a, JsonNode b) {
        if (a.isNumber() && b.isNumber()) {
            return JsonNode.number((double) (a.asLong() | b.asLong()));
        } else {
            return JsonNode.bool(truthy(a) || truthy(b));
        }
    }

    // a ^ b
    static JsonNode bxor(JsonNode a, JsonNode b) {
        if (a.isNumber() && b.isNumber()) {
            return JsonNode.number((double) (a.asLong() ^ b.asLong()));
        } else {
            return JsonNode.bool(truthy(a) ^ truthy(b));
        }
    }

    // a ? b : c
    static JsonNode cond(JsonNode a, JsonNode b, JsonNode c) {
        return truthy(a) ? b : c;
    }
}
