package com.minibanco.cuentas.account;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(Long accountId) {
        super("Insufficient funds in account id: " + accountId);
    }
}
