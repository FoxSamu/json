package net.shadew.json;

/**
 * An enumeration of all valid JSON types.
 */
public enum JsonType {
    /**
     * The string type, standing for string literals ({@code "foo"})
     */
    STRING(true, false),

    /**
     * The number type, standing for number literals ({@code 52})
     */
    NUMBER(true, false),

    /**
     * The boolean type, standing for boolean literals ({@code true})
     */
    BOOLEAN(true, false),

    /**
     * The null type, standing for the null literal ({@code null})
     */
    NULL(false, false),

    /**
     * The array type, standing for arrays ({@code ["foo", "bar"]})
     */
    ARRAY(false, true),

    /**
     * The object type, standing for objects ({@code {"foo": "bar", "baz": 42}})
     */
    OBJECT(false, true);

    // Not private for quick internal access (skipping array copy)
    static final JsonType[][] EXCLUDING_ARRAYS = {
        {NUMBER, BOOLEAN, NULL, ARRAY, OBJECT},
        {STRING, BOOLEAN, NULL, ARRAY, OBJECT},
        {STRING, NUMBER, NULL, ARRAY, OBJECT},
        {STRING, NUMBER, BOOLEAN, ARRAY, OBJECT},
        {STRING, NUMBER, BOOLEAN, NULL, OBJECT},
        {STRING, NUMBER, BOOLEAN, NULL, ARRAY}
    };

    static final JsonType[] PRIMITIVES = {STRING, NUMBER, BOOLEAN};
    static final JsonType[] CONSTRUCTS = {ARRAY, OBJECT};
    static final JsonType[] NOT_PRIMITIVES = {NULL, ARRAY, OBJECT};
    static final JsonType[] NOT_CONSTRUCTS = {STRING, NUMBER, BOOLEAN, NULL};
    static final JsonType[] WITH_LENGTH = {STRING, ARRAY, OBJECT};

    static final JsonType[] VALUES = values();

    private final boolean primitive;
    private final boolean construct;

    JsonType(boolean primitive, boolean construct) {
        this.primitive = primitive;
        this.construct = construct;
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
    public static JsonType[] allExcluding(JsonType type) {
        if (type == null)
            return values();
        return EXCLUDING_ARRAYS[type.ordinal()].clone();
    }

    /**
     * Returns an array of all {@linkplain #isPrimitive() primitive types}. This array contains {@link #STRING}, {@link
     * #NUMBER} and {@link #BOOLEAN}, in that order.
     *
     * @return An array of all primitive types.
     */
    public static JsonType[] primitives() {
        return PRIMITIVES.clone();
    }

    /**
     * Returns an array of all {@linkplain #isConstruct() construct types}. This array contains {@link #ARRAY} and
     * {@link #OBJECT}, in that order.
     *
     * @return An array of all construct types.
     */
    public static JsonType[] constructs() {
        return CONSTRUCTS.clone();
    }

    /**
     * Returns an array of all not-{@linkplain #isPrimitive() primitive} types. This array contains {@link #NULL},
     * {@link #ARRAY} and {@link #OBJECT}, in that order.
     *
     * @return An array of all not-primitive types.
     */
    public static JsonType[] notPrimitives() {
        return NOT_PRIMITIVES.clone();
    }

    /**
     * Returns an array of all not-{@linkplain #isConstruct() construct} types. This array contains {@link #STRING},
     * {@link #NUMBER}, {@link #BOOLEAN} and {@link #NULL}, in that order.
     *
     * @return An array of all not-primitive types.
     */
    public static JsonType[] notConstructs() {
        return NOT_CONSTRUCTS.clone();
    }

    // Internal no-copy version of allExcluding
    static JsonType[] allExcluding0(JsonType type) {
        if (type == null)
            return VALUES;
        return EXCLUDING_ARRAYS[type.ordinal()].clone();
    }
}
