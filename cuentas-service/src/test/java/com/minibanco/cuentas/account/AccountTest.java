package com.minibanco.cuentas.account;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AccountTest {

    @Test
    void debit_whenEnoughBalance_subtractsAmount(){
        Account ana = new Account("Ana", new BigDecimal("100.00"));

        ana.debit(new BigDecimal("30.00"));

        assertEquals(new BigDecimal("70.00"), ana.getBalance());
    }

    @Test
    void debit_whenInsufficientBalance_throwsInsufficientFundsException(){
        Account ana = new Account("Ana", new BigDecimal("100.00"));

        assertThrows(InsufficientFundsException.class, () -> ana.debit(new BigDecimal("150.00")));
    }
}
