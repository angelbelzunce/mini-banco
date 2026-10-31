package com.minibanco.cuentas.transfer;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferResponse(Long id, Long sourceAccountId, Long targetAccountId, BigDecimal amount, String status,
                               LocalDateTime createdAt) {

    public static TransferResponse from(Transfer transfer) {
        return new TransferResponse(transfer.getId(), transfer.getSourceAccountId(), transfer.getTargetAccountId(), transfer.getAmount(), transfer.getStatus(), transfer.getCreatedAt());

    }
}
