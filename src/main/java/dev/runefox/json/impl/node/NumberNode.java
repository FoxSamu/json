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
import dev.runefox.json.impl.KotlinUnsignedIntWrapper;
import dev.runefox.json.impl.UnparsedHexNumber;
import dev.runefox.json.impl.UnparsedNumber;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.function.BiConsumer;

public final class NumberNode extends AbstractPrimitiveNode {
    private static final int NAN_HASH = 8811;
    private static final int POSITIVE_INFINITY_HASH = 11579;
    private static final int NEGATIVE_INFINITY_HASH = 10082;

    private final Number number;
    private BigInteger bigInteger;
    private BigDecimal bigDecimal;
    private String string;

    public NumberNode(Number number) {
        super(NodeType.NUMBER);
        this.number = number;
    }

    @Override
    public JsonNode ifNumber(BiConsumer<JsonNode, Number> action) {
        action.accept(this, number);
        return this;
    }

    @Override
    public JsonNode ifByte(BiConsumer<JsonNode, Byte> action) {
        action.accept(this, asByte());
        return this;
    }

    @Override
    public JsonNode ifShort(BiConsumer<JsonNode, Short> action) {
        action.accept(this, asShort());
        return this;
    }

    @Override
    public JsonNode ifInt(BiConsumer<JsonNode, Integer> action) {
        action.accept(this, asInt());
        return this;
    }

    @Override
    public JsonNode ifLong(BiConsumer<JsonNode, Long> action) {
        action.accept(this, asLong());
        return this;
    }

    @Override
    public JsonNode ifFloat(BiConsumer<JsonNode, Float> action) {
        action.accept(this, asFloat());
        return this;
    }

    @Override
    public JsonNode ifDouble(BiConsumer<JsonNode, Double> action) {
        action.accept(this, asDouble());
        return this;
    }

    @Override
    public JsonNode ifBigInteger(BiConsumer<JsonNode, BigInteger> action) {
        action.accept(this, asBigInteger());
        return this;
    }

    @Override
    public JsonNode ifBigDecimal(BiConsumer<JsonNode, BigDecimal> action) {
        action.accept(this, asBigDecimal());
        return this;
    }

    @Override
    public String asExactString() {
        throw new IncorrectTypeException(NodeType.NUMBER, NodeType.STRING);
    }

    @Override
    public String asString() {
        return toString();
    }

    @Override
    public byte asByte() {
        return number.byteValue();
    }

    @Override
    public short asShort() {
        return number.shortValue();
    }

    @Override
    public int asInt() {
        return number.intValue();
    }

    @Override
    public long asLong() {
        return number.longValue();
    }

    @Override
    public float asFloat() {
        return number.floatValue();
    }

    @Override
    public double asDouble() {
        return number.doubleValue();
    }

    @Override
    public BigInteger asBigInteger() {
        if (bigInteger != null)
            return bigInteger;

        if (number instanceof BigInteger bi)
            return bigInteger = bi;

        if (number instanceof BigDecimal bd)
            return bigInteger = bd.toBigInteger();

        if (number instanceof UnparsedNumber un)
            return bigInteger = un.bigIntegerValue();

        if (number instanceof UnparsedHexNumber uhn)
            return bigInteger = uhn.bigIntegerValue();

        if (number instanceof KotlinUnsignedIntWrapper knw)
            return bigInteger = knw.toBigInteger();

        if (finiteness() != Finiteness.FINITE)
            return bigInteger = BigInteger.ZERO;

        return bigInteger = asBigDecimal().toBigInteger();
    }

    @Override
    public BigDecimal asBigDecimal() {
        if (bigDecimal != null)
            return bigDecimal;

        if (number instanceof BigInteger bi)
            return bigDecimal = new BigDecimal(bi);

        if (number instanceof BigDecimal bd)
            return bigDecimal = bd;

        if (number instanceof UnparsedNumber un)
            return bigDecimal = un.bigDecimalValue();

        if (number instanceof UnparsedHexNumber uhn)
            return bigDecimal = uhn.bigDecimalValue();

        if (number instanceof KotlinUnsignedIntWrapper knw)
            return bigDecimal = knw.toBigDecimal();

        // BigDecimal does not support NaN or Infinity, we'll just return zero
        if (finiteness() != Finiteness.FINITE)
            return bigDecimal = BigDecimal.ZERO;

        return bigDecimal = BigDecimal.valueOf(asDouble());
    }

    @Override
    public Number asNumber() {
        return number;
    }

    @Override
    public boolean asBoolean() {
        throw new IncorrectTypeException(NodeType.NUMBER, NodeType.BOOLEAN);
    }

    public Finiteness finiteness() {
        if (number instanceof Double d) {
            if (d.isNaN()) {
                return Finiteness.NAN;
            }

            if (d.isInfinite()) {
                return d < 0d ? Finiteness.NEGATIVE_INFINITE : Finiteness.POSITIVE_INFINITE;
            }
        }

        if (number instanceof Float f) {
            if (f.isNaN()) {
                return Finiteness.NAN;
            }

            if (f.isInfinite()) {
                return f < 0f ? Finiteness.NEGATIVE_INFINITE : Finiteness.POSITIVE_INFINITE;
            }
        }

        return Finiteness.FINITE;
    }

    // We use BigDecimal to resemble this number so numbers of different types are equal
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        NumberNode other = (NumberNode) o;
        Finiteness finiteness = finiteness();

        if (!finiteness.equals(other.finiteness())) {
            return false;
        }

        if (finiteness != Finiteness.FINITE) {
            return true;
        }

        return asBigDecimal().equals(other.asBigDecimal());
    }

    @Override
    public int hashCode() {
        Finiteness finiteness = finiteness();

        return switch (finiteness) {
            case FINITE -> Objects.hash(asBigDecimal());
            case NAN -> NAN_HASH;
            case POSITIVE_INFINITE -> POSITIVE_INFINITY_HASH;
            case NEGATIVE_INFINITE -> NEGATIVE_INFINITY_HASH;
        };
    }

    @Override
    public String toString() {
        if (string != null) {
            return string;
        }

        if (number instanceof KotlinUnsignedIntWrapper knw) {
            return string = knw.represent();
        }

        if (number instanceof UnparsedNumber un) {
            return string = un.toJsonValidString();
        }

        if (number instanceof UnparsedHexNumber uhn) {
            return string = uhn.toJsonValidString();
        }

        return string = switch (finiteness()) {
            case FINITE -> {
                // Try print as integer, otherwise print decimal. Avoid printing 1 as 1.0.
                BigDecimal decimal = asBigDecimal();
                try {
                    BigInteger integer = decimal.toBigIntegerExact();
                    yield integer.toString();
                } catch (ArithmeticException exc) {
                    yield decimal.toString();
                }
            }
            case NAN -> "NaN";
            case POSITIVE_INFINITE -> "Infinity";
            case NEGATIVE_INFINITE -> "-Infinity";
        };
    }
}
