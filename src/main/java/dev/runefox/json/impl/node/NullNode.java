/*
 * Copyright 2022-2026 O. W. Nankman
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "
 * AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */

package dev.runefox.json.impl.node;

import dev.runefox.json.IncorrectTypeException;
import dev.runefox.json.JsonNode;
import dev.runefox.json.NodeType;
import dev.runefox.json.impl.Internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.function.Consumer;

public final class NullNode extends AbstractPrimitiveNode {
    public NullNode() {
        super(NodeType.NULL);
    }

    @Override
    public JsonNode ifNull(Consumer<JsonNode> action) {
        action.accept(this);
        return this;
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public JsonNode requirePrimitive() {
        throw new IncorrectTypeException(NodeType.NULL, Internal.PRIMITIVES);
    }

    @Override
    public String asExactString() {
        throw new IncorrectTypeException(NodeType.NULL, NodeType.STRING);
    }

    @Override
    public String asString() {
        return "null";
    }

    @Override
    public byte asByte() {
        throw new IncorrectTypeException(NodeType.NULL, NodeType.NUMBER);
    }

    @Override
    public short asShort() {
        throw new IncorrectTypeException(NodeType.NULL, NodeType.NUMBER);
    }

    @Override
    public int asInt() {
        throw new IncorrectTypeException(NodeType.NULL, NodeType.NUMBER);
    }

    @Override
    public long asLong() {
        throw new IncorrectTypeException(NodeType.NULL, NodeType.NUMBER);
    }

    @Override
    public float asFloat() {
        throw new IncorrectTypeException(NodeType.NULL, NodeType.NUMBER);
    }

    @Override
    public double asDouble() {
        throw new IncorrectTypeException(NodeType.NULL, NodeType.NUMBER);
    }

    @Override
    public BigInteger asBigInteger() {
        throw new IncorrectTypeException(NodeType.NULL, NodeType.NUMBER);
    }

    @Override
    public BigDecimal asBigDecimal() {
        throw new IncorrectTypeException(NodeType.NULL, NodeType.NUMBER);
    }

    @Override
    public BigDecimal asNumber() {
        throw new IncorrectTypeException(NodeType.NULL, NodeType.NUMBER);
    }

    @Override
    public boolean asBoolean() {
        throw new IncorrectTypeException(NodeType.NULL, NodeType.BOOLEAN);
    }

    @Override
    public int hashCode() {
        return 42;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        return obj.getClass() == getClass();
    }

    @Override
    public String toString() {
        return "null";
    }
}
