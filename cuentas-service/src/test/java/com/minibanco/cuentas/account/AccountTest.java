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

    @Test
    void debit_whenAmountEqualsBalance_leavesZeroBalance(){
        Account ana = new Account("Ana", new BigDecimal("100.00"));

        ana.debit(new BigDecimal("100.00"));

        assertEquals(new BigDecimal("0.00"), ana.getBalance());

    }

    @Test
    void credit_whenAmountIsNegative_throwsIllegalArgumentException(){
        Account ana = new Account("Ana", new BigDecimal("100.00"));

        assertThrows(IllegalArgumentException.class, () -> ana.credit(new BigDecimal("-50.00")));

    }

    @Test
    void credit_whenAmountIsZero_throwsIllegalArgumentException(){
        Account ana = new Account("Ana", new BigDecimal("100.00"));

        assertThrows(IllegalArgumentException.class, () -> ana.credit(new BigDecimal("0.00")));
    }

    @Test
    void credit_whenAmountIsPositive_addsAmount(){
        Account ana = new Account("Ana", new BigDecimal("100.00"));

        ana.credit(new BigDecimal("50.00"));

        assertEquals(new BigDecimal("150.00"), ana.getBalance());
    }
}
