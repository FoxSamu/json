package dev.runefox.json.impl;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Wraps Kotlin unsigned numbers and is checked for to correctly represent them in JSON.
 */
public abstract class KotlinNumberWrapper extends Number {
    public abstract String represent();
    public abstract BigInteger toBigInteger();
    public abstract BigDecimal toBigDecimal();
}
