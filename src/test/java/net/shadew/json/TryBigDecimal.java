package net.shadew.json;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TryBigDecimal {
    public static void main(String[] args) {
        BigDecimal a = new BigDecimal("1");
        BigDecimal b = new BigDecimal("3");
        System.out.println(a.divide(b, 15, RoundingMode.DOWN));
    }
}
