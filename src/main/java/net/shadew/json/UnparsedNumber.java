package net.shadew.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Locale;

class UnparsedNumber extends Number {
    private final String number;
    private boolean hasValue;
    private double value;
    private BigDecimal bigValue;

    UnparsedNumber(String number) {
        this.number = number;
    }

    @Override
    public int intValue() {
        return (int) doubleValue();
    }

    @Override
    public long longValue() {
        return (long) doubleValue();
    }

    @Override
    public float floatValue() {
        return (float) doubleValue();
    }

    @Override
    public double doubleValue() {
        if (!hasValue) {
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

            String full = integral;
            if (full.isEmpty()) full = "0";
            if (full.equals("-")) full = "-0";
            if (full.equals("+")) full = "0";
            if (!decimal.isEmpty()) {
                full += "." + decimal;
            }
            if (!exponent.isEmpty()) {
                if (exponent.equals("-")) exponent = "-0";
                if (exponent.equals("+")) exponent = "0";
                full += "e" + exponent;
            }
            value = Double.parseDouble(full);
            hasValue = true;
        }
        return value;
    }

    public BigInteger bigIntegerValue() {
        return bigDecimalValue().toBigInteger();
    }

    public BigDecimal bigDecimalValue() {
        if (bigValue == null) {
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

            String full = integral;
            if (full.isEmpty()) full = "0";
            if (full.equals("-")) full = "-0";
            if (full.equals("+")) full = "0";
            if (!decimal.isEmpty()) {
                full += "." + decimal;
            }
            if (!exponent.isEmpty()) {
                if (exponent.equals("-")) exponent = "-0";
                if (exponent.equals("+")) exponent = "0";
                full += "e" + exponent;
            }
            bigValue = new BigDecimal(full);
        }
        return bigValue;
    }

    @Override
    public String toString() {
        return number;
    }

    public String toJsonValidString() {
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

        String full = integral;
        if (full.isEmpty()) full = "0";
        if (full.equals("-")) full = "-0";
        if (full.equals("+")) full = "0";
        if (!decimal.isEmpty()) {
            full += "." + decimal;
        }
        if (!exponent.isEmpty()) {
            if (exponent.equals("-")) exponent = "-0";
            if (exponent.equals("+")) exponent = "0";
            full += "e" + exponent;
        }
        return new BigDecimal(full).toString().toLowerCase(Locale.ROOT);
    }
}
