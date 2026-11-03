package com.minibanco.cuentas.commission;

import java.math.BigDecimal;

public interface CommissionStrategy {

    BigDecimal calculate(BigDecimal amount);
}
