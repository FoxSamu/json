package dev.runefox.json;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/*
 * These tests ensure, to a certain extent, that methods like asString, asByte, etc. from JsonNode work as expected.
 */
public class ValueConversionTests {
    private static final UnparsedNumber U31 = new UnparsedNumber("31");
    private static final BigInteger I31 = new BigInteger("31");
    private static final BigDecimal D31 = new BigDecimal("31");

    @Test
    void testNullString() {
        JsonNode node = JsonNode.NULL;
        assertEquals("null", node.asString());
    }

    @Test
    void testNumString() {
        JsonNode node = JsonNode.number(U31);
        assertEquals("31", node.asString());
    }

    @Test
    void testByte() {
        JsonNode node = JsonNode.number(U31);
        assertEquals((byte) 31, node.asByte());
    }

    @Test
    void testShort() {
        JsonNode node = JsonNode.number(U31);
        assertEquals((short) 31, node.asShort());
    }

    @Test
    void testInt() {
        JsonNode node = JsonNode.number(U31);
        assertEquals(31, node.asInt());
    }

    @Test
    void testLong() {
        JsonNode node = JsonNode.number(U31);
        assertEquals(31L, node.asLong());
    }

    @Test
    void testFloat() {
        JsonNode node = JsonNode.number(U31);
        assertEquals(31f, node.asFloat(), 0.01f);
    }

    @Test
    void testDouble() {
        JsonNode node = JsonNode.number(U31);
        assertEquals(31d, node.asDouble(), 0.01);
    }

    @Test
    void testBigInt() {
        JsonNode node = JsonNode.number(U31);
        assertEquals(I31, node.asBigInteger());
    }

    @Test
    void testBigDec() {
        JsonNode node = JsonNode.number(U31);
        assertEquals(D31, node.asBigDecimal());
    }

    @Test
    void testString() {
        JsonNode node = JsonNode.string("string");
        assertEquals("string", node.asExactString());
        assertEquals("string", node.asString());
    }

    @Test
    void testBoolean() {
        JsonNode node = JsonNode.bool(true);
        assertEquals(true, node.asBoolean());
        assertEquals("true", node.asString());
    }

    @Test
    void testByteArr() {
        JsonNode node = JsonNode.numberArray(U31, U31);
        assertArrayEquals(new byte[] {31, 31}, node.asByteArray());
    }

    @Test
    void testShortArr() {
        JsonNode node = JsonNode.numberArray(U31, U31);
        assertArrayEquals(new short[] {31, 31}, node.asShortArray());
    }

    @Test
    void testIntArr() {
        JsonNode node = JsonNode.numberArray(U31, U31);
        assertArrayEquals(new int[] {31, 31}, node.asIntArray());
    }

    @Test
    void testLongArr() {
        JsonNode node = JsonNode.numberArray(U31, U31);
        assertArrayEquals(new long[] {31, 31}, node.asLongArray());
    }

    @Test
    void testFloatArr() {
        JsonNode node = JsonNode.numberArray(U31, U31);
        assertArrayEquals(new float[] {31, 31}, node.asFloatArray(), 0.01f);
    }

    @Test
    void testDoubleArr() {
        JsonNode node = JsonNode.numberArray(U31, U31);
        assertArrayEquals(new double[] {31, 31}, node.asDoubleArray(), 0.01);
    }

    @Test
    void testBigIntArr() {
        JsonNode node = JsonNode.numberArray(U31, U31);
        assertArrayEquals(new BigInteger[] {I31, I31}, node.asBigIntegerArray());
    }

    @Test
    void testBigDecArr() {
        JsonNode node = JsonNode.numberArray(U31, U31);
        assertArrayEquals(new BigDecimal[] {D31, D31}, node.asBigDecimalArray());
    }

    @Test
    void testStringArr() {
        JsonNode node = JsonNode.stringArray("string", "string");
        assertArrayEquals(new String[] {"string", "string"}, node.asStringArray());
    }

    @Test
    void testBoolArr() {
        JsonNode node = JsonNode.boolArray(true, false);
        assertArrayEquals(new boolean[] {true, false}, node.asBooleanArray());
    }

    @Test
    void testByteArrLenCorrect() {
        JsonNode node = JsonNode.numberArray(U31, U31);
        assertArrayEquals(new byte[] {31, 31}, node.asByteArray(2));
    }

    @Test
    void testShortArrLenCorrect() {
        JsonNode node = JsonNode.numberArray(U31, U31);
        assertArrayEquals(new short[] {31, 31}, node.asShortArray(2));
    }

    @Test
    void testIntArrLenCorrect() {
        JsonNode node = JsonNode.numberArray(U31, U31);
        assertArrayEquals(new int[] {31, 31}, node.asIntArray(2));
    }

    @Test
    void testLongArrLenCorrect() {
        JsonNode node = JsonNode.numberArray(U31, U31);
        assertArrayEquals(new long[] {31, 31}, node.asLongArray(2));
    }

    @Test
    void testFloatArrLenCorrect() {
        JsonNode node = JsonNode.numberArray(U31, U31);
        assertArrayEquals(new float[] {31, 31}, node.asFloatArray(2), 0.01f);
    }

    @Test
    void testDoubleArrLenCorrect() {
        JsonNode node = JsonNode.numberArray(U31, U31);
        assertArrayEquals(new double[] {31, 31}, node.asDoubleArray(2), 0.01);
    }

    @Test
    void testBigIntArrLenCorrect() {
        JsonNode node = JsonNode.numberArray(U31, U31);
        assertArrayEquals(new BigInteger[] {I31, I31}, node.asBigIntegerArray(2));
    }

    @Test
    void testBigDecArrLenCorrect() {
        JsonNode node = JsonNode.numberArray(U31, U31);
        assertArrayEquals(new BigDecimal[] {D31, D31}, node.asBigDecimalArray(2));
    }

    @Test
    void testStringArrLenCorrect() {
        JsonNode node = JsonNode.stringArray("string", "string");
        assertArrayEquals(new String[] {"string", "string"}, node.asStringArray(2));
    }

    @Test
    void testBoolArrLenCorrect() {
        JsonNode node = JsonNode.boolArray(true, false);
        assertArrayEquals(new boolean[] {true, false}, node.asBooleanArray(2));
    }

    @Test
    void testByteArrLenNotCorrect() {
        JsonNode node = JsonNode.numberArray(U31, U31);
        assertThrows(IncorrectSizeException.class, () -> node.asByteArray(1));
    }

    @Test
    void testShortArrLenNotCorrect() {
        JsonNode node = JsonNode.numberArray(U31, U31);
        assertThrows(IncorrectSizeException.class, () -> node.asShortArray(1));
    }

    @Test
    void testIntArrLenNotCorrect() {
        JsonNode node = JsonNode.numberArray(U31, U31);
        assertThrows(IncorrectSizeException.class, () -> node.asIntArray(1));
    }

    @Test
    void testLongArrLenNotCorrect() {
        JsonNode node = JsonNode.numberArray(U31, U31);
        assertThrows(IncorrectSizeException.class, () -> node.asLongArray(1));
    }

    @Test
    void testFloatArrLenNotCorrect() {
        JsonNode node = JsonNode.numberArray(U31, U31);
        assertThrows(IncorrectSizeException.class, () -> node.asFloatArray(1));
    }

    @Test
    void testDoubleArrLenNotCorrect() {
        JsonNode node = JsonNode.numberArray(U31, U31);
        assertThrows(IncorrectSizeException.class, () -> node.asDoubleArray(1));
    }

    @Test
    void testBigIntArrLenNotCorrect() {
        JsonNode node = JsonNode.numberArray(U31, U31);
        assertThrows(IncorrectSizeException.class, () -> node.asBigIntegerArray(1));
    }

    @Test
    void testBigDecArrLenNotCorrect() {
        JsonNode node = JsonNode.numberArray(U31, U31);
        assertThrows(IncorrectSizeException.class, () -> node.asBigDecimalArray(1));
    }

    @Test
    void testStringArrLenNotCorrect() {
        JsonNode node = JsonNode.stringArray("string", "string");
        assertThrows(IncorrectSizeException.class, () -> node.asStringArray(1));
    }

    @Test
    void testBoolArrLenNotCorrect() {
        JsonNode node = JsonNode.boolArray(true, false);
        assertThrows(IncorrectSizeException.class, () -> node.asBooleanArray(1));
    }

    @Test
    void testList() {
        JsonNode a = JsonNode.bool(true);
        JsonNode b = JsonNode.string("hello");
        JsonNode node = JsonNode.array(a, b);
        assertEquals(node.asList(), Arrays.asList(a, b));
    }

    @Test
    void testMap() {
        JsonNode a = JsonNode.bool(true);
        JsonNode b = JsonNode.string("hello");
        Map<String, JsonNode> map = new HashMap<>();
        map.put("a", a);
        map.put("b", b);
        JsonNode node = JsonNode.object(map);
        assertEquals(node.asMap(), map);
    }
}
