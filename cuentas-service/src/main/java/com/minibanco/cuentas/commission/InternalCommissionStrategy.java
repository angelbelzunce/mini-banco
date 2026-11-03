package com.minibanco.cuentas.commission;

import com.minibanco.cuentas.transfer.TransferType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class InternalCommissionStrategy implements CommissionStrategy {

    @Override
    public BigDecimal calculate(BigDecimal amount) {
        return BigDecimal.ZERO;
    }

    @Override
    public TransferType supports() {
        return TransferType.INTERNAL;
    }
}
