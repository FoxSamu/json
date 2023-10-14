package dev.runefox.json;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Wraps Kotlin unsigned numbers and is checked for to correctly represent them in JSON.
 */
abstract class KotlinNumberWrapper extends Number {
    public abstract String represent();
    public abstract BigInteger toBigInteger();
    public abstract BigDecimal toBigDecimal();
}
