package dev.runefox.json.impl;

import dev.runefox.json.impl.parse.CharUtil;

import java.math.BigDecimal;
import java.math.BigInteger;

public class LazyParseRadix extends Number {
    private final String number;
    private final int radix;
    private final int radixP2;
    private final int prefixLen;
    private boolean hasIntValue;
    private int intValue;
    private boolean hasLongValue;
    private long longValue;
    private boolean hasFloatValue;
    private float floatValue;
    private boolean hasDoubleValue;
    private double doubleValue;
    private BigInteger bigIntValue;
    private BigDecimal bigDecValue;
    private boolean isZeroDefined;
    private boolean isZero;

    // radixP2 is the power of 2, not the radix itself.
    // radix = 1 << radixP2

    // For decimal numbers, see LazyParseNumber
    private LazyParseRadix(String number, int radixP2, int prefixLen) {
        this.number = number;
        this.radix = 1 << radixP2;
        this.radixP2 = radixP2;
        this.prefixLen = prefixLen;
    }

    public static LazyParseRadix hex(String number) {
        return new LazyParseRadix(number, 4, 2);
    }

    public static LazyParseRadix oct(String number) {
        return new LazyParseRadix(number, 3, 2);
    }

    public static LazyParseRadix bin(String number) {
        return new LazyParseRadix(number, 1, 2);
    }

    public boolean isZero() {
        if (!isZeroDefined) {
            boolean zero = true;
            String nr = number;
            int start = prefixLen;
            if (nr.startsWith("-")) {
                start = prefixLen + 1;
            }
            if (nr.startsWith("+")) {
                start = prefixLen + 1;
            }
            for (int i = start, l = nr.length(); i < l; i++) {
                char c = nr.charAt(i);
                if (c != '0') {
                    zero = false;
                    break;
                }
            }
            isZero = zero;
            isZeroDefined = true;
        }
        return isZero;
    }

    @Override
    public int intValue() {
        if (!hasIntValue) {
            int v = 0;
            int sign = 1;
            String nr = number;
            int start = prefixLen;
            if (nr.startsWith("-")) {
                sign = -1;
                start = prefixLen + 1;
            }
            if (nr.startsWith("+")) {
                start = prefixLen + 1;
            }
            for (int i = start, l = nr.length(); i < l; i++) {
                char c = nr.charAt(i);
                int bin = CharUtil.radit(c, radix);
                if (bin < 0) {
                    throw new NumberFormatException("For input string: " + number);
                }
                v <<= radixP2;
                v |= bin;
            }
            intValue = v * sign;
            hasIntValue = true;
        }
        return intValue;
    }

    @Override
    public long longValue() {
        if (!hasLongValue) {
            long v = 0;
            long sign = 1;
            String nr = number;
            int start = prefixLen;
            if (nr.startsWith("-")) {
                sign = -1;
                start = prefixLen + 1;
            }
            if (nr.startsWith("+")) {
                start = prefixLen + 1;
            }
            for (int i = start, l = nr.length(); i < l; i++) {
                char c = nr.charAt(i);
                int bin = CharUtil.radit(c, radix);
                if (bin < 0) {
                    throw new NumberFormatException("For input string: " + number);
                }
                v <<= radixP2;
                v |= bin;
            }
            longValue = v * sign;
            hasLongValue = true;
        }
        return longValue;
    }

    @Override
    public float floatValue() {
        if (!hasFloatValue) {
            float v = 0;
            float sign = 1;
            String nr = number;
            int start = prefixLen;
            if (nr.startsWith("-")) {
                sign = -1;
                start = prefixLen + 1;
            }
            if (nr.startsWith("+")) {
                start = prefixLen + 1;
            }
            for (int i = start, l = nr.length(); i < l; i++) {
                char c = nr.charAt(i);
                int bin = CharUtil.radit(c, radix);
                if (bin < 0) {
                    throw new NumberFormatException("For input string: " + number);
                }
                v *= radix;
                v += bin;
            }
            floatValue = v * sign;
            hasFloatValue = true;
        }
        return floatValue;
    }

    @Override
    public double doubleValue() {
        if (!hasDoubleValue) {
            double v = 0;
            double sign = 1;
            String nr = number;
            int start = prefixLen;
            if (nr.startsWith("-")) {
                sign = -1;
                start = prefixLen + 1;
            }
            if (nr.startsWith("+")) {
                start = prefixLen + 1;
            }
            for (int i = start, l = nr.length(); i < l; i++) {
                char c = nr.charAt(i);
                int bin = CharUtil.radit(c, radix);
                if (bin < 0) {
                    throw new NumberFormatException("For input string: " + number);
                }
                v *= radix;
                v += bin;
            }
            doubleValue = v * sign;
            hasDoubleValue = true;
        }
        return doubleValue;
    }

    public BigInteger bigIntegerValue() {
        if (bigIntValue == null) {
            boolean sign = true;
            String nr = number;

            int start = prefixLen;
            if (nr.startsWith("-")) {
                sign = false;
                start = prefixLen + 1;
            }
            if (nr.startsWith("+")) {
                start = prefixLen + 1;
            }
            String bin = (sign ? "" : "-") + nr.substring(start);
            bigIntValue = new BigInteger(bin, 2);
        }
        return bigIntValue;
    }

    public BigDecimal bigDecimalValue() {
        if (bigDecValue == null) {
            bigDecValue = new BigDecimal(bigIntegerValue());
        }
        return bigDecValue;
    }

    @Override
    public String toString() {
        return number;
    }

    public String toJsonValidString() {
        String sign = number.startsWith("-") ? "-" : number.startsWith("+") ? "+" : "";
        BigInteger i = new BigInteger(sign + number.substring(sign.length() + prefixLen), radix);
        return i.toString();
    }
}
