package net.shadew.json;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NumberTests {
    @Test
    void testExponentToInt() {
        UnparsedNumber num = new UnparsedNumber("3e5");
        Assertions.assertEquals("300000", num.integral());
    }
}
