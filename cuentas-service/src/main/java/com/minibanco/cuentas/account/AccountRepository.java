package com.minibanco.cuentas.account;

import org.springframework.data.jpa.repository.JpaRepository;

// Isn´t neccesary @Repository because extends of JpaRepository and Spring detects it
public interface AccountRepository extends JpaRepository<Account, Long> {
}
