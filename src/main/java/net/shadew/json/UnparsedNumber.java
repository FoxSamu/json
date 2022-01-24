package net.shadew.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

class UnparsedNumber extends Number {
    private final String number;
    private String full, integral;
    private boolean hasValue;
    private double value;
    private boolean hasIntValue;
    private long intValue;
    private BigInteger bigIntValue;
    private BigDecimal bigValue;

    UnparsedNumber(String number) {
        this.number = number;
    }

    @Override
    public int intValue() {
        return (int) longValue();
    }

    private String full() {
        if (full != null) return full;

        String integral = number.toLowerCase(Locale.ROOT);
        String decimal = "";
        String exponent = "";
        int dot = integral.indexOf('.');

        if (dot >= 0) {
            String igr = integral;
            integral = igr.substring(0, dot);
            decimal = igr.substring(dot + 1);

            int e = decimal.indexOf('e');
            if (e >= 0) {
                String dc = decimal;
                decimal = dc.substring(0, e);
                exponent = dc.substring(e + 1);
            }
        } else {
            int e = integral.indexOf('e');
            if (e >= 0) {
                String igr = integral;
                integral = igr.substring(0, e);
                exponent = igr.substring(e + 1);
            }
        }

        if (integral.isEmpty()) integral = "0";
        if (integral.equals("-")) integral = "-0";
        if (integral.equals("+")) integral = "0";

        String full = integral;
        if (!decimal.isEmpty()) {
            full += "." + decimal;
        }
        if (!exponent.isEmpty()) {
            if (exponent.equals("-")) exponent = "-0";
            if (exponent.equals("+")) exponent = "0";
            full += "e" + exponent;
        }
        this.full = full;
        this.integral = integral;
        return full;
    }

    private String integral() {
        if (integral == null) full();
        return integral;
    }

    @Override
    public long longValue() {
        if (!hasIntValue) {
            try {
                intValue = Long.parseLong(integral());
            } catch (NumberFormatException exc) {
                intValue = 0;
            }
            hasIntValue = true;
        }
        return intValue;
    }

    @Override
    public float floatValue() {
        return (float) doubleValue();
    }

    @Override
    public double doubleValue() {
        if (!hasValue) {
            try {
                value = Double.parseDouble(full());
            } catch (NumberFormatException exc) {
                value = Double.NaN;
            }
            hasValue = true;
        }
        return value;
    }

    public BigInteger bigIntegerValue() {
        if (bigIntValue == null) {
            try {
                bigIntValue = new BigInteger(integral());
            } catch (NumberFormatException exc) {
                bigIntValue = BigInteger.ZERO;
            }
        }
        return bigIntValue;
    }

    public BigDecimal bigDecimalValue() {
        if (bigValue == null) {
            try {
                bigValue = new BigDecimal(full());
            } catch (NumberFormatException exc) {
                bigValue = BigDecimal.ZERO;
            }
        }
        return bigValue;
    }

    @Override
    public String toString() {
        return number;
    }

    public String toJsonValidString() {
        return new BigDecimal(full()).toString().toLowerCase(Locale.ROOT);
    }
}
