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
    private boolean isZero;
    private boolean isIntegral;

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

        boolean integralZero = false;
        if (integral.isEmpty()) {
            integral = "0";
            integralZero = true;
        }
        if (integral.equals("-")) {
            integral = "-0";
            integralZero = true;
        }
        if (integral.equals("+")) {
            integral = "0";
            integralZero = true;
        }
        zeroCheck:
        if (!integralZero) {
            int s = 0;
            if (integral.startsWith("-") || integral.startsWith("+")) s = 1;
            for (int i = s, l = integral.length(); i < l; i++) {
                if (integral.charAt(i) != '0')
                    break zeroCheck;
            }
            integralZero = true;
        }

        boolean decimalZero = true;
        String full = integral;
        if (!decimal.isEmpty()) {
            full += "." + decimal;
            decimalZero = false;
        }

        zeroCheck:
        if (!decimalZero) {
            for (int i = 0, l = decimal.length(); i < l; i++) {
                if (decimal.charAt(i) != '0')
                    break zeroCheck;
            }
            decimalZero = true;
        }

        if (!exponent.isEmpty()) {
            if (exponent.equals("-")) exponent = "-0";
            if (exponent.equals("+")) exponent = "0";
            full += "e" + exponent;
        }
        // If number is zero, exponent can be anything but it will remain zero

        boolean zero = integralZero && decimalZero;
        if (!zero) {
            if (!exponent.isEmpty()) {
                BigInteger i = new BigInteger(exponent);
                int comp = i.compareTo(BigInteger.ZERO);
                if (comp < 0) {
                    integral = null;
                } else if (comp > 0 && i.compareTo(BigInteger.TEN) <= 0) {
                    integral += "0".repeat(i.intValue());
                }
            }
        } else {
            integral = "0";
            bigIntValue = BigInteger.ZERO;
            bigValue = BigDecimal.ZERO;
            value = 0;
            hasValue = true;
            intValue = 0;
            hasIntValue = true;
        }

        this.full = full;
        this.integral = integral;
        this.isZero = integralZero && decimalZero;
        this.isIntegral = decimalZero;
        return full;
    }

    boolean isZero() {
        full();
        return isZero;
    }

    boolean isIntegral() {
        full();
        return isIntegral;
    }

    String integral() {
        if (integral == null) full();
        return integral;
    }

    @Override
    public long longValue() {
        if (!hasIntValue) {
            try {
                String i = integral();
                intValue = i == null ? bigDecimalValue().longValue() : Long.parseLong(i);
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
                String i = integral();
                bigIntValue = i == null ? bigDecimalValue().toBigInteger() : new BigInteger(i);
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

    public static boolean isZero(Number number) {
        if (number instanceof UnparsedNumber)
            return ((UnparsedNumber) number).isZero();
        if (number instanceof UnparsedHexNumber)
            return ((UnparsedHexNumber) number).isZero();
        if (number instanceof BigInteger)
            return number.equals(BigInteger.ZERO);
        if (number instanceof BigDecimal)
            return number.equals(BigDecimal.ZERO);
        if (number instanceof Byte || number instanceof Short || number instanceof Integer)
            return number.intValue() == 0;
        if (number instanceof Long)
            return number.longValue() == 0L;
        if (number instanceof Float)
            return number.floatValue() == 0F;
        return number.doubleValue() == 0D;
    }
}
