package com.minibanco.cuentas.commission;

import com.minibanco.cuentas.transfer.TransferType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InternalCommissionStrategyTest {

    @Test
    void calculate_whenAmountIs100_returnsZero() {

        InternalCommissionStrategy strategy = new InternalCommissionStrategy();
        BigDecimal amount = new BigDecimal("100.00");

        BigDecimal commission = strategy.calculate(amount);

        assertEquals(new BigDecimal("0.00"), commission);

    }

    @Test
    void supports_returnsInternal(){
        InternalCommissionStrategy internalCommissionStrategy = new InternalCommissionStrategy();

        assertEquals(TransferType.INTERNAL, internalCommissionStrategy.supports());
    }
}
