package dev.runefox.json;

import dev.runefox.json.impl.Internal;

import java.util.function.Predicate;

/**
 * An enumeration of all valid JSON types.
 */
public enum NodeType {
    /**
     * The string type, standing for string literals ({@code "foo"})
     */
    STRING(true, false, false, JsonNode::isString),

    /**
     * The number type, standing for number literals ({@code 52})
     */
    NUMBER(true, false, false, JsonNode::isNumber),

    /**
     * The boolean type, standing for boolean literals ({@code true})
     */
    BOOLEAN(true, false, false, JsonNode::isBoolean),

    /**
     * The null type, standing for the null literal ({@code null})
     */
    NULL(false, false, false, JsonNode::isNull),

    /**
     * The array type, standing for arrays ({@code ["foo", "bar"]})
     */
    ARRAY(false, true, false, JsonNode::isArray),

    /**
     * The object type, standing for objects ({@code {"foo": "bar", "baz": 42}})
     */
    OBJECT(false, true, false, JsonNode::isObject),

    /**
     * The offset date+time type, standing for timestamps with zone offset (a type not present in JSON, but present in
     * other formats, such as TOML)
     */
    OFFSET_DATE_TIME(true, false, true, JsonNode::isOffsetDateTime),

    /**
     * The local date+time type, standing for timestamps without zone offset (a type not present in JSON, but present in
     * other formats, such as TOML)
     */
    LOCAL_DATE_TIME(true, false, true, JsonNode::isLocalDateTime),

    /**
     * The local date type, standing for dates without time component (a type not present in JSON, but present in other
     * formats, such as TOML)
     */
    LOCAL_DATE(true, false, true, JsonNode::isLocalDate),

    /**
     * The local time type, standing for timestamps without date component (a type not present in JSON, but present in
     * other formats, such as TOML)
     */
    LOCAL_TIME(true, false, true, JsonNode::isLocalTime);

    private final boolean primitive;
    private final boolean construct;
    private final boolean temporal;
    private final Predicate<JsonNode> check;

    NodeType(boolean primitive, boolean construct, boolean temporal, Predicate<JsonNode> check) {
        this.primitive = primitive;
        this.construct = construct;
        this.temporal = temporal;
        this.check = check;
    }

    /**
     * Returns whether this JSON type is a primitive type. Primitive types are, with the exception of the null type, all
     * types that represent one value. This are {@linkplain #STRING strings}, {@linkplain #NUMBER numbers} and
     * {@linkplain #BOOLEAN booleans}.
     *
     * @return Whether this JSON type is a primitive type
     */
    public boolean isPrimitive() {
        return primitive;
    }

    /**
     * Returns whether this JSON type is a construct type. Construct types are, all types that are constructed of zero
     * or more value. This are {@linkplain #OBJECT objects} and {@linkplain #ARRAY arrays}.
     *
     * @return Whether this JSON type is a construct type
     */
    public boolean isConstruct() {
        return construct;
    }

    public boolean isTemporal() {
        return temporal;
    }

    public Predicate<JsonNode> check() {
        return check;
    }

    /**
     * Returns whether this JSON type is {@linkplain #NULL null}. This is equivalent of comparing to {@link #NULL}
     * directly: {@code type == JsonType.NULL}.
     *
     * @return Whether this JSON type is {@linkplain #NULL null}
     */
    public boolean isNull() {
        return this == NULL;
    }

    /**
     * Returns an array of all JSON types excluding the given type. If the given type is null (Java null), this method
     * returns {@link #values()}.
     *
     * @param type The type to exclude
     * @return All JSON types excluding the given type
     */
    public static NodeType[] allExcluding(NodeType type) {
        return Internal.allExcluding(type);
    }

    /**
     * Returns an array of all {@linkplain #isPrimitive() primitive types}. This array contains {@link #STRING},
     * {@link #NUMBER} and {@link #BOOLEAN}, in that order.
     *
     * @return An array of all primitive types.
     */
    public static NodeType[] primitives() {
        return Internal.primitives();
    }

    /**
     * Returns an array of all {@linkplain #isConstruct() construct types}. This array contains {@link #ARRAY} and
     * {@link #OBJECT}, in that order.
     *
     * @return An array of all construct types.
     */
    public static NodeType[] constructs() {
        return Internal.constructs();
    }

    /**
     * Returns an array of all not-{@linkplain #isPrimitive() primitive} types. This array contains {@link #NULL},
     * {@link #ARRAY} and {@link #OBJECT}, in that order.
     *
     * @return An array of all not-primitive types.
     */
    public static NodeType[] notPrimitives() {
        return Internal.notPrimitives();
    }

    /**
     * Returns an array of all not-{@linkplain #isConstruct() construct} types. This array contains {@link #STRING},
     * {@link #NUMBER}, {@link #BOOLEAN} and {@link #NULL}, in that order.
     *
     * @return An array of all not-primitive types.
     */
    public static NodeType[] notConstructs() {
        return Internal.notConstructs();
    }
}
