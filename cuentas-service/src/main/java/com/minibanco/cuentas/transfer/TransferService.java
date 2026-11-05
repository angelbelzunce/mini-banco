package com.minibanco.cuentas.transfer;

import com.minibanco.cuentas.account.Account;
import com.minibanco.cuentas.account.AccountNotFoundException;
import com.minibanco.cuentas.account.AccountRepository;
import com.minibanco.cuentas.commission.CommissionStrategy;
import com.minibanco.cuentas.event.TransferEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransferRepository transferRepository;

    private final List<CommissionStrategy> commissionStrategies;
    private final TransferEventPublisher transferEventPublisher;

    public TransferService(AccountRepository accountRepository, TransferRepository transferRepository, List<CommissionStrategy> commissionStrategies, TransferEventPublisher transferEventPublisher) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;

        this.commissionStrategies = commissionStrategies;
        this.transferEventPublisher = transferEventPublisher;
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

        BigDecimal commission = findStrategy(request.transferType()).calculate(request.amount());

        // 3. Muevo el dinero
        source.debit(request.amount().add(commission));
        target.credit(request.amount());

        // 4. Guardo la transferencia
        Transfer transfer = new Transfer(request.sourceAccountId(), request.targetAccountId(), request.amount(), idempotencyKey, LocalDateTime.now(), "COMPLETED");

        TransferResponse saved = TransferResponse.from(transferRepository.save(transfer));
        transferEventPublisher.publish(saved.id());
        return saved;

    }

    public TransferResponse findById(Long id) {
        Transfer transfer = transferRepository.findById(id).orElseThrow(() -> new TransferNotFoundException(id));

        return TransferResponse.from(transfer);
    }

    private CommissionStrategy findStrategy(TransferType transferType) {

        for (CommissionStrategy strategy : commissionStrategies) {
            if (strategy.supports() == transferType) {
                return strategy;
            }
        }
        throw new IllegalStateException("No commission strategy for type " + transferType);
    }
}
