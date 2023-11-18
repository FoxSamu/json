package dev.runefox.json.impl;

import dev.runefox.json.NodeType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static dev.runefox.json.NodeType.*;

/**
 * <h1>DO NOT USE</h1>
 */
public class Internal {
    // Not private for quick internal access (skipping array copy)
    public static final NodeType[][] EXCLUDING_ARRAYS = {
        {NUMBER, BOOLEAN, NULL, ARRAY, OBJECT},
        {STRING, BOOLEAN, NULL, ARRAY, OBJECT},
        {STRING, NUMBER, NULL, ARRAY, OBJECT},
        {STRING, NUMBER, BOOLEAN, ARRAY, OBJECT},
        {STRING, NUMBER, BOOLEAN, NULL, OBJECT},
        {STRING, NUMBER, BOOLEAN, NULL, ARRAY}
    };

    public static final NodeType[] PRIMITIVES = {STRING, NUMBER, BOOLEAN};
    public static final NodeType[] CONSTRUCTS = {ARRAY, OBJECT};
    public static final NodeType[] NOT_PRIMITIVES = {NULL, ARRAY, OBJECT};
    public static final NodeType[] NOT_CONSTRUCTS = {STRING, NUMBER, BOOLEAN, NULL};
    public static final NodeType[] WITH_LENGTH = {STRING, ARRAY, OBJECT};

    public static final NodeType[] VALUES = values();

    public static NodeType[] allExcluding(NodeType type) {
        if (type == null)
            return values();
        return EXCLUDING_ARRAYS[type.ordinal()].clone();
    }

    public static NodeType[] primitives() {
        return PRIMITIVES.clone();
    }

    public static NodeType[] constructs() {
        return CONSTRUCTS.clone();
    }

    public static NodeType[] notPrimitives() {
        return NOT_PRIMITIVES.clone();
    }

    public static NodeType[] notConstructs() {
        return NOT_CONSTRUCTS.clone();
    }

    // Internal no-copy version of allExcluding
    public static NodeType[] allExcluding0(NodeType type) {
        if (type == null)
            return VALUES;
        return EXCLUDING_ARRAYS[type.ordinal()].clone();
    }


    public static String makeMessageInv(NodeType found, NodeType... prohibited) {
        Set<NodeType> types = new HashSet<>(Arrays.asList(prohibited));
        return String.format(
            "Unmatched types, required %s, found %s",
            Stream.of(VALUES).filter(types::contains).map(NodeType::name).collect(Collectors.joining(", ")),
            found
        );
    }
}
