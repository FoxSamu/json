package net.shadew.json;

import java.math.BigDecimal;
import java.math.BigInteger;

class UnparsedHexNumber extends Number {
    private final String number;
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

    UnparsedHexNumber(String number) {
        this.number = number;
    }

    @Override
    public int intValue() {
        if (!hasIntValue) {
            int v = 0;
            int sign = 1;
            String nr = number;
            int start = 2;
            if (nr.startsWith("-")) {
                sign = -1;
                start = 3;
            }
            if (nr.startsWith("+")) {
                start = 3;
            }
            for (int i = start, l = nr.length(); i < l; i++) {
                char c = nr.charAt(i);
                int hex = CharUtil.getHexDigitValue(c);
                if (hex < 0) {
                    throw new NumberFormatException("For input string: " + number);
                }
                v <<= 4;
                v |= hex;
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
            int start = 2;
            if (nr.startsWith("-")) {
                sign = -1;
                start = 3;
            }
            if (nr.startsWith("+")) {
                start = 3;
            }
            for (int i = start, l = nr.length(); i < l; i++) {
                char c = nr.charAt(i);
                int hex = CharUtil.getHexDigitValue(c);
                if (hex < 0) {
                    throw new NumberFormatException("For input string: " + number);
                }
                v <<= 4;
                v |= hex;
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
            int start = 2;
            if (nr.startsWith("-")) {
                sign = -1;
                start = 3;
            }
            if (nr.startsWith("+")) {
                start = 3;
            }
            for (int i = start, l = nr.length(); i < l; i++) {
                char c = nr.charAt(i);
                int hex = CharUtil.getHexDigitValue(c);
                if (hex < 0) {
                    throw new NumberFormatException("For input string: " + number);
                }
                v *= 16;
                v += hex;
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
            int start = 2;
            if (nr.startsWith("-")) {
                sign = -1;
                start = 3;
            }
            if (nr.startsWith("+")) {
                start = 3;
            }
            for (int i = start, l = nr.length(); i < l; i++) {
                char c = nr.charAt(i);
                int hex = CharUtil.getHexDigitValue(c);
                if (hex < 0) {
                    throw new NumberFormatException("For input string: " + number);
                }
                v *= 16;
                v += hex;
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

            int start = 2;
            if (nr.startsWith("-")) {
                sign = false;
                start = 3;
            }
            if (nr.startsWith("+")) {
                start = 3;
            }
            String hex = (sign ? "" : "-") + nr.substring(start);
            bigIntValue = new BigInteger(hex, 16);
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
        BigInteger i = new BigInteger(sign + number.substring(sign.length() + 2), 16);
        return i.toString();
    }
}
