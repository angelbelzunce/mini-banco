package com.minibanco.cuentas.commission;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ExternalCommissionStrategy implements CommissionStrategy {

    private static final BigDecimal RATE = new BigDecimal("0.01");

    @Override
    public BigDecimal calculate(BigDecimal amount) {
        return amount.multiply(RATE).setScale(2, RoundingMode.HALF_EVEN);
    }
}
