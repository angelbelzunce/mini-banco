package com.minibanco.cuentas.commission;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExternalCommissionStrategyTest {

    @Test
    void calculate_whenAmountIs100_returnsOnePercent() {

        ExternalCommissionStrategy strategy = new ExternalCommissionStrategy();
        BigDecimal amount = new BigDecimal("100.00");

        BigDecimal commission = strategy.calculate(amount);

        assertEquals(new BigDecimal("1.00"), commission);

    }

    @Test
    void calculate_whenResultHasMoreThanTwoDecimals_roundsToTwo() {

        ExternalCommissionStrategy strategy = new ExternalCommissionStrategy();
        BigDecimal amount = new BigDecimal("33.33");

        BigDecimal commission = strategy.calculate(amount);

        assertEquals(new BigDecimal("0.33"), commission);

    }
}
