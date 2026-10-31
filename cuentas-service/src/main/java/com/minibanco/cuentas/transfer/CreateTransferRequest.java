package com.minibanco.cuentas.transfer;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreateTransferRequest(@NotNull Long sourceAccountId, @NotNull Long targetAccountId, @NotNull @Positive @Digits(integer = 17, fraction = 2) BigDecimal amount) {
}
