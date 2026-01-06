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

public abstract sealed class AbstractConstructNode extends AbstractJsonNode permits ArrayNode, ObjectNode {
    protected AbstractConstructNode(NodeType type) {
        super(type);
    }

    @Override
    public boolean isPrimitive() {
        return false;
    }

    @Override
    public boolean isConstruct() {
        return true;
    }

    @Override
    public JsonNode requirePrimitive() {
        throw new IncorrectTypeException(type(), Internal.PRIMITIVES);
    }

    @Override
    public JsonNode requireNotPrimitive() {
        return this;
    }

    @Override
    public JsonNode requireConstruct() {
        return this;
    }

    @Override
    public JsonNode requireNotConstruct() {
        throw new IncorrectTypeException(type(), Internal.NOT_CONSTRUCTS);
    }

    @Override
    public JsonNode ifConstruct(Consumer<JsonNode> action) {
        action.accept(this);
        return this;
    }

    @Override
    public String asExactString() {
        throw new IncorrectTypeException(type(), NodeType.STRING);
    }

    @Override
    public String asString() {
        throw new IncorrectTypeException(type(), Internal.NOT_CONSTRUCTS);
    }

    @Override
    public byte asByte() {
        throw new IncorrectTypeException(type(), NodeType.NUMBER);
    }

    @Override
    public short asShort() {
        throw new IncorrectTypeException(type(), NodeType.NUMBER);
    }

    @Override
    public int asInt() {
        throw new IncorrectTypeException(type(), NodeType.NUMBER);
    }

    @Override
    public long asLong() {
        throw new IncorrectTypeException(type(), NodeType.NUMBER);
    }

    @Override
    public float asFloat() {
        throw new IncorrectTypeException(type(), NodeType.NUMBER);
    }

    @Override
    public double asDouble() {
        throw new IncorrectTypeException(type(), NodeType.NUMBER);
    }

    @Override
    public BigInteger asBigInteger() {
        throw new IncorrectTypeException(type(), NodeType.NUMBER);
    }

    @Override
    public BigDecimal asBigDecimal() {
        throw new IncorrectTypeException(type(), NodeType.NUMBER);
    }

    @Override
    public BigDecimal asNumber() {
        throw new IncorrectTypeException(type(), NodeType.NUMBER);
    }

    @Override
    public boolean asBoolean() {
        throw new IncorrectTypeException(type(), NodeType.BOOLEAN);
    }
}
