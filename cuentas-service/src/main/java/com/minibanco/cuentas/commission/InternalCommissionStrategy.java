package com.minibanco.cuentas.commission;

import com.minibanco.cuentas.transfer.TransferType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class InternalCommissionStrategy implements CommissionStrategy {

    private final static BigDecimal ZERO_COMMISSION = new BigDecimal("0.00");

    @Override
    public BigDecimal calculate(BigDecimal amount) {
        return ZERO_COMMISSION;
    }

    @Override
    public TransferType supports() {
        return TransferType.INTERNAL;
    }
}
