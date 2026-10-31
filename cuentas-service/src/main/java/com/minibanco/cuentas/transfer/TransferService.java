package com.minibanco.cuentas.transfer;

import com.minibanco.cuentas.account.Account;
import com.minibanco.cuentas.account.AccountNotFoundException;
import com.minibanco.cuentas.account.AccountRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;

    public TransferService(AccountRepository accountRepository, TransferRepository transferRepository) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
    }

    @Transactional
    public TransferResponse transfer(String idempotencyKey, CreateTransferRequest request) {
        // 1. Idempotencia: si ya existe una transferencia con esta clave, devuelvo esa
        Optional<Transfer> existing = transferRepository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            return TransferResponse.from(existing.get());
        }
        // 2. Busco las dos cuentas (404 si no existen)
        Account source = accountRepository.findById(request.sourceAccountId()).orElseThrow(() -> new AccountNotFoundException(request.sourceAccountId()));

        Account target = accountRepository.findById(request.targetAccountId()).orElseThrow(() -> new AccountNotFoundException(request.targetAccountId()));

        // 3. Muevo el dinero
        source.debit(request.amount());
        target.credit(request.amount());

        // 4. Guardo la transferencia
        Transfer transfer = new Transfer(request.sourceAccountId(), request.targetAccountId(), request.amount(), idempotencyKey, LocalDateTime.now(), "COMPLETED");

        return TransferResponse.from(transferRepository.save(transfer));

    }

    public TransferResponse findById(Long id) {
        Transfer transfer = transferRepository.findById(id).orElseThrow(() -> new TransferNotFoundException(id));

        return TransferResponse.from(transfer);
    }
}
