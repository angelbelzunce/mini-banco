package com.minibanco.cuentas.account;

import java.math.BigDecimal;

public record AccountResponse(Long id, String holder, BigDecimal balance) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getId(), account.getHolder(), account.getBalance());
    }
}
