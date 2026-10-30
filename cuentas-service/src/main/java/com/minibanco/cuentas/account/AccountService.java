package com.minibanco.cuentas.account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountResponse create(CreateAccountRequest request) {

        Account account = new Account(request.holder(), request.initialBalance());

        Account savedAccount = accountRepository.save(account);

        return AccountResponse.from(savedAccount);
    }

    public AccountResponse findById(Long id) {

        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        return AccountResponse.from(account);
    }

    // Metodo encargado de paginar una consulta
    public Page<AccountResponse> findAll(Pageable pageable) {

        return accountRepository.findAll(pageable)
                .map(AccountResponse::from);
    }
}
