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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.function.BiConsumer;

public final class BooleanNode extends AbstractPrimitiveNode {
    private final boolean bool;
    private final String string;

    public BooleanNode(boolean bool) {
        super(NodeType.BOOLEAN);
        this.bool = bool;
        this.string = bool ? "true" : "false";
    }

    @Override
    public JsonNode ifBoolean(BiConsumer<JsonNode, Boolean> action) {
        action.accept(this, bool);
        return this;
    }

    @Override
    public String asExactString() {
        throw new IncorrectTypeException(NodeType.BOOLEAN, NodeType.STRING);
    }

    @Override
    public String asString() {
        return string;
    }

    @Override
    public byte asByte() {
        throw new IncorrectTypeException(NodeType.BOOLEAN, NodeType.NUMBER);
    }

    @Override
    public short asShort() {
        throw new IncorrectTypeException(NodeType.BOOLEAN, NodeType.NUMBER);
    }

    @Override
    public int asInt() {
        throw new IncorrectTypeException(NodeType.BOOLEAN, NodeType.NUMBER);
    }

    @Override
    public long asLong() {
        throw new IncorrectTypeException(NodeType.BOOLEAN, NodeType.NUMBER);
    }

    @Override
    public float asFloat() {
        throw new IncorrectTypeException(NodeType.BOOLEAN, NodeType.NUMBER);
    }

    @Override
    public double asDouble() {
        throw new IncorrectTypeException(NodeType.BOOLEAN, NodeType.NUMBER);
    }

    @Override
    public BigInteger asBigInteger() {
        throw new IncorrectTypeException(NodeType.BOOLEAN, NodeType.NUMBER);
    }

    @Override
    public BigDecimal asBigDecimal() {
        throw new IncorrectTypeException(NodeType.BOOLEAN, NodeType.NUMBER);
    }

    @Override
    public BigDecimal asNumber() {
        throw new IncorrectTypeException(NodeType.BOOLEAN, NodeType.NUMBER);
    }

    @Override
    public boolean asBoolean() {
        return bool;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        BooleanNode other = (BooleanNode) o;
        return bool == other.bool;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bool);
    }

    @Override
    public String toString() {
        return string;
    }
}
