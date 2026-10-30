package com.minibanco.cuentas.account;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String holder;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    // Optimistic locking: rejects concurrent updates on the same account
    @Version
    private Long version;

    protected Account() {
    }

    public Account(String holder, BigDecimal initialBalance) {
        this.holder = holder;
        this.balance = initialBalance;
    }

    public Long getId() {
        return id;
    }

    public String getHolder() {
        return holder;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public Long getVersion() {
        return version;
    }
}
