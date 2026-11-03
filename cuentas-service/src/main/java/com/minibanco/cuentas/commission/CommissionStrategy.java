package com.minibanco.cuentas.commission;

import com.minibanco.cuentas.transfer.TransferType;

import java.math.BigDecimal;

public interface CommissionStrategy {

    BigDecimal calculate(BigDecimal amount);

    TransferType supports();
}
